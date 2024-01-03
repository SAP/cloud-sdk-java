/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import lombok.Data;

/**
 * POJO implmentation of the {@link CacheStatsMXBean} interface.
 */
@Data
public class CacheStats implements CacheStatsMXBean
{
    private final long cacheSize;
    private final long hitCount;
    private final long missCount;
    private final long loadSuccessCount;
    private final long loadExceptionCount;
    private final long totalLoadTime;
    private final long evictionCount;
}
