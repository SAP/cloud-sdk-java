/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class ODataV2FunctionImportIntegrationTest
{
    @Rule
    public final WireMockRule server = new WireMockRule(wireMockConfig().dynamicPort());

    private static final String ODATA_ENDPOINT_URL = "/path/to/service";
    private static final String ODATA_FUNCTION_IMPORT_URL = ODATA_ENDPOINT_URL + "/CancelItem.*";
    private static final String CSRF_TOKEN = "awesome-token";
    private static final String CSRF_TOKEN_HEADER_KEY = "x-csrf-token";

    private static final String XML_ERROR_STRING = "<error>an exception was raised </error>";;
    private static final String JSON_ERROR_STRING = "{\"error\": \"an exception was raised\"}";
    private static final String RESPONSE =
        "{\"d\": {"
            + "  \"CancelItem\": {"
            + "    \"__metadata\": { \"type\": \"API_TEST_SRV/CancelItem\" },"
            + "    \"SomeField\": \"1010\""
            + "  }"
            + "}}";

    @Data
    @EqualsAndHashCode( callSuper = true )
    @JsonAdapter( ODataVdmEntityAdapterFactory.class )
    public static class TestEntity extends VdmEntity<TestEntity>
    {
        private final String entityCollection = "TestEntities";
        private final Class<TestEntity> type = TestEntity.class;

        @SerializedName( "SomeField" )
        @JsonProperty( "SomeField" )
        @ODataField( odataName = "SomeField" )
        private String someField;
    }

    private DefaultHttpDestination destination;

    @Before
    public void before()
    {
        destination = DefaultHttpDestination.builder(server.baseUrl()).build();
    }

    @Test
    public void testFunctionImportWithCsrfToken()
    {
        wiremockIssueCsrfTokenWithStatusCodeOk();

        wiremockSendSuccesfulJsonResponse();

        final TestEntity materialDocumentItem = getCancelItemFluentHelper().executeRequest(destination);

        assertOnFunctionImportResponseEntity(materialDocumentItem);
    }

    @Test
    public void testFunctionImportWithCsrfTokenAndServerErrorResponse()
    {
        wiremockIssueCsrfToken(WireMock.serverError());

        wiremockSendSuccesfulJsonResponse();

        final TestEntity materialDocumentItem = getCancelItemFluentHelper().executeRequest(destination);

        assertOnFunctionImportResponseEntity(materialDocumentItem);
    }

    @Test
    public void testFunctionImportWithCsrfTokenAndMethodNotAllowedResponse()
    {
        wiremockIssueCsrfToken(WireMock.status(HttpStatus.SC_METHOD_NOT_ALLOWED));

        wiremockSendSuccesfulJsonResponse();

        final TestEntity materialDocumentItem = getCancelItemFluentHelper().executeRequest(destination);

        assertOnFunctionImportResponseEntity(materialDocumentItem);
    }

    @Test
    public void testFunctionImportWithoutCsrfToken()
    {
        //Return OK but without csrf token in response header
        stubFor(
            head(urlEqualTo(ODATA_ENDPOINT_URL))
                .withHeader(CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("Fetch"))
                .willReturn(WireMock.ok()));

        stubFor(post(urlMatching(ODATA_FUNCTION_IMPORT_URL)).willReturn(okJson(RESPONSE)));

        final TestEntity materialDocumentItem = getCancelItemFluentHelper().executeRequest(destination);

        assertOnFunctionImportResponseEntity(materialDocumentItem);

        server.verify(postRequestedFor(urlMatching(ODATA_FUNCTION_IMPORT_URL)).withoutHeader(CSRF_TOKEN_HEADER_KEY));
    }

    @Test
    public void testFunctionImportWithJsonError()
    {
        wiremockIssueCsrfTokenWithStatusCodeOk();

        stubFor(
            post(urlMatching(ODATA_FUNCTION_IMPORT_URL)).willReturn(WireMock.badRequest().withBody(JSON_ERROR_STRING)));

        assertThatExceptionOfType(ODataException.class)
            .isThrownBy(() -> getCancelItemFluentHelper().executeRequest(destination));
    }

    @Test
    public void testFunctionImportReturnsSanitizedXmlErrorBody()
    {
        wiremockIssueCsrfTokenWithStatusCodeOk();

        stubFor(
            post(urlMatching(ODATA_FUNCTION_IMPORT_URL)).willReturn(WireMock.badRequest().withBody(XML_ERROR_STRING)));

        assertThatExceptionOfType(ODataException.class)
            .isThrownBy(() -> getCancelItemFluentHelper().executeRequest(destination));
    }

    /*
     * This test verifies that header manipulation attempts (sneaking in new line characters in "user-supplied" HTTP
     * header) are prevented. At this place this should not be necessary, as the system we receive the CSRF token from
     * is the same one receiving the next request with this token, so any manipulation would hit the system that sent us
     * the manipulated csrf token in the first place. But, better be safe than sorry, so we remove all non-printable
     * characters.
     * Additional note: As tomcat (so also wiremock) automatically removes new line characters we cannot directly test
     * this logic here, therefore we test it with a tab (\t) character.
     */
    @Test
    public void testNonPrintableCharactersRemovedFromCsrfTokenValue()
    {
        final String tokenWithNonPrintableCharacters = CSRF_TOKEN + "\t<CR><LF>%0a%0d<script>alert()</script>";
        final String tokenWithoutNonPrintableCharacters = CSRF_TOKEN + "<CR><LF>%0a%0d<script>alert()</script>";

        stubFor(
            head(urlEqualTo(ODATA_ENDPOINT_URL))
                .withHeader(CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("Fetch"))
                .willReturn(ok().withHeader(CSRF_TOKEN_HEADER_KEY, tokenWithNonPrintableCharacters)));

        stubFor(
            post(urlMatching(ODATA_FUNCTION_IMPORT_URL))
                .withHeader(CSRF_TOKEN_HEADER_KEY, equalTo(tokenWithoutNonPrintableCharacters))
                .willReturn(okJson(RESPONSE)));

        getCancelItemFluentHelper().executeRequest(destination);

        server
            .verify(
                postRequestedFor(urlMatching(ODATA_FUNCTION_IMPORT_URL))
                    .withHeader(CSRF_TOKEN_HEADER_KEY, equalTo(tokenWithoutNonPrintableCharacters)));
    }

    private void wiremockIssueCsrfToken( final ResponseDefinitionBuilder responseDefinitionBuilder )
    {
        stubFor(
            head(urlEqualTo(ODATA_ENDPOINT_URL))
                .withHeader(CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase("Fetch"))
                .willReturn(responseDefinitionBuilder.withHeader(CSRF_TOKEN_HEADER_KEY, CSRF_TOKEN)));
    }

    private void wiremockIssueCsrfTokenWithStatusCodeOk()
    {
        wiremockIssueCsrfToken(WireMock.ok());
    }

    private void assertOnFunctionImportResponseEntity( @Nullable final TestEntity materialDocumentItem )
    {
        assertThat(materialDocumentItem).isNotNull();
        assertThat(materialDocumentItem.getSomeField()).isEqualTo("1010");
    }

    private void wiremockSendSuccesfulJsonResponse()
    {
        stubFor(
            post(urlMatching(ODATA_FUNCTION_IMPORT_URL))
                .withHeader(CSRF_TOKEN_HEADER_KEY, equalTo(CSRF_TOKEN))
                .willReturn(okJson(RESPONSE)));
    }

    private static FluentHelperFunction<?, ?, TestEntity> getCancelItemFluentHelper()
    {
        final Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("FileDocumentYear", "2015");
        parameters.put("FileDocument", "00281");
        parameters.put("FileDocumentItem", "1");
        parameters.put("PostingDate", LocalDateTime.of(2015, 1, 12, 12, 12));
        return FluentHelperFactory
            .withServicePath(ODATA_ENDPOINT_URL)
            .functionSinglePost(parameters, "CancelItem", TestEntity.class);
    }
}
