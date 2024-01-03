/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_CLIENT_CREDENTIALS;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_JWT_BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.SneakyThrows;

class GetOrComputeDestinationCommandTest
{
    private static final String DESTINATION_NAME = "SomeDestinationName";
    private static final DestinationOptions EMPTY_OPTIONS = DestinationOptions.builder().build();

    private static final Tenant t1 = new DefaultTenant("tenant-1");
    private static final Principal p1 = new DefaultPrincipal("principal-1");

    private Cache<CacheKey, Destination> destinationCache;
    private Cache<CacheKey, ReentrantLock> isolationLocks;

    @BeforeEach
    void setup()
    {
        destinationCache = Caffeine.newBuilder().build();
        isolationLocks = Caffeine.newBuilder().build();
    }

    @AfterEach
    void cleanUp()
    {
        destinationCache.invalidateAll();
        isolationLocks.invalidateAll();
    }

    @Test
    @SuppressWarnings( "deprecation" )
    void testDefaultTokenExchangeStrategy()
    {
        final DestinationOptions options = DestinationOptions.builder().build();
        // make sure there is no token exchange strategy set
        assertThat(DestinationServiceOptionsAugmenter.getTokenExchangeStrategy(options)).isEmpty();

        final GetOrComputeSingleDestinationCommand sut =
            GetOrComputeSingleDestinationCommand
                .prepareCommand("destination", options, destinationCache, isolationLocks, ( foo, bar ) -> null, null)
                .get();

        assertThat(sut.getExchangeStrategy()).isEqualTo(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
    }

    @Test
    @SuppressWarnings( "deprecation" )
    void testPrepareCommandWithLookupOnlyExchangeStrategy()
    {
        TenantAccessor.executeWithTenant(t1, () -> {
            final CacheKey expectedCacheKey = CacheKey.ofTenantIsolation();

            assertCacheKeysMatchExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY, expectedCacheKey);
        });
    }

    @Test
    @SuppressWarnings( "deprecation" )
    void testPrepareCommandWithLookupThenExchangeStrategy()
    {
        PrincipalAccessor.executeWithPrincipal(p1, () -> TenantAccessor.executeWithTenant(t1, () -> {
            final CacheKey expectedCacheKey = CacheKey.ofTenantIsolation();
            final CacheKey expectedAdditionalCacheKey = CacheKey.ofTenantAndPrincipalIsolation();

            assertCacheKeysMatchExchangeStrategy(
                DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE,
                expectedCacheKey,
                expectedAdditionalCacheKey);
        }));
    }

    @Test
    void testPrepareCommandWithForwardUserTokenStrategy()
    {
        PrincipalAccessor.executeWithPrincipal(p1, () -> TenantAccessor.executeWithTenant(t1, () -> {
            final CacheKey expectedCacheKey = CacheKey.ofTenantIsolation();
            final CacheKey expectedAdditionalCacheKey = CacheKey.ofTenantAndPrincipalIsolation();

            assertCacheKeysMatchExchangeStrategy(
                DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN,
                expectedCacheKey,
                expectedAdditionalCacheKey);
        }));
    }

    @Test
    void testPrepareCommandWithExchangeOnlyStrategy()
    {
        PrincipalAccessor.executeWithPrincipal(p1, () -> TenantAccessor.executeWithTenant(t1, () -> {
            final CacheKey cacheKey = CacheKey.ofTenantAndPrincipalIsolation();
            assertCacheKeysMatchExchangeStrategy(DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY, cacheKey);
        }));
    }

    private void assertCacheKeysMatchExchangeStrategy(
        DestinationServiceTokenExchangeStrategy tokenExchangeStrategy,
        CacheKey expectedCacheKey )
    {
        assertCacheKeysMatchExchangeStrategy(tokenExchangeStrategy, expectedCacheKey, null);
    }

    private void assertCacheKeysMatchExchangeStrategy(
        DestinationServiceTokenExchangeStrategy tokenExchangeStrategy,
        CacheKey expectedCacheKey,
        CacheKey expectedAdditionalCacheKey )
    {
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter.augmenter().tokenExchangeStrategy(tokenExchangeStrategy))
                .build();

        expectedCacheKey.append(DESTINATION_NAME, options);
        if( expectedAdditionalCacheKey != null ) {
            expectedAdditionalCacheKey.append(DESTINATION_NAME, options);
        }

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> {
                throw new IllegalStateException("The function is not expected to be executed in this test class");
            };

        final GetOrComputeSingleDestinationCommand sut =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(DESTINATION_NAME, options, destinationCache, isolationLocks, function, null)
                .get();

        assertThat(sut.getCacheKey()).isEqualTo(expectedCacheKey);
        assertThat(sut.getAdditionalKeyWithTenantAndPrincipal()).isEqualTo(expectedAdditionalCacheKey);
        assertThat(isolationLocks.getIfPresent(expectedCacheKey)).isNotNull();
    }

    /**
     * Tests that for destinations that require a user token exchange, explicitly using the
     * {@code ScpCfDestinationTokenExchangeStrategy.LOOKUP_ONLY} forces the destination to be cached for a tenant
     */
    @Test
    void testLookupOnlyOnUserTokenDestination()
    {
        final Destination scpCfDestination =
            DefaultHttpDestination
                .builder("")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_JWT_BEARER)
                .build();

        final CacheKey expectedCacheKey = CacheKey.ofTenantOptionalIsolation();
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        expectedCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfDestination;

        final GetOrComputeSingleDestinationCommand sut =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(DESTINATION_NAME, options, destinationCache, isolationLocks, function, null)
                .get();

        final Try<Destination> fetchedDestination = sut.execute();

        assertThat(destinationCache.getIfPresent(expectedCacheKey)).isNotNull();
        assertThat(isolationLocks.getIfPresent(expectedCacheKey)).isNotNull();
        assertThat(fetchedDestination.get()).isSameAs(scpCfDestination);
    }

    @Test
    void testLookupOnlyExchangeStrategy()
    {
        final Destination scpCfDestination =
            DefaultHttpDestination
                .builder("")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_CLIENT_CREDENTIALS)
                .build();

        final CacheKey expectedCacheKey = CacheKey.ofTenantOptionalIsolation();
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY))
                .build();

        expectedCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfDestination;

        final GetOrComputeSingleDestinationCommand sut =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(DESTINATION_NAME, options, destinationCache, isolationLocks, function, null)
                .get();

        final Try<Destination> fetchedDestination = sut.execute();

        assertThat(destinationCache.getIfPresent(expectedCacheKey)).isNotNull();
        assertThat(isolationLocks.getIfPresent(expectedCacheKey)).isNotNull();
        assertThat(fetchedDestination.get()).isSameAs(scpCfDestination);
    }

    @Test
    void testLookupThenExchangeStrategy()
    {
        final Destination scpCfDestination =
            DefaultHttpDestination
                .builder("")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_JWT_BEARER)
                .build();

        final CacheKey expectedCacheKey = CacheKey.of(t1, p1);
        final CacheKey tenantCacheKey = CacheKey.of(t1, null);
        @SuppressWarnings( "deprecation" )
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE))
                .build();

        expectedCacheKey.append(DESTINATION_NAME, options);
        tenantCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfDestination;

        final GetOrComputeSingleDestinationCommand sut =
            PrincipalAccessor
                .executeWithPrincipal(
                    p1,
                    () -> TenantAccessor
                        .executeWithTenant(
                            t1,
                            () -> GetOrComputeSingleDestinationCommand
                                .prepareCommand(
                                    DESTINATION_NAME,
                                    options,
                                    destinationCache,
                                    isolationLocks,
                                    function,
                                    null)
                                .get()));

        final Try<Destination> fetchedDestination = sut.execute();

        assertThat(sut.getAdditionalKeyWithTenantAndPrincipal()).isNotNull();
        //The destination cache key contains both tenant and principal
        assertThat(destinationCache.getIfPresent(expectedCacheKey)).isNotNull();
        //The isolation cache key contains only tenant
        assertThat(isolationLocks.getIfPresent(tenantCacheKey)).isNotNull();
        assertThat(fetchedDestination.get()).isSameAs(scpCfDestination);
    }

    @Test
    void testForwardUserTokenStrategyForUserPropagationDestination()
    {
        final Destination scpCfUserPropagationDestination =
            DefaultHttpDestination
                .builder("")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_JWT_BEARER)
                .build();

        final CacheKey expectedCacheKey = CacheKey.of(t1, p1);
        final CacheKey tenantCacheKey = CacheKey.of(t1, null);
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN))
                .build();

        expectedCacheKey.append(DESTINATION_NAME, options);
        tenantCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfUserPropagationDestination;

        final GetOrComputeSingleDestinationCommand sut =
            PrincipalAccessor
                .executeWithPrincipal(
                    p1,
                    () -> TenantAccessor
                        .executeWithTenant(
                            t1,
                            () -> GetOrComputeSingleDestinationCommand
                                .prepareCommand(
                                    DESTINATION_NAME,
                                    options,
                                    destinationCache,
                                    isolationLocks,
                                    function,
                                    null)
                                .get()));

        final Try<Destination> fetchedDestination =
            PrincipalAccessor.executeWithPrincipal(p1, () -> TenantAccessor.executeWithTenant(t1, () -> sut.execute()));

        assertThat(sut.getAdditionalKeyWithTenantAndPrincipal()).isEqualTo(expectedCacheKey);
        //The destination cache key contains both tenant and principal
        assertThat(destinationCache.getIfPresent(expectedCacheKey)).isEqualTo(scpCfUserPropagationDestination);
        //The isolation cache key contains only tenant
        assertThat(isolationLocks.getIfPresent(tenantCacheKey)).isNotNull();
        assertThat(fetchedDestination.get()).isSameAs(scpCfUserPropagationDestination);

    }

    @Test
    void testForwardUserTokenStrategyForUserPropagationDestinationWithoutJwt()
    {
        final CacheKey cacheKeyWithTenantAndPrincipal = CacheKey.of(t1, p1);
        final CacheKey tenantCacheKey = CacheKey.of(t1, null);
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN))
                .build();

        cacheKeyWithTenantAndPrincipal.append(DESTINATION_NAME, options);
        tenantCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> {
                throw new DestinationAccessException("error-message-because-principal-is-missing");
            };

        final GetOrComputeSingleDestinationCommand sut =
            PrincipalAccessor
                .executeWithPrincipal(
                    p1,
                    () -> TenantAccessor
                        .executeWithTenant(
                            t1,
                            () -> GetOrComputeSingleDestinationCommand
                                .prepareCommand(
                                    DESTINATION_NAME,
                                    options,
                                    destinationCache,
                                    isolationLocks,
                                    function,
                                    null)
                                .get()));

        final Try<Destination> fetchedDestination = sut.execute();

        assertThat(sut.getAdditionalKeyWithTenantAndPrincipal()).isEqualTo(cacheKeyWithTenantAndPrincipal);
        //The destination cache does not contain any entries
        assertThat(destinationCache.getIfPresent(cacheKeyWithTenantAndPrincipal)).isNull();
        assertThat(destinationCache.getIfPresent(tenantCacheKey)).isNull();
        assertThat(destinationCache.estimatedSize()).isEqualTo(0);
        //The isolation cache key contains only tenant
        assertThat(isolationLocks.getIfPresent(tenantCacheKey)).isNotNull();
        assertThat(fetchedDestination.getCause())
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("principal-is-missing");
    }

    @Test
    void testForwardUserTokenStrategyForUserPropagationDestinationWithoutPrincipalUnexpectedState()
    {
        final Destination scpCfUserPropagationDestination =
            DefaultHttpDestination
                .builder("")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_JWT_BEARER)
                .build();

        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN))
                .build();

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfUserPropagationDestination;

        final GetOrComputeSingleDestinationCommand sut =
            TenantAccessor
                .executeWithTenant(
                    t1,
                    () -> GetOrComputeSingleDestinationCommand
                        .prepareCommand(DESTINATION_NAME, options, destinationCache, isolationLocks, function, null)
                        .get());

        final Try<Destination> shouldBeFailure = TenantAccessor.executeWithTenant(t1, sut::execute);
        assertThat(shouldBeFailure.getCause())
            .isInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("No principal is available in the current ThreadContext");
    }

    @Test
    void testForwardUserTokenStrategyForClientCredentialsDestination()
    {
        final Destination scpCfClientCredentialsDestination =
            DefaultHttpDestination
                .builder("foo")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TYPE, OAUTH2_CLIENT_CREDENTIALS)
                .build();

        final CacheKey tenantCacheKey = CacheKey.of(t1, null);
        final CacheKey tenantAndPrincipalCacheKey = CacheKey.of(t1, p1);
        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    DestinationServiceOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN))
                .build();

        tenantCacheKey.append(DESTINATION_NAME, options);
        tenantAndPrincipalCacheKey.append(DESTINATION_NAME, options);

        final BiFunction<String, DestinationOptions, Destination> function =
            ( destinationName, destinationOptions ) -> scpCfClientCredentialsDestination;

        final GetOrComputeSingleDestinationCommand sut =
            PrincipalAccessor
                .executeWithPrincipal(
                    p1,
                    () -> TenantAccessor
                        .executeWithTenant(
                            t1,
                            () -> GetOrComputeSingleDestinationCommand
                                .prepareCommand(
                                    DESTINATION_NAME,
                                    options,
                                    destinationCache,
                                    isolationLocks,
                                    function,
                                    null)
                                .get()));

        final Try<Destination> fetchedDestination = PrincipalAccessor.executeWithPrincipal(p1, () -> sut.execute());

        assertThat(sut.getAdditionalKeyWithTenantAndPrincipal()).isEqualTo(tenantAndPrincipalCacheKey);
        //The destination cache key contains only tenant
        assertThat(destinationCache.getIfPresent(tenantCacheKey)).isEqualTo(scpCfClientCredentialsDestination);
        //The destination cache key does not contain principal and tenant key
        assertThat(destinationCache.getIfPresent(tenantAndPrincipalCacheKey)).isNull();
        //The isolation cache key contains only tenant
        assertThat(isolationLocks.getIfPresent(tenantCacheKey)).isNotNull();
        assertThat(fetchedDestination.get()).isSameAs(scpCfClientCredentialsDestination);

    }

    @Test
    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    void testChangeDetectionOnValidAuthToken()
    {
        // destination in single cache has valid auth token
        final Destination cachedDestination;
        final DestinationServiceV1Response.DestinationAuthToken authToken =
            mock(DestinationServiceV1Response.DestinationAuthToken.class);
        final LocalDateTime expiryTimestamp = LocalDateTime.now().plusDays(1L);
        when(authToken.getExpiryTimestamp()).thenReturn(expiryTimestamp);

        cachedDestination =
            DefaultHttpDestination
                .builder("https://bar")
                .name(DESTINATION_NAME)
                .property(DestinationProperty.AUTH_TOKENS, Collections.singletonList(authToken))
                .build();
        final CacheKey destinationCacheKey =
            CacheKey.ofTenantOptionalIsolation().append(DESTINATION_NAME, EMPTY_OPTIONS);
        destinationCache.put(destinationCacheKey, cachedDestination);

        // new destination in getAll cache
        final Destination newDestination = DefaultHttpDestination.builder("https://foo").name(DESTINATION_NAME).build();
        final GetOrComputeAllDestinationsCommand getAllCommand = mock(GetOrComputeAllDestinationsCommand.class);
        when(getAllCommand.execute()).thenReturn(Try.success(Collections.singletonList(newDestination)));
        // querying the destination service returns the new destination
        final BiFunction<String, DestinationOptions, Destination> destinationRetriever =
            (BiFunction<String, DestinationOptions, Destination>) mock(BiFunction.class);
        when(destinationRetriever.apply(any(), any())).thenReturn(newDestination);

        final Try<Destination> result =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(
                    DESTINATION_NAME,
                    EMPTY_OPTIONS,
                    destinationCache,
                    isolationLocks,
                    destinationRetriever,
                    getAllCommand)
                .flatMap(GetOrComputeSingleDestinationCommand::execute);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.get()).isNotSameAs(cachedDestination);
        assertThat(destinationCache.getIfPresent(destinationCacheKey)).isSameAs(result.get());

        // 2 invocations because of double-checked locking
        verify(getAllCommand, times(2)).execute();
        verify(destinationRetriever, times(1)).apply(eq(DESTINATION_NAME), eq(EMPTY_OPTIONS));
    }

    @Test
    void testChangeDetectionIgnoresPropertiesNotFromDestinationService()
    {
        final Destination destination =
            getPreparedBuilder()
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();
        final Destination destinationWithAuthToken =
            getPreparedBuilder()
                .property("authToken", "some auth token")
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();

        assertThat(GetOrComputeSingleDestinationCommand.destinationIsEqualTo(destination, destinationWithAuthToken))
            .isTrue();
    }

    @Test
    void testChangeDetectionDetectsChangedProperties()
    {
        final Destination destination =
            getPreparedBuilder()
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();
        final Destination destinationChanged =
            getPreparedBuilder()
                .property(DestinationProperty.URI, "https://new-url")
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();

        assertThat(GetOrComputeSingleDestinationCommand.destinationIsEqualTo(destination, destinationChanged))
            .isFalse();
    }

    @Test
    void testChangeDetectionDetectsAddedProperties()
    {
        final Destination destination =
            getPreparedBuilder()
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();
        final Collection<String> newRelevantProperties = getPreparedRelevantProperties();
        newRelevantProperties.add("new-property");
        final Destination destinationChanged =
            getPreparedBuilder()
                .property("new-property", "foo")
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, newRelevantProperties)
                .build();

        assertThat(GetOrComputeSingleDestinationCommand.destinationIsEqualTo(destination, destinationChanged))
            .isFalse();
    }

    @SuppressWarnings( "unchecked" )
    @Test
    void testFirstDestinationAccessDoesNotTriggerGetAllRequest()
    {
        // the getAll call only happens on the second access of a destination
        // the first access is always a cache miss and thus no change detection is triggered
        final Destination newDestination = DefaultHttpDestination.builder("https://foo").name(DESTINATION_NAME).build();
        final BiFunction<String, DestinationOptions, Destination> destinationRetriever =
            (BiFunction<String, DestinationOptions, Destination>) mock(BiFunction.class);
        when(destinationRetriever.apply(any(), any())).thenReturn(newDestination);

        final GetOrComputeAllDestinationsCommand getAllMock = mock(GetOrComputeAllDestinationsCommand.class);
        final GetOrComputeSingleDestinationCommand sut =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(
                    DESTINATION_NAME,
                    EMPTY_OPTIONS,
                    destinationCache,
                    isolationLocks,
                    destinationRetriever,
                    getAllMock)
                .get();
        final Try<Destination> result = sut.execute();

        assertThat(result).contains(newDestination);
        verify(getAllMock, never()).execute();
    }

    @Test
    void testChangeDetectionDetectsDeletedProperties()
    {
        final Collection<String> oldRelevantProperties = getPreparedRelevantProperties();
        oldRelevantProperties.add("to-be-deleted-property");
        final Destination destination =
            getPreparedBuilder()
                .property("to-be-deleted-property", "foo")
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, oldRelevantProperties)
                .build();

        final Destination destinationChanged =
            getPreparedBuilder()
                .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, getPreparedRelevantProperties())
                .build();

        assertThat(GetOrComputeSingleDestinationCommand.destinationIsEqualTo(destination, destinationChanged))
            .isFalse();
    }

    private static DefaultHttpDestination.Builder getPreparedBuilder()
    {
        return DefaultHttpDestination.builder("https://foo").name("foo");
    }

    private static Collection<String> getPreparedRelevantProperties()
    {
        return new ArrayList<>(
            Arrays
                .asList(
                    DestinationProperty.NAME.getKeyName(),
                    DestinationProperty.TYPE.getKeyName(),
                    DestinationProperty.URI.getKeyName()));
    }
}
