package com.sap.cloud.sdk.frameworks.resilience4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationKey;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

/**
 * Decorates a callable with a rate limiter based on a provided resilience configuration.
 */
public class DefaultRateLimiterProvider implements RateLimiterProvider, GenericDecorator
{
    private static final RateLimiterConfig DEFAULT_RATE_LIMITER_CONFIG = RateLimiterConfig.custom().build();

    @Nonnull
    private final ConcurrentHashMap<ResilienceIsolationKey, RateLimiterRegistry> rateLimiterRegistries =
        new ConcurrentHashMap<>();

    private RateLimiterRegistry getRateLimiterRegistry( @Nonnull final ResilienceIsolationKey isolationKey )
    {
        return rateLimiterRegistries
            .computeIfAbsent(isolationKey, k -> RateLimiterRegistry.of(DEFAULT_RATE_LIMITER_CONFIG));
    }

    @Nonnull
    @Override
    public <T> Callable<T> decorateCallable(
        @Nonnull final Callable<T> callable,
        @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.rateLimiterConfiguration().isEnabled() ) {
            return callable;
        }
        final RateLimiter rateLimiter = getRateLimiter(configuration);
        return RateLimiter.decorateCallable(rateLimiter, callable);
    }

    @Nonnull
    @Override
    public RateLimiter getRateLimiter( @Nonnull final ResilienceConfiguration configuration )
    {
        if( !configuration.rateLimiterConfiguration().isEnabled() ) {
            throw new IllegalArgumentException("The provided resilience configuration does not set a rate limiter.");
        }

        final String identifier = configuration.identifier();
        final ResilienceIsolationKey isolationKey = ResilienceIsolationKey.of(configuration.isolationMode());
        final RateLimiterRegistry rateLimiterRegistry = getRateLimiterRegistry(isolationKey);

        final RateLimiterConfig rateLimiterConfig =
            RateLimiterConfig
                .custom()
                .limitRefreshPeriod(configuration.rateLimiterConfiguration().limitRefreshPeriod())
                .limitForPeriod(configuration.rateLimiterConfiguration().limitForPeriod())
                .timeoutDuration(configuration.rateLimiterConfiguration().timeoutDuration())
                .build();

        return rateLimiterRegistry.rateLimiter(identifier, rateLimiterConfig);
    }
}
