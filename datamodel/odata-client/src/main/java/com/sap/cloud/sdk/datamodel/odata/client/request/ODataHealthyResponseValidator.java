/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import com.google.gson.JsonObject;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataDeserializationException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataResponseException;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceError;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.GsonResultObject;
import com.sap.cloud.sdk.result.ResultObject;

import io.vavr.control.Try;

/**
 * Utility class to enable a healthy response validation.
 */
class ODataHealthyResponseValidator
{
    /**
     * Check the HTTP response code and body of the OData request result. If the code indicates an unhealthy response,
     * an exception will be thrown with further details.
     *
     * @param result
     *            The OData response object.
     *
     * @throws ODataResponseException
     *             When the response code infers an unhealthy state, i.e. when >= 400.
     * @throws ODataServiceErrorException
     *             When the response contains an OData error message according to specification.
     */
    static void requireHealthyResponse( @Nonnull final ODataRequestResult result )
    {
        final ODataRequestGeneric request = result.getODataRequest();
        final HttpResponse httpResponse = result.getHttpResponse();
        final StatusLine statusLine = httpResponse.getStatusLine();

        if( statusLine != null && statusLine.getStatusCode() < HttpStatus.SC_BAD_REQUEST ) { // code < 400
            return;
        }

        ODataRequestGeneric batchFailedRequest = null;
        if( request instanceof ODataRequestBatch oDataRequestBatch ) {
            batchFailedRequest = findFailedBatchRequest(httpResponse, oDataRequestBatch);
        }

        final Integer statusCode = statusLine == null ? null : statusLine.getStatusCode();
        final String msg = "The HTTP response code (" + statusCode + ") indicates an error.";
        final ODataResponseException preparedException =
            new ODataResponseException(request, httpResponse, msg, null, batchFailedRequest);

        final Try<ODataServiceError> odataError = Try.of(() -> loadErrorFromResponse(result));
        if( odataError.isSuccess() ) {
            final String msgError = msg + " The OData service responded with an error message.";
            throw new ODataServiceErrorException(request, httpResponse, msgError, null, odataError.get());
        }

        throw preparedException;
    }

    private static
        ODataRequestGeneric
        findFailedBatchRequest( final HttpResponse httpResponse, final ODataRequestBatch oDataRequestBatch )
    {
        try {
            final byte[] content = httpResponse.getEntity().getContent().readAllBytes();
            final int failedBatchRequestNumber =
                Integer.parseInt(new String(content, StandardCharsets.UTF_8).split("Content-ID: ")[1].split("\r\n")[0]);
            for( final ODataRequestBatch.BatchItem requestGeneric : oDataRequestBatch.getRequests() ) {
                if( requestGeneric instanceof ODataRequestBatch.BatchItemChangeset changeset ) {
                    for( final ODataRequestBatch.BatchItemSingle single : changeset.getRequests() ) {
                        if( single.getContentId() == failedBatchRequestNumber ) {
                            return single.getRequest();
                        }
                    }
                } else if( requestGeneric instanceof ODataRequestBatch.BatchItemSingle single ) {
                    if( single.getContentId() == failedBatchRequestNumber ) {
                        return single.getRequest();
                    }
                }
            }
        }
        catch( final Exception ignored ) {
        }
        return null;
    }

    @Nonnull
    private static ODataServiceError loadErrorFromResponse( final ODataRequestResult result )
        throws ODataDeserializationException
    {
        final GsonResultElementFactory elementFactory = new GsonResultElementFactory(ODataGsonBuilder.newGsonBuilder());

        return HttpEntityReader.read(result, root -> {
            final JsonObject error = root.getAsJsonObject().get("error").getAsJsonObject();
            final ResultObject errorObject = new GsonResultObject(error, elementFactory);
            return ODataServiceError.fromResultObject(errorObject, result.getODataRequest().getProtocol());
        });
    }
}
