/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.auth0.jwt.JWT;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.testutil.TestContext;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.test.JwtGenerator;

import lombok.SneakyThrows;

@WireMockTest
class OAuth2DestinationBuilderTest
{
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    @BeforeEach
    void setup()
    {
        stubFor(
            post(urlEqualTo("/oauth/token"))
                .withHeader("Authorization", absent())
                .withRequestBody(notContaining("&assertion="))
                .willReturn(okJson("{\"access_token\":\"TECHNICAL\",\"expires_in\":3600}")));
        stubFor(
            post(urlEqualTo("/oauth/token"))
                .withRequestBody(containing("&assertion="))
                .willReturn(okJson("{\"access_token\":\"PERSONAL\",\"expires_in\":3600}")));
        stubFor(get(anyUrl()).willReturn(ok()));
    }

    @SneakyThrows
    @Test
    void testClientCredentialsTechnicalCurrentTenant( @Nonnull final WireMockRuntimeInfo wm )
    {
        final HttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(wm.getHttpBaseUrl())
                .withTokenEndpoint(wm.getHttpBaseUrl())
                .withClient(new ClientCredentials("clientid", "clientsecret"), OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT)
                .build();

        // direct invocation test assertion
        assertThat(destination.getHeaders()).containsExactly(new Header("Authorization", "Bearer TECHNICAL"));

        // indirect header validation
        final HttpResponse response =
            HttpClientAccessor.getHttpClient((Destination) destination).execute(new HttpGet("/technical"));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // assert tokens are cached
        destination.getHeaders();
        destination.getHeaders();

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
        verify(1, getRequestedFor(urlEqualTo("/technical")).withHeader("Authorization", equalTo("Bearer TECHNICAL")));
    }

    @SneakyThrows
    @Test
    void testClientCredentialsNamedUser( @Nonnull final WireMockRuntimeInfo wm )
    {
        final HttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(wm.getHttpBaseUrl())
                .withTokenEndpoint(wm.getHttpBaseUrl())
                .withClient(new ClientCredentials("clientid", "clientsecret"), OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        final String token = JwtGenerator.getInstance(Service.XSUAA, "client-id").createToken().getTokenValue();
        context.setAuthToken(JWT.decode(token));
        // direct invocation test assertion
        final Collection<Header> headers = destination.getHeaders();
        assertThat(headers).containsExactly(new Header("Authorization", "Bearer PERSONAL"));

        // indirect header validation
        final HttpResponse response = HttpClientAccessor.getHttpClient(destination).execute(new HttpGet("/personal"));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // assert tokens are cached
        destination.getHeaders();
        destination.getHeaders();

        verify(1, postRequestedFor(urlEqualTo("/oauth/token")).withRequestBody(containing(token)));
        verify(1, getRequestedFor(urlEqualTo("/personal")).withHeader("Authorization", equalTo("Bearer PERSONAL")));
    }

    @Test
    void testOtherBuilderMethodsCanBeUsed( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination =
            OAuth2DestinationBuilder
                .forTargetUrl(wm.getHttpBaseUrl())
                .withTokenEndpoint(wm.getHttpBaseUrl())
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
