package com.sap.cloud.sdk.services.openapi.apache;

/**
 * Listener for receiving metadata about HTTP responses.
 *
 * @since 5.25.0
 */
@FunctionalInterface
public interface ResponseMetadataListener
{
    /**
     * Called when an HTTP response is received.
     *
     * @param response
     *            The response metadata.
     */
    void onResponse( OpenApiResponse response );
}
