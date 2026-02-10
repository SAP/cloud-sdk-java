package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

class ConnectionPoolManagerProvidersTest
{
    private static final ConnectionPoolSettings DEFAULT_SETTINGS = DefaultConnectionPoolSettings.ofDefaults();

    @Test
    void testNoCacheCreatesNewManagerEachTime()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.noCache();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        assertThat(manager1).isNotNull();
        assertThat(manager2).isNotNull();
        assertThat(manager1).isNotSameAs(manager2);
    }

    @Test
    void testCachedGlobalReturnsSameManagerForAllCalls()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().by(destination -> true);

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        final HttpDestinationProperties destination =
            DefaultHttpDestination.builder(URI.create("http://example.com")).build();
        final HttpClientConnectionManager manager3 = provider.getConnectionManager(DEFAULT_SETTINGS, destination);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2);
        assertThat(manager1).isSameAs(manager3);
    }

    @Test
    void testCachedByDestinationNameCachesByName()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byDestinationName();

        final HttpDestinationProperties dest1 =
            DefaultHttpDestination.builder(URI.create("http://example1.com")).name("dest-a").build();
        final HttpDestinationProperties dest2 =
            DefaultHttpDestination.builder(URI.create("http://example2.com")).name("dest-a").build();
        final HttpDestinationProperties dest3 =
            DefaultHttpDestination.builder(URI.create("http://example3.com")).name("dest-b").build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, dest1);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, dest2);
        final HttpClientConnectionManager manager3 = provider.getConnectionManager(DEFAULT_SETTINGS, dest3);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2); // Same name "dest-a"
        assertThat(manager1).isNotSameAs(manager3); // Different name "dest-b"
    }

    @Test
    void testCachedByDestinationNameHandlesNullDestination()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byDestinationName();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isNotSameAs(manager2);
    }

    @Test
    void testCachedByDestinationNameHandlesUnnamedDestination()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byDestinationName();

        final HttpDestinationProperties unnamedDest =
            DefaultHttpDestination.builder(URI.create("http://example.com")).build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, unnamedDest);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        // Both should use the same "null key" bucket
        assertThat(manager1).isNotNull();
        assertThat(manager1).isNotSameAs(manager2);
    }

    @Test
    void testCachedByTenantCachesByTenant()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byCurrentTenant();

        final HttpClientConnectionManager managerTenant1 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("tenant-1"),
                    () -> provider.getConnectionManager(DEFAULT_SETTINGS, null));

        final HttpClientConnectionManager managerTenant1Again =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("tenant-1"),
                    () -> provider.getConnectionManager(DEFAULT_SETTINGS, null));

        final HttpClientConnectionManager managerTenant2 =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("tenant-2"),
                    () -> provider.getConnectionManager(DEFAULT_SETTINGS, null));

        assertThat(managerTenant1).isNotNull();
        assertThat(managerTenant1).isSameAs(managerTenant1Again); // Same tenant
        assertThat(managerTenant1).isNotSameAs(managerTenant2); // Different tenant
    }

    @Test
    void testCachedByTenantHandlesNoTenant()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byCurrentTenant();

        // Without tenant context
        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isNotSameAs(manager2);
    }

    @Test
    void testCachedWithCacheKeyCustomExtractor()
    {
        // Custom extractor that uses the URI host as cache key
        final ConnectionPoolManagerProvider provider =
            ConnectionPoolManagerProviders.cached().by(dest -> {
                if( dest == null ) {
                    return "no-destination";
                }
                return dest.getUri().getHost();
            });

        final HttpDestinationProperties dest1 =
            DefaultHttpDestination.builder(URI.create("http://host-a.com/path1")).build();
        final HttpDestinationProperties dest2 =
            DefaultHttpDestination.builder(URI.create("http://host-a.com/path2")).build();
        final HttpDestinationProperties dest3 =
            DefaultHttpDestination.builder(URI.create("http://host-b.com/path1")).build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, dest1);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, dest2);
        final HttpClientConnectionManager manager3 = provider.getConnectionManager(DEFAULT_SETTINGS, dest3);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2); // Same host "host-a.com"
        assertThat(manager1).isNotSameAs(manager3); // Different host "host-b.com"
    }

    @Test
    void testCachedWithCustomConcurrentMap()
    {
        // Use a custom ConcurrentMap
        final ConcurrentMap<Object, HttpClientConnectionManager> customCache = new ConcurrentHashMap<>();

        final ConnectionPoolManagerProvider provider =
            ConnectionPoolManagerProviders.cached(customCache::computeIfAbsent).byDestinationName();

        final HttpDestinationProperties dest =
            DefaultHttpDestination.builder(URI.create("http://example.com")).name("my-dest").build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, dest);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, dest);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2);

        // Verify the cache was used
        assertThat(customCache).hasSize(1);
        assertThat(customCache).containsKey("my-dest");
        assertThat(customCache.get("my-dest")).isSameAs(manager1);
    }

    @Test
    void testCachedWithCustomCacheFunction()
    {
        // Simulate a Caffeine-like cache with a custom BiFunction
        final ConcurrentMap<Object, HttpClientConnectionManager> backingMap = new ConcurrentHashMap<>();
        final AtomicInteger loadCount = new AtomicInteger(0);

        final BiFunction<Object, Function<Object, HttpClientConnectionManager>, HttpClientConnectionManager> cacheFunction =
            ( key, loader ) -> {
                return backingMap.computeIfAbsent(key, k -> {
                    loadCount.incrementAndGet();
                    return loader.apply(k);
                });
            };

        final ConnectionPoolManagerProvider provider =
            ConnectionPoolManagerProviders.cached(cacheFunction).by(destination -> true);

        // First call should load
        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        assertThat(loadCount.get()).isEqualTo(1);

        // Second call should use cache
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        assertThat(loadCount.get()).isEqualTo(1); // Still 1, no new load

        assertThat(manager1).isSameAs(manager2);
    }

    @Test
    void testNullCacheKeyExtractorThrowsException()
    {
        assertThatNullPointerException()
            .isThrownBy(() -> ConnectionPoolManagerProviders.cached().by(null))
            .withMessageContaining("Cache key extractor must not be null");
    }

    @Test
    void testNullCacheFunctionThrowsException()
    {
        assertThatNullPointerException()
            .isThrownBy(() -> ConnectionPoolManagerProviders.cached(null))
            .withMessageContaining("Cache function must not be null");
    }

    @Test
    void testFunctionalInterfaceCanBeUsedWithLambda()
    {
        // Verify that ConnectionPoolManagerProvider can be used as a lambda
        final ConnectionPoolManagerProvider lambdaProvider =
            ( settings, dest ) -> ConnectionPoolManagerProviders.noCache().getConnectionManager(settings, dest);

        final HttpClientConnectionManager manager = lambdaProvider.getConnectionManager(DEFAULT_SETTINGS, null);
        assertThat(manager).isNotNull();
    }
}