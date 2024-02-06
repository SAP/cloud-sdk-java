/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.cache.CacheManager;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;

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

    private static final Principal PRINCIPAL = new DefaultPrincipal("P");
    private static final Principal PRINCIPAL_NONE = null;

    private static final Tenant TENANT = new DefaultTenant("T");
    private static final Tenant TENANT_NONE = null;

    private Principal mockedPrincipal = PRINCIPAL;
    private Tenant mockedTenant = TENANT;

    @BeforeEach
    void setup()
    {
        CacheManager.invalidateAll();
        PrincipalAccessor.setPrincipalFacade(() -> Option.of(mockedPrincipal).toTry());
        TenantAccessor.setTenantFacade(() -> Option.of(mockedTenant).toTry());
    }

    @AfterEach
    void tearDown()
    {
        CacheManager.invalidateAll();
        PrincipalAccessor.setPrincipalFacade(null);
        TenantAccessor.setTenantFacade(null);
    }

    @Test
    void testGetClientExpiresAfterWrite()
    {
        final AtomicLong ticker = new AtomicLong(0);
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES, ticker::get);

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
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final Tenant[] tenantsToTest = { new DefaultTenant("T#1"), new DefaultTenant("T#2"), TENANT_NONE };
        final Principal[] principalsToTest =
            { new DefaultPrincipal("P#1"), new DefaultPrincipal("P#2"), PRINCIPAL_NONE };
        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenantsToTest ) {
            for( final Principal principalToTest : principalsToTest ) {
                mockedTenant = tenantToTest;
                mockedPrincipal = principalToTest;

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
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final Tenant[] tenantsToTest = { new DefaultTenant("T#1"), new DefaultTenant("T#2"), TENANT_NONE };
        final Principal[] principalsToTest =
            { new DefaultPrincipal("P#1"), new DefaultPrincipal("P#2"), PRINCIPAL_NONE };
        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenantsToTest ) {
            for( final Principal principalToTest : principalsToTest ) {
                mockedTenant = tenantToTest;
                mockedPrincipal = principalToTest;

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
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final Tenant[] tenantsToTest = { new DefaultTenant("T#1"), new DefaultTenant("T#2"), TENANT_NONE };
        final Principal[] principalsToTest =
            { new DefaultPrincipal("P#1"), new DefaultPrincipal("P#2"), PRINCIPAL_NONE };
        final List<HttpClient> clients = new ArrayList<>();
        final Set<HttpClient> tenantClients = new HashSet<>();

        for( final Tenant tenantToTest : tenantsToTest ) {
            for( final Principal principalToTest : principalsToTest ) {
                mockedTenant = tenantToTest;
                mockedPrincipal = principalToTest;

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
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);
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
    void testGetClientUsesTenantAndPrincipalOptionalForIsolation()
    {
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final Tenant[] tenantsToTest = { new DefaultTenant("T#1"), new DefaultTenant("T#2"), TENANT_NONE };
        final Principal[] principalsToTest =
            { new DefaultPrincipal("P#1"), new DefaultPrincipal("P#2"), PRINCIPAL_NONE };
        final List<HttpClient> clients = new ArrayList<>();

        for( final Tenant tenantToTest : tenantsToTest ) {
            for( final Principal principalToTest : principalsToTest ) {
                mockedTenant = tenantToTest;
                mockedPrincipal = principalToTest;

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
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final String untestedTenantId = "some-tenant";
        mockedTenant = new DefaultTenant(untestedTenantId);

        final HttpClient unclearedClientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> tenantsToTest = Arrays.asList("tenant#1", null);
        for( final String tenantId : tenantsToTest ) {
            mockedTenant = tenantId != null ? new DefaultTenant(tenantId) : TENANT_NONE;

            final HttpClient clientWithDestination = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
            assertThat(clientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            assertThat(CacheManager.invalidateTenantCaches(tenantId)).isEqualTo(2);

            assertThat(clientWithDestination).isNotSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }

        // make sure the cache entries for the untested tenant were not invalidated
        mockedTenant = new DefaultTenant(untestedTenantId);
        assertThat(unclearedClientWithDestination).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntries()
    {
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final String tenantId = "tenant#1";
        mockedTenant = new DefaultTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        mockedPrincipal = new DefaultPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            mockedPrincipal = principalId != null ? new DefaultPrincipal(principalId) : PRINCIPAL_NONE;

            final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
            assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

            //Only clientWithoutDestination is cached with the cache key containing principal
            assertThat(CacheManager.invalidatePrincipalCaches(tenantId, principalId)).isEqualTo(1);

            assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());
        }
        // make sure the cache entries for the untested principal were not invalidated
        mockedPrincipal = new DefaultPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }

    @Test
    void testInvalidatePrincipalCacheEntriesWithUserTokenExchangeDestination()
    {
        final DefaultHttpClientCache sut = new DefaultHttpClientCache(5L, TimeUnit.MINUTES);

        final String tenantId = "tenant#1";
        mockedTenant = new DefaultTenant(tenantId);

        final String untestedPrincipalId = "some-principal";
        mockedPrincipal = new DefaultPrincipal(untestedPrincipalId);

        final HttpClient unclearedClientWithDestination =
            sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient unclearedClientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        final List<String> principalsToTest = Arrays.asList("principal#1", null);

        for( final String principalId : principalsToTest ) {
            mockedPrincipal = principalId != null ? new DefaultPrincipal(principalId) : PRINCIPAL_NONE;

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
        mockedPrincipal = new DefaultPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
    }
}
