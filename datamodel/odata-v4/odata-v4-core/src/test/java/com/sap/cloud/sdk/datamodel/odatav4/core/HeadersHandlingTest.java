/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

@WireMockTest
class HeadersHandlingTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String SERVICE_PATH = "/remoteService";
    private static final String ENTITY_URL = SERVICE_PATH + "/People('usr')";

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";
    private static final String SET_COOKIE_HEADER_KEY = "Set-Cookie";

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    @Test
    void testUpdateRequestContainsHeadersWithNonUniqueKeys()
    {
        stubFor(head(anyUrl()).willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));

        stubFor(
            patch(urlEqualTo(ENTITY_URL))
                .willReturn(
                    ok()
                        .withHeader(SET_COOKIE_HEADER_KEY, "SimpleKey=SimpleValue")
                        .withHeader(SET_COOKIE_HEADER_KEY, "KeyWithoutAValue")
                        .withHeader(SET_COOKIE_HEADER_KEY, "KeyWithCommaValue=Value, which contains a comma")
                        .withHeader(
                            SET_COOKIE_HEADER_KEY,
                            "MultiValueKey1=Value 1, with comma; MultiValueKey2=Value 2 ; MultiValueKey3;")));

        final Person person = new Person();
        person.setUserName("usr");

        final UpdateRequestBuilder<Person> builder = new UpdateRequestBuilder<>(SERVICE_PATH, person, "People");

        final ModificationResponse<Person> response = builder.execute(destination);

        final List<String> responseHeaders =
            ImmutableList.copyOf(response.getResponseHeaders().get(SET_COOKIE_HEADER_KEY));

        assertThat(responseHeaders)
            .containsExactly(
                "SimpleKey=SimpleValue",
                "KeyWithoutAValue",
                "KeyWithCommaValue=Value, which contains a comma",
                "MultiValueKey1=Value 1, with comma; MultiValueKey2=Value 2 ; MultiValueKey3;");
    }

    @Test
    void testMultipleHeadersWithSameKey()
    {
        final Person person = new Person();
        person.setUserName("usr");

        // patch request with 2 cookie headers with the same key
        final UpdateRequestBuilder<Person> builder =
            new UpdateRequestBuilder<>(SERVICE_PATH, person, "People")
                .withHeader(SET_COOKIE_HEADER_KEY, "foo")
                .withHeader(SET_COOKIE_HEADER_KEY, "bar");

        executeMultipleHeadersWithSameKey(builder);
    }

    @Test
    void testMultipleHeadersWithSameKeyAtOnce()
    {
        final Person person = new Person();
        person.setUserName("usr");

        // patch request with 2 cookie headers with the same key
        final UpdateRequestBuilder<Person> builder =
            new UpdateRequestBuilder<>(SERVICE_PATH, person, "People")
                .withHeaders(ImmutableMap.of(SET_COOKIE_HEADER_KEY, "foo"))
                .withHeaders(ImmutableMap.of(SET_COOKIE_HEADER_KEY, "bar"));

        executeMultipleHeadersWithSameKey(builder);
    }

    public void executeMultipleHeadersWithSameKey( UpdateRequestBuilder<Person> builder )
    {
        // the server will check for the cookie headers when receiving CSRF tokens
        stubFor(
            head(anyUrl())
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("foo"))
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("bar"))
                .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));

        // the server will check for the cookie headers when receiving the patch request
        stubFor(
            patch(urlEqualTo(ENTITY_URL))
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("foo"))
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("bar"))
                .willReturn(ok()));

        builder.execute(destination);

        // check that the response is 200 with the cookie headers receive
        verify(
            headRequestedFor(urlEqualTo(SERVICE_PATH))
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("foo"))
                .withHeader(SET_COOKIE_HEADER_KEY, equalTo("bar")));
    }
}
