package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.Executable;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RunWith( Parameterized.class )
public class GetOrComputeSingleDestinationCommandWithoutAllDestinationsTest
{
    @Nullable
    private static final Tenant NO_TENANT = null;
    @Nonnull
    private static final Tenant TENANT = new DefaultTenant("T1");
    @Nullable
    private static final Principal NO_PRINCIPAL = null;
    @Nonnull
    private static final Principal PRINCIPAL =
        new DefaultPrincipal("P1", Collections.emptySet(), Collections.emptyMap());

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

    @Parameterized.Parameters( name = "{0}" )
    public static Collection<TestParameters> createTestParameters()
    {
        final Collection<TestParameters> result = new ArrayList<>();

        //region ( AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE )
        {
            final Collection<TestParameters> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(FORWARD_USER_TOKEN, LOOKUP_THEN_EXCHANGE)
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( ALL_AUTHENTICATION_TYPES ) X ( LOOKUP_ONLY )
        {
            final Collection<TestParameters> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(LOOKUP_ONLY)
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, NO_PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) X ( FORWARD_USER_TOKEN )
        {
            final Collection<TestParameters> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(FORWARD_USER_TOKEN)
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKeyBecauseDestinationRetrievalFails())
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKeyBecauseDestinationRetrievalFails())
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( ALL_AUTHENTICATION_TYPES ) x ( EXCHANGE_ONLY )
        {
            final Collection<TestParameters> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_DO_NOT_REQUIRE_TOKEN_EXCHANGE)
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(EXCHANGE_ONLY)
                    .withUserParameters(expectCommandCreationToFail(NO_TENANT, NO_PRINCIPAL))
                    .withUserParameters(expectCommandCreationToFail(NO_TENANT, PRINCIPAL))
                    .withUserParameters(expectCommandCreationToFail(TENANT, NO_PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        //region ( AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE ) x ( LOOKUP_THEN_EXCHANGE )
        {
            final Collection<TestParameters> testCases =
                createBatchTestCases()
                    .forAuthenticationTypes(AUTHENTICATION_TYPES_THAT_REQUIRE_TOKEN_EXCHANGE)
                    .forTokenExchangeStrategies(LOOKUP_THEN_EXCHANGE)
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKeyBecauseDestinationRetrievalFails())
                    .withUserParameters(
                        expectCommandCreationToSucceed(NO_TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(NO_TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(NO_TENANT, PRINCIPAL))
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, NO_PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectNoDestinationCacheKeyBecauseDestinationRetrievalFails())
                    .withUserParameters(
                        expectCommandCreationToSucceed(TENANT, PRINCIPAL)
                            .expectIsolationCacheKey(TENANT, NO_PRINCIPAL)
                            .expectDestinationCacheKey(TENANT, PRINCIPAL))
                    .build();
            result.addAll(testCases);
        }
        //endregion
        return result;
    }

    @Parameterized.Parameter
    public TestParameters testParameters;

    @Test
    public void testCorrectIsolationAndCaching()
    {
        final String destinationName = UUID.randomUUID().toString();
        final DefaultDestination destination =
            DefaultDestination
                .builder()
                .name(destinationName)
                .property(DestinationProperty.AUTH_TYPE, testParameters.getAuthenticationType())
                .build();

        final DestinationOptions options =
            DestinationOptions
                .builder()
                .augmentBuilder(
                    ScpCfDestinationOptionsAugmenter
                        .augmenter()
                        .tokenExchangeStrategy(testParameters.getTokenExchangeStrategy()))
                .build();

        for( final Tenant tenant : TENANTS ) {
            for( final Principal principal : PRINCIPALS ) {
                final UserSpecificTestParameters userParameters = testParameters.getUserParameters(tenant, principal);

                if( !userParameters.commandCreationIsExpectedToSucceed() ) {
                    assertCommandCreationFails(destination, options);
                    continue;
                }

                runCorrectIsolationAndCaching(userParameters, destination, options, tenant, principal);
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void assertCommandCreationFails(
        @Nonnull final DefaultDestination destination,
        @Nonnull final DestinationOptions destinationOptions )
    {
        final BiFunction<String, DestinationOptions, Destination> destinationRetriever =
            (BiFunction<String, DestinationOptions, Destination>) mock(BiFunction.class);
        final Cache<CacheKey, Destination> destinationCache = Caffeine.newBuilder().build();
        final Cache<CacheKey, ReentrantLock> isolationLocks = Caffeine.newBuilder().build();

        final Try<GetOrComputeSingleDestinationCommand> maybeCommand =
            GetOrComputeSingleDestinationCommand
                .prepareCommand(
                    destination.get(DestinationProperty.NAME).get(),
                    destinationOptions,
                    destinationCache,
                    isolationLocks,
                    destinationRetriever,
                    null);
        assertThat(maybeCommand.isFailure()).isTrue();
    }

    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    private void runCorrectIsolationAndCaching(
        @Nonnull final UserSpecificTestParameters userParameters,
        @Nonnull final DefaultDestination destination,
        @Nonnull final DestinationOptions destinationOptions,
        @Nullable final Tenant tenant,
        @Nullable final Principal principal )
    {
        final BiFunction<String, DestinationOptions, Destination> destinationRetriever =
            (BiFunction<String, DestinationOptions, Destination>) mock(BiFunction.class);
        if( userParameters.destinationRetrievalShouldFail() ) {
            when(destinationRetriever.apply(any(), any()))
                .thenThrow(new CloudPlatformException("The destination retrieval is expected to fail."));
        } else {
            when(destinationRetriever.apply(destination.get(DestinationProperty.NAME).get(), destinationOptions))
                .thenReturn(destination);
        }

        final Cache<CacheKey, Destination> destinationCache = Caffeine.newBuilder().build();
        final Cache<CacheKey, ReentrantLock> isolationLocks = Caffeine.newBuilder().build();

        @Nullable
        final CacheKey expectedIsolationCacheKey = userParameters.getExpectedIsolationCacheKey();
        @Nullable
        final CacheKey expectedDestinationCacheKey = userParameters.getExpectedDestinationCacheKey();
        if( expectedIsolationCacheKey != null ) {
            expectedIsolationCacheKey.append(destination.get(DestinationProperty.NAME).get(), destinationOptions);
        }
        if( expectedDestinationCacheKey != null ) {
            expectedDestinationCacheKey.append(destination.get(DestinationProperty.NAME).get(), destinationOptions);
        }

        executeInUserContext(tenant, principal, () -> {
            final GetOrComputeSingleDestinationCommand sut =
                GetOrComputeSingleDestinationCommand
                    .prepareCommand(
                        destination.get(DestinationProperty.NAME).get(),
                        destinationOptions,
                        destinationCache,
                        isolationLocks,
                        destinationRetriever,
                        null)
                    .get();

            assertThat(sut.getCacheKey())
                .withFailMessage(
                    "Isolation cache key mismatch.\nExpected: %s\nActual: %s",
                    expectedIsolationCacheKey,
                    sut.getCacheKey())
                .isEqualTo(expectedIsolationCacheKey);

            final Try<Destination> maybeResult = sut.execute();
            assertThat(maybeResult.isFailure()).isEqualTo(userParameters.destinationRetrievalShouldFail());
            if( userParameters.destinationRetrievalShouldFail() ) {
                assertThat(maybeResult.getCause()).isExactlyInstanceOf(CloudPlatformException.class);
            } else {
                assertThat(maybeResult).containsExactly(destination);
            }

            if( expectedIsolationCacheKey != null ) {
                assertThat(isolationLocks.estimatedSize()).isEqualTo(1L);
                assertThat(isolationLocks.getIfPresent(expectedIsolationCacheKey))
                    .withFailMessage("Expected isolation locks to contain key %s", expectedIsolationCacheKey)
                    .isNotNull();
            } else {
                assertThat(isolationLocks.estimatedSize()).isEqualTo(0L);
            }

            if( expectedDestinationCacheKey != null ) {
                assertThat(destinationCache.estimatedSize()).isEqualTo(1L);
                assertThat(destinationCache.getIfPresent(expectedDestinationCacheKey))
                    .withFailMessage("Expected destination to be cached using key %s", expectedDestinationCacheKey)
                    .isNotNull();
            } else {
                assertThat(destinationCache.estimatedSize()).isEqualTo(0L);
            }

            verify(destinationRetriever, times(1)).apply(any(), any());
        });

    }

    private void executeInUserContext(
        @Nullable final Tenant tenant,
        @Nullable final Principal principal,
        @Nonnull final Executable command )
        throws Exception
    {
        final List<Executable> commandStack = new ArrayList<>();
        commandStack.add(command);

        if( tenant != null ) {
            final int previousCommandStackIndex = commandStack.size() - 1;
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

    @Test
    public void testAssertAllCasesAreCovered()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Collection<TestParameters> remainingTestParameters = createTestParameters();
        for( final AuthenticationType authenticationType : AuthenticationType.values() ) {
            for( final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy : ScpCfDestinationTokenExchangeStrategy
                .values() ) {
                removeTestParametersOrLogError(
                    softly,
                    remainingTestParameters,
                    authenticationType,
                    tokenExchangeStrategy);
            }
        }

        softly.assertAll();
    }

    private void removeTestParametersOrLogError(
        @Nonnull final SoftAssertions softly,
        @Nonnull final Collection<TestParameters> remainingTestParameters,
        @Nonnull final AuthenticationType authenticationType,
        @Nullable final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
    {
        final boolean removed = remainingTestParameters.removeIf(t -> {
            if( authenticationType != t.getAuthenticationType() ) {
                return false;
            }

            if( tokenExchangeStrategy != t.getTokenExchangeStrategy() ) {
                return false;
            }

            return true;
        });

        if( !removed ) {
            softly
                .fail(
                    "No test parameters found for authentication type '"
                        + authenticationType
                        + "' and token exchange strategy '"
                        + tokenExchangeStrategy
                        + "'.");
        }
    }

    @Nonnull
    private static TestParametersBuilder createTestCase(
        @Nonnull final AuthenticationType authenticationType,
        @Nonnull final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy )
    {
        return new TestParametersBuilder(authenticationType, tokenExchangeStrategy);
    }

    @Nonnull
    private static TestParametersBatchBuilder createBatchTestCases()
    {
        return new TestParametersBatchBuilder();
    }

    private static class TestParametersBatchBuilder
    {
        @Nonnull
        private final Collection<AuthenticationType> authenticationTypes = new HashSet<>();
        @Nonnull
        private final Collection<ScpCfDestinationTokenExchangeStrategy> tokenExchangeStrategies = new HashSet<>();
        @Nonnull
        private final Collection<UserSpecificTestParameters> userTestParameters = new ArrayList<>();

        @Nonnull
        public TestParametersBatchBuilder forAuthenticationTypes(
            @Nonnull final AuthenticationType firstAuthenticationType,
            @Nonnull final AuthenticationType... furtherAuthenticationTypes )
        {
            authenticationTypes.add(firstAuthenticationType);
            authenticationTypes.addAll(Arrays.asList(furtherAuthenticationTypes));
            return this;
        }

        @Nonnull
        public TestParametersBatchBuilder forAuthenticationTypes(
            @Nonnull final Collection<AuthenticationType> authenticationTypes )
        {
            this.authenticationTypes.addAll(authenticationTypes);
            return this;
        }

        @Nonnull
        public TestParametersBatchBuilder forTokenExchangeStrategies(
            @Nonnull final ScpCfDestinationTokenExchangeStrategy firstTokenExchangeStrategy,
            @Nonnull final ScpCfDestinationTokenExchangeStrategy... furtherTokenExchangeStrategies )
        {
            tokenExchangeStrategies.add(firstTokenExchangeStrategy);
            tokenExchangeStrategies.addAll(Arrays.asList(furtherTokenExchangeStrategies));
            return this;
        }

        @Nonnull
        public TestParametersBatchBuilder forTokenExchangeStrategies(
            @Nonnull final Collection<ScpCfDestinationTokenExchangeStrategy> tokenExchangeStrategies )
        {
            this.tokenExchangeStrategies.addAll(tokenExchangeStrategies);
            return this;
        }

        @Nonnull
        public TestParametersBatchBuilder withUserParameters(
            @Nonnull final UserSpecificTestParametersBuilder userParametersBuilder )
        {
            userTestParameters.add(userParametersBuilder.build());
            return this;
        }

        @Nonnull
        public TestParametersBatchBuilder withUserParameters( @Nonnull final UserSpecificTestParameters userParameters )
        {
            userTestParameters.add(userParameters);
            return this;
        }

        @Nonnull
        public Collection<TestParameters> build()
        {
            final Collection<TestParameters> result = new ArrayList<>();
            for( final AuthenticationType authenticationType : authenticationTypes ) {
                for( final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy : tokenExchangeStrategies ) {
                    result.add(new TestParameters(authenticationType, tokenExchangeStrategy, userTestParameters));
                }
            }
            return result;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class TestParameters
    {
        @Nonnull
        private final AuthenticationType authenticationType;
        @Nonnull
        private final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy;
        @Nonnull
        private final Collection<UserSpecificTestParameters> userTestParameters;

        @Nonnull
        public
            UserSpecificTestParameters
            getUserParameters( @Nullable final Tenant tenant, @Nullable final Principal principal )
        {
            return userTestParameters
                .stream()
                .filter(
                    userTestParameters -> Objects.equals(userTestParameters.getTenant(), tenant)
                        && Objects.equals(userTestParameters.getPrincipal(), principal))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        "No test parameters found for tenant '" + tenant + "' and principal '" + principal + "'."));
        }

        @Override
        public String toString()
        {
            return authenticationType + " + " + tokenExchangeStrategy;
        }
    }

    @RequiredArgsConstructor
    private static class TestParametersBuilder
    {
        @Nonnull
        private final AuthenticationType authenticationType;
        @Nonnull
        private final ScpCfDestinationTokenExchangeStrategy tokenExchangeStrategy;
        @Nonnull
        private final Collection<UserSpecificTestParameters> userTestParameters = new ArrayList<>();

        @Nonnull
        public TestParametersBuilder withUserParameters(
            @Nonnull final UserSpecificTestParametersBuilder userParametersBuilder )
        {
            userTestParameters.add(userParametersBuilder.build());
            return this;
        }

        @Nonnull
        public TestParameters build()
        {
            return new TestParameters(authenticationType, tokenExchangeStrategy, userTestParameters);
        }
    }

    @RequiredArgsConstructor
    public static class UserSpecificTestParameters
    {
        @Getter
        @Nullable
        private final Tenant tenant;
        @Getter
        @Nullable
        private final Principal principal;
        private final boolean commandCreationIsExpectedToSucceed;
        @Nonnull
        private final Supplier<CacheKey> expectedIsolationCacheKeySupplier;
        @Nonnull
        private final Supplier<CacheKey> expectedDestinationCacheKeySupplier;
        private final boolean destinationRetrievalShouldFail;

        public boolean commandCreationIsExpectedToSucceed()
        {
            return commandCreationIsExpectedToSucceed;
        }

        @Nullable
        public CacheKey getExpectedIsolationCacheKey()
        {
            return expectedIsolationCacheKeySupplier.get();
        }

        @Nullable
        public CacheKey getExpectedDestinationCacheKey()
        {
            return expectedDestinationCacheKeySupplier.get();
        }

        public boolean destinationRetrievalShouldFail()
        {
            return destinationRetrievalShouldFail;
        }
    }

    @Nonnull
    private static
        UserSpecificTestParametersBuilder
        expectCommandCreationToSucceed( @Nullable final Tenant tenant, @Nullable final Principal principal )
    {
        return UserSpecificTestParametersBuilder.commandCanBeCreatedFor(tenant, principal);
    }

    @Nonnull
    private static
        UserSpecificTestParameters
        expectCommandCreationToFail( @Nullable final Tenant tenant, @Nullable final Principal principal )
    {
        return UserSpecificTestParametersBuilder.commandCannotBeCreatedFor(tenant, principal);
    }

    @RequiredArgsConstructor( access = AccessLevel.PRIVATE )
    private static class UserSpecificTestParametersBuilder
    {
        @Nullable
        private final Tenant tenant;
        @Nullable
        private final Principal principal;
        private final boolean commandCreationIsExpectedToSucceed;
        @Nonnull
        private Supplier<CacheKey> expectedIsolationCacheKeySupplier = failingCacheKeySupplier();
        @Nonnull
        private Supplier<CacheKey> expectedDestinationCacheKeySupplier = failingCacheKeySupplier();
        private boolean destinationRetrievalShouldFail = false;

        @Nonnull
        private static Supplier<CacheKey> failingCacheKeySupplier()
        {
            return () -> {
                throw new IllegalStateException("The cache key supplier is not expected to be invoked!");
            };
        }

        @Nonnull
        public static
            UserSpecificTestParametersBuilder
            commandCanBeCreatedFor( @Nullable final Tenant tenant, @Nullable final Principal principal )
        {
            return new UserSpecificTestParametersBuilder(tenant, principal, true);
        }

        @Nonnull
        public static
            UserSpecificTestParameters
            commandCannotBeCreatedFor( @Nullable final Tenant tenant, @Nullable final Principal principal )
        {
            return new UserSpecificTestParametersBuilder(tenant, principal, false).build();
        }

        @Nonnull
        public
            UserSpecificTestParametersBuilder
            expectIsolationCacheKey( @Nullable final Tenant tenant, @Nullable final Principal principal )
        {
            expectedIsolationCacheKeySupplier = () -> CacheKey.of(tenant, principal);
            return this;
        }

        @Nonnull
        public UserSpecificTestParametersBuilder expectNoIsolationCacheKey()
        {
            expectedIsolationCacheKeySupplier = () -> null;
            return this;
        }

        @Nonnull
        public
            UserSpecificTestParametersBuilder
            expectDestinationCacheKey( @Nullable final Tenant tenant, @Nullable final Principal principal )
        {
            expectedDestinationCacheKeySupplier = () -> CacheKey.of(tenant, principal);
            return this;
        }

        @Nonnull
        public UserSpecificTestParametersBuilder expectNoDestinationCacheKey()
        {
            expectedDestinationCacheKeySupplier = () -> null;
            return this;
        }

        @Nonnull
        public UserSpecificTestParametersBuilder expectNoDestinationCacheKeyBecauseDestinationRetrievalFails()
        {
            destinationRetrievalShouldFail = true;
            expectedDestinationCacheKeySupplier = () -> null;
            return this;
        }

        @Nonnull
        public UserSpecificTestParameters build()
        {
            return new UserSpecificTestParameters(
                tenant,
                principal,
                commandCreationIsExpectedToSucceed,
                expectedIsolationCacheKeySupplier,
                expectedDestinationCacheKeySupplier,
                destinationRetrievalShouldFail);
        }
    }
}
