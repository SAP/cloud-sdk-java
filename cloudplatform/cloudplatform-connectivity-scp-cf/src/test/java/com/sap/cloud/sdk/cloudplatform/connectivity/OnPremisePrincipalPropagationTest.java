/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ConnectivityServiceTest.mockUserAuthToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.net.URI;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformAccessor;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;

import io.vavr.control.Try;

public class OnPremisePrincipalPropagationTest
{
    private static final String MOCKED_DESTINATION_NAME = "MyDestination";
    private static final URI VALID_URI = URI.create("http://foo.bar");

    private static final String XSUAA_SERVICE_ROOT = "/xsuaa";
    private static final String XSUAA_SERVICE_PATH = XSUAA_SERVICE_ROOT + "/oauth/token";

    private static final ClientCredentials CLIENT_CREDENTIALS =
        new ClientCredentials("connectivity-client-id", "connectivity-client-secret");

    private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    final String userToken = JwtGenerator.getInstance(Service.XSUAA, "client-id").createToken().getTokenValue();

    @Rule
    public final WireMockRule wireMockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setupCloudPlatform()
    {
        final ScpCfCloudPlatform platform = (ScpCfCloudPlatform) spy(CloudPlatformAccessor.getCloudPlatform());
        CloudPlatformAccessor.setCloudPlatformFacade(() -> Try.success(platform));

        final JsonObject credConnectivity = new JsonObject();
        credConnectivity.addProperty("clientid", CLIENT_CREDENTIALS.getClientId());
        credConnectivity.addProperty("clientsecret", CLIENT_CREDENTIALS.getClientSecret());
        credConnectivity.addProperty("url", "http://localhost:" + wireMockServer.port() + XSUAA_SERVICE_ROOT);
        credConnectivity.addProperty("onpremise_proxy_host", "localhost");
        credConnectivity.addProperty("onpremise_proxy_port", "" + wireMockServer.port());
        doReturn(credConnectivity).when(platform).getServiceCredentials(eq(ConnectivityService.SERVICE_NAME));
    }

    @After
    public void resetCloudPlatform()
    {
        CloudPlatformAccessor.setCloudPlatformFacade(null);
    }

    @Before
    public void setupXsuaaForConnectivityServiceClientCredentials()
    {
        // mock XSUAA service response
        final String proxyAccessToken = "PROXY-ACCESS-TOKEN-BY-CLIENT-CREDENTIALS";
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_CLIENT_CREDENTIALS))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", proxyAccessToken)
                            .put("refresh_token", "ignored")
                            .put("expires_in", "1")
                            .build())));
    }

    @Before
    public void setupXsuaaForUserTokenExchange()
    {
        // mock XSUAA service response
        final String proxyAccessToken = "PROXY-ACCESS-TOKEN-BY-REFRESH-TOKEN";
        stubFor(
            post(urlEqualTo(XSUAA_SERVICE_PATH))
                .withRequestBody(containing("grant_type=" + GRANT_TYPE_JWT_BEARER.replaceAll(":", "%3A")))
                .withRequestBody(containing("client_secret=" + CLIENT_CREDENTIALS.getClientSecret()))
                .withRequestBody(containing("client_id=" + CLIENT_CREDENTIALS.getClientId()))
                .withRequestBody(containing("assertion=" + userToken))
                .willReturn(
                    okForJson(
                        ImmutableMap
                            .<String, String> builder()
                            .put("access_token", proxyAccessToken)
                            .put("expires_in", "1")
                            .build())));
    }

    @Test
    public void testOnPremisePrincipalPropagation()
    {
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .proxyType(ProxyType.ON_PREMISE)
                .cloudConnectorLocationId("LOC_ID");

        final Collection<Header> headers = AuthTokenAccessor.executeWithAuthToken(mockUserAuthToken(userToken), () -> {
            final DefaultHttpDestination destination = builder.build();
            return destination.getHeaders(VALID_URI);
        });

        assertThat(headers)
            .containsExactlyInAnyOrder(
                new Header("Proxy-Authorization", "Bearer PROXY-ACCESS-TOKEN-BY-REFRESH-TOKEN"),
                new Header("SAP-Connectivity-SCC-Location_ID", "LOC_ID"));
    }
}
