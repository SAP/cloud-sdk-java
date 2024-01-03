/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.Beta;
import com.google.common.collect.Streams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategyResolver.Strategy;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.resilience.CacheExpirationStrategy;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.CircuitBreakerConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration.TimeLimiterConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceIsolationMode;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Retrieves destination information from the SCP destination service on Cloud Foundry.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@SuppressWarnings( "PMD.TooManyStaticImports" )
public class DestinationService implements DestinationLoader
{
    private static final String PATH_DEFAULT = "/destinations/";

    private static final String PATH_SERVICE_INSTANCE = "/instanceDestinations";

    private static final String PATH_SUBACCOUNT = "/subaccountDestinations";

    static final TimeLimiterConfiguration DEFAULT_TIME_LIMITER =
        TimeLimiterConfiguration.of().timeoutDuration(Duration.ofSeconds(6));

    static final CircuitBreakerConfiguration DEFAULT_SINGLE_DEST_CIRCUIT_BREAKER =
        CircuitBreakerConfiguration.of().waitDuration(Duration.ofSeconds(6));

    static final CircuitBreakerConfiguration DEFAULT_ALL_DEST_CIRCUIT_BREAKER =
        CircuitBreakerConfiguration.of().waitDuration(Duration.ofMinutes(1));

    private static final Gson GSON = new Gson();

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final DestinationServiceAdapter adapter;

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ResilienceConfiguration singleDestResilience;

    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ResilienceConfiguration allDestResilience;

    /**
     * Create instance with all default settings
     */
    public DestinationService()
    {
        this(new DestinationServiceAdapter());
    }

    /**
     * Create instance with a specific adapter and default resilience
     */
    DestinationService( @Nonnull final DestinationServiceAdapter adapter )
    {
        this(
            adapter,
            createResilienceConfiguration(
                "singleDestResilience",
                DEFAULT_TIME_LIMITER,
                DEFAULT_SINGLE_DEST_CIRCUIT_BREAKER),
            createResilienceConfiguration("allDestResilience", DEFAULT_TIME_LIMITER, DEFAULT_ALL_DEST_CIRCUIT_BREAKER));
    }

    @Nonnull
    static ResilienceConfiguration createResilienceConfiguration(
        @Nonnull final String identifier,
        @Nonnull final TimeLimiterConfiguration timeLimiterConfiguration,
        @Nonnull final CircuitBreakerConfiguration circuitBreakerConfiguration )
    {
        return ResilienceConfiguration
            .of(DestinationService.class + identifier)
            .isolationMode(ResilienceIsolationMode.TENANT_OPTIONAL)
            .timeLimiterConfiguration(timeLimiterConfiguration)
            .circuitBreakerConfiguration(circuitBreakerConfiguration);
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull final DestinationOptions options )
    {
        return Cache.getOrComputeDestination(this, destinationName, options, this::loadAndParseDestination);
    }

    private Destination loadAndParseDestination( final String destName, final DestinationOptions options )
        throws DestinationAccessException,
            DestinationNotFoundException
    {
        final String servicePath = PATH_DEFAULT + destName;
        final Function<Strategy, DestinationServiceV1Response> destinationRetriever =
            strategy -> resilientCall(() -> retrieveDestination(strategy, servicePath), singleDestResilience);

        final DestinationRetrievalStrategyResolver destinationRetrievalStrategyResolver =
            DestinationRetrievalStrategyResolver
                .forSingleDestination(adapter::getProviderTenantId, destinationRetriever);

        final DestinationRetrieval retrieval = destinationRetrievalStrategyResolver.prepareSupplier(options);

        return DestinationServiceFactory.fromDestinationServiceV1Response(retrieval.get(), retrieval.getOnBehalfOf());
    }

    @Nonnull
    private DestinationServiceV1Response retrieveDestination( final Strategy strategy, final String servicePath )
    {
        final DestinationServiceV1Response response =
            deserializeDestinationResponse(
                strategy.isForwardToken()
                    ? adapter.getConfigurationAsJsonWithUserToken(servicePath, strategy.getBehalf())
                    : adapter.getConfigurationAsJson(servicePath, strategy.getBehalf()));

        // special handling for forward-token strategy: throw on header errors
        if( strategy.isForwardToken() && response.getAuthTokens() != null ) {
            response.getAuthTokens().forEach(AuthTokenHeaderProvider::throwOnHeaderError);
        }
        return response;
    }

    /**
     * Fetches all destinations on behalf of the subscriber. Convenience for
     * {@code tryGetAllDestinations(DestinationOptions.builder().build())}.
     * <p>
     * <strong>Caution: This will not perform any authorization flows for the destinations.</strong> Destinations
     * obtained this way should only be used for accessing the properties of the destination configuration.
     * </p>
     *
     * @return A Try list of destinations.
     * @see #tryGetAllDestinations(DestinationOptions)
     */
    @Nonnull
    public Try<Iterable<Destination>> tryGetAllDestinations()
    {
        return tryGetAllDestinations(DestinationOptions.builder().build());
    }

    /**
     * Retrieves destinations for the provided configuration options. In case there exist a destination with the same
     * name on service instance and on sub account level, then the method prioritizes the destination at service
     * instance level.
     * <p>
     * <strong>Caution: This will not perform any authorization flows for the destinations.</strong> Destinations
     * obtained this way should only be used for accessing the properties of the destination configuration.
     * </p>
     *
     * @param options
     *            Destination configuration object
     * @return A Try iterable of CF destinations.
     */
    @Nonnull
    public Try<Iterable<Destination>> tryGetAllDestinations( @Nonnull final DestinationOptions options )
    {
        return Cache.getOrComputeAllDestinations(options, this::getAllDestinationsByRetrievalStrategy).map(l -> l);
    }

    @Nonnull
    private Try<List<Destination>> getAllDestinationsByRetrievalStrategy( @Nonnull final DestinationOptions options )
    {
        final DestinationRetrievalStrategyResolver resolver =
            DestinationRetrievalStrategyResolver
                .forAllDestinations(adapter::getProviderTenantId, this::loadAndParseAllDestinations);

        return Try.success(options).map(resolver::prepareSupplierAllDestinations).map(Supplier::get);

    }

    @Nonnull
    private List<Destination> loadAndParseAllDestinations( @Nonnull final OnBehalfOf behalf )
    {
        // Priority list:
        // * Service Instance Destinations of same name > Subaccount Destinations of same name

        final List<Destination> serviceInstanceDestinations =
            resilientCall(() -> getAndDeserializeDestinations(PATH_SERVICE_INSTANCE, behalf), allDestResilience);
        final List<Destination> subAccountDestinations =
            resilientCall(() -> getAndDeserializeDestinations(PATH_SUBACCOUNT, behalf), allDestResilience);

        final Map<String, Destination> result = new LinkedHashMap<>();
        for( final Destination destination : serviceInstanceDestinations ) {
            result.putIfAbsent(destination.get(DestinationProperty.NAME).get(), destination);
        }
        for( final Destination destination : subAccountDestinations ) {
            result.putIfAbsent(destination.get(DestinationProperty.NAME).get(), destination);
        }
        log.debug("Loaded {} destinations: {}", result.size(), result.keySet());
        return new ArrayList<>(result.values());
    }

    @Nonnull
    private static DestinationServiceV1Response deserializeDestinationResponse( @Nonnull final String json )
        throws DestinationAccessException
    {
        try {
            return GSON.fromJson(json, DestinationServiceV1Response.class);
        }
        catch( final Exception e ) {
            log.debug("Failed to parse destination response payload: {}", json);
            final String message = "Illegal JSON response while fetching a destination from destination service";
            throw new DestinationAccessException(message, e);
        }
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    private
        List<Destination>
        getAndDeserializeDestinations( @Nonnull final String servicePath, @Nonnull final OnBehalfOf behalf )
            throws DestinationAccessException
    {
        final String json = adapter.getConfigurationAsJson(servicePath, behalf);
        return Streams
            .stream(GSON.fromJson(json, JsonElement.class).getAsJsonArray())
            .map(jsonElement -> (Map<String, Object>) GSON.fromJson(jsonElement, Map.class))
            .map(DefaultDestination::fromMap)
            .peek(d -> {
                if( d.get(DestinationProperty.NAME).isEmpty() ) {
                    throw new DestinationAccessException(
                        "Found a destination without name. A name is required for destinations defined in the destination service.");
                }
            })
            .map(DefaultDestination.Builder::build)
            .collect(Collectors.toList());
    }

    /**
     * @param supplier
     *            function that retrieves destinations
     * @param <T>
     *            the type of the return
     * @return the result of the call if successful
     * @throws DestinationAccessException
     *             or
     * @throws DestinationNotFoundException
     *             the cause of the exception, thus hiding the resilience exception
     */
    private <
        T> T resilientCall( @Nonnull final Supplier<T> supplier, @Nonnull final ResilienceConfiguration configuration )
    {
        try {
            return ResilienceDecorator.executeSupplier(supplier, configuration);
        }
        catch( final ResilienceRuntimeException e ) {
            if( hasCauseAssignableFrom(e, DestinationNotFoundException.class) ) {
                throw new DestinationNotFoundException(e);
            }
            throw new DestinationAccessException("Failed to get destination.", e);
        }
    }

    private static boolean hasCauseAssignableFrom( @Nonnull final Throwable t, @Nonnull final Class<?> cls )
    {
        return ExceptionUtils.getThrowableList(t).stream().map(Throwable::getClass).anyMatch(cls::isAssignableFrom);
    }

    /**
     * Helper class that encapsulates all caching related configuration options.
     *
     * @since 4.3.0
     */
    @Slf4j
    @Beta
    public static final class Cache
    {
        /**
         * The default size limit of the destination cache.
         */
        public static final long DEFAULT_SIZE_LIMIT = 1000L;

        /**
         * The default expiration duration of the destination cache.
         */
        public static final Duration DEFAULT_EXPIRATION_DURATION = Duration.ofMinutes(5L);

        /**
         * The default {@link CacheExpirationStrategy} of the destination cache.
         */
        public static final CacheExpirationStrategy DEFAULT_EXPIRATION_STRATEGY = CacheExpirationStrategy.WHEN_CREATED;

        @Nonnull
        private static Option<Long> sizeLimit = Option.some(DEFAULT_SIZE_LIMIT);
        @Nonnull
        @Getter( AccessLevel.PACKAGE )
        private static Option<Duration> expirationDuration = Option.some(DEFAULT_EXPIRATION_DURATION);
        @Nonnull
        private static CacheExpirationStrategy expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;
        @Nonnull
        private static Option<com.github.benmanes.caffeine.cache.Cache<CacheKey, Destination>> destinationsCache =
            Option.none();
        @Nonnull
        private static Option<com.github.benmanes.caffeine.cache.Cache<CacheKey, List<Destination>>> allDestinationsCache =
            Option.none();
        @Nonnull
        private static Option<com.github.benmanes.caffeine.cache.Cache<CacheKey, ReentrantLock>> isolationLocks =
            Option.none();

        private static boolean cacheEnabled = true;
        private static boolean changeDetectionEnabled = true;

        static {
            recreateSingleCache();
            recreateGetAllCache();
            recreateIsolationLockCache();
        }

        static boolean isEnabled()
        {
            return cacheEnabled;
        }

        static boolean isChangeDetectionEnabled()
        {
            return changeDetectionEnabled;
        }

        @Nonnull
        static com.github.benmanes.caffeine.cache.Cache<CacheKey, Destination> instanceSingle()
        {
            throwIfDisabled();
            return destinationsCache.get();
        }

        @Nonnull
        static com.github.benmanes.caffeine.cache.Cache<CacheKey, List<Destination>> instanceAll()
        {
            throwIfDisabled();
            return allDestinationsCache.get();
        }

        @Nonnull
        static com.github.benmanes.caffeine.cache.Cache<CacheKey, ReentrantLock> isolationLocks()
        {
            throwIfDisabled();
            return isolationLocks.get();
        }

        /**
         * Disables the entire destination cache.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a deletion of the destination cache. As a
         * consequence, destinations will always be fetched from the destination service, which might be slow.
         */
        public static void disable()
        {
            log.debug("Disabling the destination cache.");
            cacheEnabled = false;
            destinationsCache = prepareCache(null, destinationsCache);
            allDestinationsCache = prepareCache(null, allDestinationsCache);
            isolationLocks = prepareCache(null, isolationLocks);
        }

        /**
         * This method will set the destination cache to a new cache instance with default parameters.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost.
         */
        static void reset()
        {
            log.warn("Resetting the destination cache. This should not be done outside of testing.");
            if( !isEnabled() ) {
                cacheEnabled = true;
            }
            changeDetectionEnabled = true;

            sizeLimit = Option.some(DEFAULT_SIZE_LIMIT);
            expirationDuration = Option.some(DEFAULT_EXPIRATION_DURATION);
            expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;
            recreateSingleCache();
            recreateGetAllCache();
            recreateIsolationLockCache();
        }

        /**
         * Sets the size limit of the destination cache.
         * <p>
         * Please note that the size limit only applies for the cache of individual destinations.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost.
         *
         * @param size
         *            The size limit.
         *            <p>
         *            Using a size of {@code 0} will effectively disable the cache, as it won't store any values.
         *            Consider using {@link #disable()} instead.
         *            <p>
         *            To allow the storage of an infinite number of cache entries, use {@link #disableSizeLimit()}. You
         *            can still remove entries manually through {@link CacheManager#invalidateAll()}.
         */
        public static void setSizeLimit( @Nonnegative final long size )
        {
            throwIfDisabled();
            log.debug("Setting destination cache size limit to {}.", size);

            sizeLimit = Option.some(size);

            recreateSingleCache();
        }

        /**
         * Disables the size limit of the destination cache. You can still remove entries manually through
         * {@link CacheManager#invalidateAll()}.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost. Additionally, using an unbounded cache might lead to
         * memory exhaustion.
         */
        public static void disableSizeLimit()
        {
            throwIfDisabled();
            log.debug("Disabling destination cache size limit. The cache now stores an infinite number of entries.");

            sizeLimit = Option.none();
            recreateSingleCache();
        }

        /**
         * Sets the expiration duration and strategy for the destination cache.
         * <p>
         * In case <strong>change detection</strong> is enabled, this sets the interval in which the cache will check
         * for changes. In that case, the given strategy will only be applied to the cache for all destinations.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost.
         *
         * @param duration
         *            The expiration duration.
         *            <p>
         *            Using a duration of {@code Duration.ZERO} will effectively disable the cache, as all entries will
         *            expire immediately. Consider using {@link #disable()} instead.
         *            <p>
         *            To allow storing entries forever, use {@link #disableExpiration()}. You can still remove entries
         *            manually through {@link CacheManager#invalidateAll()}.
         * @param strategy
         *            The {@link CacheExpirationStrategy}.
         */
        public static
            void
            setExpiration( @Nonnull final Duration duration, @Nonnull final CacheExpirationStrategy strategy )
        {
            throwIfDisabled();
            log.debug("Setting destination cache expiration to {} {}.", duration, strategy.name());

            expirationDuration = Option.some(duration);
            expirationStrategy = strategy;

            recreateSingleCache();
            recreateGetAllCache();
        }

        /**
         * Disables the automatic entry expiration for the destination cache. Destinations will still be fetched if the
         * attached JWT token is expired. Additionally, you can still remove entries manually through
         * {@link CacheManager#invalidateAll()}.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost. Changes of the destination configuration via, for
         * example, the BTP cockpit will not be picked up for as long as the destination is within the cache - this
         * might be forever depending on the configured cache size.
         */
        public static void disableExpiration()
        {
            throwIfDisabled();
            log
                .debug(
                    "Destination cache expiration has been disabled. The cache will now keep its entries until evicted (potentially forever).");

            expirationDuration = Option.none();

            if( changeDetectionEnabled ) {
                log
                    .warn(
                        "Using the 'change detection' mode is not supported when disabling the Destination cache expiration. "
                            + "Therefore, change detection mode will be disabled from now on.");
                disableChangeDetection();
                return;
            }

            recreateSingleCache();
            recreateGetAllCache();
        }

        /**
         * Enables the so-called <em>"change detection"</em> mode.
         * <p>
         * Enabling the <em>change detection</em> mode has the following implications: <br>
         * 1. Destinations will be cached for a longer period of time.<br>
         * 2. The {@link DestinationService.Cache} will regularly check for updates in the Destination service.<br>
         * 3. Individual destinations will be re-fetched from the Destination service if <strong>either (A)</strong> an
         * authentication token is expired (same behavior as without the <em>change detection</em> mode) <strong>or
         * (B)</strong> the destination has been updated recently in the Destination service (e.g. via the BTP Cockpit),
         * <strong>or (C)</strong> the destination references a certificate.
         * <p>
         * The interval at which the SDK checks for changes can be configured via
         * {@link #setExpiration(Duration, CacheExpirationStrategy)}.
         * <p>
         * The <em>change detection</em> mode can significantly improve caching performance as individual destination
         * may stay cached much longer while still staying response with regard to changes in the Destination service.
         * In other words: Instead of doing (unnecessary) expensive destination retrievals for individual destinations,
         * there is only one cheap request to the Destination service that is valid for all destinations at once.
         * <p>
         * Please note that certificate based destinations are not cached for longer. Because if a certificate changes
         * but keeps the same name, the change can not be detected by the SDK. In other words, the change detection
         * feature does not apply to certificate based destinations, they are cached as if this was disabled.
         * <p>
         * <strong>Caution:</strong> This method is not thread-safe.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache. As a
         * consequence, all existing cache entries will be lost.
         *
         * @deprecated since 5.1.0. Change detection mode is enabled by default
         *
         * @since 4.7.0
         */
        @Beta
        @Deprecated
        public static void enableChangeDetection()
        {
            throwIfDisabled();
            if( changeDetectionEnabled ) {
                return;
            }

            changeDetectionEnabled = true;
            log.debug("Destination change detection has been enabled.");

            if( !expirationDuration.isDefined() ) {
                log
                    .warn(
                        String
                            .format(
                                "Using the 'change detection' mode is not supported with disabled Destination cache expiration. "
                                    + "Therefore, the default expiration (%s %s) will be restored.",
                                DEFAULT_EXPIRATION_DURATION,
                                DEFAULT_EXPIRATION_STRATEGY));

                expirationDuration = Option.some(DEFAULT_EXPIRATION_DURATION);
                expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;
            }
            allDestinationsCache =
                prepareCache(
                    prepareCacheBuilder(Option.none(), expirationDuration, CacheExpirationStrategy.WHEN_CREATED)
                        .build(),
                    allDestinationsCache);
            destinationsCache =
                prepareCache(
                    prepareCacheBuilder(sizeLimit, Option.some(Duration.ofDays(1L)), expirationStrategy).build(),
                    destinationsCache);
        }

        /**
         * Disables the <em>"change detection"</em> mode.
         * <p>
         * <strong>Caution:</strong> Using this operation will lead to a re-creation of the destination cache.
         *
         * @since 5.1.0
         */
        @Beta
        public static void disableChangeDetection()
        {
            throwIfDisabled();
            if( !changeDetectionEnabled ) {
                return;
            }

            changeDetectionEnabled = false;
            log.debug("Destination change detection has been disabled.");

            recreateSingleCache();
            recreateGetAllCache();
        }

        private static void recreateSingleCache()
        {
            if( !changeDetectionEnabled ) {
                destinationsCache = prepareCache(prepareCacheBuilder().build(), destinationsCache);
                return;
            }
            if( !expirationDuration.isDefined() ) {
                log
                    .warn(
                        String
                            .format(
                                "Using the 'change detection' mode is not supported with disabled Destination cache expiration. "
                                    + "Therefore, the default expiration strategy (%s) will be restored.",
                                DEFAULT_EXPIRATION_STRATEGY));
                expirationStrategy = DEFAULT_EXPIRATION_STRATEGY;
            }
            destinationsCache =
                prepareCache(
                    prepareCacheBuilder(sizeLimit, Option.some(Duration.ofDays(1L)), expirationStrategy).build(),
                    destinationsCache);
        }

        private static void recreateGetAllCache()
        {
            if( !changeDetectionEnabled ) {
                allDestinationsCache =
                    prepareCache(
                        prepareCacheBuilder(Option.none(), expirationDuration, expirationStrategy).build(),
                        allDestinationsCache);
                return;
            }
            if( !expirationDuration.isDefined() ) {
                log
                    .warn(
                        String
                            .format(
                                "Using the 'change detection' mode is not supported with disabled Destination cache expiration. "
                                    + "Therefore, the default expiration duration (%s) will be restored.",
                                DEFAULT_EXPIRATION_DURATION));

                expirationDuration = Option.some(DEFAULT_EXPIRATION_DURATION);
            }
            allDestinationsCache =
                prepareCache(
                    prepareCacheBuilder(Option.none(), expirationDuration, CacheExpirationStrategy.WHEN_CREATED)
                        .build(),
                    allDestinationsCache);
        }

        private static void recreateIsolationLockCache()
        {
            isolationLocks =
                prepareCache(Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(30)).build(), isolationLocks);
        }

        private static <V> Option<com.github.benmanes.caffeine.cache.Cache<CacheKey, V>> prepareCache(
            @Nullable final com.github.benmanes.caffeine.cache.Cache<CacheKey, V> newCache,
            @Nonnull final Option<com.github.benmanes.caffeine.cache.Cache<CacheKey, V>> existingCache )
        {
            if( existingCache.isDefined() ) {
                logCacheModifiedWarning(existingCache.get());

                CacheManager.unregister(existingCache.get());
                existingCache.get().invalidateAll();
                existingCache.get().cleanUp();
            }

            if( newCache == null ) {
                return Option.none();
            }

            return Option.some(CacheManager.register(newCache));
        }

        private static void logCacheModifiedWarning(
            @Nonnull final com.github.benmanes.caffeine.cache.Cache<CacheKey, ?> cache )
        {
            if( !log.isWarnEnabled() ) {
                return;
            }

            if( cache.estimatedSize() > 0L ) {
                log
                    .warn(
                        "The destination cache is changed even though there are already entries within the cache. "
                            + "Those entries will be deleted, which might result in performance degradation. "
                            + "Consider configuring the cache only once at application startup to avoid this issue.");
            }
        }

        @Nonnull
        private static Caffeine<Object, Object> prepareCacheBuilder()
        {
            return prepareCacheBuilder(sizeLimit, expirationDuration, expirationStrategy);
        }

        @Nonnull
        private static Caffeine<Object, Object> prepareCacheBuilder(
            @Nonnull final Option<Long> sizeLimit,
            @Nonnull final Option<Duration> expirationDuration,
            @Nonnull final CacheExpirationStrategy expirationStrategy )
        {
            Caffeine<Object, Object> builder = Caffeine.newBuilder();

            if( sizeLimit.isDefined() ) {
                builder = builder.maximumSize(sizeLimit.get());
            }

            if( expirationDuration.isDefined() ) {
                switch( expirationStrategy ) {
                    case WHEN_CREATED: // fallthrough
                    case WHEN_LAST_MODIFIED: {
                        builder = builder.expireAfterWrite(expirationDuration.get());
                        break;
                    }
                    case WHEN_LAST_ACCESSED: // fallthrough
                    case WHEN_LAST_TOUCHED: {
                        builder = builder.expireAfterAccess(expirationDuration.get());
                        break;
                    }
                    default: {
                        throw new IllegalStateException(
                            String
                                .format(
                                    "Unhandled '%s': %s.",
                                    CacheExpirationStrategy.class.getName(),
                                    expirationStrategy.name()));
                    }
                }
            }
            return builder;
        }

        private static Try<Destination> getOrComputeDestination(
            @Nonnull final DestinationService loader,
            @Nonnull final String destinationName,
            @Nonnull final DestinationOptions options,
            @Nonnull final BiFunction<String, DestinationOptions, Destination> destinationDownloader )
        {
            if( !cacheEnabled ) {
                return Try.ofSupplier(() -> destinationDownloader.apply(destinationName, options));
            }
            @Nullable
            final GetOrComputeAllDestinationsCommand getAllCommand;
            if( changeDetectionEnabled ) {
                getAllCommand =
                    GetOrComputeAllDestinationsCommand
                        .prepareCommand(
                            options,
                            instanceAll(),
                            isolationLocks(),
                            loader::getAllDestinationsByRetrievalStrategy);
            } else {
                getAllCommand = null;
            }

            final Try<GetOrComputeSingleDestinationCommand> command =
                GetOrComputeSingleDestinationCommand
                    .prepareCommand(
                        destinationName,
                        options,
                        instanceSingle(),
                        isolationLocks(),
                        destinationDownloader,
                        getAllCommand);
            return command.flatMap(GetOrComputeSingleDestinationCommand::execute);
        }

        private static Try<List<Destination>> getOrComputeAllDestinations(
            @Nonnull final DestinationOptions options,
            @Nonnull final Function<DestinationOptions, Try<List<Destination>>> destinationDownloader )
        {
            if( !cacheEnabled ) {
                return destinationDownloader.apply(options);
            }

            return GetOrComputeAllDestinationsCommand
                .prepareCommand(options, instanceAll(), isolationLocks(), destinationDownloader)
                .execute();
        }

        private Cache()
        {
            throw new IllegalStateException("This static class must never be instantiated.");
        }

        private static void throwIfDisabled()
        {
            if( !cacheEnabled ) {
                throw new IllegalStateException("Attempted to access or configure the cache after disabling it.");
            }
        }
    }

    /**
     * Static builder.
     *
     * @return A builder to prepare a customised instance.
     * @since 4.4.0
     */
    @Beta
    @Nonnull
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Static builder class.
     *
     * @since 4.4.0
     */
    @Beta
    @NoArgsConstructor( access = AccessLevel.PRIVATE )
    public static final class Builder
    {
        @Nullable
        private TimeLimiterConfiguration timeLimiterConfiguration = null;
        @Nullable
        private String providerTenantId;

        /**
         * Create instance applying the given timeout when retrieving destinations.
         *
         * @param timeLimiterConfiguration
         *            The time limiter configuration to be applied for the request. Use
         *            {@code TimeLimiterConfiguration.of().timeoutDuration( int )} to create a time limiter
         *            configuration
         * @return The builder itself.
         * @since 4.4.0
         */
        @Nonnull
        public Builder withTimeLimiterConfiguration( @Nonnull final TimeLimiterConfiguration timeLimiterConfiguration )
        {
            this.timeLimiterConfiguration = timeLimiterConfiguration;
            return this;
        }

        @Nonnull
        Builder withProviderTenant( @Nonnull final Tenant providerTenant )
        {
            this.providerTenantId = providerTenant.getTenantId();
            return this;
        }

        /**
         * Create the configured {@code ScpCfDestinationLoader} instance.
         *
         * @return The new instance.
         * @since 4.4.0
         */
        @Nonnull
        public DestinationService build()
        {
            final TimeLimiterConfiguration timeLimiter =
                timeLimiterConfiguration != null ? timeLimiterConfiguration : DEFAULT_TIME_LIMITER;
            return new DestinationService(
                new DestinationServiceAdapter(null, null, providerTenantId),
                createResilienceConfiguration("singleDestResilience", timeLimiter, DEFAULT_SINGLE_DEST_CIRCUIT_BREAKER),
                createResilienceConfiguration("allDestResilience", timeLimiter, DEFAULT_ALL_DEST_CIRCUIT_BREAKER));
        }
    }
}
