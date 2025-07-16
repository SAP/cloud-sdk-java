package com.sap.cloud.sdk.datamodel.odata.client.request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHttpResponse;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Enum representing the strategy for buffering HTTP responses.
 */
@FunctionalInterface
interface ODataRequestResultFactory
{
    ODataRequestResultGeneric create(
        @Nonnull final ODataRequestGeneric oDataRequest,
        @Nonnull final HttpResponse httpResponse,
        @Nullable final HttpClient httpClient );

    /**
     * Strategy that does not buffer the response.
     */
    ODataRequestResultFactory WITHOUT_BUFFER = ODataRequestResultResource::new;

    /**
     * Strategy that buffers the response by creating a copy of it.
     */
    ODataRequestResultFactory WITH_BUFFER = ( oDataRequest, httpResponse, httpClient ) -> {
        final BasicHttpResponse copy = new BasicHttpResponse(httpResponse.getStatusLine());
        Option.of(httpResponse.getLocale()).peek(copy::setLocale);
        Option.of(httpResponse.getAllHeaders()).peek(copy::setHeaders);
        Option
            .of(httpResponse.getEntity())
            .peek(entity -> Try.run(() -> copy.setEntity(new BufferedHttpEntity(entity))));
        return new ODataRequestResultGeneric(oDataRequest, copy, httpClient);
    };
}
