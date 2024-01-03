/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import java.net.URI;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;

public abstract class NoopCacheProvider implements CachingProvider
{
    @Override
    public abstract CacheManager getCacheManager();

    @Override
    public CacheManager getCacheManager( URI uri, ClassLoader classLoader, Properties properties )
    {
        return getCacheManager();
    }

    @Override
    public ClassLoader getDefaultClassLoader()
    {
        return null;
    }

    @Override
    public URI getDefaultURI()
    {
        return null;
    }

    @Override
    public Properties getDefaultProperties()
    {
        return null;
    }

    @Override
    public CacheManager getCacheManager( URI uri, ClassLoader classLoader )
    {
        return getCacheManager();
    }

    @Override
    public void close()
    {

    }

    @Override
    public void close( ClassLoader classLoader )
    {

    }

    @Override
    public void close( URI uri, ClassLoader classLoader )
    {

    }

    @Override
    public boolean isSupported( OptionalFeature optionalFeature )
    {
        return false;
    }
}
