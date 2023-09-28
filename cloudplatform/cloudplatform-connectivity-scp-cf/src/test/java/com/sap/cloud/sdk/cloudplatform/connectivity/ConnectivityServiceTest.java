package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;

import io.vavr.control.Try;

public class ConnectivityServiceTest
{
    private static final MockUtil mockUtil = new MockUtil();

    private static final String XSUAA_SERVICE_ROOT = "/xsuaa";
    private static final String XSUAA_SERVICE_PATH = XSUAA_SERVICE_ROOT + "/oauth/token";

    private static final ClientCredentials CLIENT_CREDENTIALS =
        new ClientCredentials("connectivity-client-id", "connectivity-client-secret");

    private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    private ConnectivityService connectivityService;

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setupTenant()
    {
        mockUtil.mockCurrentTenant();
        connectivityService =
            new ConnectivityService(new XsuaaService(), ResilienceConfiguration.empty(ConnectivityServiceTest.class));
    }

    @Before
    public void setupCloudPlatform()
    {
        final ScpCfCloudPlatform platform = (ScpCfCloudPlatform) spy(CloudPlatformAccessor.getCloudPlatform());
        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(platform));

        final JsonObject credConnectivity = new JsonObject();
        credConnectivity.addProperty("clientid", CLIENT_CREDENTIALS.getClientId());
        credConnectivity.addProperty("clientsecret", CLIENT_CREDENTIALS.getClientSecret());
        credConnectivity.addProperty("onpremise_proxy_host", "proxy.onpremise.com");
        credConnectivity.addProperty("onpremise_proxy_port", 1234);
        credConnectivity.addProperty("uri", "http://foobar/");
        credConnectivity.addProperty("url", "http://localhost:" + wireMockServer.port() + XSUAA_SERVICE_ROOT);
        doReturn(credConnectivity).when(platform).getServiceCredentials(eq(ConnectivityService.SERVICE_NAME));
    }

    @After
    public void resetCloudPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @AfterClass
    public static void resetFacades()
    {
        TenantAccessor.setTenantFacade(null);
    }

    @Test
    public void testGetOnPremiseProxyConfiguration()
        throws URISyntaxException
    {
        final ProxyConfiguration expectedProxyConfig =
            new ProxyConfiguration(new URI("http://proxy.onpremise.com:1234"));

        assertThat(ConnectivityService.getOnPremiseProxyConfiguration()).isEqualTo(expectedProxyConfig);
    }

    @Test
    public void testConnectivityServiceWithStrategyCompatibility()
    {
        // test parameters
        final boolean useProviderTenant = true;
        final String currentUserToken = "CURRENT USER TOKEN";

        // mock XSUAA service response
        final String proxyAccessToken = "PROXY ACCESS TOKEN";
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", proxyAccessToken)
                            .put("expires_in", "100000")
                            .build())));

        // mock AuthTokenFacade for current user token
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(mockUserAuthToken(currentUserToken)));

        // actual request
        final List<Header> onPremiseProxyHeaders =
            connectivityService
                .getHeadersForOnPremiseSystem(useProviderTenant, PrincipalPropagationStrategy.COMPATIBILITY);

        assertThat(onPremiseProxyHeaders)
            .containsOnly(
                new Header("Proxy-Authorization", "Bearer " + proxyAccessToken),
                new Header("SAP-Connectivity-Authentication", "Bearer " + currentUserToken));

        // verify the tokens are being cached
        assertThat(
            connectivityService
                .getHeadersForOnPremiseSystem(useProviderTenant, PrincipalPropagationStrategy.COMPATIBILITY))
            .isEqualTo(onPremiseProxyHeaders);
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWitStrategyDisabledPrincipalPropagation()
    {
        // test parameters
        final boolean useProviderTenant = true;

        // mock XSUAA service response
        final String proxyAccessToken = "PROXY ACCESS TOKEN";
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", proxyAccessToken)
                            .put("expires_in", "1")
                            .build())));

        // actual request
        final List<Header> onPremiseProxyHeaders =
            connectivityService.getHeadersForOnPremiseSystem(useProviderTenant, PrincipalPropagationStrategy.DISABLED);

        assertThat(onPremiseProxyHeaders).containsOnly(new Header("Proxy-Authorization", "Bearer " + proxyAccessToken));
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWithStrategyRecommended()
    {
        // test parameters
        final boolean useProviderTenant = true;
        final String currentUserToken =
            JwtGenerator.getInstance(Service.XSUAA, "client-id").createToken().getTokenValue();

        // mock XSUAA service response
        final String proxyAccessToken = "PROXY-ACCESS-TOKEN";
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_JWT_BEARER.replaceAll(":", "%3A")))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withRequestBody(containing("assertion=" + currentUserToken))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", proxyAccessToken)
                            .put("expires_in", "1")
                            .build())));

        // mock AuthTokenFacade for current user token
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(mockUserAuthToken(currentUserToken)));

        // actual request
        final List<Header> onPremiseProxyHeaders =
            connectivityService
                .getHeadersForOnPremiseSystem(useProviderTenant, PrincipalPropagationStrategy.RECOMMENDATION);

        assertThat(onPremiseProxyHeaders).containsOnly(new Header("Proxy-Authorization", "Bearer " + proxyAccessToken));
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWithInvalidXsuaaCredentials()
    {
        // test parameters
        final boolean useProviderTenant = true;

        // mock XSUAA service response
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .willReturn(forbidden()));

        // actual request
        assertThatCode(
            () -> new ConnectivityService()
                .getHeadersForOnPremiseSystem(useProviderTenant, PrincipalPropagationStrategy.DISABLED))
            .isInstanceOf(DestinationAccessException.class)
            .hasRootCauseInstanceOf(OAuth2ServiceException.class);
    }

    public static AuthToken mockUserAuthToken( final String jwt )
    {
        final DecodedJWT decodedJwt = mock(DecodedJWT.class);
        doReturn(jwt).when(decodedJwt).getToken();

        final Claim claimGrantType = mock(Claim.class);
        doReturn("user_token").when(claimGrantType).asString();
        doReturn(claimGrantType).when(decodedJwt).getClaim("grant_type");

        final Claim claimUserName = mock(Claim.class);
        doReturn("admin").when(claimUserName).asString();
        doReturn(claimUserName).when(decodedJwt).getClaim("user_name");

        final Claim claimScope = mock(Claim.class);
        doReturn(Collections.singletonList("uaa.user")).when(claimScope).asList(String.class);
        doReturn(claimScope).when(decodedJwt).getClaim("scope");

        return new AuthToken(decodedJwt);
    }
}
