package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

import io.vavr.control.Option;
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
     * @throws ODataResponseException
     *             When the response code infers an unhealthy state, i.e. when >= 400.
     * @throws ODataServiceErrorException
     *             When the response contains an OData error message according to specification.
     */
    static void requireHealthyResponse( @Nonnull final ODataRequestResult result )
    {
        final ODataRequestGeneric originalRequest = result.getODataRequest();
        final StatusLine statusLine = result.getStatusLine();

        if( statusLine != null && statusLine.getStatusCode() < HttpStatus.SC_BAD_REQUEST ) { // code < 400
            return;
        }

        final HttpResponse httpResponse = result.getHttpResponse();
        final ODataRequestGeneric requestRelevantForException =
            findPotentialBatchItem(httpResponse, originalRequest).getOrElse(originalRequest);

        final Integer statusCode = statusLine == null ? null : statusLine.getStatusCode();
        final String msg = "The HTTP response code (" + statusCode + ") indicates an error.";

        final Try<ODataServiceError> odataError = Try.of(() -> loadErrorFromResponse(result));
        if( odataError.isSuccess() ) {
            final String msgError = msg + " The OData service responded with an error message.";
            throw new ODataServiceErrorException(
                requestRelevantForException,
                httpResponse,
                msgError,
                null,
                odataError.get());
        }
        throw new ODataResponseException(requestRelevantForException, httpResponse, msg, null);
    }

    @Nonnull
    private static
        Option<ODataRequestGeneric>
        findPotentialBatchItem( final HttpResponse httpResponse, final ODataRequestGeneric request )
    {
        if( !(request instanceof ODataRequestBatch requestBatch)
            || !(httpResponse instanceof MultipartHttpResponse multipartHttpResponse) ) {
            return Option.none();
        }
        @Nullable
        final Integer failedBatchRequestNumber = multipartHttpResponse.getContentId();
        if( failedBatchRequestNumber == null ) {
            return Option.none();
        }

        for( final ODataRequestBatch.BatchItem requestGeneric : requestBatch.getRequests() ) {
            if( requestGeneric instanceof ODataRequestBatch.BatchItemChangeset changeset ) {
                for( final ODataRequestBatch.BatchItemSingle single : changeset.getRequests() ) {
                    if( single.getContentId() == failedBatchRequestNumber ) {
                        return Option.of(single.getRequest());
                    }
                }
            } else if( requestGeneric instanceof ODataRequestBatch.BatchItemSingle single
                && single.getContentId() == failedBatchRequestNumber ) {
                return Option.of(single.getRequest());
            }
        }
        return Option.none();
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
