package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;

import io.vavr.control.Try;

/**
 * Implementation of the {@code HttpClientCache}, caching the {@code HttpClient}s for the amount of time given in the
 * constructor.
 */
public class DefaultHttpClientCache extends AbstractHttpClientCache
{
    private final Cache<CacheKey, HttpClient> cache;

    /**
     * Caches the {@code HttpClient} for the default duration of 5 minutes.
     */
    DefaultHttpClientCache()
    {
        this(5, TimeUnit.MINUTES);
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
        cache = Caffeine.newBuilder().expireAfterWrite(duration, unit).ticker(ticker).build();
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
        return Try.of(() -> {
            if( DestinationUtility.requiresUserTokenExchange(destination) ) {
                return CacheKey.ofTenantAndPrincipalOptionalIsolation().append(destination);
            }
            return CacheKey.ofTenantOptionalIsolation().append(destination);
        });
    }

    @Nonnull
    @Override
    protected Try<CacheKey> getCacheKey()
    {
        return Try.of(CacheKey::ofTenantAndPrincipalOptionalIsolation);
    }
}
