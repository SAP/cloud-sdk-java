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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.testutil.MockUtil;

class DefaultApacheHttpClient5CacheTest
{
    private static final MockUtil mockUtil = new MockUtil();

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
    @AfterEach
    void clearAllCaches()
    {
        CacheManager.invalidateAll();
    }

    @Test
    void testGetClientExpiresAfterWrite()
    {
        mockUtil.mockDefaults();

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
        mockUtil.mockDefaults();
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                mockUtil.clearTenants();
                mockUtil.clearPrincipals();

                if( tenantId != null ) {
                    mockUtil.mockCurrentTenant(tenantId);
                }

                if( principalId != null ) {
                    mockUtil.mockCurrentPrincipal(principalId);
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
        mockUtil.mockDefaults();
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                mockUtil.clearTenants();
                mockUtil.clearPrincipals();

                if( tenantId != null ) {
                    mockUtil.mockCurrentTenant(tenantId);
                }

                if( principalId != null ) {
                    mockUtil.mockCurrentPrincipal(principalId);
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
        mockUtil.mockDefaults();
        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();
        final Set<HttpClient> tenantClients = new HashSet<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                mockUtil.clearTenants();
                mockUtil.clearPrincipals();

                if( tenantId != null ) {
                    mockUtil.mockCurrentTenant(tenantId);
                }

                if( principalId != null ) {
                    mockUtil.mockCurrentPrincipal(principalId);
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
        mockUtil.mockDefaults();

        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2", null);
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                mockUtil.clearTenants();
                mockUtil.clearPrincipals();

                if( tenantId != null ) {
                    mockUtil.mockCurrentTenant(tenantId);
                }

                if( principalId != null ) {
                    mockUtil.mockCurrentPrincipal(principalId);
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
        mockUtil.mockDefaults();

        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String untestedTenantId = "some-tenant";
        mockUtil.mockCurrentTenant(untestedTenantId);

        final HttpClient unclearedClientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> tenantsToTest = Arrays.asList("tenant#1", null);

        for( final String tenantId : tenantsToTest ) {
            mockUtil.clearTenants();

            if( tenantId != null ) {
                mockUtil.mockCurrentTenant(tenantId);
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
        mockUtil.mockCurrentTenant(untestedTenantId);
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntries()
    {
        mockUtil.mockDefaults();

        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String tenantId = "tenant#1";
        mockUtil.mockCurrentTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        mockUtil.mockCurrentPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            mockUtil.clearPrincipals();

            if( principalId != null ) {
                mockUtil.mockCurrentPrincipal(principalId);
            }

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            //Only clientWithoutDestination is cached with the cache key containing principal
            assertThat(CacheManager.invalidatePrincipalCaches(tenantId, principalId)).isEqualTo(1);

            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }
        // make sure the cache entries for the untested principal were not invalidated
        mockUtil.mockCurrentPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntriesWithUserTokenExchangeDestination()
    {
        mockUtil.mockDefaults();

        final ApacheHttpClient5Cache sut = new DefaultApacheHttpClient5Cache(FIVE_MINUTES);

        final String tenantId = "tenant#1";
        mockUtil.mockCurrentTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        mockUtil.mockCurrentPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithDestination =
            sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            mockUtil.clearPrincipals();

            if( principalId != null ) {
                mockUtil.mockCurrentPrincipal(principalId);
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
        mockUtil.mockCurrentPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }
}
