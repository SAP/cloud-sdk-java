/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.referenceservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataRequestException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultMultipartGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.referenceservice.namespaces.trippin.Person;

@Ignore( "Test runs against a v4 reference service on odata.org. Use it only to manually verify behaviour." )
public class ODataClientBatchReferenceServiceIntegrationTest
{
    private Destination httpDestination;

    @Before
    public void configure()
        throws IOException
    {
        httpDestination = TripPinUtility.getDestination();
    }

    @Test
    public void testEmptyBatch()
        throws IOException
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4).execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // response HTTP code is healthy
        final HttpResponse httpResponse = batchResponse.getHttpResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);

        // response payload can be extracted
        final String response = EntityUtils.toString(batchResponse.getHttpResponse().getEntity());
        assertThat(response).matches("^--batchresponse_[a-f0-9-]+--\r\n$");
    }

    @Test
    public void testBatchWithSingleRead()
        throws IOException
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4)
                .addRead(new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4))
                .execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // response HTTP code is healthy
        final HttpResponse httpResponse = batchResponse.getHttpResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);

        // response payload can be extracted
        final String response = EntityUtils.toString(batchResponse.getHttpResponse().getEntity());
        assertThat(response).isNotEmpty();

        // response payload contains expected JSON result
        final Matcher matcher = Pattern.compile("\"value\":\\[(.*?)]}\r\n").matcher(response);
        assertThat(matcher.find()).isTrue();

        // response JSON contains a valid Person
        final Person person = new Gson().fromJson(matcher.group(1), Person.class);
        assertThat(person).isNotNull().matches(p -> p.getUserName() != null);
    }

    @Test
    public void testBatchWithReadWrite()
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(httpDestination);

        final ODataRequestResultMultipartGeneric batchResponse =
            new ODataRequestBatch("/", ODataProtocol.V4)
                .addRead(new ODataRequestRead("/", "People", "$top=1", ODataProtocol.V4))
                .beginChangeset()
                .addCreate(new ODataRequestCreate("/", "People", "{\"UserName\":\"JohnDoe1\"}", ODataProtocol.V4))
                .endChangeset()
                .execute(httpClient);

        // response object not null
        assertThat(batchResponse).isNotNull();

        // response HTTP code is healthy
        final HttpResponse httpResponse = batchResponse.getHttpResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void testBatchErrorWithDifferentServicePath()
    {
        final HttpClient httpClient = mock(HttpClient.class);

        assertThatCode(
            () -> new ODataRequestBatch("this/", ODataProtocol.V4)
                .addRead(new ODataRequestRead("this/", "People", "$top=1", ODataProtocol.V4))
                .addRead(new ODataRequestRead("other/", "People", "$top=2", ODataProtocol.V4))
                .execute(httpClient))
            .isInstanceOf(ODataRequestException.class);
    }
}
