package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2Service.Endpoints;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2Service.TenantPropagationStrategy;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import lombok.SneakyThrows;

class OAuth2ServiceTest
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
    private static final ClientIdentity IDENTITY_1 = new ClientCredentials("client-1", "sec1");
    private static final ClientIdentity IDENTITY_2 = new ClientCredentials("client-2", "sec2");

    private static final String TOKEN_1 = "token_1";
    private static final String TOKEN_2 = "token_2";

    @RegisterExtension
    static final WireMockExtension SERVER_1 =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();
    @RegisterExtension
    static final WireMockExtension SERVER_2 =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @BeforeEach
    void setUp()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        SERVER_1.stubFor(post("/oauth/token").willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_1))));
        SERVER_2.stubFor(post("/oauth/token").willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_2))));
    }

    @AfterEach
    void tearDown()
    {
        TenantAccessor.setTenantFacade(null);
        OAuth2Service.clearCache();
    }

    @Test
    void singleOAuth2ServiceImpl()
    {
        final OAuth2Service service = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_PROVIDER);

        final String token1 = service.retrieveAccessToken();
        final String token2 = service.retrieveAccessToken();

        assertThat(token1).isEqualTo(TOKEN_1);
        assertThat(token2).isEqualTo(TOKEN_1);

        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void multipleOAuth2ServiceImpl()
    {
        OAuth2Service sut;
        // tokens for the same server and identity should be cached
        sut = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);

        sut = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));

        // A different identity should lead to a cache miss
        sut = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_2, TECHNICAL_USER_CURRENT_TENANT);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        SERVER_1.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));

        // A different server should also lead to a cache miss
        sut = new OAuth2Service(SERVER_2.baseUrl(), IDENTITY_2, TECHNICAL_USER_CURRENT_TENANT);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_2);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_2);
        SERVER_1.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
        SERVER_2.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void singleOAuth2ServiceImplSingleSubscriber()
    {
        final OAuth2Service service = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        final String token1 = TenantAccessor.executeWithTenant(new DefaultTenant("abcd"), service::retrieveAccessToken);
        final String token2 = TenantAccessor.executeWithTenant(new DefaultTenant("abcd"), service::retrieveAccessToken);

        assertThat(token1).isEqualTo(TOKEN_1);
        assertThat(token2).isEqualTo(TOKEN_1);

        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void singleOAuth2ServiceImplMultipleSubscriber()
    {
        final OAuth2Service service = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        final String token1 =
            TenantAccessor.executeWithTenant(new DefaultTenant("tenant 1"), service::retrieveAccessToken);
        final String token2 =
            TenantAccessor.executeWithTenant(new DefaultTenant("tenant 2"), service::retrieveAccessToken);

        assertThat(token1).isEqualTo(TOKEN_1);
        assertThat(token2).isEqualTo(TOKEN_1);

        SERVER_1.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void httpClientTenantSeparation()
    {
        // The reason for this test is to verify, that cookies set for one tenant are not forwarded to another tenant.
        SERVER_1
            .stubFor(
                post("/oauth/token")
                    .willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_1)).withHeader("Set-Cookie", "myCookie=123")));

        final OAuth2Service service = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        TenantAccessor.executeWithTenant(new DefaultTenant("tenant1"), service::retrieveAccessToken);
        TenantAccessor.executeWithTenant(new DefaultTenant("tenant2"), service::retrieveAccessToken);

        final List<ServeEvent> events = SERVER_1.getAllServeEvents();

        assertThat(events).hasSize(2);
        assertThat(events)
            .allSatisfy(
                event -> assertThat(event.getRequest().getHeaders().all())
                    .noneSatisfy(header -> assertThat(header.key()).isEqualToIgnoringCase("Cookie")));
    }

    @Test
    void retrieveAccessTokenWithSameIdentityOnDifferentUrisShouldNotReturnCachedResponse()
    {
        final OAuth2Service service1 = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);
        final OAuth2Service service2 = new OAuth2Service(SERVER_2.baseUrl(), IDENTITY_1, TECHNICAL_USER_CURRENT_TENANT);

        final String token1 = service1.retrieveAccessToken();
        final String token2 = service2.retrieveAccessToken();

        assertThat(token1).isEqualTo(TOKEN_1);
        assertThat(token2).isEqualTo(TOKEN_2);

        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        SERVER_2.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    @SneakyThrows
    void testRetrieveAccessTokenHandlesNullResponse()
    {
        final XsuaaTokenFlows tokenFlows = mock(XsuaaTokenFlows.class);
        final ClientCredentialsTokenFlow clientCredentialsTokenFlows = mock(ClientCredentialsTokenFlow.class);
        final ClientCredentials identity = new ClientCredentials("clientid", "clientsecret");

        when(tokenFlows.clientCredentialsTokenFlow()).thenReturn(clientCredentialsTokenFlows);
        doReturn(clientCredentialsTokenFlows).when(clientCredentialsTokenFlows).zoneId(anyString());

        // this is the crucial part:
        // the token flow returns null instead of a token BUT does not throw an exception.
        // as per API contract, that seems to be a valid outcome.
        doReturn(null).when(clientCredentialsTokenFlows).execute();

        final OAuth2Service sut = spy(new OAuth2Service("some.uri", identity, TECHNICAL_USER_PROVIDER));
        doReturn(tokenFlows).when(sut).getTokenFlowFactory(isNull());

        assertThatThrownBy(sut::retrieveAccessToken)
            .isExactlyInstanceOf(DestinationOAuthTokenException.class)
            .hasMessageContaining("OAuth2 token request failed");
    }

    @Test
    void testResilienceIsAdded()
    {
        final OAuth2Service service = new OAuth2Service(SERVER_1.baseUrl(), IDENTITY_1, TECHNICAL_USER_PROVIDER);

        final ResilienceConfiguration config = service.getResilienceConfig();
        assertThat(config.identifier()).isEqualTo(URI.create(SERVER_1.baseUrl()).getHost() + "-" + IDENTITY_1.getId());
        assertThat(config.isolationMode()).isEqualTo(ResilienceIsolationMode.TENANT_OPTIONAL);
        assertThat(config.timeLimiterConfiguration().timeoutDuration()).isGreaterThan(Duration.ZERO);
    }

    @Test
    void testTenantPropagationStrategy()
    {
        assertThat(TenantPropagationStrategy.fromServiceIdentifier(ServiceIdentifier.of("identity")))
            .isEqualTo(TenantPropagationStrategy.IAS_SUBDOMAIN);
        assertThat(TenantPropagationStrategy.fromServiceIdentifier(ServiceIdentifier.of("IDENTITY")))
            .isEqualTo(TenantPropagationStrategy.IAS_SUBDOMAIN);
        assertThat(TenantPropagationStrategy.fromServiceIdentifier(ServiceIdentifier.of("service")))
            .isEqualTo(TenantPropagationStrategy.XSUAA_ZID_HEADER);
    }

    @Test
    void testEndpointsBehavior()
    {
        // this first case is covered to keep compatibility with our old behavior where we always assumed that we were using XSUAA
        assertThat(endpointsOf("https://foo.bar").getTokenEndpoint()).hasToString("https://foo.bar/oauth/token");

        // the second case covers our IAS support where the `OAuth2PropertySupplier` already provides the full URL.
        // in this case, we are NOT adding the default path.
        assertThat(endpointsOf("https://foo.bar/baz").getTokenEndpoint()).hasToString("https://foo.bar/baz");
    }

    private static Endpoints endpointsOf( @Nonnull final String baseUri )
    {
        return OAuth2Service.Endpoints.fromBaseUri(URI.create(baseUri));
    }
}
