/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hc.client5.http.classic.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.testutil.TestContext;

class DefaultApacheHttpClient5CacheTest
{
    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext();

    private static final HttpDestination DESTINATION = DefaultHttpDestination.builder("https://url1").build();
    private static final DefaultHttpDestination USER_TOKEN_EXCHANGE_DESTINATION =
        DefaultHttpDestination
            .builder("https://url1")
            .authenticationType(AuthenticationType.OAUTH2_USER_TOKEN_EXCHANGE)
            .build();

    private static final ApacheHttpClient5Factory FACTORY =
        new DefaultApacheHttpClient5Factory(
            DefaultApacheHttpClient5Factory.DEFAULT_TIMEOUT,
            DefaultApacheHttpClient5Factory.DEFAULT_MAX_CONNECTIONS_TOTAL,
            DefaultApacheHttpClient5Factory.DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
    private static final long NANOSECONDS_IN_MINUTE = 60_000_000_000L;
    private static final Duration FIVE_MINUTES = Duration.ofMinutes(5L);

    @BeforeEach
    void setUp()
    {
        CacheManager.invalidateAll();
        context.setPrincipal();
        context.setTenant();
    }

    @Test
    void testGetClientExpiresAfterWrite()
    {
        final AtomicLong ticker = new AtomicLong(0);
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES, ticker::get);

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
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                context.clearTenant();
                context.clearPrincipal();

                if( tenantId != null ) {
                    context.setTenant(tenantId);
                }

                if( principalId != null ) {
                    context.setPrincipal(principalId);
                }

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
    void testGetClientWithUserTokenExchangeDestinationUsesTenantAndPrincipalOptionalForIsolation()
    {
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                context.clearTenant();
                context.clearPrincipal();

                if( tenantId != null ) {
                    context.setTenant(tenantId);
                }

                if( principalId != null ) {
                    context.setPrincipal(principalId);
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
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();
        final Set<HttpClient> tenantClients = new HashSet<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                context.clearTenant();
                context.clearPrincipal();

                if( tenantId != null ) {
                    context.setTenant(tenantId);
                }

                if( principalId != null ) {
                    context.setPrincipal(principalId);
                }

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
    void testGetClientUsesTenantAndPrincipalOptionalForIsolation()
    {
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                context.clearTenant();
                context.clearPrincipal();

                if( tenantId != null ) {
                    context.setTenant(tenantId);
                }

                if( principalId != null ) {
                    context.setPrincipal(principalId);
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
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String untestedTenantId = "some-tenant";
        context.setTenant(untestedTenantId);

        final HttpClient unclearedClientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> tenantsToTest = Arrays.asList("tenant#1", null);

        for( final String tenantId : tenantsToTest ) {
            context.clearTenant();

            if( tenantId != null ) {
                context.setTenant(tenantId);
            }

            final HttpClient clientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
            assertThat(clientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            assertThat(CacheManager.invalidateTenantCaches(tenantId)).isEqualTo(2);

            assertThat(clientWithDestination).isNotSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }

        // make sure the cache entries for the untested tenant were not invalidated
        context.setTenant(untestedTenantId);
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntries()
    {
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String tenantId = "tenant#1";
        context.setTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        context.setPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            context.clearPrincipal();

            if( principalId != null ) {
                context.setPrincipal(principalId);
            }

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            //Only clientWithoutDestination is cached with the cache key containing principal
            assertThat(CacheManager.invalidatePrincipalCaches(tenantId, principalId)).isEqualTo(1);

            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }
        // make sure the cache entries for the untested principal were not invalidated
        context.setPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntriesWithUserTokenExchangeDestination()
    {
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String tenantId = "tenant#1";
        context.setTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        context.setPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithDestination =
            sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            context.clearPrincipal();

            if( principalId != null ) {
                context.setPrincipal(principalId);
            }

            final HttpClient clientWithDestination =
                sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
            assertThat(clientWithDestination)
                .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            //Both clientWithoutDestination and clientWithDestination are cached with the cache key containing principal
            assertThat(CacheManager.invalidatePrincipalCaches(tenantId, principalId)).isEqualTo(2);

            assertThat(clientWithDestination)
                .isNotSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }

        // make sure the cache entries for the untested principal were not invalidated
        context.setPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }
}
