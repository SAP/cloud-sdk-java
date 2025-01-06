/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

/**
 * JMX wrapper interface for the Guava {@link com.sap.cloud.sdk.cloudplatform.cache.CacheStats}.
 */
public interface CacheStatsMXBean
{
    /**
     * Returns the number of times {@link com.google.common.cache.Cache} lookup methods have returned a cached value.
     *
     * @return The number of successful cache lookups.
     *
     * @see com.google.common.cache.CacheStats#hitCount()
     */
    long getHitCount();

    /**
     * Returns the number of times {@link com.google.common.cache.Cache} lookup methods have returned an uncached (newly
     * loaded) value, or null. Multiple concurrent calls to {@link com.google.common.cache.Cache} lookup methods on an
     * absent value can result in multiple misses, all returning the results of a single cache load operation.
     *
     * @return The number of cache lookup misses.
     *
     * @see com.google.common.cache.CacheStats#missCount()
     */
    long getMissCount();

    /**
     * Returns the number of times {@link com.google.common.cache.Cache} lookup methods have successfully loaded a new
     * value. This is usually incremented in conjunction with {@link #getMissCount()}, though {@code getMissCount} is
     * also incremented when an exception is encountered during cache loading (see {@link #getLoadExceptionCount()}).
     * Multiple concurrent misses for the same key will result in a single load operation. This may be incremented not
     * in conjunction with {@code getMissCount} if the load occurs as a result of a refresh or if the cache loader
     * returned more items than was requested. {@code getMissCount} may also be incremented not in conjunction with this
     * (nor {@link #getLoadExceptionCount}) on calls to {@code getIfPresent}.
     *
     * @return The number of successful loads of new cache entries.
     *
     * @see com.google.common.cache.CacheStats#loadSuccessCount()
     */
    long getLoadSuccessCount();

    /**
     * Returns the number of times {@link com.google.common.cache.Cache} lookup methods threw an exception while loading
     * a new value. This is usually incremented in conjunction with {@code getMissCount}, though {@code getMissCount} is
     * also incremented when cache loading completes successfully (see {@link #getLoadSuccessCount}). Multiple
     * concurrent misses for the same key will result in a single load operation. This may be incremented not in
     * conjunction with {@code getMissCount} if the load occurs as a result of a refresh or if the cache loader returned
     * more items than was requested. {@code getMissCount} may also be incremented not in conjunction with this (nor
     * {@link #getLoadSuccessCount}) on calls to {@code getIfPresent}.
     *
     * @return The number of exception during the load of new cache entries.
     *
     * @see com.google.common.cache.CacheStats#loadExceptionCount()
     */
    long getLoadExceptionCount();

    /**
     * Returns the total number of nanoseconds the cache has spent loading new values. This can be used to calculate the
     * miss penalty. This value is increased every time {@code getLoadSuccessCount} or {@code getLoadExceptionCount} is
     * incremented.
     *
     * @return The total time spent loading new values, in nanoseconds.
     *
     * @see com.google.common.cache.CacheStats#totalLoadTime()
     */
    long getTotalLoadTime();

    /**
     * Returns the number of times an entry has been evicted. This count does not include manual
     * {@linkplain com.google.common.cache.Cache#invalidate invalidations}.
     *
     * @return The number of automatic eviction of entries.
     *
     * @see com.google.common.cache.CacheStats#evictionCount()
     */
    long getEvictionCount();
}
