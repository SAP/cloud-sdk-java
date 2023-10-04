/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.http.HttpHeaders;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.security.config.ClientIdentity;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to conveniently create an {@link HttpDestination HttpDestination} instance while automatically adding
 * an authentication header retrieved from an OAuth2 service.
 *
 * @since 4.10.0
 */
@Slf4j
@Beta
public class OAuth2DestinationBuilder
{
    private static final Duration TOKEN_RETRIEVAL_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Helper interface to serve mandatory input for OAuth2 token endpoint.
     *
     * @since 4.10.0
     */
    @FunctionalInterface
    @Beta
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
    @Beta
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
         * @since 4.10.0
         */
        @Nonnull
        BuilderWithClient withClient( @Nonnull final ClientIdentity clientIdentity, @Nonnull final OnBehalfOf behalf );
    }

    /**
     * Helper interface to serve optional properties.
     *
     * @since 4.10.0
     */
    @FunctionalInterface
    @Beta
    public interface BuilderWithClient
    {
        /**
         * Apply destination properties.
         *
         * @param properties
         *            The destination properties.
         * @return The same builder instance.
         * @since 4.10.0
         */
        @Nonnull
        BuilderWithProperties withProperties( @Nonnull final Map<String, String> properties );

        /**
         * Finalize build process.
         *
         * @return A new {@link HttpDestination} instance.
         * @since 4.10.0
         */
        @Nonnull
        default HttpDestination build()
        {
            return withProperties(Collections.emptyMap()).build();
        }
    }

    /**
     * Helper interface to serve build method.
     *
     * @since 4.10.0
     */
    @FunctionalInterface
    @Beta
    public interface BuilderWithProperties
    {
        /**
         * Finalize build process.
         *
         * @return A new {@link HttpDestination} instance.
         * @since 4.10.0
         */
        @Nonnull
        HttpDestination build();
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
        return ( tokenUrl ) -> ( client, behalf ) -> ( properties ) -> () -> {
            final OAuth2ServiceImpl oauth2service = OAuth2ServiceImpl.fromCredentials(tokenUrl, client);
            final DefaultHttpDestination.Builder destinationBuilder = DefaultHttpDestination.builder(targetUrl);

            // random uuid to make sure equals and hash code works effectively by reference
            final String destinationName = UUID.randomUUID().toString();
            destinationBuilder.name(destinationName);

            properties.forEach(destinationBuilder::property);

            destinationBuilder
                .property(
                    OAuthHeaderProvider.PROPERTY_OAUTH2_RESILIENCE_CONFIG,
                    createTokenRetrievalResilienceConfiguration(destinationName));

            return destinationBuilder
                .headerProviders(new OAuthHeaderProvider(oauth2service, behalf, HttpHeaders.AUTHORIZATION))
                .build();
        };
    }

    @Nonnull
    private static ResilienceConfiguration createTokenRetrievalResilienceConfiguration(
        @Nonnull final String destinationName )
    {
        return ResilienceConfiguration
            .of(destinationName)
            .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
            .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(TOKEN_RETRIEVAL_TIMEOUT));
    }
}
