/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.datamodel.odatav4.TestUtility.readResourceFileCrlf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;

import lombok.SneakyThrows;

@WireMockTest
class ODataV4BatchRequestUnitTest
{
    private static final TestEntityService SERVICE = new TestEntityService()
    {
    };

    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";
    private static final String REQUEST_URL_BATCH = "/$batch";
    private static final String RESPONSE_BODY =
        readResourceFileCrlf(ODataV4BatchRequestUnitTest.class, "BatchReadsAndWritesSuccessResponse.txt");

    private static final String REQUEST_BODY =
        readResourceFileCrlf(ODataV4BatchRequestUnitTest.class, "BatchReadsAndWritesSuccessRequest.txt");
    private static final String BATCH_BAD_CHANGESET_RESPONSE_BODY =
        readResourceFileCrlf(ODataV4BatchRequestUnitTest.class, "BatchBadChangesetResponse.txt");

    private static final TestEntity ENTITY_CREATE = new TestEntity();
    private static final TestEntity ENTITY_UPDATE = TestEntity.builder().id("upd").build();
    private static final TestEntity ENTITY_DELETE = TestEntity.builder().id("del").build();

    private static final GetAllRequestBuilder<TestEntity> READ_ALL = SERVICE.getTestEntities();
    private static final GetByKeyRequestBuilder<TestEntity> READ_BY_KEY = SERVICE.getTestEntitiesByKey("foobar");
    private static final CreateRequestBuilder<TestEntity> CREATE = SERVICE.createTestEntity(ENTITY_CREATE);
    private static final UpdateRequestBuilder<TestEntity> UPDATE = SERVICE.updateTestEntity(ENTITY_UPDATE);
    private static final DeleteRequestBuilder<TestEntity> DELETE = SERVICE.deleteTestEntity(ENTITY_DELETE);
    private static final SingleValueFunctionRequestBuilder<Integer> FUNC_SINGLE = SERVICE.functionSingleResult();
    private static final CollectionValueFunctionRequestBuilder<String> FUNC_MULTIPLE = SERVICE.functionMultipleResult();
    private static final SingleValueActionRequestBuilder<Integer> ACT_SINGLE = SERVICE.actionSingleResult();
    private static final CollectionValueActionRequestBuilder<String> ACT_MULTIPLE = SERVICE.actionMultipleResult();
    private static final int MAX_PARALLEL_CONNECTIONS = 10;

    private Destination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

        // Mock CSRF token handling

        stubFor(
            head(urlEqualTo("/"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE))
                .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));

        // Mock OData Batch response
        final String contentType = "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(post(urlEqualTo(REQUEST_URL_BATCH)).willReturn(okForContentType(contentType, RESPONSE_BODY)));

        HttpClientAccessor
            .setHttpClientFactory(
                DefaultHttpClientFactory
                    .builder()
                    .maxConnectionsTotal(MAX_PARALLEL_CONNECTIONS)
                    .maxConnectionsPerRoute(MAX_PARALLEL_CONNECTIONS)
                    .build());
    }

    @AfterEach
    void teardown()
    {
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @Test
    void testAllOperations()
    {
        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            // execute batched requests
            final BatchResponse response =
                SERVICE
                    .batch()
                    .addReadOperations(READ_ALL, READ_BY_KEY)
                    .addChangeset(CREATE, UPDATE, DELETE)
                    .addReadOperations(FUNC_SINGLE, FUNC_MULTIPLE)
                    .addChangeset(ACT_SINGLE, ACT_MULTIPLE)
                    .execute(destination);

            // assertion on response parsing
            final List<TestEntity> readAllResult = response.getReadResult(READ_ALL);
            assertThat(readAllResult).isNotEmpty();

            final TestEntity readByKeyResult = response.getReadResult(READ_BY_KEY);
            assertThat(readByKeyResult).isNotNull();

            final ModificationResponse<TestEntity> createResult = response.getModificationResult(CREATE);
            assertThat(createResult).isNotNull();
            assertThat(createResult.getResponseEntity()).isNotNull().isNotEqualTo(ENTITY_CREATE);
            assertThat(createResult.getResponseHeaders().get("Location"))
                .containsExactly("https://localhost/service/TestEntities('new')");

            final ModificationResponse<TestEntity> updateResult = response.getModificationResult(UPDATE);
            assertThat(updateResult).isNotNull();
            assertThat(updateResult.getResponseEntity()).isNotNull().isNotEqualTo(ENTITY_CREATE);

            final ModificationResponse<TestEntity> deleteResult = response.getModificationResult(DELETE);
            assertThat(readByKeyResult).isNotNull();
            assertThat(deleteResult.getResponseEntity()).isEmpty();

            final Integer functionSingleResult = response.getReadResult(FUNC_SINGLE);
            assertThat(functionSingleResult).isNotNull();

            final List<String> functionMultipleResult = response.getReadResult(FUNC_MULTIPLE);
            assertThat(functionMultipleResult).isNotEmpty();

            final ActionResponseSingle<Integer> actionSingleResult = response.getModificationResult(ACT_SINGLE);
            assertThat(actionSingleResult).isNotNull();
            assertThat(actionSingleResult.getResponseResult()).isNotEmpty();

            final ActionResponseCollection<String> actionMultipleResult = response.getModificationResult(ACT_MULTIPLE);
            assertThat(actionMultipleResult).isNotNull();
            assertThat(actionMultipleResult.getResponseResult()).isNotEmpty();
            assertThat(actionMultipleResult.getResponseResult().get()).isNotEmpty();

        }
        // Verify request body
        final String requestContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
        verify(
            postRequestedFor(urlEqualTo(REQUEST_URL_BATCH))
                .withHeader("Content-Type", equalTo(requestContentType))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_VALUE))
                .withRequestBody(equalTo(REQUEST_BODY)));

        verify(
            MAX_PARALLEL_CONNECTIONS * 2,
            headRequestedFor(urlEqualTo("/"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
    }

    @Test
    void testIdenticalOperations()
    {
        final CreateRequestBuilder<TestEntity> identicalCreate = SERVICE.createTestEntity(ENTITY_CREATE);

        final BatchResponse response =
            SERVICE
                .batch()
                .addReadOperations(READ_ALL, READ_BY_KEY)
                .addChangeset(CREATE, identicalCreate)
                .execute(destination);

        final ModificationResponse<TestEntity> result1 = response.getModificationResult(CREATE);
        final ModificationResponse<TestEntity> result2 = response.getModificationResult(identicalCreate);

        assertThat(result1.getModifiedEntity().getId()).isEqualTo("new");
        assertThat(result2.getModifiedEntity().getId()).isEqualTo("updated");

        verify(
            1,
            headRequestedFor(urlEqualTo("/"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
    }

    @Test
    void testFailedBatchChangesetResponse()
    {
        final TestEntity testEntityA = new TestEntity("a");
        final TestEntity testEntityB = new TestEntity("b");
        final TestEntity testEntityC = new TestEntity("c");

        final CreateRequestBuilder<TestEntity> createTestEntityA = SERVICE.createTestEntity(testEntityA);
        final CreateRequestBuilder<TestEntity> createTestEntityB = SERVICE.createTestEntity(testEntityB);
        final CreateRequestBuilder<TestEntity> createTestEntityC = SERVICE.createTestEntity(testEntityC);

        // Override from setup
        // Mock OData Batch response
        final String responseContentType =
            "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(
            post(urlEqualTo(REQUEST_URL_BATCH))
                .willReturn(
                    aResponse()
                        .withHeader(CONTENT_TYPE, responseContentType)
                        .withBody(BATCH_BAD_CHANGESET_RESPONSE_BODY)
                        .withStatus(HttpServletResponse.SC_BAD_REQUEST)));

        try(
            final BatchResponse badResponse =
                SERVICE
                    .batch()
                    .addChangeset(createTestEntityA, createTestEntityB, createTestEntityC)
                    .execute(destination) ) {// will throw every time
            badResponse.getResponseStatusCode();// suppress compiler warning
        }
        catch( final ODataResponseException e ) {
            // The http response says the 3rd request fails, meaning the entityC
            assertThat(e.getFailedBatchRequest().get()).isEqualTo(createTestEntityC.toRequest());
        }

        try(
            final BatchResponse badResponse =
                SERVICE
                    .batch()
                    .addReadOperations(READ_ALL, READ_BY_KEY)
                    .addChangeset(createTestEntityA, createTestEntityB, createTestEntityC)
                    .execute(destination) ) {// will throw every time
            badResponse.getResponseStatusCode();// suppress compiler warning
        }
        catch( final ODataResponseException e ) {
            // The http response says the 3rd request fails, meaning the entityA
            assertThat(e.getFailedBatchRequest().get()).isEqualTo(createTestEntityA.toRequest());
        }
    }

    @Test
    void testBatchWithoutCsrfTokenRetrievalIfSkipped()
    {
        SERVICE.batch().withoutCsrfToken().addReadOperations(READ_ALL).execute(destination);

        verify(
            0,
            headRequestedFor(anyUrl())
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
    }

    @Test
    void testLowLevelToHighLevel()
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

        final BatchRequestBuilder batchBuilder =
            SERVICE
                .batch()
                .addReadOperations(READ_ALL, READ_BY_KEY)
                .addChangeset(CREATE, UPDATE, DELETE)
                .addReadOperations(FUNC_SINGLE, FUNC_MULTIPLE)
                .addChangeset(ACT_SINGLE, ACT_MULTIPLE);

        final ODataRequestBatch lowLevelRequest = batchBuilder.toRequest();

        final ODataRequestResultMultipartGeneric lowLevelResult = lowLevelRequest.execute(httpClient);

        //Consumer transition from generic result object to typed result
        final BatchResponse convertedHighLevelResult = BatchResponse.of(lowLevelResult, batchBuilder);
        final BatchResponse expectedHighLevelResult = batchBuilder.execute(destination);

        assertThat(expectedHighLevelResult.getReadResult(READ_ALL))
            .isEqualTo(convertedHighLevelResult.getReadResult(READ_ALL));
        assertThat(expectedHighLevelResult.getReadResult(READ_BY_KEY))
            .isEqualTo(convertedHighLevelResult.getReadResult(READ_BY_KEY));
        assertThat(expectedHighLevelResult.getModificationResult(CREATE).getModifiedEntity())
            .isEqualTo(convertedHighLevelResult.getModificationResult(CREATE).getModifiedEntity());
        assertThat(expectedHighLevelResult.getModificationResult(CREATE).getResponseHeaders())
            .isEqualTo(convertedHighLevelResult.getModificationResult(CREATE).getResponseHeaders());
        assertThat(expectedHighLevelResult.getModificationResult(UPDATE).getModifiedEntity())
            .isEqualTo(convertedHighLevelResult.getModificationResult(UPDATE).getModifiedEntity());
        assertThat(expectedHighLevelResult.getModificationResult(UPDATE).getResponseHeaders())
            .isEqualTo(convertedHighLevelResult.getModificationResult(UPDATE).getResponseHeaders());
        assertThat(expectedHighLevelResult.getModificationResult(DELETE).getModifiedEntity())
            .isEqualTo(convertedHighLevelResult.getModificationResult(DELETE).getModifiedEntity());
        assertThat(expectedHighLevelResult.getModificationResult(DELETE).getResponseHeaders())
            .isEqualTo(convertedHighLevelResult.getModificationResult(DELETE).getResponseHeaders());
        assertThat(expectedHighLevelResult.getReadResult(FUNC_SINGLE))
            .isEqualTo(convertedHighLevelResult.getReadResult(FUNC_SINGLE));
        assertThat(expectedHighLevelResult.getReadResult(FUNC_MULTIPLE))
            .isEqualTo(convertedHighLevelResult.getReadResult(FUNC_MULTIPLE));
        assertThat(expectedHighLevelResult.getModificationResult(ACT_SINGLE).getResponseResult())
            .isEqualTo(convertedHighLevelResult.getModificationResult(ACT_SINGLE).getResponseResult());
        assertThat(expectedHighLevelResult.getModificationResult(ACT_SINGLE).getResponseHeaders())
            .isEqualTo(convertedHighLevelResult.getModificationResult(ACT_SINGLE).getResponseHeaders());
        assertThat(expectedHighLevelResult.getModificationResult(ACT_MULTIPLE).getResponseResult())
            .isEqualTo(convertedHighLevelResult.getModificationResult(ACT_MULTIPLE).getResponseResult());
        assertThat(expectedHighLevelResult.getModificationResult(ACT_MULTIPLE).getResponseHeaders())
            .isEqualTo(convertedHighLevelResult.getModificationResult(ACT_MULTIPLE).getResponseHeaders());
    }

    @Test
    @SneakyThrows
    void testBatchOpenConnection()
    {
        final int N = MAX_PARALLEL_CONNECTIONS * 2;
        final List<InputStream> inputStreams = new ArrayList<>();

        final HttpDestination dest = DefaultHttpDestination.builder("").build();
        final HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.execute(argThat(req -> req instanceof HttpPost))).thenAnswer(args -> {
            final BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "ok");
            final InputStream inStream = mock(InputStream.class);
            inputStreams.add(inStream);
            response.setEntity(new InputStreamEntity(inStream));
            return response;
        });

        // configure test setup
        HttpClientAccessor.setHttpClientFactory(( anyDestination ) -> httpClient);

        // TEST: invoke many batch request each spawning an InputStream
        for( int i = 0; i < N; i++ ) {
            SERVICE.batch().addReadOperations(READ_ALL).execute(dest);
        }

        // ASSERTION: input streams are loaded but never fully consumed
        assertThat(inputStreams).hasSize(N);
        for( final InputStream inStream : inputStreams ) {
            Mockito.verify(inStream, never()).close();
        }

        // TEST: invoke one batch request using try-with-resource
        try( BatchResponse result = SERVICE.batch().addReadOperations(READ_ALL).execute(dest) ) {

            assertThat(result).isNotNull();
        }

        // ASSERTION: input stream is fully consumed
        Mockito.verify(inputStreams.get(N), times(1)).close();

        // reset test setup
        HttpClientAccessor.setHttpClientFactory(null);
    }
}
