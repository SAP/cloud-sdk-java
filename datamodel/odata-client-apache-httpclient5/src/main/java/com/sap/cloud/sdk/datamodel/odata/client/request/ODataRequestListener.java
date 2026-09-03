package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.time.Duration;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpResponse;

/**
 * Consumer class for the Listener Pattern to monitor and react on OData actions.
 *
 * @since 5.34.0
 */
public interface ODataRequestListener
{
    /**
     * Handler to react before execution of an HTTP request.
     *
     * @param request
     *            The HTTP request.
     */
    void listenOnRequest( @Nonnull final HttpUriRequestBase request );

    /**
     * Handler to react after execution of an HTTP request, when the response is received.
     *
     * @param response
     *            The HTTP response.
     * @since 5.35.0
     */
    default void listenOnResponse( @Nonnull final ClassicHttpResponse response )
    {
    }

    /**
     * Handler to react after the request execution has finished (either successfully or with an error).
     *
     * @param duration
     *            The duration of the request execution.
     * @since 5.35.0
     */
    default void listenOnExecutionFinished( @Nonnull final Duration duration )
    {
    }

    /**
     * Handler to react on an error during request generation.
     *
     * @param error
     *            The exception reference.
     */
    void listenOnRequestError( @Nonnull final Exception error );

    /**
     * Handler to react on an error during response parsing.
     *
     * @param error
     *            The exception reference.
     */
    void listenOnParsingError( @Nonnull final Exception error );
}
