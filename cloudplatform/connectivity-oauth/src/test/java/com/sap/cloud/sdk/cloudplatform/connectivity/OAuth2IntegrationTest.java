package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Map.entry;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;

import io.vavr.control.Try;

@WireMockTest( proxyMode = true )
class OAuth2IntegrationTest
{
    private static final String RESPONSE_TEMPLATE = """
        {
          "access_token": "%s",
          "token_type": "Bearer",
          "expires_in": 3600,
          "scope": "uaa.resource",
          "jti": "abc456"
        }
        """;

    private List<HttpClientFactory> oldFactories = List.of();

    @BeforeEach
    void mockClientFactory()
    {
        oldFactories = HttpClientFactory.services;
        HttpClientFactory.services.clear();

        // `useSystemProperties` is needed for the WireMock proxying
        HttpClientFactory.services.add(identity -> HttpClientBuilder.create().useSystemProperties().build());
        HttpClientAccessor.setHttpClientFactory(new DefaultHttpClientFactory()
        {
            @Override
            protected HttpClientBuilder getHttpClientBuilder( @Nullable HttpDestinationProperties destination )
                throws HttpClientInstantiationException
            {
                return super.getHttpClientBuilder(destination).useSystemProperties();
            }
        });
    }

    @AfterEach
    void restoreClientFactories()
    {
        HttpClientFactory.services.clear();
        HttpClientFactory.services.addAll(oldFactories);
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @Test
    void testIasTokenFlow()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.IDENTITY_AUTHENTICATION,
                entry("credential-type", "binding-secret"),
                entry("clientid", "myClientId"),
                entry("clientsecret", "myClientSecret"),
                entry("url", "http://provider.ias.domain"),
                entry("app_tid", "provider"));
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final Try<HttpDestination> maybeDestination =
            new OAuth2ServiceBindingDestinationLoader().tryGetDestination(options);
        assertThat(maybeDestination.isSuccess()).isTrue();
        final HttpDestination destination = maybeDestination.get();

        {
            // provider test
            final String token = "providerToken";

            stubFor(
                post("/oauth2/token")
                    .withHost(equalTo("provider.ias.domain"))
                    .willReturn(okJson(RESPONSE_TEMPLATE.formatted(token))));

            // no tenant - provider case
            assertThat(TenantAccessor.tryGetCurrentTenant().isFailure()).isTrue();
            assertThat(destination.getHeaders()).contains(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

            // call the method a second time, so we can be sure the token is cached correctly
            assertThat(destination.getHeaders()).contains(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

            verify(
                1,
                postRequestedFor(urlEqualTo("/oauth2/token"))
                    .withHost(equalTo("provider.ias.domain"))
                    .withRequestBody(containing("client_id=myClientId"))
                    .withRequestBody(containing("client_secret=myClientSecret"))
                    .withRequestBody(containing("app_tid=provider")));
        }

        {
            // subscriber test
            final String token = "subscriberToken";

            stubFor(
                post("/oauth2/token")
                    .withHost(equalTo("subscriber.ias.domain"))
                    .willReturn(okJson(RESPONSE_TEMPLATE.formatted(token))));

            TenantAccessor.executeWithTenant(new DefaultTenant("subscriber", "subscriber"), () -> {
                assertThat(destination.getHeaders()).contains(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + token));

                // call the method a second time, so we can be sure the token is cached correctly
                assertThat(destination.getHeaders()).contains(new Header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
            });

            verify(
                1,
                postRequestedFor(urlEqualTo("/oauth2/token"))
                    .withHost(equalTo("subscriber.ias.domain"))
                    .withRequestBody(containing("client_id=myClientId"))
                    .withRequestBody(containing("client_secret=myClientSecret"))
                    .withRequestBody(containing("app_tid=subscriber")));
        }
    }

    @Test
    void testExtended401ErrorMessage()
    {
        final ServiceBinding binding =
            bindingWithCredentials(
                ServiceIdentifier.IDENTITY_AUTHENTICATION,
                entry("credential-type", "binding-secret"),
                entry("clientid", "myClientId"),
                entry("clientsecret", "myClientSecret"),
                entry("url", "http://provider.ias.domain"),
                entry("app_tid", "provider"));
        final ServiceBindingDestinationOptions options = ServiceBindingDestinationOptions.forService(binding).build();

        final Try<HttpDestination> maybeDestination =
            new OAuth2ServiceBindingDestinationLoader().tryGetDestination(options);
        assertThat(maybeDestination.isSuccess()).isTrue();
        final HttpDestination destination = maybeDestination.get();

        {
            // provider case - no tenant:
            // Here, the short error message is returned.
            stubFor(post("/oauth2/token").withHost(equalTo("provider.ias.domain")).willReturn(unauthorized()));
            assertThatCode(destination::getHeaders)
                .isInstanceOf(DestinationAccessException.class)
                .hasMessageEndingWith("Failed to resolve access token.")
                .hasRootCauseInstanceOf(OAuth2ServiceException.class);
        }
        {
            // subscriber tenant:
            // Here, the error message contains a note about the SaaS registry.
            stubFor(post("/oauth2/token").withHost(equalTo("subscriber.ias.domain")).willReturn(unauthorized()));

            TenantAccessor.executeWithTenant(new DefaultTenant("subscriber", "subscriber"), () -> {
                assertThatCode(destination::getHeaders)
                    .isInstanceOf(DestinationAccessException.class)
                    .hasMessageEndingWith("subscribed for the current tenant.")
                    .hasRootCauseInstanceOf(OAuth2ServiceException.class);
            });
        }
    }

    @Test
    @DisplayName( "The subdomain should be replaced for subscriber tenants when using IAS and ZTIS" )
    void testIasFlowWithZeroTrustAndSubscriberTenant()
        throws KeyStoreException,
            CertificateException,
            IOException,
            NoSuchAlgorithmException
    {
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        final ClientIdentity identity = new SecurityLibWorkarounds.ZtisClientIdentity("myClientId", ks);

        stubFor(
            post("/oauth2/token")
                .withHost(equalTo("provider.ias.domain"))
                .willReturn(okJson(RESPONSE_TEMPLATE.formatted("providerToken"))));
        stubFor(
            post("/oauth2/token")
                .withHost(equalTo("subscriber.ias.domain"))
                .willReturn(okJson(RESPONSE_TEMPLATE.formatted("subscriberToken"))));

        final OAuth2Service sut =
            OAuth2Service
                .builder()
                .withTokenUri(URI.create("http://provider.ias.domain/oauth2/token"))
                .withIdentity(identity)
                .withTenantPropagationStrategy(OAuth2Service.TenantPropagationStrategy.TENANT_SUBDOMAIN)
                .build();
        // provider test
        assertThat(sut.retrieveAccessToken()).contains("providerToken");

        // subscriber test
        TenantAccessor.executeWithTenant(new DefaultTenant("subscriber", "subscriber"), () -> {
            assertThat(sut.retrieveAccessToken()).contains("subscriberToken");
        });
        verify(
            1,
            postRequestedFor(urlEqualTo("/oauth2/token"))
                .withHost(equalTo("provider.ias.domain"))
                .withRequestBody(containing("client_id=myClientId")));
        verify(
            1,
            postRequestedFor(urlEqualTo("/oauth2/token"))
                .withHost(equalTo("subscriber.ias.domain"))
                .withRequestBody(containing("client_id=myClientId"))
                .withRequestBody(containing("app_tid=subscriber")));
    }
}
