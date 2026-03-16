package com.sap.cloud.sdk.datamodel.odata.client.request;

import static org.slf4j.LoggerFactory.getLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.StatusLine;
import org.slf4j.Logger;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Enum representing the strategy for buffering HTTP responses.
 */
@FunctionalInterface
interface ODataRequestResultFactory
{
    /**
     * Strategy that does not buffer the response.
     */
    ODataRequestResultFactory WITHOUT_BUFFER = ODataRequestResultResource::new;

    /**
     * Strategy that buffers the response by creating a copy of it.
     */
    ODataRequestResultFactory WITH_BUFFER = ( oDataRequest, httpResponse, httpClient ) -> {
        final StatusLine status = new StatusLine(httpResponse);
        final BasicClassicHttpResponse copy = new BasicClassicHttpResponse(status.getStatusCode());
        Option.of(httpResponse.getLocale()).peek(copy::setLocale);
        Option.of(httpResponse.getHeaders()).peek(copy::setHeaders);

        final Logger log = getLogger(ODataRequestResultFactory.class);
        Option
            .of(httpResponse.getEntity())
            .onEmpty(() -> log.debug("HTTP response entity is empty: {}", status))
            .map(entity -> Try.run(() -> copy.setEntity(new BufferedHttpEntity(entity))))
            .peek(b -> b.onSuccess(v -> log.debug("Successfully buffered the HTTP response entity.")))
            .peek(b -> b.onFailure(e -> log.warn("Failed to buffer HTTP response entity: {}", status, e)));

        return new ODataRequestResultGeneric(oDataRequest, copy, httpClient);
    };

    ODataRequestResultGeneric create(
        @Nonnull final ODataRequestGeneric oDataRequest,
        @Nonnull final ClassicHttpResponse httpResponse,
        @Nullable final HttpClient httpClient );
}
