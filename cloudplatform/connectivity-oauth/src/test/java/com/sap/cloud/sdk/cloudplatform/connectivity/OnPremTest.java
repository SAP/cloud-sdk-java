package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingTestUtility.bindingWithCredentials;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

@SuppressWarnings( "unchecked" )
public class OnPremTest
{

    @Rule
    public WireMockRule csMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setUp()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
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
        stubFor(
            post("/oauth/token")
                .willReturn(
                    okJson(
                        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")));

        final OAuth2ServiceImpl service =
            OAuth2ServiceImpl
                .fromCredentials(csMockServer.baseUrl(), new ClientCredentials("clientid", "clientsecret"));

        final String token1 =
            service.retrieveAccessToken(OnBehalfOf.TECHNICAL_USER_PROVIDER, ResilienceConfiguration.empty("test"));
        final String token2 =
            service.retrieveAccessToken(OnBehalfOf.TECHNICAL_USER_PROVIDER, ResilienceConfiguration.empty("test"));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void multipleOAuth2ServiceImpl()
    {
        stubFor(
            post("/oauth/token")
                .willReturn(
                    okJson(
                        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")));

        final OAuth2ServiceImpl service1 =
            OAuth2ServiceImpl
                .fromCredentials(csMockServer.baseUrl(), new ClientCredentials("clientid", "clientsecret"));
        final OAuth2ServiceImpl service2 =
            OAuth2ServiceImpl
                .fromCredentials(csMockServer.baseUrl(), new ClientCredentials("clientid", "clientsecret"));

        final String token1 =
            service1.retrieveAccessToken(OnBehalfOf.TECHNICAL_USER_PROVIDER, ResilienceConfiguration.empty("test"));
        final String token2 =
            service2.retrieveAccessToken(OnBehalfOf.TECHNICAL_USER_PROVIDER, ResilienceConfiguration.empty("test"));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void singleOAuth2ServiceImplSingleSubscriber()
    {
        stubFor(
            post("/oauth/token")
                .willReturn(
                    okJson(
                        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")));

        final OAuth2ServiceImpl service =
            OAuth2ServiceImpl
                .fromCredentials(csMockServer.baseUrl(), new ClientCredentials("clientid", "clientsecret"));

        final String token1 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("abcd"),
                    () -> service
                        .retrieveAccessToken(
                            OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                            ResilienceConfiguration.empty("test")));
        final String token2 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("abcd"),
                    () -> service
                        .retrieveAccessToken(
                            OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                            ResilienceConfiguration.empty("test")));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void singleOAuth2ServiceImplMultipleSubscriber()
    {
        stubFor(
            post("/oauth/token")
                .willReturn(
                    okJson(
                        "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")));

        final OAuth2ServiceImpl service =
            OAuth2ServiceImpl
                .fromCredentials(csMockServer.baseUrl(), new ClientCredentials("clientid", "clientsecret"));

        final String token1 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("tenant 1"),
                    () -> service
                        .retrieveAccessToken(
                            OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                            ResilienceConfiguration.empty("test")));
        final String token2 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("tenant 2"),
                    () -> service
                        .retrieveAccessToken(
                            OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                            ResilienceConfiguration.empty("test")));

        assertThat(token1).isEqualTo("token");
        assertThat(token2).isEqualTo("token");

        verify(2, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Test
    public void httpClientTenantSeparation()
    {

        final StubMapping stubMapping =
            stubFor(
                post("/oauth/token")
                    .willReturn(
                        okJson(
                            "{\"access_token\": \"token\", \"token_type\": \"Bearer\", \"expires_in\": 50000, \"scope\": \"uaa.resource\", \"jti\": \"abc456\"}")
                            .withHeader("Set-Cookie", "myCookie=123")));

        final ClientCredentials identity = new ClientCredentials("clientid", "clientsecret");

        final OAuth2ServiceImpl service = OAuth2ServiceImpl.fromCredentials(csMockServer.baseUrl(), identity);

        /*final DefaultOAuth2TokenService tokenService =
            new DefaultOAuth2TokenService(HttpClientFactory.create(identity));
        final OAuth2ServiceEndpointsProvider endpoints =
            OAuth2ServiceImpl.Endpoints.fromBaseUri(URI.create(csMockServer.baseUrl()));
        final XsuaaTokenFlows tokenFlow = new XsuaaTokenFlows(tokenService, endpoints, identity);

        final OAuth2ServiceImpl service = new OAuth2ServiceImpl(tokenFlow);*/

        TenantAccessor
            .executeWithTenant(
                new DefaultTenant("tenant1"),
                () -> service
                    .retrieveAccessToken(
                        OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                        ResilienceConfiguration.empty("test")));
        TenantAccessor
            .executeWithTenant(
                new DefaultTenant("tenant2"),
                () -> service
                    .retrieveAccessToken(
                        OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT,
                        ResilienceConfiguration.empty("test")));

        final List<ServeEvent> events = getAllServeEvents();

        assertThat(events).allSatisfy(event -> {
            assertThat(event.getRequest().getHeaders().all())
                .noneSatisfy(header -> assertThat(header.key()).isEqualToIgnoringCase("Cookie"));
        });
        /*assertThat(events.get(0).getRequest().getHeaders().all())
            .doesNotContain(HttpHeader.httpHeader("Cookie", "myCookie=123"));
        assertThat(events.get(1).getRequest().getHeaders().all())
                .doesNotContain(HttpHeader.httpHeader("Cookie", "myCookie=123"));*/
    }

    private static ServiceBindingDestinationOptions createOptionsWithCredentials(
        final Map.Entry<String, Object>... entries )
    {
        final ServiceBinding binding = bindingWithCredentials(ServiceIdentifier.CONNECTIVITY, entries);

        return ServiceBindingDestinationOptions.forService(binding).build();
    }
}
