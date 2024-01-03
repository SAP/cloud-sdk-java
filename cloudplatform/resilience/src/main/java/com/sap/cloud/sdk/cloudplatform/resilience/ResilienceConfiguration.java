/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * This type provides options to configure the resilience function decoration.
 */
@Accessors( fluent = true )
@EqualsAndHashCode
@Getter
@Setter
public class ResilienceConfiguration
{
    @Nonnull
    private final String identifier;
    @Nonnull
    private ResilienceIsolationMode isolationMode = ResilienceIsolationMode.TENANT_AND_USER_OPTIONAL;
    @Nonnull
    private TimeLimiterConfiguration timeLimiterConfiguration = TimeLimiterConfiguration.of();
    @Nonnull
    private CircuitBreakerConfiguration circuitBreakerConfiguration = CircuitBreakerConfiguration.of();
    @Nonnull
    private BulkheadConfiguration bulkheadConfiguration = BulkheadConfiguration.of();
    @Nonnull
    private CacheConfiguration cacheConfiguration = CacheConfiguration.disabled();
    @Nonnull
    private RetryConfiguration retryConfiguration = RetryConfiguration.disabled();
    @Nonnull
    private RateLimiterConfiguration rateLimiterConfiguration = RateLimiterConfiguration.disabled();

    /**
     * Provides options specific to rate limits.
     */
    @AllArgsConstructor( access = AccessLevel.PRIVATE )
    @Accessors( fluent = true )
    @EqualsAndHashCode
    @Getter
    @Setter
    public static final class RateLimiterConfiguration
    {
        /**
         * The default wait time a thread waits for a permission.
         */
        public static final Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(5);

        /**
         * The default period of a limit refresh. After each period the rate limiter sets its permissions count back to
         * the limitForPeriod value.
         */
        public static final Duration DEFAULT_LIMIT_REFRESH_PERIOD = Duration.ofSeconds(1);

        /**
         * The default number of permissions available during one limit refresh period.
         */
        public static final int DEFAULT_LIMIT_FOR_PERIOD = 20;

        /**
         * Flag to indicate active RateLimiter.
         */
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private boolean enabled = true;

        /**
         * The wait time a thread waits for a permission.
         */
        @Nonnull
        private final Duration timeoutDuration;

        /**
         * The period of a limit refresh. After each period the rate limiter sets its permissions count back to the
         * limitForPeriod value.
         */
        @Nonnull
        private final Duration limitRefreshPeriod;

        /**
         * The number of permissions available during one limit refresh period.
         */
        private final int limitForPeriod;

        /**
         * Get the status indicator for RateLimiter.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Creates a new {@code RateLimiterConfiguration} that allows for infinite amount of requests, effectively
         * disabling the rate limiter.
         *
         * @return A disabled rate limiter.
         */
        @Nonnull
        public static RateLimiterConfiguration disabled()
        {
            final RateLimiterConfiguration rateLimiterConfiguration = RateLimiterConfiguration.of();
            rateLimiterConfiguration.enabled = false;
            return rateLimiterConfiguration;
        }

        /**
         * Creates a new {@code RateLimiterConfiguration} with default values of {@linkplain #DEFAULT_TIMEOUT_DURATION},
         * {@linkplain #DEFAULT_LIMIT_REFRESH_PERIOD} and {@linkplain #DEFAULT_LIMIT_FOR_PERIOD}
         *
         * @return An immutable {@code RateLimiterConfiguration}.
         */
        @Nonnull
        public static RateLimiterConfiguration of()
        {
            return new RateLimiterConfiguration(
                true,
                DEFAULT_TIMEOUT_DURATION,
                DEFAULT_LIMIT_REFRESH_PERIOD,
                DEFAULT_LIMIT_FOR_PERIOD);
        }

        /**
         * Creates a new {@code RateLimiterConfiguration} by specifying a timeout duration, limit refresh period and
         * limit for period.
         *
         * @param timeoutDuration
         *            The maximum duration a thread waits for a permission to execute
         * @param limitRefreshPeriod
         *            The time window in which requests are counted.
         * @param limitForPeriod
         *            The maximum number of request allowed during one window.
         * @return An immutable {@code RateLimiterConfiguration}.
         */
        @Nonnull
        public static RateLimiterConfiguration of(
            @Nonnull final Duration timeoutDuration,
            @Nonnull final Duration limitRefreshPeriod,
            final int limitForPeriod )
        {
            return new RateLimiterConfiguration(true, timeoutDuration, limitRefreshPeriod, limitForPeriod);
        }
    }

    /**
     * Provides options specific to caching.
     */
    @EqualsAndHashCode
    @Getter
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @AllArgsConstructor( access = AccessLevel.PRIVATE )
    public static final class CacheConfiguration
    {
        /**
         * Default value of Expiration Strategy is set as the strategy which is based on the last modification of the
         * cache.
         */
        public static final CacheExpirationStrategy DEFAULT_EXPIRATION_STRATEGY =
            CacheExpirationStrategy.WHEN_LAST_MODIFIED;

        /**
         * Trigger for caching.
         */
        @Getter( AccessLevel.NONE )
        private final boolean enabled;

        /**
         * Flag to indicate parameters being serializable.
         */
        private boolean serializable;

        /**
         * Duration after which the cache entry will be invalidated.
         */
        @Nonnull
        private Duration expirationDuration = Duration.ZERO;

        /**
         * Expiration strategy for the cache.
         */
        @Nonnull
        private CacheExpirationStrategy expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;

        /**
         * Additional parameters added to the cache key.
         */
        @Nonnull
        @Getter
        private Iterable<Object> parameters = Lists.newArrayList();

        /**
         * Get the status indicator for the Cache.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Factory method to create a cache configuration builder instance.
         *
         * @param expirationDuration
         *            The duration after which the cache entry will be invalidated automatically.
         * @return A new cache configuration builder instance.
         */
        @Nonnull
        public static CacheConfigurationBuilder of( @Nonnull final Duration expirationDuration )
        {
            return new CacheConfigurationBuilder(expirationDuration);
        }

        /**
         * Factory method to create a disabled cache configuration.
         *
         * @return A disabled cache configuration instance.
         */
        @Nonnull
        public static CacheConfiguration disabled()
        {
            return new CacheConfiguration(false);
        }

        /**
         * Builder class for cache configuration. It enforces a decision on additional cache parameters.
         */
        @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
        public static final class CacheConfigurationBuilder
        {
            private final Duration expirationDuration;

            private CacheExpirationStrategy expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;

            /**
             * Setter to set the Expiration Strategy for the cache configuration
             *
             * @param expirationStrategy
             *            defines the expiration strategy for the cache configuration
             * @return The cache configuration builder instance
             */
            @Nonnull
            public CacheConfigurationBuilder withExpirationStrategy(
                @Nonnull final CacheExpirationStrategy expirationStrategy )
            {
                this.expirationStrategy = expirationStrategy;
                return this;
            }

            /**
             * Instantiate the cache configuration with additional serializable parameters for the cache key.
             *
             * @param component
             *            A component to parameterize the cache configuration.
             * @param otherComponents
             *            Optionally further components to parameterize the cache.
             * @return The immutable cache configuration instance
             */
            @Nonnull
            public
                CacheConfiguration
                withParameters( @Nonnull final Serializable component, @Nonnull final Serializable... otherComponents )
            {
                final List<Object> components = Lists.newArrayList(component);
                Collections.addAll(components, otherComponents);
                return new CacheConfiguration(true, true, expirationDuration, expirationStrategy, components);
            }

            /**
             * Instantiate the cache configuration with additional non-serializable parameters for the cache key.
             *
             * @param component
             *            A component to parameterize the cache configuration.
             * @param otherComponents
             *            Optionally further components to parameterize the cache.
             * @return The immutable cache configuration instance
             */
            @Nonnull
            public
                CacheConfiguration
                withParameters( @Nonnull final Object component, @Nonnull final Object... otherComponents )
            {
                final List<Object> components = Lists.newArrayList(component);
                Collections.addAll(components, otherComponents);
                return new CacheConfiguration(true, false, expirationDuration, expirationStrategy, components);
            }

            /**
             * Instantiate the cache configuration without additional parameters for the cache key.
             *
             * @return The immutable cache configuration instance
             */
            @Nonnull
            public CacheConfiguration withoutParameters()
            {
                return new CacheConfiguration(
                    true,
                    true,
                    expirationDuration,
                    expirationStrategy,
                    Collections.emptyList());
            }
        }
    }

    /**
     * Provides options specific to timeouts.
     */
    @NoArgsConstructor( staticName = "of" )
    @Accessors( fluent = true )
    @EqualsAndHashCode
    @Getter
    @Setter
    public static final class TimeLimiterConfiguration
    {
        /**
         * The default timeout duration.
         */
        public static final Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(30);

        /**
         * The default behaviour for canceling running tasks.
         */
        public static final boolean DEFAULT_SHOULD_CANCEL_RUNNING_FUTURE = true;

        /**
         * Flag to indicate active TimeLimiter.
         */
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private boolean enabled = true;

        /**
         * The timeout duration.
         */
        @Nonnull
        private Duration timeoutDuration = DEFAULT_TIMEOUT_DURATION;

        /**
         * Whether to cancel the running future.
         */
        private boolean shouldCancelRunningFuture = DEFAULT_SHOULD_CANCEL_RUNNING_FUTURE;

        /**
         * Get the status indicator for TimeLimiter.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Creates a new {@code TimeLimiterConfiguration} that allows for requests to run indefinitely, effectively
         * disabling timeouts.
         *
         * @return A disabled time limiter.
         */
        @Nonnull
        public static TimeLimiterConfiguration disabled()
        {
            final TimeLimiterConfiguration timeLimiterConfiguration = new TimeLimiterConfiguration();
            timeLimiterConfiguration.enabled = false;
            return timeLimiterConfiguration;
        }

        /**
         * Create a timeout by specifying a timeout duration. <br>
         * Alternatively use {@code TimeLimiterConfiguration.of().timeoutDuration( int )} instead.
         *
         * @param timeoutDuration
         *            The maximum duration to wait for a request to return.
         * @return An immutable {@code TimeLimiterConfiguration}.
         */
        @Nonnull
        public static TimeLimiterConfiguration of( @Nonnull final Duration timeoutDuration )
        {
            return of().timeoutDuration(timeoutDuration);
        }
    }

    /**
     * Provides options specific to circuit breakers.
     *
     * @see <a href="https://resilience4j.readme.io/v0.17.0/docs/circuitbreaker">Circuit Breakers</a>
     */
    @NoArgsConstructor( staticName = "of" )
    @Accessors( fluent = true )
    @EqualsAndHashCode
    @Getter
    @Setter
    public static final class CircuitBreakerConfiguration
    {
        /**
         * The default threshold at which to transition to the <i>OPEN</i> state.
         */
        public static final float DEFAULT_FAILURE_RATE_THRESHOLD = 50;

        /**
         * The default size of the buffer in <i>CLOSED</i> state.
         */
        public static final int DEFAULT_CLOSED_BUFFER_SIZE = 10;

        /**
         * The default size of the buffer in <i>HALF OPEN</i> state.
         */
        public static final int DEFAULT_HALF_OPEN_BUFFER_SIZE = 5;

        /**
         * The default duration to wait in <i>OPEN</i> state before transitioning into the <i>HALF OPEN</i> state.
         */
        public static final Duration DEFAULT_WAIT_DURATION = Duration.ofSeconds(10);

        /**
         * Flag to indicate active CircuitBreakerConfiguration.
         */
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private boolean enabled = true;

        /**
         * The failure rate threshold (as percentage within [0, 100]).
         */
        private float failureRateThreshold = DEFAULT_FAILURE_RATE_THRESHOLD;

        /**
         * The wait duration in the OPEN state before transitioning into the <i>HALF OPEN</i> state.
         */
        @Nonnull
        private Duration waitDuration = DEFAULT_WAIT_DURATION;

        /**
         * The number of latest attempts in the <i>CLOSED</i> state to apply the threshold to. If the failure rate over
         * the last {@code closedRingBufferSize} calls exceeds the threshold, the circuit breaker will block further
         * calls (<i>OPEN</i> state). Be aware that this is the minimum number of attempts that must have occurred
         * before the circuit breaker can take effect.
         */
        private int closedBufferSize = DEFAULT_CLOSED_BUFFER_SIZE;

        /**
         * The number of latest attempts in the <i>HALF OPEN</i> state to apply the threshold to. Sets the amount of
         * attempts after which the circuit breaker will transition back to the CLOSED or OPEN state. Furthermore, this
         * is the maximum number of requests that may take place in parallel during the <i>HALF OPEN</i> state.
         */
        private int halfOpenBufferSize = DEFAULT_HALF_OPEN_BUFFER_SIZE;

        /**
         * Get the status indicator for the CircuitBreaker.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Creates a disabled {@code CircuitBreakerConfiguration}.
         *
         * @return A disabled time limiter.
         */
        @Nonnull
        public static CircuitBreakerConfiguration disabled()
        {
            final CircuitBreakerConfiguration circuitBreakerConfiguration = new CircuitBreakerConfiguration();
            circuitBreakerConfiguration.enabled = false;
            return circuitBreakerConfiguration;
        }
    }

    /**
     * Provides options specific to bulkheads.
     */
    @NoArgsConstructor( staticName = "of" )
    @Accessors( fluent = true )
    @EqualsAndHashCode
    @Getter
    @Setter
    public static final class BulkheadConfiguration
    {
        /**
         * The default maximum number of concurrent calls.
         */
        public static final int DEFAULT_MAX_CONCURRENT_CALLS = 50;

        /**
         * The default maximum duration a thread will wait for to enter the bulkhead.
         */
        public static final Duration DEFAULT_MAX_WAIT_DURATION = Duration.ofSeconds(60);

        /**
         * Flag to indicate active BulkheadConfiguration.
         */
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private boolean enabled = true;

        /**
         * The maximum number of concurrent calls.
         */
        private int maxConcurrentCalls = DEFAULT_MAX_CONCURRENT_CALLS;

        /**
         * The maximum duration the calling thread will wait to enter the bulkhead.
         */
        @Nonnull
        private Duration maxWaitDuration = DEFAULT_MAX_WAIT_DURATION;

        /**
         * Get the status indicator for the Bulkhead.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Instantiates a new {@code BulkheadConfiguration} that allows for unlimited concurrent calls, effectively
         * disabling the bulkhead functionality.
         *
         * @return A disabled {@code BulkheadConfiguration}.
         */
        @Nonnull
        public static BulkheadConfiguration disabled()
        {
            final BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration();
            bulkheadConfiguration.enabled = false;
            return bulkheadConfiguration;
        }
    }

    /**
     * Provides options specific to retries, which will reattempt failed requests a limited amount of times.
     */
    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    @AllArgsConstructor( access = AccessLevel.PRIVATE )
    @Accessors( fluent = true )
    @EqualsAndHashCode
    @Getter
    @Setter
    public static final class RetryConfiguration
    {
        /**
         * The default number for maximum attempts.
         */
        public static final int DEFAULT_MAX_ATTEMPTS = 3;

        /**
         * The default waiting duration time.
         */
        public static final Duration DEFAULT_WAIT_DURATION = Duration.ofMillis(500);

        /**
         * The default predicate for retrying if any exception occurs.
         */
        public static final Predicate<Throwable> DEFAULT_RETRY_ON_EXCEPTION_PREDICATE = any -> true;

        /**
         * Flag to indicate active RetryConfiguration.
         */
        @Getter( AccessLevel.NONE )
        @Setter( AccessLevel.NONE )
        private final boolean enabled;

        /**
         * The maximum number of total attempts. Set this to 1 to not retry failed attempts.
         */
        private final int maxAttempts;

        /**
         * The duration to wait before retrying.
         */
        @Nonnull
        private Duration waitDuration = DEFAULT_WAIT_DURATION;

        /**
         * A predicate which evaluates if an exception should be retried. The predicate must return true, if the
         * exception should be retried, otherwise it must return false. By default, all exceptions are retried.
         */
        @Nonnull
        private Predicate<Throwable> retryOnExceptionPredicate = DEFAULT_RETRY_ON_EXCEPTION_PREDICATE;

        /**
         * Get the status indicator for the Retry.
         *
         * @return True if the configuration is enabled.
         */
        public boolean isEnabled()
        {
            return enabled;
        }

        /**
         * Instantiate a new {@code RetryConfiguration} with the given parameters.
         *
         * @param maxAttempts
         *            The maximum number of attempts to successfully execute a request.
         * @param waitDuration
         *            The time duration the system will wait before retrying.
         * @return An immutable {@code RetryConfiguration}.
         */
        @Nonnull
        public static RetryConfiguration of( final int maxAttempts, @Nonnull final Duration waitDuration )
        {
            return new RetryConfiguration(true, maxAttempts, waitDuration, DEFAULT_RETRY_ON_EXCEPTION_PREDICATE);
        }

        /**
         * Instantiate a new {@code RetryConfiguration} with the given parameter. Before retrying the system will wait
         * for {@linkplain #DEFAULT_WAIT_DURATION}.
         *
         * @param maxAttempts
         *            The maximum number of attempts to successfully execute a request.
         * @return An instance of {@code RetryConfiguration}.
         */
        @Nonnull
        public static RetryConfiguration of( final int maxAttempts )
        {
            return new RetryConfiguration(true, maxAttempts);
        }

        /**
         * Instantiate a new {@code RetryConfiguration} that only permits a single attempt, effectively disabling
         * retries.
         *
         * @return A disabled instance of {@code RetryConfiguration}.
         */
        @Nonnull
        public static RetryConfiguration disabled()
        {
            return new RetryConfiguration(false, 1);
        }
    }

    /**
     * Factory function to create a resilience configuration with default values for the given identifier.
     * <p>
     * By default the following resilience features are active with default values:
     * <p>
     * <table border="1">
     * <tr>
     * <td>Timeouts</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Circuit Breaker</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Bulkhead</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Caching</td>
     * <td>Disabled</td>
     * </tr>
     * <tr>
     * <td>Retries</td>
     * <td>Disabled</td>
     * </tr>
     * <tr>
     * <td>Rate Limiter</td>
     * <td>Disabled</td>
     * </tr>
     * </table>
     * </p>
     *
     * @param identifier
     *            A unique identifier for this configuration.
     * @return A resilience configuration.
     */
    @Nonnull
    public static ResilienceConfiguration of( @Nonnull final String identifier )
    {
        return new ResilienceConfiguration(identifier);
    }

    /**
     * Factory function to create a resilience configuration with only a subset of resilience features enabled by
     * default.
     * <p>
     * By default the following resilience features are active with default values:
     * <p>
     * <table border="1">
     * <tr>
     * <td>Timeouts</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Circuit Breaker</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Bulkhead</td>
     * <td>Enabled</td>
     * </tr>
     * <tr>
     * <td>Caching</td>
     * <td>Disabled</td>
     * </tr>
     * <tr>
     * <td>Retries</td>
     * <td>Disabled</td>
     * </tr>
     * <tr>
     * <td>Rate Limiter</td>
     * <td>Disabled</td>
     * </tr>
     * </table>
     * </p>
     *
     * @param serviceClass
     *            The invoking caller class.
     * @return An new instance of resilience configuration.
     */
    @Nonnull
    public static ResilienceConfiguration of( @Nonnull final Class<?> serviceClass )
    {
        return of(serviceClass.getName());
    }

    /**
     * Factory function to create a resilience configuration with all resilience features disabled by default. Enable
     * them explicitly through the setters to only add the functionality that is needed.
     *
     * @param identifier
     *            A unique identifier for this configuration.
     * @return An empty resilience configuration.
     */
    @Nonnull
    public static ResilienceConfiguration empty( @Nonnull final String identifier )
    {
        return new ResilienceConfiguration(identifier)
            .rateLimiterConfiguration(RateLimiterConfiguration.disabled())
            .timeLimiterConfiguration(TimeLimiterConfiguration.disabled())
            .circuitBreakerConfiguration(CircuitBreakerConfiguration.disabled())
            .bulkheadConfiguration(BulkheadConfiguration.disabled())
            .retryConfiguration(RetryConfiguration.disabled())
            .cacheConfiguration(CacheConfiguration.disabled());
    }

    /**
     * Factory function to create a resilience configuration with all resilience features disabled by default. Enable
     * them explicitly through the setters to only add the functionality that is needed.
     *
     * @param serviceClass
     *            The invoking caller class.
     * @return An empty resilience configuration.
     */
    @Nonnull
    public static ResilienceConfiguration empty( @Nonnull final Class<?> serviceClass )
    {
        return empty(serviceClass.getName());
    }

    /**
     * Constructor for resilience configuration.
     *
     * @param identifier
     *            A unique identifier for this configuration.
     */
    protected ResilienceConfiguration( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }
}
