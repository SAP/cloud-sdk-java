package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.net.URI;

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
    void testGlobalReturnsSameManagerForAllCalls()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.global();

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
    void testByDestinationNameCachesByName()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.byDestinationName();

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
    void testByDestinationNameHandlesNullDestination()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.byDestinationName();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2);
    }

    @Test
    void testByDestinationNameHandlesUnnamedDestination()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.byDestinationName();

        final HttpDestinationProperties unnamedDest =
            DefaultHttpDestination.builder(URI.create("http://example.com")).build();

        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, unnamedDest);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        // Both should use the same "null key" bucket
        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2);
    }

    @Test
    void testByTenantCachesByTenant()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.byTenant();

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
    void testByTenantHandlesNoTenant()
    {
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.byTenant();

        // Without tenant context
        final HttpClientConnectionManager manager1 = provider.getConnectionManager(DEFAULT_SETTINGS, null);
        final HttpClientConnectionManager manager2 = provider.getConnectionManager(DEFAULT_SETTINGS, null);

        assertThat(manager1).isNotNull();
        assertThat(manager1).isSameAs(manager2);
    }

    @Test
    void testWithCacheKeyCustomExtractor()
    {
        // Custom extractor that uses the URI host as cache key
        final ConnectionPoolManagerProvider provider = ConnectionPoolManagerProviders.withCacheKey(dest -> {
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
    void testNullCacheKeyExtractorThrowsException()
    {
        assertThatNullPointerException()
            .isThrownBy(() -> ConnectionPoolManagerProviders.withCacheKey(null))
            .withMessageContaining("Cache key extractor must not be null");
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
