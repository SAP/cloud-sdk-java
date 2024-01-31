/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.security.xsuaa.util.UriUtil.expandPath;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;

import lombok.Value;

/**
 * A supplier of OAuth client information. Implementations should extract the client information out of a
 * {@link com.sap.cloud.environment.servicebinding.api.ServiceBinding}.
 *
 * @since 4.20.0
 */
@Beta
public interface OAuth2PropertySupplier
{
    /**
     * A default implementation of the {@link OAuth2ServiceEndpointsProvider} interface that provides convenient access
     * to the default endpoints of either the XSUAA or the IAS service.
     *
     * @since 5.2.1
     */
    @Value
    class DefaultTokenEndpoints implements OAuth2ServiceEndpointsProvider
    {
        private static class Xsuaa
        {
            private static final String TOKEN_PATH = "/oauth/token";
            private static final String AUTHORIZE_PATH = "/oauth/authorize";
            private static final String KEYSET_PATH = "/token_keys";
        }

        private static class Ias
        {
            private static final String TOKEN_PATH = "/oauth2/token";
            private static final String AUTHORIZE_PATH = "/oauth2/authorize";
            // TODO: is this actually correct?
            private static final String KEYSET_PATH = "/token_keys";
        }

        @Nonnull
        URI tokenEndpoint;

        @Nullable
        URI authorizeEndpoint;

        @Nullable
        URI jwksUri;

        /**
         * Returns a new {@link OAuth2ServiceEndpointsProvider} that points to the default XSUAA endpoints, based on the
         * provided {@code baseUri}.
         *
         * @param baseUri
         *            The token service uri.
         * @return A new {@link OAuth2ServiceEndpointsProvider}.
         */
        @Nonnull
        static OAuth2ServiceEndpointsProvider fromXsuaaUri( final URI baseUri )
        {
            final URI tokenEndpoint = expandPath(baseUri, Xsuaa.TOKEN_PATH);
            final URI authorizeEndpoint = expandPath(baseUri, Xsuaa.AUTHORIZE_PATH);
            final URI jwksUri = expandPath(baseUri, Xsuaa.KEYSET_PATH);
            return new DefaultTokenEndpoints(tokenEndpoint, authorizeEndpoint, jwksUri);
        }

        /**
         * Returns a new {@link OAuth2ServiceEndpointsProvider} that points to the default IAS endpoints, based on the
         * provided {@code baseUri}.
         *
         * @param baseUri
         *            The token service uri.
         * @return A new {@link OAuth2ServiceEndpointsProvider}.
         */
        @Nonnull
        static OAuth2ServiceEndpointsProvider fromIasUri( final URI baseUri )
        {
            final URI tokenEndpoint = expandPath(baseUri, Ias.TOKEN_PATH);
            final URI authorizeEndpoint = expandPath(baseUri, Ias.AUTHORIZE_PATH);
            final URI jwksUri = expandPath(baseUri, Ias.KEYSET_PATH);
            return new DefaultTokenEndpoints(tokenEndpoint, authorizeEndpoint, jwksUri);
        }
    }

    /**
     * Indicates if the binding is supported by this supplier.
     *
     * @return True, if the binding appears to be an OAuth binding and this property supplier is capable of parsing it.
     */
    boolean isOAuth2Binding();

    /**
     * URL that the OAuth client should authenticate to.
     *
     * @return A valid URI.
     */
    @Nonnull
    URI getServiceUri();

    /**
     * URL of the OAuth token service.
     *
     * @return A valid URI.
     */
    @Nonnull
    URI getTokenUri();

    /**
     * {@link OAuth2ServiceEndpointsProvider} for the used token service.
     * <p>
     * By default, this method returns endpoints that are specific to the XSUAA service.
     * <p>
     * For example, authorization tokens will be retrieved from {@code getTokenServiceUri().resolve("/oauth/token")}.
     *
     * @return An instance of {@link OAuth2ServiceEndpointsProvider}.
     * @since 5.2.1
     */
    @Nonnull
    default OAuth2ServiceEndpointsProvider getTokenEndpoints()
    {
        return DefaultTokenEndpoints.fromXsuaaUri(getTokenUri());
    }

    /**
     * OAuth client identity to be used for obtaining a token.
     *
     * @return a {@link ClientIdentity}.
     */
    @Nonnull
    ClientIdentity getClientIdentity();
}
