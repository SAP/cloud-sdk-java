package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.sap.cloud.sdk.datamodel.odata.helper.batch.TestVdmEntityBatch.TestEntityByKey;
import static com.sap.cloud.sdk.datamodel.odata.helper.batch.TestVdmEntityBatch.TestEntityCreate;
import static com.sap.cloud.sdk.datamodel.odata.helper.batch.TestVdmEntityBatch.TestEntityDelete;
import static com.sap.cloud.sdk.datamodel.odata.helper.batch.TestVdmEntityBatch.TestEntityRead;
import static com.sap.cloud.sdk.datamodel.odata.helper.batch.TestVdmEntityBatch.TestEntityUpdate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity;

import lombok.SneakyThrows;

@WireMockTest
class ODatav2BatchConnectionTest
{
    private static final String RESPONSE_CONTENT_TYPE =
        "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
    private static final String RESPONSE_WITH_CHANGESET = readResourceFileCrlf("BatchResponseWithChangeset.txt");
    private static final String RESPONSE_WITHOUT_CHANGESET = readResourceFileCrlf("BatchResponseWithoutChangeset.txt");
    private static final String RESPONSE_WITH_ERROR = readResourceFileCrlf("BatchResponseWithError.txt");
    private static final int MAX_PARALLEL_CONNECTIONS = 10;
    private DefaultHttpDestination destination;

    @SneakyThrows
    private static String readResourceFileCrlf( final String resourceFileName )
    {
        final URL resourceUrl =
            ODatav2BatchConnectionTest.class
                .getClassLoader()
                .getResource(ODatav2BatchConnectionTest.class.getSimpleName() + "/" + resourceFileName);
        final String result = Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        return result.replaceAll("(?<!\\r)\\n", "" + ((char) 13) + (char) 10);
    }

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
            final TestEntityRead read = new TestEntityRead();
            final TestEntityByKey readByKey = new TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));
            final TestVdmEntityBatch request =
                new TestVdmEntityBatch("").addReadOperations(read).addReadOperations(readByKey);
            final BatchResponse batchResponse = request.executeRequest(destination);
            final List<TestVdmEntity> result = batchResponse.getReadResult(read);
            assertThat(result).isNotEmpty();
            final TestVdmEntity readByKeyResult = batchResponse.getReadResult(readByKey);
            assertThat(readByKeyResult).extracting(TestVdmEntity::getIntegerValue).isEqualTo(9000);
        }
    }

    @Test
    void testNoConnectionTimeoutWhenBatchResponseContainsChangeset()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_CHANGESET)));

        final TestEntityRead read = new TestEntityRead();
        final TestEntityByKey readByKey = new TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));
        final TestEntityCreate create = new TestEntityCreate(TestVdmEntity.builder().integerValue(1337).build());
        final TestVdmEntity updateEntity = TestVdmEntity.builder().integerValue(13).build();
        final TestEntityUpdate update = new TestEntityUpdate(updateEntity);
        final TestVdmEntity deleteEntity = TestVdmEntity.builder().integerValue(14).build();
        final TestEntityDelete delete = new TestEntityDelete(deleteEntity);
        final TestEntityCreate createAnother = new TestEntityCreate(TestVdmEntity.builder().integerValue(1447).build());

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            final TestVdmEntityBatch request =
                new TestVdmEntityBatch("")
                    .addReadOperations(read)
                    .addChangeSet(create)
                    .addReadOperations(readByKey)
                    .addChangeSet(update, delete, createAnother);
            final BatchResponse batchResponse = request.executeRequest(destination);
            final List<TestVdmEntity> result = batchResponse.getReadResult(read);
            assertThat(result).isNotEmpty();

            final TestVdmEntity readByKeyResult = batchResponse.getReadResult(readByKey);
            assertThat(readByKeyResult).extracting(TestVdmEntity::getIntegerValue).isEqualTo(9000);

            assertThat(batchResponse.get(0).isSuccess()).isTrue();
            assertThat(batchResponse.get(0).get().getCreatedEntities()).hasSize(1);
            assertThat(batchResponse.get(1).isSuccess()).isTrue();
            assertThat(batchResponse.get(1).get().getCreatedEntities()).hasSize(1);
        }
    }

    @Test
    void testNoConnectionTimeoutWhenBatchResponseContainsError()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_ERROR)));

        final TestEntityRead read = new TestEntityRead();
        final TestEntityByKey readByKey = new TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));
        final TestVdmEntity deleteEntity = TestVdmEntity.builder().integerValue(14).build();
        final TestEntityDelete delete = new TestEntityDelete(deleteEntity);

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            final TestVdmEntityBatch request =
                new TestVdmEntityBatch("").addReadOperations(read).addReadOperations(readByKey).addChangeSet(delete);
            final BatchResponse batchResponse = request.executeRequest(destination);
            final List<TestVdmEntity> result = batchResponse.getReadResult(read);
            assertThat(result).isNotEmpty();
            final TestVdmEntity readByKeyResult = batchResponse.getReadResult(readByKey);
            assertThat(readByKeyResult).extracting(TestVdmEntity::getIntegerValue).isEqualTo(9000);
            assertThat(batchResponse.get(0).isSuccess()).isFalse();
            assertThat(batchResponse.get(0).getCause()).isInstanceOfAny(ODataServiceErrorException.class);
        }
    }

    @Test
    @Timeout( value = 300_000L, unit = TimeUnit.MILLISECONDS )
    @Disabled( "Test triggers a ConnectionPoolTimeoutException. Use it only to manually verify behaviour." )
    void testConnectionTimeoutWhenBatchResponseIsNotConsumedFully()
    {
        stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_CHANGESET)));

        final TestEntityRead read = new TestEntityRead();
        final TestEntityByKey readByKey = new TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));
        final TestEntityCreate create = new TestEntityCreate(TestVdmEntity.builder().integerValue(1337).build());
        final TestVdmEntity updateEntity = TestVdmEntity.builder().integerValue(13).build();
        final TestEntityUpdate update = new TestEntityUpdate(updateEntity);
        final TestVdmEntity deleteEntity = TestVdmEntity.builder().integerValue(14).build();
        final TestEntityDelete delete = new TestEntityDelete(deleteEntity);
        final TestEntityCreate createAnother = new TestEntityCreate(TestVdmEntity.builder().integerValue(1447).build());
        final TestVdmEntityBatch request =
            new TestVdmEntityBatch("")
                .addReadOperations(read)
                .addChangeSet(create)
                .addReadOperations(readByKey)
                .addChangeSet(update, delete, createAnother);

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS; i++ ) {
            assertThatNoException().isThrownBy(() -> request.executeRequest(destination));
        }

        assertThatThrownBy(() -> request.executeRequest(destination))
            .isInstanceOf(ODataConnectionException.class)
            .hasRootCauseExactlyInstanceOf(ConnectionPoolTimeoutException.class)
            .hasMessageContaining(
                "Please execute your request with try-with-resources to ensure resources are properly closed.");
    }
}
