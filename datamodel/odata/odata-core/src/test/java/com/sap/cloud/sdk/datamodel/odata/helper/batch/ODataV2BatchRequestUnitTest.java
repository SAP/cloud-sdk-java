/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToIgnoreCase;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odata.helper.TestVdmEntity;

import lombok.SneakyThrows;
import lombok.Value;

@Isolated
class ODataV2BatchRequestUnitTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";
    private static final String REQUEST_URL_BATCH = "/$batch";

    private static final String REQUEST_BODY = readResourceFileCrlf("BatchRequest.txt");
    private static final String REQUEST_BODY_WITH_CUSTOM_HEADERS =
        readResourceFileCrlf("BatchRequestWithCustomHeaders.txt");
    private static final String RESPONSE_BODY = readResourceFileCrlf("BatchResponse.txt");
    private static final int MAX_PARALLEL_CONNECTIONS = 10;

    private final WireMockServer server = new WireMockServer(WIREMOCK_CONFIGURATION);
    private DefaultHttpDestination destination;

    private static Stream<TestParameter> getTestParameters()
    {
        return Stream.of(new TestParameter("#executeRequest", BatchFluentHelperBasic::executeRequest, REQUEST_BODY));
    }

    private static Stream<TestParameter> getTestParametersWithCustomHeaders()
    {
        return Stream
            .of(
                new TestParameter(
                    "#executeRequest",
                    BatchFluentHelperBasic::executeRequest,
                    REQUEST_BODY_WITH_CUSTOM_HEADERS));
    }

    @Value
    static class TestParameter
    {
        String label;
        Executor executor;
        String requestBody;

        public String toString()
        {
            return label;
        }

        interface Executor
        {
            BatchResponse execute( BatchFluentHelperBasic<?, ?> fluentHelper, HttpDestinationProperties destination )
                throws Exception;
        }
    }

    @BeforeEach
    void setup()
    {
        server.start();
        destination = DefaultHttpDestination.builder(server.baseUrl()).build();

        // Mock CSRF token handling
        server
            .stubFor(
                head(urlEqualTo("/"))
                    .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase(X_CSRF_TOKEN_HEADER_FETCH_VALUE))
                    .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));

        // Mock OData Batch response
        final String contentType = "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        server.stubFor(post(urlEqualTo(REQUEST_URL_BATCH)).willReturn(okForContentType(contentType, RESPONSE_BODY)));

        HttpClientAccessor
            .setHttpClientFactory(
                DefaultHttpClientFactory
                    .builder()
                    .maxConnectionsTotal(MAX_PARALLEL_CONNECTIONS)
                    .maxConnectionsPerRoute(MAX_PARALLEL_CONNECTIONS)
                    .build());
    }

    @AfterEach
    void shutdown()
    {
        server.shutdown();
        HttpClientAccessor.setHttpClientFactory(null);
    }

    @SneakyThrows
    @ParameterizedTest( name = "{0}" )
    @MethodSource( "getTestParameters" )
    void testAllOperations( @Nonnull final TestParameter parameter )
    {
        final TestVdmEntity entity12 = TestVdmEntity.builder().integerValue(12).build();
        final TestVdmEntity entity13 = TestVdmEntity.builder().integerValue(13).build();
        final TestVdmEntity entity14 = TestVdmEntity.builder().integerValue(14).build();
        final TestVdmEntity entity15 = TestVdmEntity.builder().integerValue(15).build();
        final TestVdmEntity entity16 = TestVdmEntity.builder().integerValue(16).build();

        final TestVdmEntityBatch.TestEntityRead readAll =
            new TestVdmEntityBatch.TestEntityRead().select(() -> "StringValue", () -> "IntegerValue").top(10);
        final TestVdmEntityBatch.TestEntityByKey readByKey =
            new TestVdmEntityBatch.TestEntityByKey(ImmutableMap.of("IntegerValue", 9000));

        // prepare batched requests
        final TestVdmEntityBatch request =
            new TestVdmEntityBatch("")

                // read operation
                .addReadOperations(readAll)

                // changeset:0
                .beginChangeSet()
                .create(entity12)
                .endChangeSet()

                // read operation
                .addReadOperations(readByKey)

                // changeset:1
                .beginChangeSet()
                .update(entity13)
                .delete(entity14)
                .endChangeSet()

                // changeset:2
                .beginChangeSet()
                .delete(entity15)
                .delete(entity16)
                .endChangeSet();

        // execute batch request with try-with-resources to ensure no connection leaks
        for( int i = 0; i <= MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            try( final BatchResponse response = request.executeRequest(destination) ) {
                verifyBatchChangeSets(response);
                verifyBatchReadAll(response, readAll);
                verifyBatchReadByKey(response, readByKey);
                verifyBatchRequestBody(parameter);
            }
        }
    }

    @SneakyThrows
    @ParameterizedTest( name = "{0}" )
    @MethodSource( "getTestParametersWithCustomHeaders" )
    void testAllOperationsWithCustomHeaders( @Nonnull final TestParameter parameter )
    {
        final TestVdmEntity entity12 = TestVdmEntity.builder().integerValue(12).build();
        final TestVdmEntity entity13 = TestVdmEntity.builder().integerValue(13).build();
        final TestVdmEntity entity14 = TestVdmEntity.builder().integerValue(14).build();
        final TestVdmEntity entity15 = TestVdmEntity.builder().integerValue(15).build();
        final TestVdmEntity entity16 = TestVdmEntity.builder().integerValue(16).build();

        final TestVdmEntityBatch.TestEntityRead readAll =
            new TestVdmEntityBatch.TestEntityRead()
                .select(() -> "StringValue", () -> "IntegerValue")
                .top(10)
                .withHeader("header-read_all", "read_all");
        final TestVdmEntityBatch.TestEntityByKey readByKey =
            new TestVdmEntityBatch.TestEntityByKey(ImmutableMap.of("IntegerValue", 9000))
                .withHeader("header-read_by_key", "read_by_key");
        final TestVdmEntityBatch.TestEntityCreate create =
            new TestVdmEntityBatch.TestEntityCreate(entity12).withHeader("header-create", "create");
        final TestVdmEntityBatch.TestEntityUpdate update =
            new TestVdmEntityBatch.TestEntityUpdate(entity13).withHeader("header-update", "update");
        final TestVdmEntityBatch.TestEntityDelete deleteEntity14 =
            new TestVdmEntityBatch.TestEntityDelete(entity14).withHeader("header-delete", "delete-entity14");
        final TestVdmEntityBatch.TestEntityDelete deleteEntity15 =
            new TestVdmEntityBatch.TestEntityDelete(entity15).withHeader("header-delete", "delete-entity15");
        final TestVdmEntityBatch.TestEntityDelete deleteEntity16 =
            new TestVdmEntityBatch.TestEntityDelete(entity16).withHeader("header-delete", "delete-entity16");

        // prepare batched requests
        final TestVdmEntityBatch request =
            new TestVdmEntityBatch("")

                // read operation
                .addReadOperations(readAll)

                // changeset:0
                .addChangeSet(create)

                // read operation
                .addReadOperations(readByKey)

                // changeset:1
                .addChangeSet(update, deleteEntity14)

                // changeset:2
                .addChangeSet(deleteEntity15, deleteEntity16);

        for( int i = 0; i < MAX_PARALLEL_CONNECTIONS * 2; i++ ) {
            // execute batch request
            final BatchResponse response = parameter.getExecutor().execute(request, destination);

            verifyBatchChangeSets(response);
            verifyBatchReadAll(response, readAll);
            verifyBatchReadByKey(response, readByKey);
            verifyBatchRequestBody(parameter);
        }
    }

    private void verifyBatchChangeSets( @Nonnull final BatchResponse response )
    {
        // assertion on response parsing
        assertThat(response.get(0).isSuccess()).isTrue();
        assertThat(response.get(0).get().getCreatedEntities()).hasSize(1);
        assertThat(response.get(1).isSuccess()).isTrue();
        assertThat(response.get(1).get().getCreatedEntities()).hasSize(0);

        assertThat(response.get(2).isSuccess()).isFalse(); // legitimately no success: unhealthy response
        assertThat(response.get(2).getCause()).isInstanceOfAny(ODataServiceErrorException.class);
        assertThat(response.get(3).isSuccess()).isFalse(); // index out of bounds
        assertThat(response.get(3).getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyBatchReadAll(
        @Nonnull final BatchResponse response,
        @Nonnull final TestVdmEntityBatch.TestEntityRead readAll )
    {
        assertThat(response.getReadResult(readAll))
            .satisfiesExactly(item -> assertThat(item.getStringValue()).isEqualTo("Foo"));
    }

    private void verifyBatchReadByKey(
        @Nonnull final BatchResponse response,
        @Nonnull final TestVdmEntityBatch.TestEntityByKey readByKey )
    {
        assertThat(response.getReadResult(readByKey)).extracting(TestVdmEntity::getIntegerValue).isEqualTo(9000);
    }

    private void verifyBatchRequestBody( @Nonnull final TestParameter parameter )
    {
        server
            .verify(
                postRequestedFor(urlEqualTo(REQUEST_URL_BATCH))
                    .withHeader("Content-Type", matching("multipart/mixed;boundary=batch_[a-z0-9-]+"))
                    .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase(X_CSRF_TOKEN_HEADER_VALUE))
                    .withRequestBody(matching("\\Q" + parameter.getRequestBody() + "\\E")));
    }

    @Test
    void testBatchWithoutCsrfTokenIfSkipped()
    {
        new TestVdmEntityBatch("/").withoutCsrfToken().executeRequest(destination);

        // NOT SUPPORTED - Service SDK does not allow for disabling CSRF token fetching.
        // new TestVdmEntityBatch("/").withoutCsrfToken().execute(destination);

        server
            .verify(
                0,
                headRequestedFor(anyUrl())
                    .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalToIgnoreCase(X_CSRF_TOKEN_HEADER_FETCH_VALUE)));
    }

    @SneakyThrows
    private static String readResourceFileCrlf( final String resourceFileName )
    {
        final URL resourceUrl =
            ODataV2BatchRequestUnitTest.class
                .getClassLoader()
                .getResource(ODataV2BatchRequestUnitTest.class.getSimpleName() + "/" + resourceFileName);
        final String result = Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        return result.replaceAll("(?<!\\r)\\n", "" + ((char) 13) + (char) 10);
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
            new TestVdmEntityBatch("").addReadOperations(new TestVdmEntityBatch.TestEntityRead()).executeRequest(dest);
        }

        // ASSERTION: input streams are loaded but never fully consumed
        assertThat(inputStreams).hasSize(N);
        for( final InputStream inStream : inputStreams ) {
            Mockito.verify(inStream, never()).close();
        }

        // TEST: invoke one batch request using try-with-resource
        try(
            BatchResponse result =
                new TestVdmEntityBatch("")
                    .addReadOperations(new TestVdmEntityBatch.TestEntityRead())
                    .executeRequest(dest) ) {

            assertThat(result).isNotNull();
        }

        // ASSERTION: input stream is fully consumed
        Mockito.verify(inputStreams.get(N), times(1)).close();
    }
}
