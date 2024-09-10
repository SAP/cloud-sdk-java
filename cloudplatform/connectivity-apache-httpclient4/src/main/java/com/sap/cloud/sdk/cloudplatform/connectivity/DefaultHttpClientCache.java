/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@code HttpClientCache}, caching the {@code HttpClient}s for the amount of time given in the
 * constructor.
 */
@Slf4j
public class DefaultHttpClientCache extends AbstractHttpClientCache
{
    private final Cache<CacheKey, HttpClient> cache;

    /**
     * Caches the {@code HttpClient} for the default duration of 5 minutes.
     */
    DefaultHttpClientCache()
    {
        this(1, TimeUnit.HOURS);
    }

    /**
     * Caches the {@code HttpClient} for the given duration after it has been accessed last.
     *
     * @param duration
     *            The number of time units to store the {@code HttpClient}s.
     * @param unit
     *            The {@code TimeUnit} the duration is given in.
     */
    public DefaultHttpClientCache( final long duration, @Nonnull final TimeUnit unit )
    {
        this(duration, unit, Ticker.systemTicker());
    }

    /**
     * Caches any {@code HttpClient} in a cache created from the given builder.
     * <p>
     * <strong>Caution:</strong> This constructor is intended for <strong>testing purposes only</strong>.
     *
     * @param duration
     *            The number of time units to store the {@code HttpClient}s.
     * @param unit
     *            The {@code TimeUnit} the duration is given in.
     * @param ticker
     *            The {@link Ticker} to determine the passed time.
     */
    DefaultHttpClientCache( final long duration, @Nonnull final TimeUnit unit, @Nonnull final Ticker ticker )
    {
        cache = Caffeine.newBuilder().expireAfterAccess(duration, unit).ticker(ticker).build();
        CacheManager.register(cache);
    }

    @Nonnull
    @Override
    protected Try<Cache<CacheKey, HttpClient>> getCache()
    {
        return Try.success(cache);
    }

    @Nonnull
    @Override
    protected Try<CacheKey> getCacheKey( @Nonnull final HttpDestinationProperties destination )
    {
        if( !requiresPrincipalIsolation(destination) ) {
            return Try.success(CacheKey.ofTenantOptionalIsolation().append(destination));
        }
        final Try<Tenant> maybeTenant = TenantAccessor.tryGetCurrentTenant();
        final Try<Principal> principal = PrincipalAccessor.tryGetCurrentPrincipal();
        if( principal.isFailure() ) {
            return Try
                .failure(
                    new IllegalStateException(
                        "The destination requires a principal, but none was found in the current context.",
                        principal.getCause()));
        }
        if( maybeTenant.isFailure() ) {
            final String msg =
                "Tenant and Principal accessors are returning inconsistent results: A principal is defined, but no tenant is defined in the current context.";
            return Try.failure(new IllegalStateException(msg, maybeTenant.getCause()));
        }
        return Try.success(CacheKey.of(maybeTenant.getOrNull(), principal.get()).append(destination));
    }

    static boolean requiresPrincipalIsolation( @Nonnull final HttpDestinationProperties destination )
    {
        return DestinationUtility.requiresUserTokenExchange(destination)
            || destination.getAuthenticationType() == AuthenticationType.PRINCIPAL_PROPAGATION;
    }

    @Nonnull
    @Override
    protected Try<CacheKey> getCacheKey()
    {
        return Try.of(CacheKey::ofTenantAndPrincipalOptionalIsolation);
    }
}
