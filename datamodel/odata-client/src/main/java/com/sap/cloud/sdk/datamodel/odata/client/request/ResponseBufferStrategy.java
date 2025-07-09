package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.function.UnaryOperator;

import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHttpResponse;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing the strategy for buffering HTTP responses.
 */
@RequiredArgsConstructor
@Slf4j
@Getter
enum ResponseBufferStrategy
{
    /**
     * Strategy that does not buffer the response.
     */
    DISABLED(response -> response),

    /**
     * Strategy that buffers the response by creating a copy of it.
     */
    ENABLED(response -> {
        final BasicHttpResponse copy = new BasicHttpResponse(response.getStatusLine());
        Option.of(response.getLocale()).peek(copy::setLocale);
        Option.of(response.getAllHeaders()).peek(copy::setHeaders);
        Option.of(response.getEntity()).peek(entity -> Try.run(() -> copy.setEntity(new BufferedHttpEntity(entity))));
        return copy;
    });

    private final UnaryOperator<HttpResponse> handler;
}
