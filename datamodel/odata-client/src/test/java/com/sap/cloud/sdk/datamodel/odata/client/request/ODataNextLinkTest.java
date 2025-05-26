package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import javax.annotation.Nonnull;

class ODataNextLinkTest
{
    private static final String PAYLOAD_NEXT_LINK = """
        {
          "d": {
            "results": [],
            "__next": "/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar"
          }
        }
        """;

    @RequiredArgsConstructor( staticName = "named" )
    @AllArgsConstructor
    @Accessors( fluent = true )
    @Setter
    @ToString( includeFieldNames = false, exclude = { "with", "expects" } )
    static class QueryParameterCase
    {
        final String label;
        Setup with;
        Expectation expects;

        @Builder
        static class Setup
        {
            String destinationQuery;
            String propertiesQuery;
            String initialQuery;
            String nextLinkQuery;
        }

        @Builder
        static class Expectation
        {
            String initialQuerySent;
            String nextLinkQueryParsed;
            String nextLinkQuerySent;
        }
    }

    private static QueryParameterCase[] QUERY_PARAMETERS_CASES =
        {
            // case 1: query-parameters from destination uri, destination properties, next-link and odata-request are distinct
            QueryParameterCase
                .named("DISTINCT")
                .with(
                    QueryParameterCase.Setup
                        .builder()
                        .destinationQuery("dest1=one&dest2=two")
                        .propertiesQuery("prop1=one&prop2=two")
                        .initialQuery("odata1=one&odata2=two")
                        .nextLinkQuery("next1=one&next2=two")
                        .build())
                .expects(
                    QueryParameterCase.Expectation
                        .builder()
                        .initialQuerySent("dest1=one&dest2=two&odata1=one&odata2=two&prop1=one&prop2=two")
                        .nextLinkQueryParsed("$skiptoken=42&next1=one&next2=two")
                        .nextLinkQuerySent("dest1=one&dest2=two&$skiptoken=42&next1=one&next2=two&prop1=one&prop2=two")
                        .build()),
            // case 2: query-parameters from destination uri, destination properties, next-link and odata-request with equal values
            QueryParameterCase
                .named("EQUAL")
                .with(
                    QueryParameterCase.Setup
                        .builder()
                        .destinationQuery("dest1=one&dest2=two")
                        .propertiesQuery("prop1=one&prop2=two")
                        .initialQuery("odata1=one&odata2=two")
                        .nextLinkQuery("next1=one&dest1=one&prop1=one&odata1=one")
                        .build())
                .expects(
                    QueryParameterCase.Expectation
                        .builder()
                        .initialQuerySent("dest1=one&dest2=two&odata1=one&odata2=two&prop1=one&prop2=two")
                        .nextLinkQueryParsed("$skiptoken=42&next1=one&odata1=one")
                        .nextLinkQuerySent("dest1=one&dest2=two&$skiptoken=42&next1=one&odata1=one&prop1=one&prop2=two")
                        .build()),
            // case 3: query-parameters from next link may be in conflict with destination uri or destination properties
            QueryParameterCase
                .named("CONFLICT")
                .with(
                    QueryParameterCase.Setup
                        .builder()
                        .destinationQuery("dest1=one&dest2=two")
                        .propertiesQuery("prop1=one&prop2=two")
                        .initialQuery("odata1=one&odata2=two")
                        .nextLinkQuery("next1=eins&dest1=eins&prop1=eins&odata1=eins")
                        .build())
                .expects(
                    QueryParameterCase.Expectation
                        .builder()
                        .initialQuerySent("dest1=one&dest2=two&odata1=one&odata2=two&prop1=one&prop2=two")
                        .nextLinkQueryParsed("$skiptoken=42&next1=eins&dest1=eins&prop1=eins&odata1=eins")
                        .nextLinkQuerySent(
                            "dest1=one&dest2=two" // destination parameters
                                + "&$skiptoken=42&next1=eins&dest1=eins&prop1=eins&odata1=eins" // next-link parameters
                                + "&prop1=one&prop2=two" // properties parameters
                        )
                        .build()),

        };

    @ParameterizedTest
    @FieldSource( "QUERY_PARAMETERS_CASES" )
    void testRemoveDuplicateQueryArguments1( @Nonnull final QueryParameterCase testCase )
    {
        final WireMockConfiguration wiremockConfig = wireMockConfig().dynamicPort();
        final WireMockServer wiremock = new WireMockServer(wiremockConfig);
        wiremock.start();

        // TEST SETUP: Mock first request and response
        final String initialRequest = "/v1/%s/endpoint?%s".formatted(testCase.label, testCase.expects.initialQuerySent);
        final String initialResponse =
            "{\"d\":{\"results\":[],\"__next\":\"/v1/%s/endpoint?$skiptoken=42&%s\"}}"
                .formatted(testCase.label, testCase.with.nextLinkQuery);
        wiremock.stubFor(get(urlEqualTo(initialRequest)).willReturn(okJson(initialResponse)));

        // TEST SETUP: Mock second request and response
        final String secondRequest = "/v1/%s/endpoint?%s".formatted(testCase.label, testCase.expects.nextLinkQuerySent);
        final String secondResponse = "{\"d\":{\"results\":[]}}";
        wiremock.stubFor(get(urlEqualTo(secondRequest)).willReturn(okJson(secondResponse)));

        // TEST SETUP: construct destination and HttpClient
        final String destinationUrl = wiremock.baseUrl() + "/?" + testCase.with.destinationQuery;
        final DefaultHttpDestination.Builder destinationBuilder = DefaultHttpDestination.builder(destinationUrl);
        for( final String queryArg : testCase.with.propertiesQuery.split("&") ) {
            final String[] queryArgParts = queryArg.split("=", 2);
            destinationBuilder.property("URL.queries." + queryArgParts[0], queryArgParts[1]);
        }
        final HttpClient client = HttpClientAccessor.getHttpClient(destinationBuilder.build());

        // TEST EXECUTION: Run OData request on behalf of HttpClient
        final ODataRequestRead request =
            new ODataRequestRead("/v1/" + testCase.label, "endpoint", testCase.with.initialQuery, ODataProtocol.V2);
        final ODataRequestResultGeneric resultFirst = request.execute(client);

        // TEST VALIDATION: Validate first actual request uri
        assertThat(resultFirst.getNextLink())
            .contains("/v1/%s/endpoint?%s".formatted(testCase.label, testCase.expects.nextLinkQueryParsed));
        wiremock.verify(getRequestedFor(urlEqualTo(initialRequest)));

        // TEST VALIDATION: Validate second actual request uri
        final Try<ODataRequestResultGeneric> resultSecond = resultFirst.tryGetNextPage();
        assertThat(resultSecond).isNotEmpty();
        wiremock.verify(getRequestedFor(urlEqualTo(secondRequest)));

        wiremock.shutdown();
    }

    @Test
    void testRemoveDuplicateQueryArguments()
    {
        final ODataRequestGeneric request =
            new ODataRequestRead("/v1/path/to/", "endpoint", "blub=42", ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));

        final String baseUrl = "http://blub/?high=five";

        // case 1: query parameters are EQUAL in destination and in nextLink -> remove redundant query parameter
        final Destination dest1 = DefaultHttpDestination.builder(baseUrl).property("URL.queries.foo", "bar").build();
        final HttpClient client1 = HttpClientAccessor.getHttpClient(dest1);
        final ODataRequestResultGeneric result1 = new ODataRequestResultGeneric(request, httpResponse, client1);
        assertThat(result1.getNextLink()).contains("/v1/path/to/endpoint?$skiptoken=s3cReT-t0k3n");

        // case 2: query parameters are NOT EQUAL in destination and in nextLink -> retain query parameter
        final Destination dest2 = DefaultHttpDestination.builder(baseUrl).property("URL.queries.foo", "baz").build();
        final HttpClient client2 = HttpClientAccessor.getHttpClient(dest2);
        final ODataRequestResultGeneric result2 = new ODataRequestResultGeneric(request, httpResponse, client2);
        assertThat(result2.getNextLink()).contains("/v1/path/to/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar");
    }

    @Test
    void testNotParsedNextLinkV4()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V4);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).isEmpty();
    }

    @Test
    void testParsedNextLinkV2()
    {
        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(PAYLOAD_NEXT_LINK, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).containsExactly("/v1/foo/bar/endpoint?$skiptoken=s3cReT-t0k3n&foo=bar");
        assertThat(result.getNextLink().flatMap(ODataUriFactory::extractSkipToken)).containsExactly("s3cReT-t0k3n");
    }

    @Test
    void testEmptySkipTokenV2()
    {
        final String emptyToken = "{\"d\": {\"__next\": \"/v1/foo/bar/endpoint?$skiptoken=&foo=bar\"}}";

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(emptyToken, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).containsExactly("/v1/foo/bar/endpoint?$skiptoken=&foo=bar");
        assertThat(result.getNextLink().flatMap(ODataUriFactory::extractSkipToken)).isEmpty();
    }

    @Test
    void testNoNextLinkV2()
    {
        final String noLink = "{\"d\": {}}";

        final ODataRequestGeneric request = mock(ODataRequestGeneric.class);
        when(request.getProtocol()).thenReturn(ODataProtocol.V2);

        final HttpResponse httpResponse = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "Ok");
        httpResponse.setEntity(new StringEntity(noLink, ContentType.APPLICATION_JSON));
        final ODataRequestResultGeneric result = new ODataRequestResultGeneric(request, httpResponse);

        assertThat(result.getNextLink()).isEmpty();
    }
}
