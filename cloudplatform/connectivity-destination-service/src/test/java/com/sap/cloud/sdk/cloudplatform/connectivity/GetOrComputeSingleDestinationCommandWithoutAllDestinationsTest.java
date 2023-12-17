/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
import com.sap.cloud.sdk.cloudplatform.thread.Executable;

import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings( "unchecked" )
@DisplayName( "Test all Combinations of AuthType, TokenExchangeStrategy, Tenant and Principal" )
class GetOrComputeSingleDestinationCommandWithoutAllDestinationsTest
{
    // region ( CONSTANTS )
    @Nullable
    private static final Tenant NO_TENANT = null;
    @Nonnull
    private static final Tenant TENANT = new DefaultTenant("T1");
    @Nullable
    private static final Principal NO_PRINCIPAL = null;
    @Nonnull
    private static final Principal PRINCIPAL = new DefaultPrincipal("P1");

    @Nonnull
    private static final Collection<Tenant> TENANTS = Arrays.asList(NO_TENANT, TENANT);

    @Nonnull
    private static final Collection<Principal> PRINCIPALS = Arrays.asList(NO_PRINCIPAL, PRINCIPAL);
    @Nonnull
    private static final Collection<AuthenticationType> AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE =
        new ArrayList<>();
    @Nonnull
    private static final Collection<AuthenticationType> AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE =
        new ArrayList<>();

    static {
        for( final AuthenticationType authenticationType : AuthenticationType.values() ) {
            if( DestinationUtility.requiresUserTokenExchange(authenticationType, null) ) {
                AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE.add(authenticationType);
            } else {
                AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE.add(authenticationType);
            }
        }
    }
    //endregion

    static Collection<TestCase> createTestParameters()
    {
        final Collection<TestCase> result = new ArrayList<>();

        //region ( AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE )
        {
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE)
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( ALL_AUTHENTICATION_TYPES ) X ( LOOKUP_ONLY )
        {
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(LOOKUP_ONLY)
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN )
        {
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(FORWARD_USER_TOKEN)
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKey())
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKey())
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( ALL_AUTHENTICATION_TYPES ) x ( EXCHANGE_ONLY )
        {
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(EXCHANGE_ONLY)
                    .withExpectation(forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL).expectCommandCreationToFail())
                    .withExpectation(forTenantAndPrincipal(NO_TENANT, PRINCIPAL).expectCommandCreationToFail())
                    .withExpectation(forTenantAndPrincipal(TENANT, NO_PRINCIPAL).expectCommandCreationToFail())
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) x ( LOOKUP_THEN_EXCHANGE )
        {
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(LOOKUP_THEN_EXCHANGE)
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, PRINCIPAL))
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        return result;
    }

    private Cache<CacheKey, Destination> destinationCache;
    private Cache<CacheKey, ReentrantLock> isolationLocks;
    private BiFunction<String, DestinationOptions, Destination> destinationRetriever;
    private SoftAssertions softly;

    @BeforeEach
    void setup()
    {
        destinationCache = Caffeine.newBuilder().build();
        isolationLocks = Caffeine.newBuilder().build();
        destinationRetriever = (BiFunction<String, DestinationOptions, Destination>) mock(BiFunction.class);
        softly = new SoftAssertions();
    }

    @ParameterizedTest
    @MethodSource( "createTestParameters" )
    void testCorrectIsolationAndCaching( @Nonnull final TestCase testCase )
        throws Exception
    {
        testCase.stubDestinationRetriever(destinationRetriever);

        executeInUserContext(testCase.getTenant(), testCase.getPrincipal(), () -> {
            runTest(testCase);
            assertOnCacheAndIsolationLocks(testCase);
        });
        softly.assertAll();
    }

    private void runTest( TestCase testCase )
    {
        final Try<GetOrComputeSingleDestinationCommand> maybeCommand =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(
                    testCase.getDestinationName(),
                    testCase.getOptions(),
                    destinationCache,
                    isolationLocks,
                    destinationRetriever,
                    null);

        if( maybeCommand.isFailure() ) {
            if( testCase.isCommandCreationExpectedToSucceed() ) {
                softly
                    .fail(
                        "Expected command creation to succeed, but it failed with exception: "
                            + maybeCommand.getCause());
            }
            return;
        } else if( maybeCommand.isSuccess() && !testCase.isCommandCreationExpectedToSucceed() ) {
            softly.fail("Expected command creation to fail, but it succeeded.");
            return;
        }

        final Try<Destination> maybeDestination = maybeCommand.get().execute();
        if( maybeDestination.isFailure() ) {
            if( testCase.isCommandExecutionExpectedToSucceed() ) {
                softly
                    .fail(
                        "Expected command execution to succeed, but it failed with exception: "
                            + maybeDestination.getCause());
            }
            return;
        } else if( maybeDestination.isSuccess() && !testCase.isCommandExecutionExpectedToSucceed() ) {
            softly.fail("Expected command execution to fail, but it succeeded.");
        }
        softly.assertThat(maybeDestination.get()).isEqualTo(testCase.getDestination());
        // sanity checks no cache was hit
        verify(destinationRetriever, times(1)).apply(eq(testCase.getDestinationName()), same(testCase.getOptions()));
    }

    private void assertOnCacheAndIsolationLocks( TestCase testCase )
    {
        final CacheKey isolationKey = testCase.getExpectedIsolationKey();

        isolationLocks
            .asMap()
            .keySet()
            .stream()
            .filter(key -> !key.equals(isolationKey))
            .forEach(key -> softly.fail("Isolation locks contained unexpected key %s", key));
        if( isolationKey != null ) {
            softly
                .assertThat(isolationLocks.getIfPresent(isolationKey))
                .withFailMessage("Isolation locks did not contain expected key %s", isolationKey)
                .isNotNull();
        }

        final CacheKey destinationKey = testCase.getExpectedDestinationKey();
        destinationCache
            .asMap()
            .keySet()
            .stream()
            .filter(key -> !key.equals(destinationKey))
            .forEach(
                key -> softly
                    .fail(
                        "Destination cache contained unexpected key %s with value %s",
                        key,
                        destinationCache.getIfPresent(key)));

        if( destinationKey != null ) {
            softly
                .assertThat(destinationCache.getIfPresent(destinationKey))
                .withFailMessage("Destination cache did not contain expected key %s", isolationKey)
                .isNotNull();
        }
    }

    @Test
    void testAssertAllCasesAreCovered()
    {
        final Collection<TestCase> testCases = createTestParameters();
        for( final AuthenticationType authenticationType : AuthenticationType.values() ) {
            for( final Tenant tenant : TENANTS ) {
                for( final Principal principal : PRINCIPALS ) {
                    for( final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy : DestinationServiceTokenExchangeStrategy
                        .values() ) {
                        softly
                            .assertThat(testCases)
                            .withFailMessage(
                                "No test parameters found for authentication type '%s', token exchange strategy '%s', tenant '%s' and principal '%s.",
                                authenticationType,
                                tokenExchangeStrategy,
                                tenant,
                                principal)
                            .anyMatch(
                                t -> t.getAuthenticationType() == authenticationType
                                    && t.getTokenExchangeStrategy() == tokenExchangeStrategy
                                    && t.getTenant() == tenant
                                    && t.getPrincipal() == principal);
                    }
                }
            }
        }
        softly.assertAll();
    }

    // region (BUILDERS)
    static TestCase.Builder forTenantAndPrincipal( @Nullable final Tenant tenant, @Nullable final Principal principal )
    {
        final TestCase.Builder builder = new TestCase.Builder();
        builder.tenant = tenant;
        builder.principal = principal;
        return builder;
    }

    @RequiredArgsConstructor
    @Getter
    static class TestCase
    {
        // input parameters
        @Nonnull
        private final Destination destination;
        @Nonnull
        private final DestinationOptions options;
        @Nullable
        private final Tenant tenant;
        @Nullable
        private final Principal principal;

        // expected outcome
        private final boolean isCommandCreationExpectedToSucceed;
        private final boolean isCommandExecutionExpectedToSucceed;
        @Nullable
        private final CacheKey expectedIsolationKey;
        @Nullable
        private final CacheKey expectedDestinationKey;

        String getDestinationName()
        {
            return destination.get(DestinationProperty.NAME).get();
        }

        AuthenticationType getAuthenticationType()
        {
            return destination.get(DestinationProperty.AUTH_TYPE).get();
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder();

            sb.append(getAuthenticationType());
            sb.append(" + ");
            sb.append(getTokenExchangeStrategy());
            sb.append(" + ");
            if( tenant != null ) {
                sb.append("Tenant");
            } else {
                sb.append("NO_TENANT");
            }
            sb.append(" + ");
            if( principal != null ) {
                sb.append("Principal");
            } else {
                sb.append("NO_PRINCIPAL");
            }
            return sb.toString();
        }

        DestinationServiceTokenExchangeStrategy getTokenExchangeStrategy()
        {
            return DestinationServiceOptionsAugmenter.getTokenExchangeStrategy(options).get();
        }

        void stubDestinationRetriever( BiFunction<String, DestinationOptions, Destination> destinationRetriever )
        {
            if( DestinationUtility.requiresUserTokenExchange(getAuthenticationType(), null)
                && principal == null
                && getTokenExchangeStrategy() == LOOKUP_THEN_EXCHANGE ) {
                doThrow(
                    new DestinationAccessException(
                        LOOKUP_THEN_EXCHANGE
                            + " throws on authentication types that require user token exchange and no principal is given."))
                    .when(destinationRetriever)
                    .apply(any(), any());
            } else {
                doReturn(getDestination()).when(destinationRetriever).apply(any(), any());
            }
        }

        static final class Builder
        {
            @Nullable
            private Tenant tenant;
            @Nullable
            private Principal principal;

            // expected outcome
            private boolean isCommandCreationExpectedToSucceed = true;
            private boolean isCommandExecutionExpectedToSucceed = true;
            private Tuple2<Tenant, Principal> expectedIsolationKeyStrategy;
            @Nullable
            private Tuple2<Tenant, Principal> expectedDestinationKeyStrategy;

            @Nonnull
            Builder expectIsolationCacheKey( @Nullable final Tenant tenant, @Nullable final Principal principal )
            {
                expectedIsolationKeyStrategy = new Tuple2<>(tenant, principal);
                return this;
            }

            @Nonnull
            Builder expectDestinationCacheKey( @Nullable final Tenant tenant, @Nullable final Principal principal )
            {
                expectedDestinationKeyStrategy = new Tuple2<>(tenant, principal);
                return this;
            }

            @Nonnull
            Builder expectNoDestinationCacheKey()
            {
                expectedDestinationKeyStrategy = null;
                return this;
            }

            @Nonnull
            Builder expectCommandCreationToFail()
            {
                isCommandCreationExpectedToSucceed = false;
                return this;
            }

            @Nonnull
            Builder expectCommandExecutionToFail()
            {
                isCommandExecutionExpectedToSucceed = false;
                return this;
            }

            TestCase forAuthenticationTypeAndOptions(
                AuthenticationType authType,
                DestinationServiceTokenExchangeStrategy strategy )
            {
                if( isCommandCreationExpectedToSucceed && expectedIsolationKeyStrategy == null ) {
                    throw new IllegalStateException(
                        "Test setup error: Cannot expect command creation to succeed and expect no isolation cache key.");
                }
                if( !isCommandCreationExpectedToSucceed && expectedIsolationKeyStrategy != null ) {
                    throw new IllegalStateException(
                        "Test setup error: Cannot expect command creation to fail and expect an isolation cache key.");
                }
                // Note that the case:
                // commandExecutionIsExpectedToSucceed && expectedDestinationKey.get() == null
                // is allowed, because some results may be returned but not cached
                if( !isCommandExecutionExpectedToSucceed && expectedDestinationKeyStrategy != null ) {
                    throw new IllegalStateException(
                        "Test setup error: Cannot expect command execution to fail and expect a destination cache key.");
                }
                final Destination destination = prepareDestination(authType, principal);

                final DestinationOptions options =
                    DestinationOptions
                        .builder()
                        .augmentBuilder(DestinationServiceOptionsAugmenter.augmenter().tokenExchangeStrategy(strategy))
                        .build();
                final CacheKey expectedIsolationKey =
                    expectedIsolationKeyStrategy == null
                        ? null
                        : CacheKey
                            .of(expectedIsolationKeyStrategy._1, expectedIsolationKeyStrategy._2)
                            .append(destination.get(DestinationProperty.NAME).get(), options);
                final CacheKey expectedDestinationKey =
                    expectedDestinationKeyStrategy == null
                        ? null
                        : CacheKey
                            .of(expectedDestinationKeyStrategy._1, expectedDestinationKeyStrategy._2)
                            .append(destination.get(DestinationProperty.NAME).get(), options);

                return new TestCase(
                    destination,
                    options,
                    tenant,
                    principal,
                    isCommandCreationExpectedToSucceed,
                    isCommandExecutionExpectedToSucceed,
                    expectedIsolationKey,
                    expectedDestinationKey);
            }

            private static Destination prepareDestination( AuthenticationType authType, Principal principal )
            {
                final DefaultDestination.Builder builder =
                    DefaultDestination
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .property(DestinationProperty.AUTH_TYPE, authType);

                final DestinationServiceV1Response.DestinationAuthToken token =
                    new DestinationServiceV1Response.DestinationAuthToken();
                if( DestinationUtility.requiresUserTokenExchange(authType, null) && principal == null ) {
                    token.setError("This is an error in the destination auth token");
                } else {
                    token.setValue("success");
                    token.setHttpHeaderSuggestion(new Header("destination-auth-token", "success"));
                    token.setExpiryTimestamp(LocalDateTime.now().plus(Duration.ofHours(12)));
                }
                builder.property(DestinationProperty.AUTH_TOKENS, List.of(token));
                return builder.build();
            }
        }
    }

    @Nonnull
    private static TestCaseBatchBuilder createBatchTestCases()
    {
        return new TestCaseBatchBuilder();
    }

    private static class TestCaseBatchBuilder
    {
        private final Collection<TestCase> testCases = new ArrayList<>();
        @Nonnull
        private final Collection<AuthenticationType> authenticationTypes = new HashSet<>();
        @Nonnull
        private final Collection<DestinationServiceTokenExchangeStrategy> tokenExchangeStrategies = new HashSet<>();

        @Nonnull
        TestCaseBatchBuilder forAuthenticationTypes( @Nonnull final Collection<AuthenticationType> authenticationTypes )
        {
            if( !testCases.isEmpty() ) {
                throw new IllegalStateException(
                    "Incorrect test setup: Attempting to add further authentication types after test cases have been created via withExpectation().");
            }
            this.authenticationTypes.addAll(authenticationTypes);
            return this;
        }

        @Nonnull
        TestCaseBatchBuilder forTokenExchangeStrategies(
            @Nonnull final DestinationServiceTokenExchangeStrategy firstTokenExchangeStrategy,
            @Nonnull final DestinationServiceTokenExchangeStrategy... furtherTokenExchangeStrategies )
        {
            if( !testCases.isEmpty() ) {
                throw new IllegalStateException(
                    "Incorrect test setup: Attempting to add further token strategies after test cases have been created via withExpectation().");
            }
            tokenExchangeStrategies.add(firstTokenExchangeStrategy);
            tokenExchangeStrategies.addAll(Arrays.asList(furtherTokenExchangeStrategies));
            return this;
        }

        @Nonnull
        TestCaseBatchBuilder withExpectation( TestCase.Builder builder )
        {
            for( final AuthenticationType authenticationType : authenticationTypes ) {
                for( final DestinationServiceTokenExchangeStrategy tokenExchangeStrategy : tokenExchangeStrategies ) {
                    final TestCase testCase =
                        builder.forAuthenticationTypeAndOptions(authenticationType, tokenExchangeStrategy);
                    testCases.add(testCase);
                }
            }
            return this;
        }

        @Nonnull
        public Collection<TestCase> build()
        {
            return testCases;
        }
    }

    // endregion
    private static void executeInUserContext(
        @Nullable final Tenant tenant,
        @Nullable final Principal principal,
        @Nonnull final Executable command )
        throws Exception
    {
        final List<Executable> commandStack = new ArrayList<>();
        commandStack.add(command);

        if( tenant != null ) {
            final int previousCommandStackIndex = 0;
            commandStack
                .add(() -> TenantAccessor.executeWithTenant(tenant, commandStack.get(previousCommandStackIndex)));
        }

        if( principal != null ) {
            final int previousCommandStackIndex = commandStack.size() - 1;
            commandStack
                .add(
                    () -> PrincipalAccessor
                        .executeWithPrincipal(principal, commandStack.get(previousCommandStackIndex)));
        }

        commandStack.get(commandStack.size() - 1).execute();
    }
}
