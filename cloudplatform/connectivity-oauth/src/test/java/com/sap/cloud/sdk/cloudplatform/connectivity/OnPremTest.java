package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.ClientIdentity;

public class OnPremTest
{
    private static final String MOCKED_RESPONSE_BODY =
        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}";
    private static final String ALTERNATIVE_MOCKED_RESPONSE_BODY =
        "{\"access_token\": \"token2\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}";
    private static final ClientIdentity SOME_IDENTITY = new ClientCredentials("clientid", "clientsecret");
    private static final ResilienceConfiguration NO_RESILIENCE =
        ResilienceConfiguration.empty(OnPremTest.class.getName() + "_empty");

    @Rule
    public WireMockRule csMockServer = new WireMockRule(wireMockConfig().dynamicPort());
    @Rule
    public WireMockRule csMockServer2 = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setUp()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        csMockServer.stubFor(post("/oauth/token").willReturn(okJson(MOCKED_RESPONSE_BODY)));
        csMockServer2.stubFor(post("/oauth/token").willReturn(okJson(ALTERNATIVE_MOCKED_RESPONSE_BODY)));
    }

    @After
    public void tearDown()
    {
        TenantAccessor.setTenantFacade(null);
        OAuth2ServiceImpl.clearCache();
    }

    @Test
    public void singleOAuth2ServiceImpl()
    {
        final OAuth2ServiceImpl service =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);

        final String token1 = service.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void multipleOAuth2ServiceImpl()
    {
        final OAuth2ServiceImpl service1 =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);
        final OAuth2ServiceImpl service2 =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_PROVIDER);

        final String token1 = service1.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service2.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void singleOAuth2ServiceImplSingleSubscriber()
    {
        final OAuth2ServiceImpl service =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("abcd"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        final String token2 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("abcd"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void singleOAuth2ServiceImplMultipleSubscriber()
    {
        final OAuth2ServiceImpl service =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("tenant 1"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        final String token2 =
            TenantAccessor
                .executeWithTenant(new DefaultTenant("tenant 2"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void httpClientTenantSeparation()
    {
        // The reason for this test is to verify, that cookies set for one tenant are not forwarded to another tenant.
        stubFor(post("/oauth/token").willReturn(okJson(MOCKED_RESPONSE_BODY).withHeader("Set-Cookie", "myCookie=123")));

        final OAuth2ServiceImpl service =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        TenantAccessor
            .executeWithTenant(new DefaultTenant("tenant1"), () -> service.retrieveAccessToken(NO_RESILIENCE));
        TenantAccessor
            .executeWithTenant(new DefaultTenant("tenant2"), () -> service.retrieveAccessToken(NO_RESILIENCE));

        final List<ServeEvent> events = getAllServeEvents();

        assertThat(events).hasSize(2);
        assertThat(events)
            .allSatisfy(
                event -> assertThat(event.getRequest().getHeaders().all())
                    .noneSatisfy(header -> assertThat(header.key()).isEqualToIgnoringCase("Cookie")));
    }

    @Test
    public void retrieveAccessTokenWithSameIdentityOnDifferentUrisShouldNotReturnCachedResponse()
    {
        final OAuth2ServiceImpl service1 =
            new OAuth2ServiceImpl(csMockServer.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
        final OAuth2ServiceImpl service2 =
            new OAuth2ServiceImpl(csMockServer2.baseUrl(), SOME_IDENTITY, OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);

        final String token1 = service1.retrieveAccessToken(NO_RESILIENCE);
        final String token2 = service2.retrieveAccessToken(NO_RESILIENCE);

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token2");

        csMockServer.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        csMockServer2.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }
}
