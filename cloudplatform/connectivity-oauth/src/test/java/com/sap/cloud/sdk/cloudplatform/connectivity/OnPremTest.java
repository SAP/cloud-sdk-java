package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;

class OnPremTest
{
    private static final String MOCKED_RESPONSE_BODY =
        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}";
    private static final String ALTERNATIVE_MOCKED_RESPONSE_BODY =
        "{\"access_token\": \"token2\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}";
    private static final ClientIdentity SOME_IDENTITY = new ClientCredentials("clientid", "clientsecret");
    private static final ResilienceConfiguration NO_RESILIENCE =
        ResilienceConfiguration.empty(OnPremTest.class.getName() + "_empty");

    @RegisterExtension
    static final WireMockExtension MOCK_SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();
    @RegisterExtension
    static final WireMockExtension SECOND_MOCK_SERVER =
        WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    @BeforeEach
    void setUp()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        MOCK_SERVER.stubFor(post("/oauth/token").willReturn(okJson(MOCKED_RESPONSE_BODY)));
        SECOND_MOCK_SERVER.stubFor(post("/oauth/token").willReturn(okJson(ALTERNATIVE_MOCKED_RESPONSE_BODY)));
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
        final OAuth2Service service =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);

        final String token1 = service.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        MOCK_SERVER.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void multipleOAuth2ServiceImpl()
    {
        final OAuth2Service service1 =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);
        final OAuth2Service service2 =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);

        final String token1 = service1.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service2.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        MOCK_SERVER.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void singleOAuth2ServiceImplSingleSubscriber()
    {
        final OAuth2Service service =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("abcd"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        final String token2 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("abcd"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        MOCK_SERVER.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void singleOAuth2ServiceImplMultipleSubscriber()
    {
        final OAuth2Service service =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("tenant 1"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        final String token2 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("tenant 2"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        MOCK_SERVER.verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    void httpClientTenantSeparation()
    {
        // The reason for this test is to verify, that cookies set for one tenant are not forwarded to another tenant.
        MOCK_SERVER
            .stubFor(
                post("/oauth/token").willReturn(okJson(MOCKED_RESPONSE_BODY).withHeader("Set-Cookie", "myCookie=123")));

        final OAuth2Service service =
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        TenantAccessor
            .executeWithTenant(new DefaultTenant("tenant1"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        TenantAccessor
            .executeWithTenant(new DefaultTenant("tenant2"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        final List<ServeEvent> events = MOCK_SERVER.getAllServeEvents();

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
            new OAuth2Service(MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        final OAuth2Service service2 =
            new OAuth2Service(SECOND_MOCK_SERVER.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 = service1.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service2.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token2");

        MOCK_SERVER.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        SECOND_MOCK_SERVER.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }
}
