package com.sap.cloud.sdk.services.openapi.genericreturntype;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

/**
 * This test covers an edge case of API endpoints whose response is configured as follows:
 *
 * // @formatter:off
 * responses:
 *         '200':
 *           description: Returns the context of the specified user task.
 *           schema:
 *             type: object
 *             description: The context of the user task.
 *           examples:
 *             application/json:
 *               firstname: John
 *               lastname: Doe
 * // @formatter:on
 *
 * More specifically, the edge case is that the response body schema is not specified. We only know that it represents a generic response
 * object
 *
 * This test case demonstrates that the returned Java type at runtime is a {@link LinkedHashMap}.
 */
public class GenericReturnTypeTest
{
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Test
    public void testGenericAccessToNonNestedJsonObject()
    {
        final String responseBody = "{\"firstname\":\"John\",\"lastname\":\"Doe\"}";

        final HttpDestination httpDestination = DefaultHttpDestination.builder(wireMockRule.baseUrl()).build();

        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = new TestApi(httpDestination).testMethod();

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, String> castedReturnObject = (LinkedHashMap<String, String>) context;

        assertThat(castedReturnObject.get("firstname")).isEqualTo("John");
        assertThat(castedReturnObject.get("lastname")).isEqualTo("Doe");
    }

    @Test
    public void testGenericAccessToNestedJsonObject()
    {
        final String responseBody = "{" + "\"foo\": \"bar\"," + "\"bar\": {" + "\"foobar\": \"barfoo\"" + "}" + "}";

        final HttpDestination httpDestination = DefaultHttpDestination.builder(wireMockRule.baseUrl()).build();

        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = new TestApi(httpDestination).testMethod();

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, Object> castedReturnObject = (LinkedHashMap<String, Object>) context;

        assertThat(castedReturnObject.get("foo")).isEqualTo("bar");

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, Object> castedNestedReturnObject =
            (LinkedHashMap<String, Object>) castedReturnObject.get("bar");

        assertThat(castedNestedReturnObject.get("foobar")).isEqualTo("barfoo");
    }

    @Test
    public void testGenericAccessToArray()
    {
        final String responseBody = "[\"foo\", \"bar\", \"foo\", \"bar\"]";

        final HttpDestination httpDestination = DefaultHttpDestination.builder(wireMockRule.baseUrl()).build();

        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = new TestApi(httpDestination).testMethod();

        @SuppressWarnings( "unchecked" )
        final List<String> castedReturnObject = (List<String>) context;

        assertThat(castedReturnObject).hasSize(4);
        assertThat(castedReturnObject).containsExactly("foo", "bar", "foo", "bar");
    }
}
