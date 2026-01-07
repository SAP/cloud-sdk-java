package com.sap.cloud.sdk.services.openapi.genericreturntype;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
@WireMockTest
class GenericReturnTypeTest
{

    HttpDestination httpDestination;

    @BeforeEach
    void setUp( @Nonnull final WireMockRuntimeInfo wm )
    {
        httpDestination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
    }

    static Stream<Arguments> provideContext()
    {
        return Stream
            .of(
                Arguments.of((Function<HttpDestination, Object>) ( des ) -> new TestSpringApi(des).testMethod()),
                Arguments.of((Function<HttpDestination, Object>) ( des ) -> new TestSpringApi(des).testMethod()));
    }

    @MethodSource( "provideContext" )
    @ParameterizedTest
    void testGenericAccessToNonNestedJsonObject( @Nonnull final Function<HttpDestination, Object> contextFactory )
    {
        final String responseBody = "{\"firstname\":\"John\",\"lastname\":\"Doe\"}";
        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = contextFactory.apply(httpDestination);

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, String> castedReturnObject = (LinkedHashMap<String, String>) context;

        assertThat(castedReturnObject.get("firstname")).isEqualTo("John");
        assertThat(castedReturnObject.get("lastname")).isEqualTo("Doe");
    }

    @MethodSource( "provideContext" )
    @ParameterizedTest
    void testGenericAccessToNestedJsonObject( @Nonnull final Function<HttpDestination, Object> contextFactory )
    {
        final String responseBody = "{" + "\"foo\": \"bar\"," + "\"bar\": {" + "\"foobar\": \"barfoo\"" + "}" + "}";

        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = contextFactory.apply(httpDestination);

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, Object> castedReturnObject = (LinkedHashMap<String, Object>) context;

        assertThat(castedReturnObject.get("foo")).isEqualTo("bar");

        @SuppressWarnings( "unchecked" )
        final LinkedHashMap<String, Object> castedNestedReturnObject =
            (LinkedHashMap<String, Object>) castedReturnObject.get("bar");

        assertThat(castedNestedReturnObject.get("foobar")).isEqualTo("barfoo");
    }

    @MethodSource( "provideContext" )
    @ParameterizedTest
    void testGenericAccessToArray( @Nonnull final Function<HttpDestination, Object> contextFactory )
    {
        final String responseBody = "[\"foo\", \"bar\", \"foo\", \"bar\"]";

        stubFor(get(urlEqualTo("/endpoint")).willReturn(okJson(responseBody)));

        final Object context = contextFactory.apply(httpDestination);

        @SuppressWarnings( "unchecked" )
        final List<String> castedReturnObject = (List<String>) context;

        assertThat(castedReturnObject).hasSize(4);
        assertThat(castedReturnObject).containsExactly("foo", "bar", "foo", "bar");
    }
}
