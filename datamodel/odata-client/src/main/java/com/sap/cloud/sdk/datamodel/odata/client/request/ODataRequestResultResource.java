package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

import com.google.common.annotations.Beta;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * OData request result for reading entities. The connection is not yet closed.
 */
@Slf4j
@Beta
@EqualsAndHashCode( callSuper = true )
public class ODataRequestResultResource extends ODataRequestResultGeneric implements AutoCloseable
{
    /**
     * Default constructor.
     *
     * @param oDataRequest
     *            The original OData request
     * @param httpResponse
     *            The original Http response
     * @param httpClient
     *            The Http client used to execute the request
     */
    ODataRequestResultResource(
        @Nonnull final ODataRequestGeneric oDataRequest,
        @Nonnull final HttpResponse httpResponse,
        @Nullable final HttpClient httpClient )
    {
        super(oDataRequest, httpResponse, httpClient);
    }

    @Override
    public void close()
    {
        try {
            EntityUtils.consume(getHttpResponse().getEntity());
        }
        catch( final IOException e ) {
            log.warn("Failed to close the HTTP response entity.", e);
        }
    }

    /**
     * Interface for executing OData requests that return a resource which must be closed.
     */
    @FunctionalInterface
    public interface Executable extends ODataRequestExecutable
    {
        @Nonnull
        @Override
        ODataRequestResultResource execute( @Nonnull final HttpClient httpClient );
    }
}
