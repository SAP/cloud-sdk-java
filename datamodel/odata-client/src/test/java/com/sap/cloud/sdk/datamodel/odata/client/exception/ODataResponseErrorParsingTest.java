package com.sap.cloud.sdk.datamodel.odata.client.exception;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.forbidden;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

import lombok.SneakyThrows;

public class ODataResponseErrorParsingTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String ODATA_SERVICE_PATH = "/service/";
    private static final String ODATA_ENTITY_COLLECTION = "Entity";

    @Rule
    public final WireMockRule server = new WireMockRule(WIREMOCK_CONFIGURATION);

    private HttpClient httpClient;

    @Before
    public void setup()
    {
        httpClient =
            HttpClientAccessor.getHttpClient((Destination) DefaultHttpDestination.builder(server.baseUrl()).build());
    }

    @Test
    public void testHttpErrorCodes()
    {
        server.stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + "forbidden")).willReturn(forbidden()));
        server.stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + "badRequest")).willReturn(badRequest()));
        server.stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + "unauthorized")).willReturn(unauthorized()));
        server.stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + "notFound")).willReturn(notFound()));
        server.stubFor(get(urlPathEqualTo(ODATA_SERVICE_PATH + "error")).willReturn(serverError()));

        final ImmutableMap<ODataRequestRead, Integer> expectedRequestsAndErrors =
            ImmutableMap
                .<ODataRequestRead, Integer> builder()
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "forbidden", "", ODataProtocol.V4), SC_FORBIDDEN)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "forbidden", "", ODataProtocol.V2), SC_FORBIDDEN)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "badRequest", "", ODataProtocol.V2), SC_BAD_REQUEST)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "badRequest", "", ODataProtocol.V4), SC_BAD_REQUEST)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "unauthorized", "", ODataProtocol.V2), SC_UNAUTHORIZED)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "unauthorized", "", ODataProtocol.V4), SC_UNAUTHORIZED)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "notFound", "", ODataProtocol.V2), SC_NOT_FOUND)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "notFound", "", ODataProtocol.V4), SC_NOT_FOUND)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "error", "", ODataProtocol.V2), SC_INTERNAL_SERVER_ERROR)
                .put(new ODataRequestRead(ODATA_SERVICE_PATH, "error", "", ODataProtocol.V4), SC_INTERNAL_SERVER_ERROR)
                .build();

        expectedRequestsAndErrors
            .forEach(
                ( request, errorCode ) -> assertThatExceptionOfType(ODataResponseException.class)
                    .isThrownBy(() -> request.execute(httpClient))
                    .satisfies(e -> {
                        assertThat(e.getSuppressed()).isEmpty();
                        assertThat(e.getHttpCode()).isEqualTo(errorCode);
                        assertThat(e.getHttpBody()).containsExactly("");
                    }));
    }

    @SneakyThrows
    @Test
    public void testWithoutHttpEntity()
    {
        final HttpClient mockedClient = mock(HttpClient.class);
        final BasicStatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "oh");
        doReturn(new BasicHttpResponse(statusLine)).when(mockedClient).execute(any(HttpUriRequest.class));

        final ODataRequestRead request = new ODataRequestRead(ODATA_SERVICE_PATH, "", "", ODataProtocol.V4);
        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> request.execute(mockedClient))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(500);
                assertThat(e.getHttpBody()).isEmpty();
            });
    }

    @Test
    public void testParsingODataV2Error()
    {
        final String json =
            "{\n"
                + "   \"error\":{\n"
                + "      \"code\":\"005056A509B11EE1B9A8FEC11C23378E\",\n"
                + "      \"message\":{\n"
                + "         \"lang\":\"en\",\n"
                + "         \"value\":\"System query options '$orderby,$skip,$top,$skiptoken,$inlinecount' are not allowed in the requested URI\"\n"
                + "      },\n"
                + "      \"innererror\":{\n"
                + "         \"transactionid\":\"25669476CF9401E0E005F2FA0752F574\",\n"
                + "         \"timestamp\":\"20200813143348.4420100\",\n"
                + "         \"Error_Resolution\":{\n"
                + "            \"SAP_Transaction\":\"For backend administrators: use ADT feed reader \\\"SAP Gateway Error Log\\\" or run transaction /IWFND/ERROR_LOG on SAP Gateway hub system and search for entries with the timestamp above for more details\",\n"
                + "            \"SAP_Note\":\"See SAP Note 1797736 for error analysis (https://service.sap.com/sap/support/notes/1797736)\"\n"
                + "         },\n"
                + "         \"errordetails\": [\n"
                + "          { \n"
                + "            \"code\": \"UF1\",\n"
                + "            \"message\": \"$search query option not supported\",\n"
                + "            \"target\": \"t1\",\n"
                + "            \"additionalTargets\": [\"t2\",\"t3\"],\n"
                + "            \"severity\": \"error\"\n"
                + "          }]"
                + "      }\n"
                + "   }\n"
                + "}";

        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V2);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withHeader("Content-Type", "application/json").withBody(json)));

        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .satisfies(e -> {
                assertThat(e).hasNoSuppressedExceptions();
                assertThat(e.getOdataError()).satisfies(error -> {
                    assertThat(error.getODataCode()).isEqualTo("005056A509B11EE1B9A8FEC11C23378E");
                    assertThat(error.getODataMessage()).startsWith("System query options");
                    assertThat(error.getDetails()).isNotNull();
                    assertThat(error.getDetails())
                        .containsOnly(new ODataServiceError("UF1", "$search query option not supported", "t1"));
                    assertThat(error.getInnerError()).isNotNull();
                    assertThat(error.getInnerError()).isNotEmpty();
                    assertThat(error.getInnerError()).containsKeys("transactionid", "timestamp", "Error_Resolution");
                    assertThat(error.getInnerError().get("Error_Resolution")).isInstanceOf(Map.class);
                });
            });
    }

    @Test
    public void testParsingODataV4Error()
    {
        final String json =
            "{"
                + "\"error\": {"
                + "  \"code\": \"err123\","
                + "  \"message\": \"Unsupported functionality\","
                + "  \"target\": \"query\","
                + "  \"details\": ["
                + "     {"
                + "       \"code\": \"forty-two\","
                + "       \"target\": \"$search\", "
                + "       \"message\": \"$search query option not supported\""
                + "     }"
                + "   ],"
                + "  \"innererror\": {"
                + "     \"foo\": 123,"
                + "     \"bar\": \"ok\""
                + "  }"
                + "}"
                + "}";

        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V4);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withHeader("Content-Type", "application/json").withBody(json)));

        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .matches(e -> "err123".equals(e.getOdataError().getODataCode()))
            .matches(e -> "Unsupported functionality".equals(e.getOdataError().getODataMessage()))
            .satisfies(e -> {
                final List<ODataServiceErrorDetails> details = e.getOdataError().getDetails();
                assertThat(details)
                    .containsExactly(
                        new ODataServiceError(
                            "forty-two",
                            "$search query option not supported",
                            "$search",
                            Collections.emptyList(),
                            Collections.emptyMap()));
            })
            .satisfies(e -> {
                final Map<String, Object> innerError = e.getOdataError().getInnerError();
                assertThat(innerError).containsEntry("foo", 123.0).containsEntry("bar", "ok");
            });
    }

    @Test
    public void testParsingBrokenODataErrorV2()
    {
        final String json = "{\"error\": {\"a\"}}";
        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V2);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withHeader("Content-Type", "application/json").withBody(json)));

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .satisfies(e -> {
                assertThat(e.getSuppressed()).isEmpty();
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody()).containsExactly(json);
            });
    }

    @Test
    public void testParsingBrokenODataErrorV4()
    {
        final String json = "{\"error\": {\"a\"}}";
        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V4);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withHeader("Content-Type", "application/json").withBody(json)));

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .satisfies(e -> {
                assertThat(e.getSuppressed()).isEmpty();
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody()).containsExactly(json);
            });
    }

    @Test
    public void testEmptyODataErrorV2()
    {
        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V2);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withBody("foo")));

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .satisfies(e -> {
                assertThat(e.getSuppressed()).isEmpty();
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody()).containsExactly("foo");
            });
    }

    @Test
    public void testEmptyODataErrorV4()
    {
        final ODataRequestRead request =
            new ODataRequestRead(ODATA_SERVICE_PATH, ODATA_ENTITY_COLLECTION, "", ODataProtocol.V4);
        server
            .stubFor(
                get(urlPathEqualTo(ODATA_SERVICE_PATH + ODATA_ENTITY_COLLECTION))
                    .willReturn(badRequest().withBody("foo")));

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> request.execute(httpClient))
            .satisfies(e -> {
                assertThat(e.getSuppressed()).isEmpty();
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody()).containsExactly("foo");
            });
    }
}
