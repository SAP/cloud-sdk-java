/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
class GetOrComputeAllDestinationsCommand
{
    @Nonnull
    private final CacheKey cacheKey;
    @Nonnull
    private final ReentrantLock isolationLock;
    @Nonnull
    private final Cache<CacheKey, List<DestinationProperties>> cache;
    @Nonnull
    private final Supplier<Try<List<DestinationProperties>>> destinationSupplier;

    static GetOrComputeAllDestinationsCommand prepareCommand(
        @Nonnull final DestinationOptions destinationOptions,
        @Nonnull final Cache<CacheKey, List<DestinationProperties>> destinationCache,
        @Nonnull final Cache<CacheKey, ReentrantLock> isolationLocks,
        @Nonnull final Function<DestinationOptions, Try<List<DestinationProperties>>> destinationRetriever )
    {
        final Supplier<Try<List<DestinationProperties>>> destinationSupplier =
            () -> destinationRetriever.apply(destinationOptions);

        final CacheKey cacheKey = CacheKey.ofTenantOptionalIsolation();

        cacheKey.append(destinationOptions);

        final ReentrantLock isolationLock =
            Objects.requireNonNull(isolationLocks.get(cacheKey, any -> new ReentrantLock()));

        return new GetOrComputeAllDestinationsCommand(cacheKey, isolationLock, destinationCache, destinationSupplier);
    }

    @Nonnull
    Try<List<DestinationProperties>> execute()
    {
        @Nullable
        List<DestinationProperties> destinations = cache.getIfPresent(cacheKey);

        if( destinations != null ) {
            return Try.success(destinations);
        }

        try {
            isolationLock.lock();

            // double-checked locking
            destinations = cache.getIfPresent(cacheKey);
            if( destinations != null ) {
                return Try.success(destinations);
            }

            final Try<List<DestinationProperties>> result = destinationSupplier.get();
            if( result.isFailure() ) {
                return result;
            }
            cache.put(cacheKey, result.get());
            return result;
        }
        finally {
            isolationLock.unlock();
        }
    }
}
