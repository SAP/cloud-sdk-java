/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.HttpClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultApacheHttpClient5Cache implements ApacheHttpClient5Cache
{
    static final Duration DEFAULT_DURATION = Duration.ofHours(1L);
    static final Ticker DEFAULT_TICKER = Ticker.systemTicker();

    @Nonnull
    private final Cache<CacheKey, HttpClient> cache;

    DefaultApacheHttpClient5Cache( @Nonnull final Duration cacheDuration )
    {
        this(cacheDuration, DEFAULT_TICKER);
    }

    DefaultApacheHttpClient5Cache( @Nonnull final Duration cacheDuration, @Nonnull final Ticker ticker )
    {
        cache = Caffeine.newBuilder().expireAfterAccess(cacheDuration).ticker(ticker).build();
        CacheManager.register(cache);
    }

    @Nonnull
    @Override
    public Try<HttpClient> tryGetHttpClient(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final ApacheHttpClient5Factory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, destination);
    }

    @Nonnull
    @Override
    public Try<HttpClient> tryGetHttpClient( @Nonnull final ApacheHttpClient5Factory httpClientFactory )
    {
        return tryGetOrCreateHttpClient(httpClientFactory, null);
    }

    private Try<HttpClient> tryGetOrCreateHttpClient(
        @Nonnull final ApacheHttpClient5Factory httpClientFactory,
        @Nullable final HttpDestinationProperties destination )
    {
        final Supplier<HttpClient> createHttpClient = () -> {
            log.debug("HttpClient with given cache key is not yet in the cache.");
            return destination != null
                ? httpClientFactory.createHttpClient(destination)
                : httpClientFactory.createHttpClient();
        };
        final CacheKey cacheKey;
        try {
            cacheKey = getCacheKey(destination);
        }
        catch( final TenantAccessException | PrincipalAccessException | IllegalStateException e ) {
            return Try.failure(new HttpClientInstantiationException("Failed to create cache key for HttpClient", e));
        }

        final HttpClient httpClient;
        try {
            httpClient = cache.get(cacheKey, anyKey -> createHttpClient.get());
            Objects
                .requireNonNull(
                    httpClient,
                    "Failed to create HttpClient: The registered HttpClient5Factory unexpectedly returned null.");
        }
        catch( final HttpClientInstantiationException e ) {
            return Try.failure(e);
        }
        catch( final RuntimeException e ) {
            return Try.failure(new HttpClientInstantiationException(e));
        }
        if( destination != null && httpClient instanceof ApacheHttpClient5Wrapper ) {
            return Try.success(((ApacheHttpClient5Wrapper) httpClient).withDestination(destination));
        }
        return Try.success(httpClient);
    }

    private CacheKey getCacheKey( @Nullable final HttpDestinationProperties destination )
    {
        if( destination == null ) {
            return CacheKey.ofTenantAndPrincipalOptionalIsolation();
        }
        if( !requiresPrincipalIsolation(destination) ) {
            return CacheKey.ofTenantOptionalIsolation().append(destination);
        }
        final Try<Tenant> maybeTenant = TenantAccessor.tryGetCurrentTenant();
        final Try<Principal> principal = PrincipalAccessor.tryGetCurrentPrincipal();
        if( principal.isFailure() ) {
            throw new IllegalStateException(
                "The destination requires a principal, but none was found in the current context.",
                principal.getCause());
        }
        if( maybeTenant.isFailure() ) {
            final String msg =
                "Tenant and Principal accessors are returning inconsistent results: A principal is defined, but no tenant is defined in the current context.";
            throw new IllegalStateException(msg, maybeTenant.getCause());
        }
        return CacheKey.of(maybeTenant.getOrNull(), principal.get()).append(destination);
    }

    private static boolean requiresPrincipalIsolation( @Nonnull final HttpDestinationProperties destination )
    {
        return DestinationUtility.requiresUserTokenExchange(destination)
            || destination.getAuthenticationType() == AuthenticationType.PRINCIPAL_PROPAGATION;
    }
}
