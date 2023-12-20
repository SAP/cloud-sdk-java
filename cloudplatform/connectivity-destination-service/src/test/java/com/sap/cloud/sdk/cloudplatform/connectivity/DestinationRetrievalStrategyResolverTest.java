/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationRetrievalStrategyResolver.Strategy;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ALWAYS_PROVIDER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceRetrievalStrategy.ONLY_SUBSCRIBER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.EXCHANGE_ONLY;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceTokenExchangeStrategy.FORWARD_USER_TOKEN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.NAMED_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT;
import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
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

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.control.Try;

@SuppressWarnings( "deprecation" )
class DestinationRetrievalStrategyResolverTest
{
    private static final Tenant providerT = new DefaultTenant("provider");
    private static final Tenant subscriberT = new DefaultTenant("subscriber");
    private DestinationRetrievalStrategyResolver sut;

    private Function<Strategy, DestinationServiceV1Response> destinationRetriever;
    private Function<OnBehalfOf, List<Destination>> allDestinationRetriever;

    @RegisterExtension
    TokenRule token = TokenRule.createXsuaa();

    @SuppressWarnings( "unchecked" )
    @BeforeEach
    void prepareResolver()
    {
        destinationRetriever = (Function<Strategy, DestinationServiceV1Response>) mock(Function.class);
        allDestinationRetriever = (Function<OnBehalfOf, List<Destination>>) mock(Function.class);
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

        final List<Tuple3<DestinationServiceRetrievalStrategy, DestinationServiceTokenExchangeStrategy, Strategy>> testCases =
            new ArrayList<>();

        testCases
            .add(
                Tuple
                    .of(
                        CURRENT_TENANT,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        new Strategy(TECHNICAL_USER_CURRENT_TENANT, false)));
        testCases
            .add(
                Tuple
                    .of(
                        ONLY_SUBSCRIBER,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        new Strategy(TECHNICAL_USER_CURRENT_TENANT, false)));
        testCases
            .add(
                Tuple
                    .of(
                        ALWAYS_PROVIDER,
                        DestinationServiceTokenExchangeStrategy.LOOKUP_ONLY,
                        new Strategy(TECHNICAL_USER_PROVIDER, false)));

        testCases.add(Tuple.of(CURRENT_TENANT, EXCHANGE_ONLY, new Strategy(NAMED_USER_CURRENT_TENANT, false)));
        testCases.add(Tuple.of(ONLY_SUBSCRIBER, EXCHANGE_ONLY, new Strategy(NAMED_USER_CURRENT_TENANT, false)));
        testCases.add(Tuple.of(ALWAYS_PROVIDER, EXCHANGE_ONLY, new Strategy(NAMED_USER_CURRENT_TENANT, false)));

        testCases.add(Tuple.of(CURRENT_TENANT, FORWARD_USER_TOKEN, new Strategy(TECHNICAL_USER_CURRENT_TENANT, true)));
        testCases.add(Tuple.of(ONLY_SUBSCRIBER, FORWARD_USER_TOKEN, new Strategy(TECHNICAL_USER_CURRENT_TENANT, true)));
        testCases.add(Tuple.of(ALWAYS_PROVIDER, FORWARD_USER_TOKEN, new Strategy(TECHNICAL_USER_PROVIDER, true)));

        testCases
            .forEach(
                c -> softly
                    .assertThat(sut.resolveSingleRequestStrategy(c._1(), c._2()))
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
                            .assertThatThrownBy(() -> sut.prepareSupplier(c._1(), c._2()))
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
            sut.prepareSupplier(ALWAYS_PROVIDER, DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);

        TenantAccessor
            .executeWithTenant(
                subscriberT,
                () -> assertThatThrownBy(
                    supplier::get,
                    "Expecting %s with %s and %s to fail when attempting the exchange",
                    ALWAYS_PROVIDER,
                    DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE,
                    subscriberT).isInstanceOf(DestinationAccessException.class));

        verify(destinationRetriever, times(1)).apply(eq(new Strategy(TECHNICAL_USER_PROVIDER, false)));
        verifyNoMoreInteractions(destinationRetriever);
    }

    @Test
    @DisplayName( "Test default strategies are set correctly" )
    void testDefaultStrategies()
    {
        // subscriber tenant is implied
        sut.prepareSupplier(DestinationOptions.builder().build());
        sut.prepareSupplierAllDestinations(DestinationOptions.builder().build());

        verify(sut).prepareSupplier(CURRENT_TENANT, FORWARD_USER_TOKEN);
        verify(sut).prepareSupplierAllDestinations(CURRENT_TENANT);
    }

    @Test
    @DisplayName( "Test default strategy for non-XSUAA tokens is set correctly" )
    void testDefaultNonXsuaaTokenStrategy()
    {
        final AuthToken nonXsuaaToken = new AuthToken(JWT.decode(JWT.create().sign(Algorithm.none())));
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(nonXsuaaToken));

        sut.prepareSupplier(DestinationOptions.builder().build());
        verify(sut).prepareSupplier(CURRENT_TENANT, DestinationServiceTokenExchangeStrategy.LOOKUP_THEN_EXCHANGE);
    }
}
