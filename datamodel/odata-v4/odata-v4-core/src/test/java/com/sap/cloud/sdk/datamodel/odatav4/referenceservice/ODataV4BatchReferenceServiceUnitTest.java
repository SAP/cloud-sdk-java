package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sap.cloud.sdk.datamodel.odatav4.TestUtility.readResourceFileCrlf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.BatchResponse;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetAllRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.core.GetByKeyRequestBuilder;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.DefaultTrippinService;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.services.TrippinService;

@WireMockTest
class ODataV4BatchReferenceServiceUnitTest
{
    private static final WireMockConfiguration WIREMOCK_CONFIGURATION = wireMockConfig().dynamicPort();

    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";

    private DefaultHttpDestination destination;
    private TrippinService service;
    private BatchRequestBuilder sut;

    @BeforeEach
    void setup( @Nonnull final WireMockRuntimeInfo wm )
    {
        destination = DefaultHttpDestination.builder(wm.getHttpBaseUrl()).build();
        service = new DefaultTrippinService();

        final AtomicInteger uuidCounter = new AtomicInteger();

        sut = new BatchRequestBuilder(TrippinService.DEFAULT_SERVICE_PATH)
        {
            @Override
            protected Supplier<UUID> getUuidProvider()
            {
                return () -> new UUID(0, uuidCounter.incrementAndGet());
            }
        };

        // mock CSRF token retrieval
        stubFor(head(anyUrl()).willReturn(ok()));
        stubFor(
            head(urlEqualTo("/"))
                .withHeader(X_CSRF_TOKEN_HEADER_KEY, equalTo(X_CSRF_TOKEN_HEADER_FETCH_VALUE))
                .willReturn(ok().withHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE)));
    }

    @Test
    void testEmptyBatch()
    {
        // Read OData response json
        final String requestBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchEmptyRequest.txt");
        final String responseBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchEmptyResponse.txt");
        final String batchRequestUrl = String.format("%s%s", TrippinService.DEFAULT_SERVICE_PATH, "/$batch");

        // Mocking OData response
        final String responseContentType =
            "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(
            post(urlEqualTo(batchRequestUrl))
                .willReturn(okForContentType(responseContentType, responseBody).withStatus(HttpStatus.SC_ACCEPTED)));

        // Run payload
        final BatchResponse batchResponse = sut.execute(destination);

        // Verify request body
        final String requestContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
        verify(
            postRequestedFor(urlEqualTo(batchRequestUrl))
                .withoutHeader(HttpHeaders.ACCEPT)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(requestContentType))
                .withRequestBody(equalTo(requestBody)));

        // Test assertion: response object not null and healthy
        assertThat(batchResponse.getResponseStatusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
        assertThat(batchResponse.getResponseHeaders()).isNotEmpty();
    }

    @Test
    void testBatchWithOnlyReads()
    {
        // Read OData response json
        final String requestBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchOnlyReadsRequest.txt");
        final String responseBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchOnlyReadsResponse.txt");
        final String batchRequestUrl = String.format("%s%s", TrippinService.DEFAULT_SERVICE_PATH, "/$batch");

        // Mocking OData response
        final String responseContentType =
            "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(
            post(urlEqualTo(batchRequestUrl))
                .willReturn(okForContentType(responseContentType, responseBody).withStatus(HttpStatus.SC_ACCEPTED)));

        // Prepare test objects
        final GetAllRequestBuilder<Person> getAll1 = service.getAllPeople().top(1);
        final GetAllRequestBuilder<Person> getAll2 = service.getAllPeople().top(2).skip(1);

        // build batch request
        final BatchResponse batchResponse = sut.addReadOperations(getAll1, getAll2).execute(destination);

        // Verify request body
        final String requestContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
        verify(
            postRequestedFor(urlEqualTo(batchRequestUrl))
                .withoutHeader(HttpHeaders.ACCEPT)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(requestContentType))
                .withRequestBody(equalTo(requestBody)));

        // Test assertion: response object not null and healthy
        assertThat(batchResponse.getResponseStatusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
        assertThat(batchResponse.getResponseHeaders()).isNotEmpty();
    }

    @Test
    void testBatchWithErrorReads()
    {
        // Read OData response json
        final String requestBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchOnlyReadsRequest.txt");
        final String responseBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchOnlyReadsErrorResponse.txt");
        final String batchRequestUrl = String.format("%s%s", TrippinService.DEFAULT_SERVICE_PATH, "/$batch");

        // Mocking OData response
        final String responseContentType =
            "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";
        stubFor(
            post(urlEqualTo(batchRequestUrl))
                .willReturn(okForContentType(responseContentType, responseBody).withStatus(HttpStatus.SC_ACCEPTED)));

        // Prepare test objects
        final GetAllRequestBuilder<Person> getAll1 = service.getAllPeople().top(1);
        final GetAllRequestBuilder<Person> getAll2 = service.getAllPeople().top(2).skip(1);

        // build batch request
        final BatchResponse batchResponse = sut.addReadOperations(getAll1, getAll2).execute(destination);

        assertThat(batchResponse.getReadResult(getAll1)).isNotEmpty();

        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> batchResponse.getReadResult(getAll2))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getOdataError().getODataCode()).isEqualTo("ZCU/100");
            });

        // Verify request body
        final String requestContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
        verify(
            postRequestedFor(urlEqualTo(batchRequestUrl))
                .withoutHeader(HttpHeaders.ACCEPT)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(requestContentType))
                .withRequestBody(equalTo(requestBody)));

        // Test assertion: response object not null and healthy
        assertThat(batchResponse.getResponseStatusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
        assertThat(batchResponse.getResponseHeaders()).isNotEmpty();
    }

    @Test
    void testBatchWithReadsAndWrites()
    {
        // read response json
        final String serverResponse =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchReadsAndWritesSuccessResponse.txt");
        final String batchRequestBody =
            readResourceFileCrlf(ODataV4BatchReferenceServiceUnitTest.class, "BatchReadsAndWritesSuccessRequest.txt");
        final String batchRequestUrl = String.format("%s%s", TrippinService.DEFAULT_SERVICE_PATH, "/$batch");

        final String responseContentType =
            "multipart/mixed; boundary=batchresponse_76ef6b0a-a0e2-4f31-9f70-f5d3f73a6bef";

        // Mocking S/4 Hana
        stubFor(
            post(urlEqualTo(batchRequestUrl))
                .willReturn(okForContentType(responseContentType, serverResponse).withStatus(HttpStatus.SC_ACCEPTED)));

        Person person1 = Person.builder().userName("JohnDoe1").firstName("John").build();
        Person person2 = Person.builder().userName("JohnDoe2").firstName("John").build();

        // prepare test objects
        final CreateRequestBuilder<Person> createPersonRequest1 = service.createPeople(person1);
        final CreateRequestBuilder<Person> createPersonRequest2 = service.createPeople(person2);
        final GetByKeyRequestBuilder<Person> getPersonByKeyRequest = service.getPeopleByKey("foo");

        // build batch request
        final BatchResponse batchResponse =
            sut
                .addChangeset(createPersonRequest1, createPersonRequest2)
                .addReadOperations(getPersonByKeyRequest)
                .execute(destination);

        assertThat(batchResponse.getResponseStatusCode()).isEqualTo(HttpStatus.SC_ACCEPTED);
        assertThat(batchResponse.getResponseHeaders()).isNotEmpty();

        // verify request body
        final String requestContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
        verify(
            postRequestedFor(urlEqualTo(batchRequestUrl))
                .withoutHeader(HttpHeaders.ACCEPT)
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(requestContentType))
                .withRequestBody(equalTo(batchRequestBody)));
    }
}
