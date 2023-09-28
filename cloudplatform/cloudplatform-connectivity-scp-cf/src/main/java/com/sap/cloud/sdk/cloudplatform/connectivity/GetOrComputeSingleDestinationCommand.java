package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.base.Functions;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
class GetOrComputeSingleDestinationCommand
{
    private static final long EXPIRATION_BUFFER_TIME = 10L; // seconds

    @Nonnull
    private final String destinationName;
    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final CacheKey cacheKey;
    @Nullable
    @Getter( AccessLevel.PACKAGE )
    private final CacheKey additionalKeyWithTenantAndPrincipal;
    @Nonnull
    private final ReentrantLock isolationLock;
    @Nonnull
    private final Cache<CacheKey, Destination> destinationCache;
    @Nonnull
    private final Supplier<Destination> destinationSupplier;
    @Nonnull
    @Getter( AccessLevel.PACKAGE )
    private final ScpCfDestinationTokenExchangeStrategy exchangeStrategy;
    @Nullable
    private final GetOrComputeAllDestinationsCommand getAllCommand;

    static Try<GetOrComputeSingleDestinationCommand> prepareCommand(
        @Nonnull final String destinationName,
        @Nonnull final DestinationOptions destinationOptions,
        @Nonnull final Cache<CacheKey, Destination> destinationCache,
        @Nonnull final Cache<CacheKey, ReentrantLock> isolationLocks,
        @Nonnull final BiFunction<String, DestinationOptions, Destination> destinationRetriever,
        @Nullable final GetOrComputeAllDestinationsCommand getAllCommand )
    {
        final Supplier<Destination> destinationSupplier =
            () -> destinationRetriever.apply(destinationName, destinationOptions);

        final ScpCfDestinationTokenExchangeStrategy exchangeStrategy =
            ScpCfDestinationOptionsAugmenter
                .getTokenExchangeStrategy(destinationOptions)
                .getOrElse(LOOKUP_THEN_EXCHANGE);

        final CacheKey cacheKey;
        CacheKey additionalKeyWithTenantAndPrincipal = null;
        if( exchangeStrategy == EXCHANGE_ONLY ) {
            try {
                cacheKey = CacheKey.ofTenantAndPrincipalIsolation();
            }
            catch( final TenantAccessException | PrincipalAccessException e ) {
                return Try
                    .failure(
                        new DestinationAccessException(
                            "Failed to determine tenant/principal information required for destination token exchange strategy "
                                + EXCHANGE_ONLY,
                            e));
            }
        } else {
            cacheKey = CacheKey.ofTenantOptionalIsolation();
            if( exchangeStrategy == LOOKUP_THEN_EXCHANGE || exchangeStrategy == FORWARD_USER_TOKEN ) {
                additionalKeyWithTenantAndPrincipal =
                    CacheKey.ofTenantAndPrincipalOptionalIsolation().append(destinationName, destinationOptions);
            }
        }
        cacheKey.append(destinationName, destinationOptions);

        final ReentrantLock isolationLock =
            Objects.requireNonNull(isolationLocks.get(cacheKey, any -> new ReentrantLock()));

        return Try
            .success(
                new GetOrComputeSingleDestinationCommand(
                    destinationName,
                    cacheKey,
                    additionalKeyWithTenantAndPrincipal,
                    isolationLock,
                    destinationCache,
                    destinationSupplier,
                    exchangeStrategy,
                    getAllCommand));
    }

    /**
     * <pre>
     *  The table illustrates how the Exchange key affects the isolation and cache keys in the implementation below
     * | #   | Lookup Strategy      | Destination Type   | Isolation Key      | Cache Key          | Comment                    |
     * |-----|----------------------|--------------------|--------------------|--------------------|----------------------------|
     * | 1   | Lookup only          | User Propagation   | Tenant             | Tenant             | warning logged             |
     * | 2   | Lookup then exchange | User Propagation   | Tenant             | Tenant + Principal |                            |
     * | 3   | Exchange only        | User Propagation   | Tenant + Principal | Tenant + Principal |                            |
     * | 4   | Forward user token
     *        (JWT is available)    | User Propagation   | Tenant             | Tenant + Principal |                            |
     * | 5   | Forward user token
     *         (JWT is unavailable) | User Propagation   | Tenant             |   -                |Retrieving destination fails|
     * | 6   | Lookup only          | Client Credentials | Tenant             | Tenant             |                            |
     * | 7   | Lookup then exchange | Client Credentials | Tenant             | Tenant             |                            |
     * | 8   | Exchange only        | Client Credentials | Tenant + Principal | Tenant + Principal | warning logged             |
     * | 9   | Forward user token   | Client Credentials | Tenant             | Tenant             |                            |
     * </pre>
     */
    @Nonnull
    Try<Destination> execute()
    {
        @Nullable
        Destination result = getCachedDestination();

        if( result != null ) {
            return Try.success(result);
        }

        try {
            isolationLock.lock();

            // double-checked locking
            result = getCachedDestination();
            if( result != null ) {
                return Try.success(result);
            }

            final Try<Destination> maybeResult = Try.ofSupplier(destinationSupplier);
            if( maybeResult.isFailure() ) {
                return maybeResult;
            }

            result = maybeResult.get();

            switch( exchangeStrategy ) {
                case LOOKUP_ONLY:
                case EXCHANGE_ONLY:
                    destinationCache.put(cacheKey, result);
                    logErroneousCombinations(result, destinationName, exchangeStrategy);
                    break;
                case LOOKUP_THEN_EXCHANGE:
                case FORWARD_USER_TOKEN:
                    if( !DestinationUtility.requiresUserTokenExchange(result) ) {
                        destinationCache.put(cacheKey, result);
                    } else {
                        //At this point principal ID is always available, otherwise maybeResult would be a failure
                        throwIfPrincipalIsUnavailable(destinationName, exchangeStrategy);
                        destinationCache.put(additionalKeyWithTenantAndPrincipal, result);
                    }
                    break;
            }
            return Try.success(result);
        }
        finally {
            isolationLock.unlock();
        }
    }

    private void throwIfPrincipalIsUnavailable(
        @Nonnull final String destinationName,
        @Nonnull final ScpCfDestinationTokenExchangeStrategy exchangeStrategy )
    {
        if( additionalKeyWithTenantAndPrincipal.getPrincipalId().isEmpty() ) {
            throw new IllegalStateException(
                "Principal ID is not available in the incoming request, but is required for fetching destination "
                    + destinationName
                    + "that requires user token exchange with strategy"
                    + exchangeStrategy);
        }
    }

    private void logErroneousCombinations(
        @Nonnull final DestinationProperties result,
        @Nonnull final String destinationName,
        @Nonnull final ScpCfDestinationTokenExchangeStrategy exchangeStrategy )
    {

        if( DestinationUtility.requiresUserTokenExchange(result) && exchangeStrategy == LOOKUP_ONLY ) {
            log
                .debug(
                    "The destination {} was retrieved with strategy {}, but it requires user token exchange."
                        + " Hence, the destination cannot be used to connect to the target system successfully, please refer to the documentation of {} to find the recommended strategy."
                        + " Caching the destination for all users of the current tenant since it was obtained without user token exchange. ",
                    destinationName,
                    exchangeStrategy,
                    ScpCfDestinationTokenExchangeStrategy.class.getSimpleName());
        }

        if( !DestinationUtility.requiresUserTokenExchange(result) && exchangeStrategy == EXCHANGE_ONLY ) {
            log
                .warn(
                    "The destination {} was retrieved with strategy {}, but doesn't require user token exchange."
                        + " This is not recommended, please refer to the documentation of {}."
                        + " Caching the destination for the current user of the current tenant since it was obtained with a user token.",
                    destinationName,
                    exchangeStrategy,
                    ScpCfDestinationTokenExchangeStrategy.class.getSimpleName());
        }
    }

    @Nullable
    private Destination getCachedDestination()
    {
        @Nullable
        Destination maybeDestination = destinationCache.getIfPresent(cacheKey);

        if( maybeDestination == null && additionalKeyWithTenantAndPrincipal != null ) {
            maybeDestination = destinationCache.getIfPresent(additionalKeyWithTenantAndPrincipal);
        }
        if( maybeDestination == null ) {
            return null;
        }
        if( authTokenIsExpired(maybeDestination) || certificateIsExpired(maybeDestination) ) {
            return null;
        }
        if( getAllCommand == null ) {
            return maybeDestination;
        }

        final Try<List<Destination>> allDestinations = getAllCommand.execute();
        if( allDestinations.isFailure() ) {
            final String message =
                "Failed to resolve all destinations of the current tenant from the destination service."
                    + " Cannot perform change detection. {} will therefore be assumed to remain unchanged.";

            log.warn(message, destinationName);
            // log the message two times because many lines might be inserted between the logs because of multi-threading
            log.debug(message, destinationName, allDestinations.getCause());
            return maybeDestination;
        }
        if( destinationIsChanged(allDestinations.get(), maybeDestination) ) {
            return null;
        }
        return maybeDestination;
    }

    /**
     * currently this effectively limits the cache duration to the change detection interval, because change detection
     * on certificates isn't possible in all cases See the note on {@link ScpCfDestinationFactory} where this property
     * is set
     */
    private static boolean certificateIsExpired( final Destination destination )
    {
        return destination
            .get(DestinationProperty.CERTIFICATES)
            .toStream()
            .flatMap(Functions.identity())
            .map(t -> ((ScpCfDestinationServiceV1Response.DestinationCertificate) t).getExpiryTimestamp())
            .filter(Objects::nonNull)
            .min()
            .filter(t -> LocalDateTime.now().plusSeconds(EXPIRATION_BUFFER_TIME).isAfter(t))
            .isDefined();
    }

    private static boolean authTokenIsExpired( @Nonnull final Destination destination )
    {
        return destination
            .get(DestinationProperty.AUTH_TOKENS)
            .toStream()
            .flatMap(Functions.identity())
            .map(t -> ((ScpCfDestinationServiceV1Response.DestinationAuthToken) t).getExpiryTimestamp())
            .filter(Objects::nonNull)
            .min()
            .filter(t -> LocalDateTime.now().plusSeconds(EXPIRATION_BUFFER_TIME).isAfter(t))
            .isDefined();
    }

    private static boolean destinationIsChanged(
        @Nonnull final List<Destination> allDestinations,
        @Nonnull final Destination cachedDestination )
    {
        final String destinationName = cachedDestination.get(DestinationProperty.NAME).get();
        final Destination matchingDestination =
            allDestinations
                .stream()
                .filter(destination -> destination.get(DestinationProperty.NAME).contains(destinationName))
                .findFirst()
                .orElse(null);
        if( matchingDestination == null ) {
            return true;
        }
        return !destinationIsEqualTo(matchingDestination, cachedDestination);
    }

    /**
     * Checks whether all properties of {@code destinationFromGetAllEndPoint} are also contained in
     * {@code individuallyRetrievedDestination}. Additional properties of {@code individuallyRetrievedDestination} are
     * <strong>not considered</strong> fpr the comparison.
     * <p>
     * <strong>Caution:</strong> This operation is <strong>not</strong> symmetric!
     *
     * @param destinationFromGetAllEndPoint
     *            The {@link DestinationProperties} as retrieved via
     *            {@link ScpCfDestinationLoader#tryGetAllDestinations()}.
     * @param individuallyRetrievedDestination
     *            The {@link DestinationProperties} as retrieved via
     *            {@link ScpCfDestinationLoader#tryGetDestination(String)}.
     * @return A boolean value that indicates whether all properties of {@code destinationFromGetAllEndPoint} are also
     *         contained in {@code individuallyRetrievedDestination}.
     */
    // internal for testing
    static boolean destinationIsEqualTo(
        @Nonnull final Destination destinationFromGetAllEndPoint,
        @Nonnull final Destination individuallyRetrievedDestination )
    {
        for( final String propertyName : destinationFromGetAllEndPoint.getPropertyNames() ) {
            final Option<Object> expectedProperty = destinationFromGetAllEndPoint.get(propertyName);
            final Option<Object> actualProperty = individuallyRetrievedDestination.get(propertyName);

            if( !expectedProperty.equals(actualProperty) ) {
                log.debug("Detected change in destination property {}", propertyName);
                return false;
            }
        }
        for( final String propertyName : individuallyRetrievedDestination
            .get(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION)
            .get() ) {
            final Option<Object> expectedProperty = individuallyRetrievedDestination.get(propertyName);
            final Option<Object> actualProperty = destinationFromGetAllEndPoint.get(propertyName);

            if( !expectedProperty.equals(actualProperty) ) {
                log.debug("Detected change in destination property {}", propertyName);
                return false;
            }
        }
        return true;
    }
}
