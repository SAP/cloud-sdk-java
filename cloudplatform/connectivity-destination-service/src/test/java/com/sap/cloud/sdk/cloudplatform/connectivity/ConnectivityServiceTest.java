/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceException;

import io.vavr.control.Try;

public class ConnectivityServiceTest
{
    private static final String XSUAA_SERVICE_ROOT = "/xsuaa";
    private static final String XSUAA_SERVICE_PATH = XSUAA_SERVICE_ROOT + "/oauth/token";

    private static final ClientCredentials CLIENT_CREDENTIALS =
        new ClientCredentials("connectivity-client-id", "connectivity-client-secret");

    private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setupServiceBinding()
    {
        final ServiceBinding connectivityService =
            new DefaultServiceBindingBuilder()
                .withServiceIdentifier(ServiceIdentifier.CONNECTIVITY)
                .withCredentials(
                    ImmutableMap
                        .<String, Object> builder()
                        .put("clientid", CLIENT_CREDENTIALS.getClientId())
                        .put("clientsecret", CLIENT_CREDENTIALS.getClientSecret())
                        .put("url", "http://localhost:" + wireMockServer.port() + XSUAA_SERVICE_ROOT)
                        .put("uri", "http://foobar/")
                        .put("onpremise_proxy_host", "localhost")
                        .put("onpremise_proxy_port", "1234")
                        .build())
                .build();

        DefaultHttpDestinationBuilderProxyHandler.setServiceBindingConnectivity(connectivityService);
    }

    @After
    public void resetServiceBinding()
    {
        DefaultHttpDestinationBuilderProxyHandler.setServiceBindingConnectivity(null);
    }

    @AfterClass
    public static void resetFacades()
    {
        TenantAccessor.setTenantFacade(null);
    }

    @Test
    public void testConnectivityServiceWithStrategyCompatibility()
    {
        // test parameters
        final String providerTenantId = "";
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
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder("http://buzz/")
                .proxyType(ProxyType.ON_PREMISE)
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .property(DestinationProperty.PRINCIPAL_PROPAGATION_MODE, PrincipalPropagationMode.COMPATIBILITY)
                .property(DestinationProperty.TENANT_ID, providerTenantId);

        final DefaultHttpDestination dest = new DefaultHttpDestinationBuilderProxyHandler().handle(builder);

        assertThat(dest).isNotNull();
        assertThat(dest.getHeaders())
            .containsOnly(
                new Header("Proxy-Authorization", "Bearer " + proxyAccessToken),
                new Header("SAP-Connectivity-Authentication", "Bearer " + currentUserToken));

        // verify the tokens are being cached
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWitStrategyDisabledPrincipalPropagation()
    {
        // test parameters
        final String providerTenantId = "";

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
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder("http://buzz/")
                .proxyType(ProxyType.ON_PREMISE)
                .property(DestinationProperty.TENANT_ID, providerTenantId);

        final DefaultHttpDestination dest = new DefaultHttpDestinationBuilderProxyHandler().handle(builder);

        assertThat(dest).isNotNull();
        assertThat(dest.getHeaders()).containsOnly(new Header("Proxy-Authorization", "Bearer " + proxyAccessToken));
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWithStrategyRecommended()
    {
        // test parameters
        final String providerTenantId = "";
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
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder("http://buzz/")
                .proxyType(ProxyType.ON_PREMISE)
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .property(DestinationProperty.PRINCIPAL_PROPAGATION_MODE, PrincipalPropagationMode.RECOMMENDED)
                .property(DestinationProperty.TENANT_ID, providerTenantId);

        final DefaultHttpDestination dest = new DefaultHttpDestinationBuilderProxyHandler().handle(builder);

        assertThat(dest).isNotNull();
        assertThat(dest.getHeaders()).containsOnly(new Header("Proxy-Authorization", "Bearer " + proxyAccessToken));
        verify(1, postRequestedFor(urlEqualTo(XSUAA_SERVICE_PATH)));
    }

    @Test
    public void testConnectivityServiceWithInvalidXsuaaCredentials()
    {
        // test parameters
        final String providerTenantId = "";

        // mock XSUAA service response
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .willReturn(forbidden()));

        // actual request
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder("http://buzz/")
                .proxyType(ProxyType.ON_PREMISE)
                .property(DestinationProperty.TENANT_ID, providerTenantId);

        final DefaultHttpDestination dest = new DefaultHttpDestinationBuilderProxyHandler().handle(builder);
        assertThat(dest).isNotNull();

        assertThatCode(dest::getHeaders)
            .hasMessageEndingWith("Failed to resolve access token.")
            .isInstanceOf(ResilienceRuntimeException.class)
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
