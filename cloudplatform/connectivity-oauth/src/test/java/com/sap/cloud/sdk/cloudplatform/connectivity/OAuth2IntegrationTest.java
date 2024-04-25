package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Map.entry;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.client.HttpClientFactory;

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
    }

    @AfterEach
    void restoreClientFactories()
    {
        HttpClientFactory.services.clear();
        HttpClientFactory.services.addAll(oldFactories);
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
}
