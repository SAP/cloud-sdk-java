package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.auth0.jwt.JWT;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.OAuth2Service.TenantPropagationStrategy;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.testutil.TestContext;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenService;

import lombok.SneakyThrows;

@Isolated( "Tests and resets the global OAuth2Service cache" )
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
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext().resetCaches();

    @BeforeEach
    void setUp()
    {
        SERVER_1.stubFor(post("/oauth/token").willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_1))));
        SERVER_2.stubFor(post("/oauth/token").willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_2))));
    }

    @Test
    void singleOAuth2ServiceImpl()
    {
        final OAuth2Service service =
            OAuth2Service
                .builder()
                .withTokenUri(SERVER_1.baseUrl())
                .withIdentity(IDENTITY_1)
                .withOnBehalfOf(TECHNICAL_USER_PROVIDER)
                .build();

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
        sut = OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();

        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);

        sut = OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();

        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));

        // A different identity should lead to a cache miss
        sut = OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_2).build();
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_1);
        SERVER_1.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));

        // A different server should also lead to a cache miss
        sut = OAuth2Service.builder().withTokenUri(SERVER_2.baseUrl()).withIdentity(IDENTITY_2).build();
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_2);
        assertThat(sut.retrieveAccessToken()).isEqualTo(TOKEN_2);
        SERVER_1.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
        SERVER_2.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void testZidHeaderTenantStrategy()
    {
        final OAuth2Service service =
            OAuth2Service
                .builder()
                .withTokenUri(SERVER_1.baseUrl())
                .withIdentity(IDENTITY_1)
                .withTenantPropagationStrategy(TenantPropagationStrategy.ZID_HEADER)
                .build();

        TenantAccessor.executeWithTenant(new DefaultTenant("abcd"), service::retrieveAccessToken);
        TenantAccessor.executeWithTenant(new DefaultTenant("abcd"), service::retrieveAccessToken);

        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withHeader("x-zid", equalTo("abcd")));
    }

    @Test
    void testSubdomainTenantStrategy()
    {
        final OAuth2Service.Builder serviceBuilder =
            OAuth2Service
                .builder()
                .withTokenUri(SERVER_1.baseUrl())
                .withIdentity(IDENTITY_1)
                .withAdditionalParameter("app_tid", "provider")
                .withTenantPropagationStrategy(TenantPropagationStrategy.TENANT_SUBDOMAIN);
        {
            // behalf: current tenant
            OAuth2Service service = serviceBuilder.build();
            // we have to use localhost here as subdomains because this test doesn't mock different subdomains
            // instead, that aspect is tested in OAuth2IntegrationTest
            service.retrieveAccessToken();
            TenantAccessor.executeWithTenant(new DefaultTenant("t1", "localhost"), service::retrieveAccessToken);
            TenantAccessor.executeWithTenant(new DefaultTenant("t2", "localhost"), service::retrieveAccessToken);

            SERVER_1
                .verify(
                    1,
                    postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing("app_tid=provider")));
            SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing("app_tid=t1")));
            SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing("app_tid=t2")));
        }
        {
            // behalf provider
            final OAuth2Service service = serviceBuilder.withOnBehalfOf(TECHNICAL_USER_PROVIDER).build();

            service.retrieveAccessToken();
            TenantAccessor.executeWithTenant(new DefaultTenant("t2", "localhost"), service::retrieveAccessToken);

            SERVER_1
                .verify(
                    1,
                    postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing("app_tid=provider")));
            SERVER_1
                .verify(
                    1,
                    postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing("app_tid=provider")));
        }
        {
            // behalf named user
            final OAuth2Service service = serviceBuilder.withOnBehalfOf(NAMED_USER_CURRENT_TENANT).build();

            assertThatThrownBy(service::retrieveAccessToken);

            context.setTenant(new DefaultTenant("tenant", "localhost"));
            context.setPrincipal();
            final String token =
                JwtGenerator
                    .getInstance(Service.IAS, "clientid")
                    .withClaimValue("app_tid", "tenant")
                    .createToken()
                    .getTokenValue();
            context.setAuthToken(new AuthToken(JWT.decode(token)));

            service.retrieveAccessToken();

            SERVER_1
                .verify(
                    1,
                    postRequestedFor(urlEqualTo("/oauth/token"))
                        .withRequestBody(containing("app_tid=tenant"))
                        .withRequestBody(
                            containing("grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer".replace(":", "%3A")))
                        .withRequestBody(containing("assertion=" + token)));
        }
    }

    @Test
    void singleOAuth2ServiceImplMultipleSubscriber()
    {
        final OAuth2Service service =
            OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();

        final String token1 =
            TenantAccessor.executeWithTenant(new DefaultTenant("tenant 1"), service::retrieveAccessToken);
        final String token2 =
            TenantAccessor.executeWithTenant(new DefaultTenant("tenant 2"), service::retrieveAccessToken);

        assertThat(token1).isEqualTo(TOKEN_1);
        assertThat(token2).isEqualTo(TOKEN_1);

        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withHeader("x-zid", equalTo("tenant 1")));
        SERVER_1.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withHeader("x-zid", equalTo("tenant 2")));
    }

    @Test
    void httpClientTenantSeparation()
    {
        // The reason for this test is to verify, that cookies set for one tenant are not forwarded to another tenant.
        SERVER_1
            .stubFor(
                post("/oauth/token")
                    .willReturn(okJson(RESPONSE_TEMPLATE.formatted(TOKEN_1)).withHeader("Set-Cookie", "myCookie=123")));

        final OAuth2Service service =
            OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();

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
        final OAuth2Service service1 =
            OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();
        final OAuth2Service service2 =
            OAuth2Service.builder().withTokenUri(SERVER_2.baseUrl()).withIdentity(IDENTITY_1).build();

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
        final OAuth2TokenService tokenService = mock(OAuth2TokenService.class);
        final ClientCredentials identity = new ClientCredentials("clientid", "clientsecret");

        // this is the crucial part:
        // the token flow returns null instead of a token BUT does not throw an exception.
        // as per API contract, that seems to be a valid outcome.
        doReturn(null)
            .when(tokenService)
            .retrieveAccessTokenViaClientCredentialsGrant(any(), any(), anyString(), anyString(), any(), anyBoolean());

        final OAuth2Service sut =
            spy(
                OAuth2Service
                    .builder()
                    .withTokenUri("some.uri")
                    .withIdentity(identity)
                    .withOnBehalfOf(TECHNICAL_USER_PROVIDER)
                    .build());
        doReturn(tokenService).when(sut).getTokenService(isNull());

        assertThatThrownBy(sut::retrieveAccessToken)
            .isExactlyInstanceOf(DestinationOAuthTokenException.class)
            .hasMessageContaining("OAuth2 token request failed");
    }

    @Test
    void testResilienceIsAdded()
    {
        final OAuth2Service service =
            OAuth2Service
                .builder()
                .withTokenUri(SERVER_1.baseUrl())
                .withIdentity(IDENTITY_1)
                .withOnBehalfOf(TECHNICAL_USER_PROVIDER)
                .build();

        final ResilienceConfiguration config = service.getResilienceConfiguration();
        assertThat(config.identifier()).isEqualTo(URI.create(SERVER_1.baseUrl()).getHost() + "-" + IDENTITY_1.getId());
        assertThat(config.isolationMode()).isEqualTo(ResilienceIsolationMode.TENANT_OPTIONAL);
        assertThat(config.timeLimiterConfiguration().timeoutDuration()).isGreaterThan(Duration.ZERO);
    }

    @Test
    void testAdditionalParametersAreSent()
    {
        final OAuth2Service service =
            OAuth2Service
                .builder()
                .withTokenUri(SERVER_1.baseUrl())
                .withIdentity(IDENTITY_1)
                .withAdditionalParameter("arg1", "val1")
                .withAdditionalParameters(Map.of("arg2", "val2"))
                .build();

        final String token = service.retrieveAccessToken();
        assertThat(token).isEqualTo(TOKEN_1);

        final List<ServeEvent> allEvents = SERVER_1.getAllServeEvents();
        assertThat(allEvents).hasSize(1);

        final ServeEvent event = allEvents.get(0);
        assertThat(event.getRequest().getMethod()).isEqualTo(RequestMethod.POST);

        final String requestBody = event.getRequest().getBodyAsString();
        assertThat(requestBody).contains("arg1=val1");
        assertThat(requestBody).contains("arg2=val2");
    }

    @Test
    void testCacheIsRegistered()
    {
        // create an instance to ensure the cache is initialised before we assert on the CacheManager
        OAuth2Service.builder().withTokenUri(SERVER_1.baseUrl()).withIdentity(IDENTITY_1).build();

        assertThat(CacheManager.getCacheList()).contains(OAuth2Service.tokenServiceCache);
    }
}
