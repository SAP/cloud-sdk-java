/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

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

        final Integer statusCode = statusLine == null ? null : statusLine.getStatusCode();
        final String msg = "The HTTP response code (" + statusCode + ") indicates an error.";
        final ODataResponseException preparedException = new ODataResponseException(request, httpResponse, msg, null);

        final Try<ODataServiceError> odataError = Try.of(() -> loadErrorFromResponse(result));
        if( odataError.isSuccess() ) {
            final String msgError = msg + " The OData service responded with an error message.";
            throw new ODataServiceErrorException(request, httpResponse, msgError, null, odataError.get());
        }

        throw preparedException;
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
