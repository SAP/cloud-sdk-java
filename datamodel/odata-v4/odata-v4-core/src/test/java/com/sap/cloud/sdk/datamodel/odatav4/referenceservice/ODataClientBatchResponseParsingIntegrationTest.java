/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

@Disabled( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
class ODataClientBatchResponseParsingIntegrationTest
{
    private Destination httpDestination;

    @BeforeEach
    void configure()
        throws IOException
    {
        httpDestination = TripPinUtility.getDestination();
    }

    @Test
    void testEmptyBatch()
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4).execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // extract unrequested read result
        final ODataRequestRead read = new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4);
        assertThatCode(() -> batchResponse.getResult(read)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testBatchWithReads()
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestRead read1 = new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4);
        final ODataRequestRead read2 = new ODataRequestRead("/", "People", "$top=2&$skip=1", ODataProtocol.V4);
        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4).addRead(read1).addRead(read2).execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // response HTTP code is healthy
        assertThat(batchResponse.getHttpResponse().getStatusLine().getStatusCode()).isEqualTo(200);

        // response payload can be extracted
        final List<Person> people1 = batchResponse.getResult(read1).asList(Person.class);
        assertThat(people1).hasSize(1).doesNotContainNull();

        final List<Person> people2 = batchResponse.getResult(read2).asList(Person.class);
        assertThat(people2).hasSize(2).doesNotContainNull().doesNotContainAnyElementsOf(people1);
    }

    @Test
    void testBatchWithReadsAndWrites()
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestRead read1 = new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4);
        final ODataRequestRead read2 = new ODataRequestRead("/", "People", "$top=2&$skip=1", ODataProtocol.V4);

        final ODataRequestCreate create1 =
            new ODataRequestCreate(
                "/",
                "People",
                "{\"UserName\":\"JohnDoe1\", \"FirstName\":\"John\"}",
                ODataProtocol.V4);
        final ODataRequestCreate create2 =
            new ODataRequestCreate(
                "/",
                "People",
                "{\"UserName\":\"JohnDoe2\", \"FirstName\":\"John\"}",
                ODataProtocol.V4);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4)
                .addRead(read1)
                .beginChangeset()
                .addCreate(create1)
                .addCreate(create2)
                .endChangeset()
                .addRead(read2)
                .execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // response parsing
        final List<Person> resultRead1 = batchResponse.getResult(read1).asList(Person.class);
        final List<Person> resultRead2 = batchResponse.getResult(read2).asList(Person.class);
        assertThat(resultRead1).isNotNull().hasSize(1).doesNotContainNull().doesNotContainAnyElementsOf(resultRead2);
        assertThat(resultRead2).isNotNull().hasSize(2).doesNotContainNull().doesNotContainAnyElementsOf(resultRead1);

        assertThat(batchResponse.getResult(create1).as(Person.class)).isNotNull();
        assertThat(batchResponse.getResult(create2).as(Person.class)).isNotNull();

        // response HTTP code is healthy
        final HttpResponse httpResponse = batchResponse.getHttpResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    void testBatchResultWithError()
        throws IOException
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestRead read = new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4).addRead(read).execute(httpClient);

        // consume result
        EntityUtils.consume(batchResponse.getHttpResponse().getEntity());

        // extract unrequested read result
        assertThatCode(() -> batchResponse.getResult(read)).isInstanceOf(IllegalStateException.class);
    }
}
