package com.sap.cloud.sdk.services.openapi.core;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.services.openapi.apiclient.ApiClient;

/**
 * Represents an OpenAPI API
 */
public abstract class AbstractOpenApiService implements OpenApiService
{
    /**
     * The instance of {@link ApiClient} the concrete API class uses to conduct HTTP commmunication.
     */
    protected ApiClient apiClient;

    /**
     * Expects the {@link Destination} instance to create the {@link ApiClient} from it.
     *
     * @param destination
     *            The destination which is used to derive the connection details of the target system from.
     */
    protected AbstractOpenApiService( @Nonnull final Destination destination )
    {
        apiClient = createApiClientFromDestination(destination);
    }

    protected AbstractOpenApiService( @Nonnull final ApiClient apiClient )
    {
        this.apiClient = apiClient;
    }

    /**
     * Derives the connection details of the target system from the provided {@link Destination} and supplies them to
     * the created instance of {@link ApiClient}.
     *
     * @param destination
     *            The destination to derive the connection details from
     * @return The API client that is supplied with the connection details derived from the provided destination.
     */
    private ApiClient createApiClientFromDestination( final Destination destination )
    {
        final ApiClient apiClient = new ApiClient(destination);

        // set root of API Client base path
        final URI uri = destination.asHttp().getUri();
        final URI path;
        try {
            path = new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null);
        }
        catch( final URISyntaxException e ) {
            throw new OpenApiRequestException(e);
        }

        apiClient.setBasePath(path.toString());

        return apiClient;
    }
}
