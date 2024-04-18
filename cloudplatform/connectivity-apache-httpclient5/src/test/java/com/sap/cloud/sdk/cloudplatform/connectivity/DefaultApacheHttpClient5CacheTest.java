/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicHeader;
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
    private static final Duration TEN_MINUTES = Duration.ofMinutes(10L);

    private ApacheHttpClient5Cache sut;

    @BeforeEach
    void setUp()
    {
        CacheManager.invalidateAll();
        context.setPrincipal();
        context.setTenant();

        sut = new DefaultApacheHttpClient5Cache(TEN_MINUTES);
    }

    @Test
    void testGetClientExpiresAfterAccess()
    {
        final AtomicLong ticker = new AtomicLong(0);
        sut = new DefaultApacheHttpClient5Cache(TEN_MINUTES, ticker::get);

        final HttpClient clientWithDestination1 = sut.tryGetHttpClient(DESTINATION, FACTORY).get();
        assertThat(clientWithDestination1).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());

        final HttpClient clientWithoutDestination1 = sut.tryGetHttpClient(FACTORY).get();
        assertThat(clientWithoutDestination1).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        ticker.updateAndGet(t -> t + 3 * NANOSECONDS_IN_MINUTE);

        // cache is still valid
        assertThat(clientWithDestination1).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(clientWithoutDestination1).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        ticker.updateAndGet(t -> t + 7 * NANOSECONDS_IN_MINUTE);

        // cache is still valid
        assertThat(clientWithDestination1).isSameAs(sut.tryGetHttpClient(DESTINATION, FACTORY).get());
        assertThat(clientWithoutDestination1).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        ticker.updateAndGet(t -> t + 11 * NANOSECONDS_IN_MINUTE);

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
    void testGetClientWithUserTokenExchangeDestinationUsesPrincipalRequiredForIsolation()
    {
        final List<String> tenantsToTest = Arrays.asList("tenant#1", "tenant#2", null);
        final List<String> principalsToTest = Arrays.asList("principal#1", "principal#2");
        final List<HttpClient> clients = new ArrayList<>();

        for( final String tenantId : tenantsToTest ) {
            for( final String principalId : principalsToTest ) {
                context.clearTenant();

                if( tenantId != null ) {
                    context.setTenant(tenantId);
                }

                context.setPrincipal(principalId);

                final HttpClient clientWithDestination =
                    sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithDestination)
                    .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

                // isolation works: None of the previously created clients is the same instance
                clients.forEach(c -> assertThat(c).isNotSameAs(clientWithDestination));

                clients.add(clientWithDestination);
            }
            context.clearPrincipal();
            assertThat(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get())
                .describedAs("Without a principal http clients should not be cached for user based destinations")
                .isNotSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        }

    }

    @Test
    void testGetClientWithDestinationUsesTenantOptionalForIsolation()
    {
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
                } else {
                    // covered by the last assertion
                    continue;
                }

                final HttpClient clientWithDestination =
                    sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();

                // caching works: Requesting a second client with the same parameters yields the exact same instance
                assertThat(clientWithDestination)
                    .describedAs("Cache should hit for tenantId: %s, principalId: %s", tenantId, principalId)
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
            context.clearPrincipal();
            assertThat(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get())
                .describedAs("Without a principal http clients should not be cached for user based destinations")
                .isNotSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        }
    }

    @Test
    void testInvalidateTenantCacheEntries()
    {
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

        final String principalId = "principal#1";
        context.setPrincipal(principalId);

        final HttpClient clientWithDestination = sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get();
        assertThat(clientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());

        final HttpClient clientWithoutDestination = sut.tryGetHttpClient(FACTORY).get();
        assertThat(clientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());

        //Both clientWithoutDestination and clientWithDestination are cached with the cache key containing principal
        assertThat(CacheManager.invalidatePrincipalCaches(tenantId, principalId)).isEqualTo(2);

        assertThat(clientWithDestination)
            .isNotSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(clientWithoutDestination).isNotSameAs(sut.tryGetHttpClient(FACTORY).get());

        // make sure the cache entries for the untested principal were not invalidated
        context.setPrincipal(untestedPrincipalId);
        assertThat(unclearedClientWithDestination)
            .isSameAs(sut.tryGetHttpClient(USER_TOKEN_EXCHANGE_DESTINATION, FACTORY).get());
        assertThat(unclearedClientWithoutDestination).isSameAs(sut.tryGetHttpClient(FACTORY).get());
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

        final ApacheHttpClient5Wrapper client1 =
            (ApacheHttpClient5Wrapper) sut.tryGetHttpClient(firstDestination, FACTORY).get();
        final ApacheHttpClient5Wrapper client2 =
            (ApacheHttpClient5Wrapper) sut.tryGetHttpClient(secondDestination, FACTORY).get();

        assertThat(client1.getDestination()).isSameAs(firstDestination);
        assertThat(client2.getDestination()).isSameAs(secondDestination);

        final ClassicHttpRequest request1 = client1.wrapRequest(new HttpGet("/"));
        final ClassicHttpRequest request2 = client2.wrapRequest(new HttpGet("/"));

        final List<org.apache.hc.core5.http.Header> headersRequest1 = new ArrayList<>();
        final List<org.apache.hc.core5.http.Header> headersRequest2 = new ArrayList<>();
        request1.headerIterator().forEachRemaining(headersRequest1::add);
        request2.headerIterator().forEachRemaining(headersRequest2::add);

        // recursive comparison because BasicHeader doesn't implement equals/hashCode
        assertThat(headersRequest1)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(new BasicHeader(header1.getName(), header1.getValue()));
        assertThat(headersRequest2)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                new BasicHeader(header1.getName(), header1.getValue()),
                new BasicHeader(header2.getName(), header2.getValue()));
    }

    @Test
    void testPrincipalPropagationIsPrincipalIsolated()
    {
        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("foo.com")
                .authenticationType(AuthenticationType.PRINCIPAL_PROPAGATION)
                .build();

        context.setPrincipal("some-principal");
        final HttpClient client = sut.tryGetHttpClient(destination, FACTORY).get();
        assertThat(client).isSameAs(sut.tryGetHttpClient(destination, FACTORY).get());

        context.setPrincipal("some-other-principal");
        assertThat(client).isNotSameAs(sut.tryGetHttpClient(destination, FACTORY).get());
        assertThat(sut.tryGetHttpClient(destination, FACTORY).get())
            .isSameAs(sut.tryGetHttpClient(destination, FACTORY).get());

        context.clearPrincipal();
        assertThat(client).isNotSameAs(sut.tryGetHttpClient(destination, FACTORY).get());
        assertThat(sut.tryGetHttpClient(destination, FACTORY).get())
            .describedAs("Without a principal http clients should not be cached for user based destinations")
            .isNotSameAs(sut.tryGetHttpClient(destination, FACTORY).get());
    }
}
