/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.HttpClientInstantiationException;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.testutil.TestContext;

@Isolated
class DefaultHttpClientCacheTest
{
    private static final HttpDestination DESTINATION = DefaultHttpDestination.builder("https://url1").build();
    private static final DefaultHttpDestination USER_TOKEN_EXCHANGE_DESTINATION =
        DefaultHttpDestination
            .builder("https://url1")
            .authenticationType(AuthenticationType.OAUTH2_USER_TOKEN_EXCHANGE)
            .build();

    private static final HttpClientFactory FACTORY = new DefaultHttpClientFactory();
    private static final long NANOSECONDS_IN_MINUTE = 60_000_000_000L;
    private static final List<Tenant> tenants = Arrays.asList(new DefaultTenant("T#1"), new DefaultTenant("T#2"), null);
    private static final List<Principal> principals =
        Arrays.asList(new DefaultPrincipal("P#1"), new DefaultPrincipal("P#2"), null);

    @RegisterExtension
    static TestContext context = TestContext.withThreadContext().resetCaches();

    private DefaultHttpClientCache sut;

    @BeforeEach
    void setUp()
    {
        sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);
    }

    @Test
    void testGetClientExpiresAfterWrite()
    {
        final AtomicLong ticker = new AtomicLong(0);
        sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES, ticker::get);

        final HttpClient clientWithDestination1 = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(clientWithDestination1).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient clientWithoutDestination1 = sut.tryGetHttpClient(FACTORY).get();
        assertThat(clientWithoutDestination1).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        ticker.updateAndGet(t -> t + 3 * NANOSECONDS_IN_MINUTE);

        // cache is still valid
        assertThat(clientWithDestination1).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(clientWithoutDestination1).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        ticker.updateAndGet(t -> t + 3 * NANOSECONDS_IN_MINUTE);

        // cache has expired
        final HttpClient clientWithDestination2 = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(clientWithDestination2).isNotSameAs(clientWithDestination1);
        assertThat(clientWithDestination2).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient clientWithoutDestination2 = sut.tryGetHttpClient(FACTORY).get();
        assertThat(clientWithoutDestination2).isNotSameAs(clientWithoutDestination1);
        assertThat(clientWithDestination2).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
    }

    @Test
    void testGetClientWithoutDestinationUsesTenantAndPrincipalOptionalForIsolation()
    {
        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenants ) {
            for( final Principal principalToTest : principals ) {
                context.setTenant(tenantToTest);
                context.setPrincipal(principalToTest);

                final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

                // isolation works: None of the previously created clients is the same instance
                clients.forEach(c -> assertThat(c).isNotSameAs(clientWithoutDestination));

                clients.add(clientWithoutDestination);
            }
        }
    }

    @Test
    void testGetClientWithUserTokenExchangeDestinationUsesPrincipalRequiredForIsolation()
    {
        final HttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenants ) {
            for( final Principal principalToTest : principals ) {
                context.setTenant(tenantToTest);
                context.setPrincipal(principalToTest);
                if( tenantToTest == null || principalToTest == null ) {
                    assertThatThrownBy(() -> sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get())
                        .describedAs(
                            "Without a tenant and principal http clients should not be cached for user based destinations")
                        .isInstanceOf(HttpClientInstantiationException.class);
                    continue;
                }

                final HttpClient clientWithDestination =
                    sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithDestination)
                    .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

                // isolation works: None of the previously created clients is the same instance
                clients.forEach(c -> assertThat(c).isNotSameAs(clientWithDestination));

                clients.add(clientWithDestination);
            }
        }
    }

    @Test
    void testGetClientWithDestinationUsesTenantOptionalForIsolation()
    {
        final HttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final List<HttpClient> clients = new ArrayList<>();
        final Set<HttpClient> tenantClients = new HashSet<>();

        for( final Tenant tenantToTest : tenants ) {
            for( final Principal principalToTest : principals ) {
                context.setTenant(tenantToTest);
                context.setPrincipal(principalToTest);

                final HttpClient clientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

                // isolation works per tenant as destination does not require user token exchange
                clients.forEach(c -> assertThat(c).isSameAs(clientWithDestination));

                clients.add(clientWithDestination);
            }
            tenantClients.addAll(clients);
            clients.clear();
        }
        //assert that clients for each tenant is different
        assertThat(tenantClients).size().isEqualTo(3);
    }

    @Test
    //This is a known limitation of excluding header providers in the equality check of destinations
    void testGetClientReturnsSameClientForDestinationsWithOnlyDifferentHeaderProviders()
    {
        final Header header1 = new Header("foo", "bar");
        final Header header2 = new Header("foo1", "bar1");

        final DefaultHttpDestination firstDestination =
            DefaultHttpDestination
                .builder("http://some-uri")
                .headerProviders(( any ) -> Collections.singletonList(header1))
                .build();

        final DefaultHttpDestination secondDestination =
            DefaultHttpDestination
                .fromDestination(firstDestination)
                .headerProviders(( any ) -> Collections.singletonList(header2))
                .build();

        final HttpClientWrapper client1 = (HttpClientWrapper) sut.tryGetHttpClient(firstDestination, FACTORY).get();
        final HttpClientWrapper client2 = (HttpClientWrapper) sut.tryGetHttpClient(secondDestination, FACTORY).get();

        assertThat(client1.getDestination()).isSameAs(firstDestination);
        assertThat(client2.getDestination()).isSameAs(secondDestination);

        final HttpUriRequest request1 = client1.wrapRequest(new HttpGet());
        final HttpUriRequest request2 = client2.wrapRequest(new HttpGet());

        // This behavior is to be improved by https://github.com/SAP/cloud-sdk-java-backlog/issues/396
        assertThat(request1.getAllHeaders()).containsExactly(new HttpClientWrapper.ApacheHttpHeader(header1));
        assertThat(request2.getAllHeaders())
            .containsExactly(
                new HttpClientWrapper.ApacheHttpHeader(header1),
                new HttpClientWrapper.ApacheHttpHeader(header2));
    }

    @Test
    void testGetClientUsesTenantAndPrincipalRequiredForIsolation()
    {
        final HttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenants ) {
            for( final Principal principalToTest : principals ) {
                context.setTenant(tenantToTest);
                context.setPrincipal(principalToTest);

                if( tenantToTest == null || principalToTest == null ) {
                    assertThatThrownBy(() -> sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get())
                        .describedAs("Without a tenant or principal http clients should not be cached")
                        .isInstanceOf(HttpClientInstantiationException.class);
                    continue;
                }

                final HttpClient clientWithDestination =
                    sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithDestination)
                    .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

                // isolation works: None of the previously created clients is the same instance
                clients.forEach(c -> assertThat(c).isNotSameAs(clientWithDestination));

                clients.add(clientWithDestination);

                final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

                // isolation works: None of the previously created clients is the same instance
                clients.forEach(c -> assertThat(c).isNotSameAs(clientWithoutDestination));

                clients.add(clientWithoutDestination);
            }
        }
    }

    @Test
    void testInvalidateTenantCacheEntries()
    {
        final HttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        context.setTenant("some-tenant");

        final HttpClient unclearedClientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        for( final Tenant tenant : tenants ) {
            context.setTenant(tenant);

            final HttpClient clientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
            assertThat(clientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            assertThat(CacheManager.invalidateTenantCaches(tenant != null ? tenant.getTenantId() : null)).isEqualTo(2);

            assertThat(clientWithDestination).isNotSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }

        // make sure the cache entries for the untested tenant were not invalidated
        context.setTenant("some-tenant");
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntries()
    {
        context.setTenant("some-tenant");
        context.setPrincipal("some-principal");

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        for( final Principal principal : principals ) {
            context.setPrincipal(principal);

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            //Only clientWithoutDestination is cached with the cache key containing principal
            assertThat(
                CacheManager
                    .invalidatePrincipalCaches("some-tenant", principal != null ? principal.getPrincipalId() : null))
                .isEqualTo(1);

            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }
        // make sure the cache entries for the untested principal were not invalidated
        context.setPrincipal("some-principal");
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntriesWithUserTokenExchangeDestination()
    {
        context.setTenant("some-tenant");
        context.setPrincipal("some-principal");
        final HttpClient unclearedClientWithDestination =
            sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final String principalId = "principal#1";
        context.setPrincipal(principalId);

        final HttpClient clientWithDestination = sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(clientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        //Both clientWithoutDestination and clientWithDestination are cached with the cache key containing principal
        assertThat(CacheManager.invalidatePrincipalCaches("some-tenant", principalId)).isEqualTo(2);

        assertThat(clientWithDestination)
            .isNotSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());

        // make sure the cache entries for the untested principal were not invalidated
        context.setPrincipal("some-principal");
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testPrincipalPropagationIsPrincipalIsolated()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("foo.com")
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .build();

        context.setTenant();
        context.setPrincipal("some-principal");
        final HttpClient client = sut.tryGetHttpClient(destination, FACTORY).get();
        assertThat(client).isSameAs(sut.tryGetHttpClient(destination, FACTORY).get());

        context.setPrincipal("some-other-principal");
        assertThat(client).isNotSameAs(sut.tryGetHttpClient(destination, FACTORY).get());
        assertThat(sut.tryGetHttpClient(destination, FACTORY).get())
            .isSameAs(sut.tryGetHttpClient(destination, FACTORY).get());

        context.clearPrincipal();
        assertThatThrownBy(() -> sut.tryGetHttpClient(destination, FACTORY).get())
            .describedAs("Without a principal http clients should not be cached for user based destinations")
            .isInstanceOf(HttpClientInstantiationException.class);
    }
}
