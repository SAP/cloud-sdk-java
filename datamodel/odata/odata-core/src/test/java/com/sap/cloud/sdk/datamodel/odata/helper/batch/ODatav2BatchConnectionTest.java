package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
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

import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
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

public class ODatav2BatchConnectionTest
{
    private static final String RESPONSE_CONTENT_TYPE =
        "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
    private static final String RESPONSE_WITH_CHANGESET = readResourceFileCrlf("BatchResponseWithChangeset.txt");
    private static final String RESPONSE_WITHOUT_CHANGESET = readResourceFileCrlf("BatchResponseWithoutChangeset.txt");
    private static final String RESPONSE_WITH_ERROR = readResourceFileCrlf("BatchResponseWithError.txt");
    private static final int MAX_PARALLEL_CONNECTIONS = 10;
    private WireMockServer server;
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

    @Before
    public void setup()
    {
        server = new WireMockRule(wireMockConfig().dynamicPort());
        server.start();
        server.stubFor(head(UrlPattern.ANY).willReturn(noContent()));
        destination = DefaultHttpDestination.builder(server.baseUrl()).build();
        HttpClientAccessor
            .setHttpClientFactory(
                DefaultHttpClientFactory
                    .builder()
                    .maxConnectionsTotal(MAX_PARALLEL_CONNECTIONS)
                    .maxConnectionsPerRoute(MAX_PARALLEL_CONNECTIONS)
                    .build());
    }

    @After
    public void teardown()
    {
        server.shutdown();
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @Test
    public void testNoConnectionTimeoutWhenBatchResponseContainsNoChangeset()
    {
        server
            .stubFor(
                post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITHOUT_CHANGESET)));

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
    public void testNoConnectionTimeoutWhenBatchResponseContainsChangeset()
    {
        server
            .stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_CHANGESET)));

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
    public void testNoConnectionTimeoutWhenBatchResponseContainsError()
    {
        server.stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_ERROR)));

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

    @Test( timeout = 300_000L )
    @Ignore( "Test triggers a ConnectionPoolTimeoutException. Use it only to manually verify behaviour." )
    public void testConnectionTimeoutWhenBatchResponseIsNotConsumedFully()
    {
        server
            .stubFor(post(UrlPattern.ANY).willReturn(okForContentType(RESPONSE_CONTENT_TYPE, RESPONSE_WITH_CHANGESET)));

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
