package com.sap.cloud.sdk.datamodel.odatav4.core;

import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.sap.cloud.sdk.datamodel.odatav4.TestUtility.readResourceFileCrlf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

@WireMockTest
class ODatav4BatchConnectionTest
{
    private static final String RESPONSE_CONTENT_TYPE =
        "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
    private static final String RESPONSE_WITH_CHANGESET =
        readResourceFileCrlf(ODatav4BatchConnectionTest.class, "BatchResponseWithChangeset.txt");
    private static final String RESPONSE_WITHOUT_CHANGESET =
        readResourceFileCrlf(ODatav4BatchConnectionTest.class, "BatchResponseWithoutChangeset.txt");
    private static final String RESPONSE_WITH_ERROR =
        readResourceFileCrlf(ODatav4BatchConnectionTest.class, "BatchResponseWithError.txt");
    private static final TestEntityService SERVICE = new TestEntityService()
    {
    };
    private static final int MAX_PARALLEL_CONNECTIONS = 10;

    private DefaultHttpDestination destination;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        stubFor(head(UrlPattern.ANY).willReturn(noContent()));
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();

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
    void testNoConnectionTimeoutWhenBatchResponseContainsNoChangeset()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITHOUT_CHANGESET)));

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            final GetAllRequestBuilder<TestEntity> READ_ALL = SERVICE.getTestEntities();
            final GetByKeyRequestBuilder<TestEntity> READ_BY_KEY = SERVICE.getTestEntitiesByKey("foobar");
            final BatchResponse batchResponse =
                SERVICE.batch().addReadOperations(READ_ALL, READ_BY_KEY).execute(destination);

            final List<TestEntity> readAllResult = batchResponse.getReadResult(READ_ALL);
            assertThat(readAllResult).isNotEmpty();

            final TestEntity readByKeyResult = batchResponse.getReadResult(READ_BY_KEY);
            assertThat(readByKeyResult).isNotNull();
        }
    }

    @Test
    void testNoConnectionTimeoutWhenBatchResponseContainsChangeset()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_CHANGESET)));

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            final GetAllRequestBuilder<TestEntity> READ_ALL = SERVICE.getTestEntities();
            final GetByKeyRequestBuilder<TestEntity> READ_BY_KEY = SERVICE.getTestEntitiesByKey("foobar");
            final CreateRequestBuilder<TestEntity> CREATE = SERVICE.createTestEntity(new TestEntity());
            final UpdateRequestBuilder<TestEntity> UPDATE =
                SERVICE.updateTestEntity(TestEntity.builder().id("upd").build());
            final DeleteRequestBuilder<TestEntity> DELETE =
                SERVICE.deleteTestEntity(TestEntity.builder().id("del").build());
            final BatchResponse batchResponse =
                SERVICE
                    .batch()
                    .addReadOperations(READ_ALL, READ_BY_KEY)
                    .addChangeset(CREATE, UPDATE, DELETE)
                    .execute(destination);

            final List<TestEntity> readAllResult = batchResponse.getReadResult(READ_ALL);
            assertThat(readAllResult).isNotEmpty();

            final TestEntity readByKeyResult = batchResponse.getReadResult(READ_BY_KEY);
            assertThat(readByKeyResult).isNotNull();

            final ModificationResponse<TestEntity> createResult = batchResponse.getModificationResult(CREATE);
            assertThat(createResult).isNotNull();

            final ModificationResponse<TestEntity> updateResult = batchResponse.getModificationResult(UPDATE);
            assertThat(updateResult).isNotNull();
            assertThat(updateResult.getResponseEntity()).isNotNull();

            final ModificationResponse<TestEntity> deleteResult = batchResponse.getModificationResult(DELETE);
            assertThat(deleteResult).isNotNull();
            assertThat(deleteResult.getResponseEntity()).isEmpty();
        }
    }

    @Test
    void testNoConnectionTimeoutWhenBatchResponseContainsError()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_ERROR)));

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            final GetAllRequestBuilder<TestEntity> READ_ALL = SERVICE.getTestEntities();
            final GetByKeyRequestBuilder<TestEntity> READ_BY_KEY = SERVICE.getTestEntitiesByKey("foobar");
            final DeleteRequestBuilder<TestEntity> DELETE =
                SERVICE.deleteTestEntity(TestEntity.builder().id("del").build());
            final BatchResponse batchResponse =
                SERVICE.batch().addReadOperations(READ_ALL, READ_BY_KEY).addChangeset(DELETE).execute(destination);

            final List<TestEntity> readAllResult = batchResponse.getReadResult(READ_ALL);
            assertThat(readAllResult).isNotEmpty();

            final TestEntity readByKeyResult = batchResponse.getReadResult(READ_BY_KEY);
            assertThat(readByKeyResult).isNotNull();

            assertThatExceptionOfType(ODataServiceErrorException.class)
                .isThrownBy(() -> batchResponse.getModificationResult(DELETE))
                .satisfies(e -> assertThat(e.getHttpCode()).isEqualTo(400));
        }
    }

    @Test
    @Timeout( value = 300_000L, unit = TimeUnit.MILLISECONDS )
    @Disabled( "Test triggers a ConnectionPoolTimeoutException. Use it only to manually verify behaviour." )
    void testConnectionTimeoutWhenBatchResponseIsNotConsumedFully()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITHOUT_CHANGESET)));

        final GetAllRequestBuilder<TestEntity> READ_ALL = SERVICE.getTestEntities();
        final GetByKeyRequestBuilder<TestEntity> READ_BY_KEY = SERVICE.getTestEntitiesByKey("foobar");

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS; ++i ) {
            assertThatNoException()
                .isThrownBy(() -> SERVICE.batch().addReadOperations(READ_ALL, READ_BY_KEY).execute(destination));
        }

        assertThatThrownBy(() -> SERVICE.batch().addReadOperations(READ_ALL, READ_BY_KEY).execute(destination))
            .isInstanceOf(ODataConnectionException.class)
            .hasRootCauseExactlyInstanceOf(ConnectionPoolTimeoutException.class)
            .hasMessageContaining(
                "Please execute your request with try-with-resources to ensure resources are properly closed.");
    }
}
