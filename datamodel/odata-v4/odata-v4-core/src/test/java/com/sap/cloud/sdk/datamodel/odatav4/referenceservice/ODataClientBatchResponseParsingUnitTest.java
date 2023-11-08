/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol.V4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.google.common.base.Objects;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestReadByKey;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.TestUtility;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ODataClientBatchResponseParsingUnitTest
{
    private static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";
    private static final String X_CSRF_TOKEN_HEADER_VALUE = "awesome-csrf-token";

    private final AtomicInteger uuidCounter = new AtomicInteger(0);
    private final Supplier<UUID> uuidProvider = () -> new UUID(0, uuidCounter.incrementAndGet());

    @Test
    public void testEmptyBatch()
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchEmptyRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchEmptyResponse.txt");

        // Prepare test objects
        final ODataRequestBatch batchRequest = new ODataRequestBatch("/", V4, uuidProvider);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // extract unrequested read result
        final ODataRequestRead read = new ODataRequestRead("/", "People", "$top=1", V4);
        assertThatCode(() -> batchResponse.getResult(read)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testBatchWithOnlyReads()
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchOnlyReadsRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchOnlyReadsResponse.txt");

        // Prepare test objects
        final ODataRequestRead read1 = new ODataRequestRead("/", "People", "$top=1", V4);
        final ODataRequestRead read2 = new ODataRequestRead("/", "People", "$top=2&$skip=1", V4);
        final ODataRequestBatch batchRequest =
            new ODataRequestBatch("/", V4, uuidProvider).addRead(read1).addRead(read2);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion: response payload can be extracted
        final List<Person> resultRead1 = batchResponse.getResult(read1).asList(Person.class);
        final List<Person> resultRead2 = batchResponse.getResult(read2).asList(Person.class);
        assertThat(resultRead1).isNotNull().hasSize(1).doesNotContainNull().doesNotContainAnyElementsOf(resultRead2);
        assertThat(resultRead2).isNotNull().hasSize(2).doesNotContainNull().doesNotContainAnyElementsOf(resultRead1);
    }

    @Test
    public void testBatchWithReadsOnMissingResponse()
    {
        // 2 requests but only 1 response
        final String requestBody = readResourceFileCrlf("BatchOnlyReadsMissingRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchOnlyReadsMissingResponse.txt");

        // Prepare test objects
        final ODataEntityKey entityKey1 = new ODataEntityKey(V4).addKeyProperty("key", "one");
        final ODataRequestReadByKey readByKey1 = new ODataRequestReadByKey("/", "People", entityKey1, "", V4);
        final ODataEntityKey entityKey2 = new ODataEntityKey(V4).addKeyProperty("key", "two");
        final ODataRequestReadByKey readByKey2 = new ODataRequestReadByKey("/", "People", entityKey2, "", V4);
        final ODataEntityKey entityKey3 = new ODataEntityKey(V4).addKeyProperty("key", "three");
        final ODataRequestReadByKey readByKey3 = new ODataRequestReadByKey("/", "People", entityKey3, "", V4);
        final ODataRequestBatch batchRequest =
            new ODataRequestBatch("/", V4, uuidProvider)
                .addReadByKey(readByKey1)
                .addReadByKey(readByKey2)
                .addReadByKey(readByKey3);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion:
        // response payload1 is 200
        assertThat(batchResponse.getResult(readByKey1)).isNotNull();

        // response payload2 is 404
        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> batchResponse.getResult(readByKey2))
            .satisfies(e -> assertThat(e.getHttpCode()).isEqualTo(404));

        // response payload3 cannot be extracted, response is missing
        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> batchResponse.getResult(readByKey3))
            .withMessage("Unable to extract batch response item at position 3. The response contains only 2 items.");
    }

    @Test
    public void testBatchWithErrorReads()
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchOnlyReadsRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchOnlyReadsErrorResponse.txt");

        // Prepare test objects
        final ODataRequestRead read1 = new ODataRequestRead("/", "People", "$top=1", V4);
        final ODataRequestRead read2 = new ODataRequestRead("/", "People", "$top=2&$skip=1", V4);
        final ODataRequestBatch batchRequest =
            new ODataRequestBatch("/", V4, uuidProvider).addRead(read1).addRead(read2);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion: response payload can be extracted
        assertThat(batchResponse.getResult(read1)).isNotNull();

        // batchResponse.getResult(read2); // expected error:
        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> batchResponse.getResult(read2))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getOdataError().getODataCode()).isEqualTo("ZCU/100");
            });
    }

    @Test
    public void testBatchWithReadsAndWrites()
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchReadsAndWritesSuccessRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchReadsAndWritesSuccessResponse.txt");

        // Prepare test objects
        final ODataRequestCreate create1 =
            new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe1\", \"FirstName\":\"John\"}", V4);
        final ODataRequestCreate create2 =
            new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe2\", \"FirstName\":\"John\"}", V4);
        final ODataEntityKey entityKey = new ODataEntityKey(V4).addKeyProperty("key", "foo");
        final ODataRequestReadByKey readByKey = new ODataRequestReadByKey("/", "People", entityKey, "", V4);
        final ODataRequestBatch batchRequest =
            new ODataRequestBatch("/", V4, uuidProvider)
                .beginChangeset()
                .addCreate(create1)
                .addCreate(create2)
                .endChangeset()
                .addReadByKey(readByKey);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion: response parsing
        final ODataRequestResultGeneric resultCreate1 = batchResponse.getResult(create1);
        assertThat(resultCreate1).isNotNull();
        assertThat(resultCreate1.as(Person.class)).isNotNull().extracting(Person::getUserName).isEqualTo("JohnDoe1");
        assertThat(resultCreate1.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(201);

        final ODataRequestResultGeneric resultCreate2 = batchResponse.getResult(create2);
        assertThat(resultCreate2).isNotNull();
        assertThat(resultCreate2.as(Person.class)).isNotNull().extracting(Person::getUserName).isEqualTo("JohnDoe2");
        assertThat(resultCreate2.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(201);

        assertThatExceptionOfType(ODataServiceErrorException.class)
            .isThrownBy(() -> batchResponse.getResult(readByKey))
            .satisfies(e -> assertThat(e.getHttpCode()).isEqualTo(404));
    }

    @Test
    public void testBatchWithErrorInChangeset()
        throws IOException
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchReadsAndWritesErrorRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchReadsAndWritesErrorResponse.txt");

        // Prepare test objects
        final ODataRequestCreate create1 =
            new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe1\", \"FirstName\":\"John\"}", V4);
        final ODataRequestCreate create2 = new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe2\"}", V4);
        final ODataEntityKey entityKey = new ODataEntityKey(V4).addKeyProperty("key", "klauskinski");
        final ODataRequestReadByKey readByKey = new ODataRequestReadByKey("/", "People", entityKey, "", V4);
        final ODataRequestBatch requestBatch =
            new ODataRequestBatch("/", V4, uuidProvider)
                .beginChangeset()
                .addCreate(create1)
                .addCreate(create2)
                .endChangeset()
                .addReadByKey(readByKey);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = requestBatch.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion: response parsing
        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> batchResponse.getResult(create1))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody().get()).contains("The FirstName field is required");
            });

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> batchResponse.getResult(create2))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody().get()).contains("The FirstName field is required");
            });

        final ODataRequestResultGeneric resultReadByKey = batchResponse.getResult(readByKey);
        assertThat(resultReadByKey).isNotNull();
        assertThat(resultReadByKey.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testBatchWithErrorInsteadOfChangeset()
        throws IOException
    {
        // Read OData response json
        final String requestBody = readResourceFileCrlf("BatchReadsAndWritesErrorRequest.txt");
        final String responseBody = readResourceFileCrlf("BatchReadsAndWritesErrorResponseWithoutChangeset.txt");

        // Prepare test objects
        final ODataRequestCreate create1 =
            new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe1\", \"FirstName\":\"John\"}", V4);
        final ODataRequestCreate create2 = new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe2\"}", V4);
        final ODataEntityKey entityKey = new ODataEntityKey(V4).addKeyProperty("key", "klauskinski");
        final ODataRequestReadByKey readByKey = new ODataRequestReadByKey("/", "People", entityKey, "", V4);
        final ODataRequestBatch batchRequest =
            new ODataRequestBatch("/", V4, uuidProvider)
                .beginChangeset()
                .addCreate(create1)
                .addCreate(create2)
                .endChangeset()
                .addReadByKey(readByKey);

        final HttpClient httpClient = MockedHttpClient.of(requestBody, responseBody);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        // Test assertion: response object not null and healthy
        assertThat(batchResponse).isNotNull();
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // Test assertion: response parsing
        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> batchResponse.getResult(create1))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody().get()).contains("The FirstName field is required");
            });

        assertThatExceptionOfType(ODataResponseException.class)
            .isThrownBy(() -> batchResponse.getResult(create2))
            .satisfies(e -> {
                assertThat(e.getHttpCode()).isEqualTo(400);
                assertThat(e.getHttpBody().get()).contains("The FirstName field is required");
            });

        final ODataRequestResultGeneric resultReadByKey = batchResponse.getResult(readByKey);
        assertThat(resultReadByKey).isNotNull();
        assertThat(resultReadByKey.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testBatchResultWithError()
    {
        // Prepare test objects
        final ODataRequestRead read = new ODataRequestRead("/", "People", "$top=1", V4);
        final ODataRequestBatch batchRequest = new ODataRequestBatch("/", V4, uuidProvider).addRead(read);

        final HttpClient httpClient = MockedHttpClient.of(null, null);
        final ODataRequestResultMultipartGeneric batchResponse = batchRequest.execute(httpClient);

        assertThat(batchResponse.getHttpResponse().getEntity()).isNull();

        // Extract requested read result - entity already consumed
        assertThatCode(() -> batchResponse.getResult(read)).isInstanceOf(ODataResponseException.class);
    }

    private static class MockedHttpClient
    {
        @Getter
        private final HttpClient httpClient = mock(HttpClient.class);

        @SneakyThrows
        static HttpClient of( @Nullable final String requestBody, @Nullable final String responseBody )
        {
            final StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
            final HttpResponse odataResponse = new BasicHttpResponse(statusLine);
            if( responseBody != null ) {
                odataResponse.setEntity(new StringEntity(responseBody));
                final String batchDelimiter = responseBody.substring(2, responseBody.indexOf("\r"));
                odataResponse.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/mixed; boundary=" + batchDelimiter);
            }

            final HttpResponse csrfResponse = new BasicHttpResponse(statusLine);
            csrfResponse.setHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_VALUE);

            final HttpClient httpClient = mock(HttpClient.class);
            doReturn(odataResponse).when(httpClient).execute(argThat(req -> isCorrectODataRequest(req, requestBody)));
            doReturn(csrfResponse).when(httpClient).execute(argThat(MockedHttpClient::isCorrectCsrfRequest));

            return httpClient;
        }

        static private boolean isCorrectCsrfRequest( @Nonnull final HttpUriRequest request )
        {
            return request instanceof HttpHead
                && X_CSRF_TOKEN_HEADER_FETCH_VALUE
                    .equalsIgnoreCase(request.getFirstHeader(X_CSRF_TOKEN_HEADER_KEY).getValue());
        }

        static private
            boolean
            isCorrectODataRequest( @Nonnull final HttpUriRequest request, @Nullable final String assertRequestBody )
        {
            if( !(request instanceof HttpPost) ) {
                log.error("Expected HTTP POST request.");
                return false;
            }

            if( !X_CSRF_TOKEN_HEADER_VALUE.equals(request.getFirstHeader(X_CSRF_TOKEN_HEADER_KEY).getValue()) ) {
                log.error("CSRF token header is invalid.");
                return false;
            }

            final Header requestedContentType = request.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            final String expectedContentType = "multipart/mixed;boundary=batch_00000000-0000-0000-0000-000000000001";
            if( !requestedContentType.getValue().equals(expectedContentType) ) {
                log.error("Expected content type: {}", expectedContentType);
                return false;
            }

            if( assertRequestBody == null ) {
                return true;
            }

            final String requestCont = Try.of(() -> EntityUtils.toString(((HttpPost) request).getEntity())).getOrNull();
            if( !Objects.equal(requestCont, assertRequestBody) ) {
                log.error("Expected request content: {}, but got {}", assertRequestBody, requestCont);
                return false;
            }

            return true;
        }
    }

    private static String readResourceFileCrlf( final String file )
    {
        return TestUtility.readResourceFileCrlf(ODataClientBatchResponseParsingUnitTest.class, file);
    }
}
