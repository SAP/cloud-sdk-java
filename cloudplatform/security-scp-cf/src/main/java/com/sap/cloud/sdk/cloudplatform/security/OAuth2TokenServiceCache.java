/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.impl.client.CloseableHttpClient;

import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2TokenService;
import com.sap.cloud.security.xsuaa.tokenflows.Cacheable;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * The cache to store instances of {@link OAuth2TokenService} according to {@link ClientIdentity}.
 */
@FunctionalInterface
public interface OAuth2TokenServiceCache
{
    /**
     * Get the token service. Optionally apply client identity secrets to the internal
     * {@link org.apache.http.client.HttpClient}.
     *
     * @param identity
     *            The client identity of a service binding.
     * @return A new or cached instance of {@link OAuth2TokenService}.
     */
    @Nonnull
    OAuth2TokenService getTokenService( @Nullable ClientIdentity identity );

    /**
     * Invalidate the cache.
     */
    default void invalidateCache()
    {
        throw new UnsupportedOperationException("Cache invalidation not possible.");
    }

    /**
     * Create an empty cache.
     *
     * @return A new instance of {@link OAuth2TokenServiceCache}.
     */
    @Nonnull
    static OAuth2TokenServiceCache create()
    {
        return create(new ConcurrentHashMap<>());
    }

    /**
     * Create a new cache based on an existing {@code Map} instance. Use this method for testing or customization.
     *
     * @param cache
     *            Custom data store for the cache.
     * @return A new instance of {@link OAuth2TokenServiceCache}.
     */
    @Nonnull
    static OAuth2TokenServiceCache create( @Nonnull final Map<ClientIdentity, OAuth2TokenService> cache )
    {
        if( cache instanceof ConcurrentHashMap ) {
            return new Default((ConcurrentHashMap<ClientIdentity, OAuth2TokenService>) cache);
        }
        return new Default(new ConcurrentHashMap<>(cache));
    }

    /**
     * Create ab immutable cache instance, with a single entry that is being returned for any provided client identity.
     * Use this method for testing or for disabling the cache.
     *
     * @param tokenService
     *            A {@link OAuth2TokenService} that will always be returned.
     * @return An immutable instance of {@link OAuth2TokenServiceCache}.
     */
    @Nonnull
    static OAuth2TokenServiceCache single( @Nonnull final OAuth2TokenService tokenService )
    {
        return identity -> tokenService;
    }

    /**
     * Default implementation of the interface.
     */
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    final class Default implements OAuth2TokenServiceCache
    {
        @Nonnull
        private final ConcurrentHashMap<ClientIdentity, OAuth2TokenService> cache;

        @Nonnull
        @Override
        public OAuth2TokenService getTokenService( @Nullable final ClientIdentity identity )
        {
            return cache.computeIfAbsent(identity, id -> new DefaultOAuth2TokenService(getHttpClient(id)));
        }

        @Nonnull
        CloseableHttpClient getHttpClient( @Nullable final ClientIdentity identity )
        {
            return HttpClientFactory.create(identity);
        }

        @Override
        public void invalidateCache()
        {
            cache
                .values()
                .stream()
                .filter(Cacheable.class::isInstance)
                .map(Cacheable.class::cast)
                .forEach(Cacheable::clearCache);
            cache.clear();
        }
    }
}
