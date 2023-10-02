/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

/**
 * Determines the type of expiry strategy for a cache configuration.
 */
public enum CacheExpirationStrategy
{
    /**
     * Key defines the cache expiry strategy based on when the cache entry is last modified
     */
    WHEN_LAST_MODIFIED,

    /**
     * Key defines the cache expiry strategy based on when the cache entry is last created
     */
    WHEN_CREATED,

    /**
     * Key defines the cache expiry strategy based on when the cache entry is last accessed
     */
    WHEN_LAST_ACCESSED,

    /**
     * Key defines the cache expiry strategy based on when the cache entry is last touched. A touch includes creation,
     * update or access.
     */
    WHEN_LAST_TOUCHED;
}
