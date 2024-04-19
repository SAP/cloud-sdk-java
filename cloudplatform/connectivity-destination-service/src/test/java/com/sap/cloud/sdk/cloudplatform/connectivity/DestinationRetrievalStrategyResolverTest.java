/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.XsuaaTokenMocker.mockXsuaaToken;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.testutil.TestContext;

import io.vavr.Tuple;
import io.vavr.Tuple3;

@SuppressWarnings( "deprecation" )
class DestinationRetrievalStrategyResolverTest
{
    private static final Tenant providerT = new DefaultTenant("provider");
    private static final Tenant subscriberT = new DefaultTenant("subscriber");

    @RegisterExtension
    static final TestContext context = TestContext.withThreadContext();

    private DestinationRetrievalStrategyResolver sut;

    private Function<DestinationRetrievalStrategy, DestinationServiceV1Response> destinationRetriever;
    private Function<OnBehalfOf, List<DestinationProperties>> allDestinationRetriever;

    @SuppressWarnings( "unchecked" )
    @BeforeEach
    void prepareResolver()
    {
        destinationRetriever =
            (Function<DestinationRetrievalStrategy, DestinationServiceV1Response>) mock(Function.class);
        allDestinationRetriever = (Function<OnBehalfOf, List<DestinationProperties>>) mock(Function.class);
        sut =
            spy(
                new DestinationRetrievalStrategyResolver(
                    providerT::getTenantId,
                    destinationRetriever,
                    allDestinationRetriever));
    }

    @Test
    void testSimpleBehalfResolutions()
    {
        final SoftAssertions softly = new SoftAssertions();

        final List<Tuple3<DestinationServiceRetrievalStrategy, DestinationServiceTokenExchangeStrategy, DestinationRetrievalStrategy>> testCases =
            new ArrayList<>();

        testCases
            .add(
                Tuple
                    .of(
                        CURRENT_TENANT,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT)));
        testCases
            .add(
                Tuple
                    .of(
                        ONLY_SUBSCRIBER,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_CURRENT_TENANT)));
        testCases
            .add(
                Tuple
                    .of(
                        ALWAYS_PROVIDER,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_PROVIDER)));

        testCases
            .add(
                Tuple
                    .of(
                        CURRENT_TENANT,
                        EXCHANGE_ONLY,
                        DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT)));
        testCases
            .add(
                Tuple
                    .of(
                        ONLY_SUBSCRIBER,
                        EXCHANGE_ONLY,
                        DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT)));
        testCases
            .add(
                Tuple
                    .of(
                        ALWAYS_PROVIDER,
                        EXCHANGE_ONLY,
                        DestinationRetrievalStrategy.withoutToken(NAMED_USER_CURRENT_TENANT)));

        testCases
            .add(
                Tuple
                    .of(
                        CURRENT_TENANT,
                        FORWARD_USER_TOKEN,
                        DestinationRetrievalStrategy.withUserToken(TECHNICAL_USER_CURRENT_TENANT, "token")));
        testCases
            .add(
                Tuple
                    .of(
                        ONLY_SUBSCRIBER,
                        FORWARD_USER_TOKEN,
                        DestinationRetrievalStrategy.withUserToken(TECHNICAL_USER_CURRENT_TENANT, "token")));
        testCases
            .add(
                Tuple
                    .of(
                        ALWAYS_PROVIDER,
                        FORWARD_USER_TOKEN,
                        DestinationRetrievalStrategy.withUserToken(TECHNICAL_USER_PROVIDER, "token")));

        testCases
            .forEach(
                c -> softly
                    .assertThat(sut.resolveSingleRequestStrategy(c._1(), c._2(), null))
                    .as("Expecting '%s' with '%s' to resolve to '%s'", c._1(), c._2(), c._3())
                    .isEqualTo(c._3()));
        softly.assertAll();
    }

    @Test
    void testExceptionsAreThrownOnIllegalCombinations()
    {
        final List<Tuple3<DestinationServiceRetrievalStrategy, DestinationServiceTokenExchangeStrategy, Tenant>> testCases =
            new ArrayList<>();

        final SoftAssertions softly = new SoftAssertions();

        testCases.add(Tuple.of(ONLY_SUBSCRIBER, DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY, providerT));
        testCases
            .add(Tuple.of(ONLY_SUBSCRIBER, DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE, providerT));
        testCases.add(Tuple.of(ONLY_SUBSCRIBER, EXCHANGE_ONLY, providerT));
        testCases.add(Tuple.of(ONLY_SUBSCRIBER, FORWARD_USER_TOKEN, providerT));

        testCases.add(Tuple.of(ALWAYS_PROVIDER, EXCHANGE_ONLY, subscriberT));

        testCases
            .forEach(
                c -> TenantAccessor
                    .executeWithTenant(
                        c._3(),
                        () -> softly
                            .assertThatThrownBy(() -> sut.prepareSupplier(c._1(), c._2(), null))
                            .as("Expecting '%s' with '%s' and '%s' to throw.", c._1(), c._2(), c._3())
                            .isInstanceOf(DestinationAccessException.class)));

        TenantAccessor
            .executeWithTenant(
                providerT,
                () -> softly
                    .assertThatThrownBy(() -> sut.prepareSupplierAllDestinations(ONLY_SUBSCRIBER))
                    .as("Expecting '%s' with '%s' to throw.", ONLY_SUBSCRIBER, providerT)
                    .isInstanceOf(DestinationAccessException.class));

        softly.assertAll();
    }

    @Test
    @DisplayName( "Test exceptions are thrown for cases where a token exchange can't be performed" )
    void testExceptionsAreThrownForImpossibleTokenExchanges()
    {
        doAnswer(( any ) -> true).when(sut).doesDestinationConfigurationRequireUserTokenExchange(any());
        final DestinationRetrieval supplier =
            sut.prepareSupplier(ALWAYS_PROVIDER, DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE, null);

        TenantAccessor
            .executeWithTenant(
                subscriberT,
                () -> assertThatThrownBy(
                    supplier::get,
                    "Expecting %s with %s and %s to fail when attempting the exchange",
                    ALWAYS_PROVIDER,
                    DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE,
                    subscriberT).isInstanceOf(DestinationAccessException.class));

        verify(destinationRetriever, times(1))
            .apply(eq(DestinationRetrievalStrategy.withoutToken(TECHNICAL_USER_PROVIDER)));
        verifyNoMoreInteractions(destinationRetriever);
    }

    @Test
    @DisplayName( "Test default strategies are set correctly" )
    void testDefaultStrategies()
    {
        context.setAuthToken(mockXsuaaToken());

        sut.prepareSupplier(DestinationOptions.builder().build());
        sut.prepareSupplierAllDestinations(DestinationOptions.builder().build());

        verify(sut).prepareSupplier(CURRENT_TENANT, FORWARD_USER_TOKEN, null);
        verify(sut).prepareSupplierAllDestinations(CURRENT_TENANT);
    }

    @Test
    @DisplayName( "Test default strategy for non-XSUAA tokens is set correctly" )
    void testDefaultNonXsuaaTokenStrategy()
    {
        context.setAuthToken();

        sut.prepareSupplier(DestinationOptions.builder().build());
        sut.prepareSupplierAllDestinations(DestinationOptions.builder().build());

        verify(sut).prepareSupplier(CURRENT_TENANT, DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE, null);
        verify(sut).prepareSupplierAllDestinations(CURRENT_TENANT);
    }
}
