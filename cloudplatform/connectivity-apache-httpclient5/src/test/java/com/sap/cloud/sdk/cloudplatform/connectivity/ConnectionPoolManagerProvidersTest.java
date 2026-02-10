package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

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

        final HttpDestinationProperties destination = DefaultHttpDestination.builder("http://example.com").build();
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
            DefaultHttpDestination.builder("http://example1.com").name("dest-a").build();
        final HttpDestinationProperties dest2 =
            DefaultHttpDestination.builder("http://example2.com").name("dest-a").build();
        final HttpDestinationProperties dest3 =
            DefaultHttpDestination.builder("http://example3.com").name("dest-b").build();

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

        final HttpDestinationProperties unnamedDest = DefaultHttpDestination.builder("http://example.com").build();

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
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().by(dest -> {
            if( dest == null ) {
                return "no-destination";
            }
            return dest.getUri().getHost();
        });

        final HttpDestinationProperties dest1 = DefaultHttpDestination.builder("http://host-a.com/path1").build();
        final HttpDestinationProperties dest2 = DefaultHttpDestination.builder("http://host-a.com/path2").build();
        final HttpDestinationProperties dest3 = DefaultHttpDestination.builder("http://host-b.com/path1").build();

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
            DefaultHttpDestination.builder("http://example.com").name("my-dest").build();

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

    @Test
    void testCachedByIndicatedBehalfOfWithCurrentTenantHeaderProvider()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byIndicatedBehalfOf();

        // Create a header provider that indicates NAMED_USER_CURRENT_TENANT
        final DestinationHeaderProvider namedUserProvider =
            new TestHeaderProvider(OnBehalfOf.NAMED_USER_CURRENT_TENANT);

        // Create destinations with the header provider
        final DefaultHttpDestination destTenant1 =
            DefaultHttpDestination.builder("http://example.com").headerProviders(namedUserProvider).build();

        final DefaultHttpDestination destTenant2 =
            DefaultHttpDestination.builder("http://example.com").headerProviders(namedUserProvider).build();

        // Same destination with same tenant should return same manager
        final DefaultTenant tenant1 = new DefaultTenant("tenant-1");
        final HttpClientConnectionManager managerTenant1 =
            TenantAccessor
                .executeWithTenant(tenant1, () -> provider.getConnectionManager(DEFAULT_SETTINGS, destTenant1));

        final HttpClientConnectionManager managerTenant1Again =
            TenantAccessor
                .executeWithTenant(tenant1, () -> provider.getConnectionManager(DEFAULT_SETTINGS, destTenant2));

        // Different tenant should return different manager
        final DefaultTenant tenant2 = new DefaultTenant("tenant-2");
        final HttpClientConnectionManager managerTenant2 =
            TenantAccessor
                .executeWithTenant(tenant2, () -> provider.getConnectionManager(DEFAULT_SETTINGS, destTenant1));

        assertThat(managerTenant1).isNotNull();
        assertThat(managerTenant1).isSameAs(managerTenant1Again); // Same tenant, same destination
        assertThat(managerTenant1).isNotSameAs(managerTenant2); // Different tenant
    }

    @Test
    void testCachedByIndicatedBehalfOfWithTechnicalUserCurrentTenant()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byIndicatedBehalfOf();

        final DefaultHttpDestination dest =
            DefaultHttpDestination
                .builder("http://example.com")
                .headerProviders(new TestHeaderProvider(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT))
                .build();

        // Different tenants should get different managers
        final DefaultTenant tenant1 = new DefaultTenant("tenant-1");
        final HttpClientConnectionManager managerTenant1 =
            TenantAccessor.executeWithTenant(tenant1, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        final DefaultTenant tenant2 = new DefaultTenant("tenant-2");
        final HttpClientConnectionManager managerTenant2 =
            TenantAccessor.executeWithTenant(tenant2, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        assertThat(managerTenant1).isNotNull();
        assertThat(managerTenant2).isNotNull();
        assertThat(managerTenant1).isNotSameAs(managerTenant2); // Different tenant
    }

    @Test
    void testCachedByIndicatedBehalfOfWithProviderUserSharesAcrossTenants()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byIndicatedBehalfOf();

        // Create a header provider that indicates TECHNICAL_USER_PROVIDER (not current tenant)
        final DefaultHttpDestination dest =
            DefaultHttpDestination
                .builder("http://example.com")
                .headerProviders(new TestHeaderProvider(OnBehalfOf.TECHNICAL_USER_PROVIDER))
                .build();

        // Different tenants should share the same manager since it's not on behalf of current tenant
        final DefaultTenant tenant1 = new DefaultTenant("tenant-1");
        final HttpClientConnectionManager managerTenant1 =
            TenantAccessor.executeWithTenant(tenant1, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        final DefaultTenant tenant2 = new DefaultTenant("tenant-2");
        final HttpClientConnectionManager managerTenant2 =
            TenantAccessor.executeWithTenant(tenant2, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        assertThat(managerTenant1).isNotNull();
        assertThat(managerTenant1).isSameAs(managerTenant2); // Same manager shared across tenants
    }

    @Test
    void testCachedByIndicatedBehalfOfWithNoHeaderProvider()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byIndicatedBehalfOf();

        // Destination without any header provider
        final DefaultHttpDestination dest = DefaultHttpDestination.builder("http://example.com").build();

        // Different tenants should share the same manager since there's no on-behalf-of indication
        final DefaultTenant tenant1 = new DefaultTenant("tenant-1");
        final HttpClientConnectionManager managerTenant1 =
            TenantAccessor.executeWithTenant(tenant1, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        final DefaultTenant tenant2 = new DefaultTenant("tenant-2");
        final HttpClientConnectionManager managerTenant2 =
            TenantAccessor.executeWithTenant(tenant2, () -> provider.getConnectionManager(DEFAULT_SETTINGS, dest));

        assertThat(managerTenant1).isNotNull();
        assertThat(managerTenant1).isSameAs(managerTenant2); // Same manager shared across tenants
    }

    @Test
    void testCachedByIndicatedBehalfOfWithNonDefaultHttpDestination()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.cached().byIndicatedBehalfOf();

        // Non-DefaultHttpDestination should return null key and create new manager each time
        final HttpDestinationProperties nonDefaultDest = DefaultHttpDestination.builder("http://example.com").build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, nonDefaultDest);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, nonDefaultDest);

        assertThat(manager1).isNotNull();
        assertThat(manager2).isNotNull();
        assertThat(manager1).isSameAs(manager2); // New manager each time for non-DefaultHttpDestination
    }

    /**
     * Test implementation of DestinationHeaderProvider that also implements IsOnBehalfOf.
     */
    @RequiredArgsConstructor
    private static class TestHeaderProvider implements DestinationHeaderProvider, IsOnBehalfOf
    {
        @Getter
        private final OnBehalfOf onBehalfOf;

        @Nonnull
        @Override
        public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
        {
            return Collections.emptyList();
        }
    }
}
