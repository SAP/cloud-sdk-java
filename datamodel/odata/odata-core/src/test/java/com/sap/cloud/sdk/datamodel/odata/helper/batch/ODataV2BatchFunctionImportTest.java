/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity;

import lombok.SneakyThrows;

@WireMockTest
class ODataV2BatchFunctionImportTest
{
    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";
    private static final String REQUEST_URL_BATCH = "/$batch";
    private static final String RESPONSE_BODY = readResourceFileCrlf("BatchResponse.txt");
    private static final String REQUEST_BODY_POST = readResourceFileCrlf("BatchRequestFunctionImportWithPost.txt");
    private static final String REQUEST_BODY_GET = readResourceFileCrlf("BatchRequestFunctionImportWithGet.txt");
    private static final String REQUEST_BODY_POST_WITH_CUSTOM_HEADER =
        readResourceFileCrlf("BatchRequestFunctionImportWithPostWithCustomHeader.txt");

    @BeforeEach
    void before()
    {
        stubFor(
            head(urlEqualTo("/"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase(X_CSRF_TOKEN_HEADER_FETCH_VALUE))
                .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));

        final String contentType = "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(post(urlEqualTo(REQUEST_URL_BATCH)).willReturn(okForContentType(contentType, RESPONSE_BODY)));
    }

    @Test
    void testIllegalStateExceptionWhenFunctionImportUsingHttpGetInChangeSet( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet functionImport =
            new TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet("John", "Doe");

        assertThatIllegalStateException().isThrownBy(() -> {
            new TestVdmEntityBatch("")
                .beginChangeSet()
                .addFunctionImport(functionImport)
                .endChangeSet()
                .executeRequest(destination);
        });
    }

    @Test
    void testFunctionImportUsingHttpPostInChangeSet( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch.TestFunctionImportHttpPost functionImport =
            new TestVdmEntityBatch.TestFunctionImportHttpPost("John", "Doe");

        new TestVdmEntityBatch("")
            .beginChangeSet()
            .addFunctionImport(functionImport)
            .endChangeSet()
            .executeRequest(destination);

        verify(postRequestedFor(urlPathEqualTo(REQUEST_URL_BATCH)).withRequestBody(equalTo(REQUEST_BODY_POST)));
    }

    @Test
    void testFunctionImportUsingHttpGetInBatchRequest( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet functionImport =
            new TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet("John", "Doe");

        new TestVdmEntityBatch("").addReadOperations(functionImport).executeRequest(destination);

        verify(postRequestedFor(urlPathEqualTo(REQUEST_URL_BATCH)).withRequestBody(equalTo(REQUEST_BODY_GET)));
    }

    @Test
    void
        testIllegalStateExceptionWhenFunctionImportUsingHttpPostInReadOperation( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch.TestFunctionImportHttpPost functionImport =
            new TestVdmEntityBatch.TestFunctionImportHttpPost("John", "Doe");

        assertThatIllegalStateException().isThrownBy(() -> {
            new TestVdmEntityBatch("").addReadOperations(functionImport).executeRequest(destination);
        });
    }

    @Test
    void testFunctionImportUsingHttpPostInChangeSetWithCustomHeader( @Nonnull final WireMockRuntimeInfo wm )
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch.TestFunctionImportHttpPost functionImport =
            new TestVdmEntityBatch.TestFunctionImportHttpPost("John", "Doe").withHeader("foo", "bar");

        new TestVdmEntityBatch("")
            .beginChangeSet()
            .addFunctionImport(functionImport)
            .endChangeSet()
            .executeRequest(destination);

        verify(
            postRequestedFor(urlPathEqualTo(REQUEST_URL_BATCH))
                .withRequestBody(equalTo(REQUEST_BODY_POST_WITH_CUSTOM_HEADER)));
    }

    @SneakyThrows
    private static String readResourceFileCrlf( final String resourceFileName )
    {
        final URL resourceUrl =
            ODataV2BatchRequestUnitTest.class
                .getClassLoader()
                .getResource(ODataV2BatchFunctionImportTest.class.getSimpleName() + "/" + resourceFileName);
        final String result = Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        return result.replaceAll("(?<!\\r)\\n", "" + ((char) 13) + (char) 10);
    }

    @Test
    void testLowLevelToHighLevel( @Nonnull final WireMockRuntimeInfo wm )
    {
        final Destination destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        final TestVdmEntityBatch testVdmEntityBatch = new TestVdmEntityBatch("");

        final TestVdmEntityBatch.TestEntityByKey entityByKey =
            new TestVdmEntityBatch.TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));
        final TestVdmEntityBatch.TestEntityCreate create =
            new TestVdmEntityBatch.TestEntityCreate(TestVdmEntity.builder().build());
        final TestVdmEntityBatch.TestEntityDelete delete =
            new TestVdmEntityBatch.TestEntityDelete(TestVdmEntity.builder().build());
        final TestVdmEntityBatch.TestEntityUpdate update = new TestVdmEntityBatch.TestEntityUpdate(new TestVdmEntity());

        final TestVdmEntity testVdmEntity = TestVdmEntity.builder().integerValue(10).build();

        final TestVdmEntityBatch.TestEntityRead readAll =
            new TestVdmEntityBatch.TestEntityRead()
                .select(() -> "StringValue", () -> "IntegerValue")
                .top(10)
                .withHeader("header-read_all", "read_all");

        final TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet testFunctionImportSingleResultHttpGet =
            new TestVdmEntityBatch.TestFunctionImportSingleResultHttpGet("Alice", "Bob");

        final TestVdmEntityBatch.TestFunctionImportCollectionResultHttpGet testFunctionImportCollectionResultHttpGet =
            new TestVdmEntityBatch.TestFunctionImportCollectionResultHttpGet();

        final TestVdmEntityBatch.TestFunctionImportHttpPost functionImport =
            new TestVdmEntityBatch.TestFunctionImportHttpPost("John", "Doe");

        final TestVdmEntityBatch.TestFunctionImportSingleEntityResultHttpGet testFunctionImportSingleEntityResultHttpGet =
            new TestVdmEntityBatch.TestFunctionImportSingleEntityResultHttpGet(33, "Alice");

        final TestVdmEntityBatch batchBuilder =
            testVdmEntityBatch
                .addReadOperations(readAll)
                .addChangeSet(create)
                .addReadOperations(entityByKey)
                .addChangeSet(update, delete)
                .beginChangeSet()
                .create(testVdmEntity)
                .addFunctionImport(functionImport)
                .endChangeSet()
                .addReadOperations(testFunctionImportSingleResultHttpGet, testFunctionImportCollectionResultHttpGet)
                .addReadOperations(testFunctionImportSingleEntityResultHttpGet);

        final ODataRequestBatch lowLevelRequest = batchBuilder.toRequest();

        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        final ODataRequestResultMultipartGeneric lowLevelResult = lowLevelRequest.execute(httpClient);

        //Customer transition example from low-level result object to typed result

        final DefaultBatchResponseResult convertedHighLevelResult =
            DefaultBatchResponseResult.of(lowLevelResult, batchBuilder);

        final BatchResponse expectedHighLevelResult = batchBuilder.executeRequest(destination);

        assertThat(expectedHighLevelResult.getReadResult(readAll))
            .isEqualTo(convertedHighLevelResult.getReadResult(readAll));

        assertThat(expectedHighLevelResult.getReadResult(entityByKey))
            .isEqualTo(convertedHighLevelResult.getReadResult(entityByKey));
        assertThat(expectedHighLevelResult.getReadResult(entityByKey))
            .extracting(TestVdmEntity::getIntegerValue)
            .isEqualTo(9000);

        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportSingleResultHttpGet))
            .isEqualTo(convertedHighLevelResult.getReadResult(testFunctionImportSingleResultHttpGet));
        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportSingleResultHttpGet))
            .isEqualTo("awesomeStuff");

        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportCollectionResultHttpGet))
            .isEqualTo(convertedHighLevelResult.getReadResult(testFunctionImportCollectionResultHttpGet));
        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportCollectionResultHttpGet))
            .isEqualTo(Arrays.asList("foo", "bar"));

        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportSingleEntityResultHttpGet))
            .isEqualTo(convertedHighLevelResult.getReadResult(testFunctionImportSingleEntityResultHttpGet));
        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportSingleEntityResultHttpGet))
            .extracting(TestVdmEntity::getIntegerValue)
            .isEqualTo(33);
        assertThat(expectedHighLevelResult.getReadResult(testFunctionImportSingleEntityResultHttpGet))
            .extracting(TestVdmEntity::getStringValue)
            .isEqualTo("Alice");

        assertThat(expectedHighLevelResult.get(0).get().getCreatedEntities())
            .isEqualTo(convertedHighLevelResult.get(0).get().getCreatedEntities());
        assertThat(expectedHighLevelResult.get(1).get().getCreatedEntities())
            .isEqualTo(convertedHighLevelResult.get(1).get().getCreatedEntities());
        assertThat(expectedHighLevelResult.get(2).isFailure()).isEqualTo(convertedHighLevelResult.get(2).isFailure());
    }
}
