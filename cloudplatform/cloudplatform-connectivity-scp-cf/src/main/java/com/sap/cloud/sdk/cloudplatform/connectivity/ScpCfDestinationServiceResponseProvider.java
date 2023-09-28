package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * Functional interface to provide an response instance for an SCP CF Destination Service query.
 */
@Beta
@FunctionalInterface
public interface ScpCfDestinationServiceResponseProvider
{
    /**
     * Query SCP CF Destination Service with the given URI.
     *
     * @param destinationServiceUri
     *            The URI being used to query the SCP CF Destination Service.
     * @return The deserialized service response.
     */
    @Nullable
    ScpCfDestinationServiceV1Response provideResponse( @Nonnull final URI destinationServiceUri );
}
