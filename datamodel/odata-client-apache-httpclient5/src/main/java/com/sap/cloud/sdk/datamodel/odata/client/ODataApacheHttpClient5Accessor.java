package com.sap.cloud.sdk.datamodel.odata.client;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Factory;
import com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Accessor for {@link HttpClient} instances suitable for OData requests.
 * <p>
 * Unlike the general-purpose {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor}, clients
 * created here have a CSRF token interceptor enabled (see
 * {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5FactoryBuilder#withCsrfTokenInterceptor()}).
 * The interceptor automatically fetches a CSRF token via a HEAD request before every mutating HTTP request (POST, PUT,
 * PATCH, DELETE) that does not already carry an {@code x-csrf-token} header, which is required for OData services that
 * enforce CSRF protection.
 * <p>
 * For non-OData use cases (REST, OpenAPI) use
 * {@link com.sap.cloud.sdk.cloudplatform.connectivity.ApacheHttpClient5Accessor} instead to avoid unnecessary CSRF HEAD
 * requests.
 *
 * @since 5.35.0
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ODataApacheHttpClient5Accessor
{
    private static final ApacheHttpClient5Factory FACTORY =
        new ApacheHttpClient5FactoryBuilder().withCsrfTokenInterceptor().build();

    /**
     * Returns an {@link HttpClient} for the given destination with the CSRF token interceptor enabled.
     *
     * @param destination
     *            The destination to get the {@link HttpClient} for.
     * @return An {@link HttpClient} configured for OData communication with the given destination.
     */
    @Nonnull
    public static HttpClient getHttpClient( @Nonnull final HttpDestinationProperties destination )
    {
        return FACTORY.createHttpClient(destination);
    }
}
