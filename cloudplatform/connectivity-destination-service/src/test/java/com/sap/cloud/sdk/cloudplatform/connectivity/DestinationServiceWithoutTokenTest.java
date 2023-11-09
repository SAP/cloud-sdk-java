/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceOptionsAugmenter.augmenter;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;

import io.vavr.control.Try;

public class DestinationServiceWithoutTokenTest
{
    private static final String PAYLOAD_AUTH_TOKEN_ERROR =
        "{\"destinationConfiguration\":{\"Name\":\"Foo\",\"Type\":\"HTTP\",\"URL\":\"https://example.com\",\"Authentication\":\"OAuth2UserTokenExchange\",\"ProxyType\":\"Internet\"},\"authTokens\":[{\"error\":\"some-error-message\"}]}";

    @Rule
    public final WireMockRule server = new WireMockRule(wireMockConfig().dynamicPort());

    private ServiceBinding serviceBinding;

    @Before
    public void setup()
    {
        // no principal
        PrincipalAccessor.setPrincipalFacade(() -> Try.failure(new IllegalStateException("No principal.")));
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(new IllegalStateException("No auth token.")));

        // prepare oauth request
        final Map<String, String> oauth =
            ImmutableMap.of("access_token", "DESTINATION_SERVICE_TOKEN", "expires_in", "1");
        stubFor(post(urlEqualTo("/token/oauth/token")).willReturn(okForJson(oauth)));

        serviceBinding =
            DestinationServiceAdapterTest
                .serviceBinding(
                    "clientId",
                    "clientSecret",
                    server.baseUrl() + "/service/",
                    server.baseUrl() + "/token/",
                    "tenantId");
    }

    @After
    public void tearDown()
    {
        PrincipalAccessor.setPrincipalFacade(null);
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    public void testForwardUserTokenWithoutPrincipalFails()
    {
        // prepare http response
        stubFor(get(anyUrl()).willReturn(okJson(PAYLOAD_AUTH_TOKEN_ERROR)));

        // prepare test
        final DestinationServiceAdapter adapter = new DestinationServiceAdapter(null, () -> serviceBinding, null);

        final DestinationServiceOptionsAugmenter augment = augmenter().tokenExchangeStrategy(FORWARD_USER_TOKEN);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(augment).build();

        // run test
        final Try<Destination> maybeDestination = new DestinationService(adapter).tryGetDestination("Foo", options);
        assertThat(maybeDestination).isEmpty();
        assertThat(maybeDestination.getCause())
            .isInstanceOf(DestinationAccessException.class)
            .rootCause()
            .hasMessageContaining("some-error-message");

        verify(1, postRequestedFor(anyUrl()));
        verify(1, getRequestedFor(anyUrl()));
    }

    @Test
    public void testLookupThenExchangeWithoutPrincipalFails()
    {
        // prepare http response
        stubFor(get(anyUrl()).willReturn(okJson(PAYLOAD_AUTH_TOKEN_ERROR)));

        // prepare test
        final DestinationServiceAdapter adapter = new DestinationServiceAdapter(null, () -> serviceBinding, null);

        final DestinationServiceOptionsAugmenter augment = augmenter().tokenExchangeStrategy(LOOKUP_THEN_EXCHANGE);
        final DestinationOptions options = DestinationOptions.builder().augmentBuilder(augment).build();

        // run test
        final Try<Destination> maybeDestination = new DestinationService(adapter).tryGetDestination("Foo", options);
        assertThat(maybeDestination).isEmpty();
        assertThat(maybeDestination.getCause())
            .isInstanceOf(DestinationAccessException.class)
            .hasRootCauseMessage("No auth token.");

        verify(1, postRequestedFor(anyUrl()));
        verify(1, getRequestedFor(anyUrl()));
    }
}
