/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.io.Resources;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataConnectionException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;

class ODataClientQueryBatchUnitTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();
    private static final String SERVICE_PATH = "/service/";
    private static final String ENTITY_COLLECTION = "Entity";
    private static final String SERVICE_PATH_BATCH = SERVICE_PATH + "$batch";
    private static final ODataEntityKey ENTITY_KEY =
        new ODataEntityKey(ODataProtocol.V4).addKeyProperty("key", "the-key#&!%");

    private static ODataRequestDelete SAMPLE_REQUEST_DELETE;
    private static ODataRequestRead SAMPLE_REQUEST_READ_MULTIPLE;
    private static ODataRequestReadByKey SAMPLE_REQUEST_READ_BY_KEY;
    private static ODataRequestCreate SAMPLE_REQUEST_CREATE;
    private static ODataRequestUpdate SAMPLE_REQUEST_UPDATE;

    private WireMockServer wireMockServer;
    private Destination destination;
    private final AtomicInteger uuidCounter = new AtomicInteger(0);
    private final Supplier<UUID> uuidProvider = () -> new UUID(0, uuidCounter.incrementAndGet());

    @BeforeEach
    void setup()
    {
        wireMockServer = new WireMockServer(WIREMOCK_CONFIGURATION);
        wireMockServer.start();
        wireMockServer
            .stubFor(head(urlPathEqualTo(SERVICE_PATH)).willReturn(noContent().withHeader("x-csrf-token", "foobar")));

        destination = DefaultHttpDestination.builder(wireMockServer.baseUrl()).build();
    }

    @AfterEach
    void teardown()
    {
        wireMockServer.stop();
    }

    @BeforeAll
    static void setupRequests()
    {
        SAMPLE_REQUEST_READ_MULTIPLE =
            new ODataRequestRead(SERVICE_PATH, ENTITY_COLLECTION, "$filter=Fieldname%20eq%20'hello'", ODataProtocol.V4);

        SAMPLE_REQUEST_READ_BY_KEY =
            new ODataRequestReadByKey(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, null, ODataProtocol.V4);

        SAMPLE_REQUEST_CREATE =
            new ODataRequestCreate(SERVICE_PATH, ENTITY_COLLECTION, "{\"foo\": \"bar\"}", ODataProtocol.V4);
        SAMPLE_REQUEST_CREATE.addHeader("Set-Cookie", "foo");
        SAMPLE_REQUEST_CREATE.addHeader("Set-Cookie", "bar");

        SAMPLE_REQUEST_UPDATE =
            new ODataRequestUpdate(
                SERVICE_PATH,
                ENTITY_COLLECTION,
                ENTITY_KEY,
                "{\"foo\": \"bar\"}",
                UpdateStrategy.MODIFY_WITH_PATCH,
                "version-identifier",
                ODataProtocol.V4);

        SAMPLE_REQUEST_DELETE =
            new ODataRequestDelete(SERVICE_PATH, ENTITY_COLLECTION, ENTITY_KEY, "version-identifier", ODataProtocol.V4);
    }

    @Test
    void testEmptyBatch()
    {
        final String requestBody =
            readResourceFileClrf(ODataClientQueryBatchUnitTest.class, "BatchEmptyRequestBody.txt");

        // create batch request
        final ODataRequestBatch request = new ODataRequestBatch(SERVICE_PATH, ODataProtocol.V4, uuidProvider);

        assertThat(request.getBatchRequestBody()).isEqualTo(requestBody);

        // check request execution
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer.stubFor(post(urlPathEqualTo(SERVICE_PATH_BATCH)).willReturn(okJson("{}")));

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer.verify(headRequestedFor(urlPathEqualTo(SERVICE_PATH)));
        wireMockServer
            .verify(
                postRequestedFor(urlPathEqualTo(SERVICE_PATH_BATCH))
                    .withRequestBody(equalTo(requestBody))
                    .withoutHeader("Accept")
                    .withHeader("Content-Type", containing("multipart/mixed;boundary=batch_"))
                    .withHeader("OData-Version", equalTo("4.0")));
    }

    @Test
    void testEmptyChangesetBatch()
    {
        final String requestBody =
            readResourceFileClrf(ODataClientQueryBatchUnitTest.class, "BatchEmptyChangesetRequestBody.txt");

        // create batch request
        final ODataRequestBatch request =
            new ODataRequestBatch(SERVICE_PATH, ODataProtocol.V4, uuidProvider).beginChangeset().endChangeset();

        assertThat(request.getBatchRequestBody()).isEqualTo(requestBody);

        // check request execution
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer.stubFor(post(urlPathEqualTo(SERVICE_PATH_BATCH)).willReturn(okJson("{}")));

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer.verify(headRequestedFor(urlPathEqualTo(SERVICE_PATH)));
        wireMockServer
            .verify(
                postRequestedFor(urlPathEqualTo(SERVICE_PATH_BATCH))
                    .withRequestBody(equalTo(requestBody))
                    .withoutHeader("Accept")
                    .withHeader("Content-Type", containing("multipart/mixed;boundary=batch_"))
                    .withHeader("OData-Version", equalTo("4.0")));
    }

    @Test
    void testReadOnlyBatch()
    {
        final String requestBody =
            readResourceFileClrf(ODataClientQueryBatchUnitTest.class, "BatchReadRequestBody.txt");

        // create batch request
        final ODataRequestBatch request =
            new ODataRequestBatch(SERVICE_PATH, ODataProtocol.V4, uuidProvider).addRead(SAMPLE_REQUEST_READ_MULTIPLE);

        assertThat(request.getBatchRequestBody()).isEqualTo(requestBody);

        // check request execution
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer.stubFor(post(urlPathEqualTo(SERVICE_PATH_BATCH)).willReturn(okJson("{}")));

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer.verify(headRequestedFor(urlPathEqualTo(SERVICE_PATH)));
        wireMockServer
            .verify(
                postRequestedFor(urlPathEqualTo(SERVICE_PATH_BATCH))
                    .withRequestBody(equalTo(requestBody))
                    .withoutHeader("Accept")
                    .withHeader("Content-Type", containing("multipart/mixed;boundary=batch_"))
                    .withHeader("OData-Version", equalTo("4.0")));
    }

    @Test
    @Disabled( "Test triggers a ConnectionPoolTimeoutException. Use it only to manually verify behaviour." )
    void testReadOnlyBatchForceConnectionLeaks()
    {
        final String requestBody =
            readResourceFileClrf(ODataClientQueryBatchUnitTest.class, "BatchReadRequestBody.txt");

        // create batch request
        final ODataRequestBatch request =
            new ODataRequestBatch(SERVICE_PATH, ODataProtocol.V4, uuidProvider).addRead(SAMPLE_REQUEST_READ_MULTIPLE);

        assertThat(request.getBatchRequestBody()).isEqualTo(requestBody);

        // check request execution
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer.stubFor(post(urlPathEqualTo(SERVICE_PATH_BATCH)).willReturn(okJson("{}")));

        try {
            //Try executing multiple batch requests re-using the same client to exhaust the connection pool
            for( int i = 0; i < 200; i++ ) {
                final ODataRequestResult result = request.execute(client);
                assertThat(result).isNotNull();
            }
        }
        catch( final Exception e ) {
            assertThat(e)
                .isInstanceOf(ODataConnectionException.class)
                .hasRootCauseExactlyInstanceOf(ConnectionPoolTimeoutException.class)
                .hasMessageContaining(
                    "Please execute your request with try-with-resources to ensure resources are properly closed.");
        }

    }

    @Test
    void testCombinedBatch()
    {
        final String requestBody = readResourceFileClrf(ODataClientQueryBatchUnitTest.class, "BatchAllRequestBody.txt");

        // create batch request
        final ODataRequestBatch request =
            new ODataRequestBatch(SERVICE_PATH, ODataProtocol.V4, uuidProvider)
                .addRead(SAMPLE_REQUEST_READ_MULTIPLE)
                .beginChangeset()
                .addCreate(SAMPLE_REQUEST_CREATE)
                .addUpdate(SAMPLE_REQUEST_UPDATE)
                .addDelete(SAMPLE_REQUEST_DELETE)
                .endChangeset()
                .addReadByKey(SAMPLE_REQUEST_READ_BY_KEY);

        assertThat(request.getBatchRequestBody()).isEqualTo(requestBody);

        // check request execution
        final HttpClient client = HttpClientAccessor.getHttpClient(destination);
        wireMockServer.stubFor(post(urlPathEqualTo(SERVICE_PATH_BATCH)).willReturn(okJson("{}")));

        final ODataRequestResult result = request.execute(client);
        assertThat(result).isNotNull();

        wireMockServer.verify(headRequestedFor(urlPathEqualTo(SERVICE_PATH)));
        wireMockServer
            .verify(
                postRequestedFor(urlPathEqualTo(SERVICE_PATH_BATCH))
                    .withRequestBody(equalTo(requestBody))
                    .withoutHeader("Accept")
                    .withHeader("Content-Type", containing("multipart/mixed;boundary=batch_"))
                    .withHeader("OData-Version", equalTo("4.0")));
    }

    @Test
    void testBatchErrorWithDifferentServicePath()
    {
        final HttpClient httpClient = mock(HttpClient.class);

        assertThatCode(
            () -> new ODataRequestBatch("this/", ODataProtocol.V4, uuidProvider)
                .addRead(new ODataRequestRead("this/", "People", "$top=1", ODataProtocol.V4))
                .addRead(new ODataRequestRead("other/", "People", "$top=2", ODataProtocol.V4))
                .execute(httpClient))
            .isInstanceOf(ODataRequestException.class);
    }

    @Test
    void testServicePathLacksLeadingSlashAndHasTrailingSlash()
    {
        final String servicePath = "service-path/";
        final String entityPath = "entity-path";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(servicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(servicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR)).isEqualTo("/service-path/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo(entityPath);
    }

    @Test
    void testServicePathHasLeadingSlashAndLacksTrailingSlash()
    {
        final String servicePath = "/service-path";
        final String entityPath = "entity-path";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(servicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(servicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR)).isEqualTo("/service-path/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo(entityPath);
    }

    @Test
    void testServicePathHasLeadingAndTrailingSlash()
    {
        final String servicePath = "/service-path/";
        final String entityPath = "entity-path";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(servicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(servicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR)).isEqualTo("/service-path/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo(entityPath);
    }

    @Test
    void testEntityPathContainsSpecialCharacter()
    {
        final String servicePath = "/service-path/";
        final String entityPath = "entity-päth";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(servicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(servicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR)).isEqualTo("/service-path/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo("entity-p%C3%A4th");
    }

    @Test
    void testServicePathContainsSpecialCharacter()
    {
        final String servicePath = "service-päth;v=001";
        final String entityPath = "entity-path";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(servicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(servicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR))
            .isEqualTo("/service-p%C3%A4th;v=001/");
        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.BATCH))
            .isEqualTo("/service-p%C3%A4th%3Bv%3D001/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo(entityPath);
    }

    @Test
    void testSingleRequestPathWithSpecialCharacterUnlikeBatchRequestServicePath()
    {
        assertThatExceptionOfType(ODataRequestException.class)
            .isThrownBy(
                () -> new ODataRequestBatch("service-path", ODataProtocol.V4, uuidProvider)
                    .beginChangeset()
                    .addCreate(new ODataRequestCreate("service-päth", "entity-path", "{}", ODataProtocol.V4))
                    .endChangeset());
    }

    @Test
    void testDifferentLeadingSlashesOnBatchAndSingleRequestServicePath()
    {
        final String batchRequestServicePath = "service-path";
        final String singleRequestServicePath = "/service-path";
        final String entityPath = "entity-path";

        final ODataRequestBatch batchRequest =
            new ODataRequestBatch(batchRequestServicePath, ODataProtocol.V4, uuidProvider)
                .beginChangeset()
                .addCreate(new ODataRequestCreate(singleRequestServicePath, entityPath, "{}", ODataProtocol.V4))
                .endChangeset();

        assertThat(batchRequest.getEncodedServicePath(UriEncodingStrategy.REGULAR)).isEqualTo("/service-path/");

        final ODataRequestBatch.BatchItemSingle singleRequest =
            ((ODataRequestBatch.BatchItemChangeset) batchRequest.getRequests().get(0)).getRequests().get(0);
        assertThat(singleRequest.getResourcePath()).isEqualTo(entityPath);
    }

    @Test
    void testBatchErrorWithDifferentServiceVersions()
    {
        final HttpClient httpClient = mock(HttpClient.class);

        assertThatCode(
            () -> new ODataRequestBatch("this/", ODataProtocol.V4, uuidProvider)
                .addRead(new ODataRequestRead("this/", "People", "$top=1", ODataProtocol.V2))
                .execute(httpClient))
            .isInstanceOf(ODataRequestException.class);
    }

    private static String readResourceFileClrf( final Class<?> cls, final String resourceFileName )
    {
        return readResourceFile(cls, resourceFileName).replaceAll("(?<!\\r)\\n", "" + ((char) 13) + (char) 10);
    }

    private static String readResourceFile( final Class<?> cls, final String resourceFileName )
    {
        try {
            final URL resourceUrl = cls.getClassLoader().getResource(cls.getSimpleName() + "/" + resourceFileName);
            return Resources.toString(resourceUrl, StandardCharsets.UTF_8);
        }
        catch( final IOException e ) {
            throw new IllegalStateException(e);
        }
    }
}
