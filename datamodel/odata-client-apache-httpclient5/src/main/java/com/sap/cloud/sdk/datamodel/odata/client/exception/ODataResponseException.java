package com.sap.cloud.sdk.datamodel.odata.client.exception;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * A generic {@link ODataException} representing an erroneous service response. This exception class comprises details
 * of the HTTP response.
 */
@EqualsAndHashCode( callSuper = true )
@Slf4j
public class ODataResponseException extends ODataException
{
    private static final long serialVersionUID = 4615831202194546242L;

    /**
     * The HTTP status code of the response received.
     */
    @Getter
    private final int httpCode;

    /**
     * The HTTP headers returned with the response.
     */
    @Getter
    @Nonnull
    private final transient Collection<Header> httpHeaders;

    /**
     * The content of the HTTP response body as plain text or null, if the response did not contain a body.
     */
    @Getter
    @Nonnull
    private final Option<String> httpBody;

    /**
     * Default constructor.
     *
     * @param request
     *            The original OData request reference.
     * @param httpResponse
     *            The {@link HttpResponse} that gave raise to this exception.
     * @param message
     *            The error message.
     * @param cause
     *            The error cause.
     */
    public ODataResponseException(
        @Nonnull final ODataRequestGeneric request,
        @Nonnull final ClassicHttpResponse httpResponse,
        @Nonnull final String message,
        @Nullable final Throwable cause )
    {
        super(request, message, cause);
        httpCode = httpResponse.getCode();
        httpHeaders = Arrays.asList(httpResponse.getHeaders());
        httpBody =
            Try
                .of(() -> EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8))
                .onFailure(e -> log.debug("HTTP response could not be consumed.", e))
                .toOption();
    }
}
