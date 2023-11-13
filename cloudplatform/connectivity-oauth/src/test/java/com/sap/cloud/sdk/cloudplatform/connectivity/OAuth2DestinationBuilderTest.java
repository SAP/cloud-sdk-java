/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.notContaining;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;

import io.vavr.control.Try;
import lombok.SneakyThrows;

public class OAuth2DestinationBuilderTest
{
    @Rule
    public WireMockRule mockServer = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void setup()
    {
        mockServer
            .stubFor(
                post(urlEqualTo("/oauth/token"))
                    .withHeader("Authorization", absent())
                    .withRequestBody(notContaining("&assertion="))
                    .willReturn(okJson("{\"access_token\":\"TECHNICAL\",\"expires_in\":42}")));
        mockServer
            .stubFor(
                post(urlEqualTo("/oauth/token"))
                    .withRequestBody(containing("&assertion="))
                    .willReturn(okJson("{\"access_token\":\"PERSONAL\",\"expires_in\":42}")));
        mockServer.stubFor(get(anyUrl()).willReturn(ok()));
    }

    @SneakyThrows
    @Test
    public void testClientCredentialsTechnicalCurrentTenant()
    {
        final HttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(mockServer.baseUrl())
                .withTokenEndpoint(mockServer.baseUrl())
                .withClient(new ClientCredentials("clientid", "clientsecret"), OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT)
                .build();

        // direct invocation test assertion
        assertThat(destination.getHeaders()).containsExactly(new Header("Authorization", "Bearer TECHNICAL"));

        // indirect header validation
        final HttpResponse response =
            HttpClientAccessor.getHttpClient((Destination) destination).execute(new HttpGet("/"));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // assert tokens are cached
        destination.getHeaders();
        destination.getHeaders();

        mockServer.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        mockServer.verify(1, getRequestedFor(urlEqualTo("/")).withHeader("Authorization", equalTo("Bearer TECHNICAL")));
    }

    @SneakyThrows
    @Test
    public void testClientCredentialsNamedUser()
    {
        final HttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(mockServer.baseUrl())
                .withTokenEndpoint(mockServer.baseUrl())
                .withClient(new ClientCredentials("clientid", "clientsecret"), OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        final String token = JwtGenerator.getInstance(Service.XSUAA, "client-id").createToken().getTokenValue();
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(new AuthToken(JWT.decode(token))));
        // direct invocation test assertion
        final Collection<Header> headers = destination.getHeaders();
        assertThat(headers).containsExactly(new Header("Authorization", "Bearer PERSONAL"));

        // indirect header validation
        final HttpResponse response =
            HttpClientAccessor.getHttpClient((Destination) destination).execute(new HttpGet("/"));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // assert tokens are cached
        destination.getHeaders();
        destination.getHeaders();

        mockServer.verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing(token)));
        mockServer.verify(1, getRequestedFor(urlEqualTo("/")).withHeader("Authorization", equalTo("Bearer PERSONAL")));
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    public void testOtherBuilderMethodsCanBeUsed()
    {
        final DefaultHttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(mockServer.baseUrl())
                .withTokenEndpoint(mockServer.baseUrl())
                .withClient(new ClientCredentials("clientid", "clientsecret"), OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .name("my-destination")
                .header("my-header", "my-value")
                .property("foo", "bar")
                .build();
        assertThat(destination.get(DestinationProperty.NAME)).contains("my-destination");
        assertThat(destination.get("foo")).contains("bar");
        assertThat(destination.customHeaders).containsExactly(new Header("my-header", "my-value"));

    }
}
