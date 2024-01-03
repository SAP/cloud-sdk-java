/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

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

@SuppressWarnings( "deprecation" )
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

        //region ( AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE, LOOKUP_ONLY )
        {
            @SuppressWarnings( "deprecation" )
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(
                        FORWARD_USER_TOKEN,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY)
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
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE )
        {
            @SuppressWarnings( "deprecation" )
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(
                        DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE)
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
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) X ( LOOKUP_ONLY )
        {
            @SuppressWarnings( "deprecation" )
            final Collection<TestCase> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY)
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .withExpectation(
                        forTenantAndPrincipal(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .withExpectation(
                        forTenantAndPrincipal(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectCommandExecutionToFail())
                    .build();
            result.addAll(testCases);
        }
        //endregion
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
        return result;
    }

    private Cache<CacheKey, Destination> destinationCache;
    private Cache<CacheKey, ReentrantLock> isolationLocks;
    private DestinationService destinationService;
    private SoftAssertions softly;

    @BeforeEach
    void setup()
    {
        destinationCache = Caffeine.newBuilder().build();
        isolationLocks = Caffeine.newBuilder().build();
        final DestinationServiceAdapter adapter = mock(DestinationServiceAdapter.class);
        destinationService = spy(new DestinationService(mock(DestinationServiceAdapter.class)));
        softly = new SoftAssertions();
    }

    @ParameterizedTest
    @MethodSource( "createTestParameters" )
    void testCorrectIsolationAndCaching( @Nonnull final TestCase testCase )
        throws Exception
    {
        testCase.stubDestinationRetriever(destinationService);

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
                    destinationService::loadAndParseDestination,
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
        // softly.assertThat(maybeDestination.get()).isEqualTo(testCase.getExpectedDestination());
        // sanity checks no cache was hit
        if( testCase.getTokenExchangeStrategy() == DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE
            && DestinationUtility.requiresUserTokenExchange(testCase.getExpectedDestination()) ) {
            verify(destinationService, times(2)).retrieveDestination(any(), any());
        } else {
            verify(destinationService, times(1)).retrieveDestination(any(), any());
        }
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
        private final DestinationServiceV1Response destinationServiceResponse;
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
        @Nonnull
        private final Destination expectedDestination;

        String getDestinationName()
        {
            return expectedDestination.get(DestinationProperty.NAME).get();
        }

        AuthenticationType getAuthenticationType()
        {
            return expectedDestination.get(DestinationProperty.AUTH_TYPE).get();
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

        void stubDestinationRetriever( DestinationService service )
        {
            doReturn(getDestinationServiceResponse()).when(service).retrieveDestination(any(), any());
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
            @Nullable
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
                final String destinationName = UUID.randomUUID().toString();

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
                            .append(destinationName, options);
                final CacheKey expectedDestinationKey =
                    expectedDestinationKeyStrategy == null
                        ? null
                        : CacheKey
                            .of(expectedDestinationKeyStrategy._1, expectedDestinationKeyStrategy._2)
                            .append(destinationName, options);
                return finaliseTestCase(
                    destinationName,
                    authType,
                    options,
                    expectedIsolationKey,
                    expectedDestinationKey);
            }

            private TestCase finaliseTestCase(
                String destinationName,
                AuthenticationType authType,
                DestinationOptions options,
                CacheKey isolationKey,
                CacheKey destinationKey )
            {
                final DestinationServiceV1Response response = new DestinationServiceV1Response();
                final Map<String, String> properties =
                    Map
                        .of(
                            DestinationProperty.AUTH_TYPE.getKeyName(),
                            authType.toString(),
                            DestinationProperty.NAME.getKeyName(),
                            destinationName,
                            DestinationProperty.TYPE.getKeyName(),
                            DestinationType.HTTP.toString(),
                            DestinationProperty.URI.getKeyName(),
                            "https://example.com");
                response.setDestinationConfiguration(properties);

                final DestinationServiceV1Response.DestinationAuthToken token =
                    new DestinationServiceV1Response.DestinationAuthToken();
                if( DestinationUtility.requiresUserTokenExchange(authType, null)
                    && (principal == null
                        || DestinationServiceOptionsAugmenter
                            .getTokenExchangeStrategy(options)
                            .contains(DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY)) ) {
                    token.setError("This is an error in the destination auth token");
                } else {
                    token.setValue("success");
                    token.setHttpHeaderSuggestion(new Header("destination-auth-token", "success"));
                    token.setExpiryTimestamp(LocalDateTime.now().plus(Duration.ofHours(12)));
                }
                response.setAuthTokens(List.of(token));

                final Destination expectedDestination =
                    DefaultDestination
                        .fromMap(properties)
                        .property(DestinationProperty.TENANT_ID, tenant == null ? "" : tenant.getTenantId())
                        .property(DestinationProperty.AUTH_TOKENS, List.of(token))
                        .property(DestinationProperty.PROPERTIES_FOR_CHANGE_DETECTION, properties.keySet())
                        .build();

                return new TestCase(
                    response,
                    options,
                    tenant,
                    principal,
                    isCommandCreationExpectedToSucceed,
                    isCommandExecutionExpectedToSucceed,
                    isolationKey,
                    destinationKey,
                    expectedDestination);
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
