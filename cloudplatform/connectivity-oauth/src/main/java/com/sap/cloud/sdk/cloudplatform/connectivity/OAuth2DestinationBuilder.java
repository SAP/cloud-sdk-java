/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.sap.cloud.security.config.ClientIdentity;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to conveniently create an {@link HttpDestination HttpDestination} instance while automatically adding
 * an authentication header retrieved from an OAuth2 service.
 *
 * @since 4.10.0
 */
@Slf4j
public class OAuth2DestinationBuilder
{
    /**
     * Helper interface to serve mandatory input for OAuth2 token endpoint.
     *
     * @since 4.10.0
     */
    @FunctionalInterface
    public interface BuilderWithTargetUrl
    {
        /**
         * Apply the OAuth2 token URL.
         *
         * @param tokenUrl
         *            The OAuth2 token URL.
         * @return The same builder instance.
         * @since 4.10.0
         */
        @Nonnull
        BuilderWithTokenEndpoint withTokenEndpoint( @Nonnull final String tokenUrl );
    }

    /**
     * Helper interface to serve mandatory input for OAuth2 client identity and OAuth2 token representative.
     *
     * @since 4.10.0
     */
    @FunctionalInterface
    public interface BuilderWithTokenEndpoint
    {
        /**
         * Apply the OAuth2 client identity and representative.
         *
         * @param clientIdentity
         *            The OAuth2 Client Identity.
         * @param behalf
         *            The OAuth2 token representative.
         * @return The same builder instance.
         * @see com.sap.cloud.security.config.ClientCertificate
         * @see com.sap.cloud.security.config.ClientCredentials
         * @since 4.10.0
         */
        @Nonnull
        DefaultHttpDestination.Builder
            withClient( @Nonnull final ClientIdentity clientIdentity, @Nonnull final OnBehalfOf behalf );
    }

    /**
     * Static factory method to initialize a fluent API builder.
     *
     * @param targetUrl
     *            The destination target URL.
     * @return A new fluent API builder instance.
     * @since 4.10.0
     */
    @Nonnull
    public static BuilderWithTargetUrl forTargetUrl( @Nonnull final String targetUrl )
    {
        return ( tokenUrl ) -> ( client, behalf ) -> {
            final OAuth2Service oauth2service =
                OAuth2Service.builder().withTokenUri(tokenUrl).withIdentity(client).withOnBehalfOf(behalf).build();
            final DefaultHttpDestination.Builder destinationBuilder = DefaultHttpDestination.builder(targetUrl);

            destinationBuilder.name(String.valueOf(client.getId().hashCode()));

            return destinationBuilder
                .headerProviders(new OAuth2HeaderProvider(oauth2service, HttpHeaders.AUTHORIZATION));
        };
    }
}
