/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

import io.vavr.control.Try;

class Resilience4jDecorationStrategyBuilderTest
{
    @Test
    void testExistingDefaultDecorators()
    {
        assertThat(Resilience4jDecorationStrategy.DEFAULT_DECORATORS).isNotEmpty();
    }

    @Test
    void testEqualityForDefaultDecorators()
    {
        final GenericDecorator[] defaultDecorators =
            Resilience4jDecorationStrategy.DEFAULT_DECORATORS.toArray(new GenericDecorator[0]);

        assertThat(Resilience4jDecorationStrategy.builder().defaultDecorators().build())
            .isEqualTo(new Resilience4jDecorationStrategy())
            .isEqualTo(new Resilience4jDecorationStrategy(defaultDecorators));
    }

    @Test
    void testEqualityForNoDecorators()
    {
        assertThat(Resilience4jDecorationStrategy.builder().build())
            .isEqualTo(new Resilience4jDecorationStrategy(new GenericDecorator[0]))
            .isNotEqualTo(new Resilience4jDecorationStrategy());
    }

    @Test
    void testEqualityForCustomDecorators()
    {
        final GenericDecorator customDecorator = mock(GenericDecorator.class);
        assertThat(Resilience4jDecorationStrategy.builder().decorator(customDecorator).build())
            .isEqualTo(new Resilience4jDecorationStrategy(customDecorator))
            .isNotEqualTo(new Resilience4jDecorationStrategy());
    }

    @Test
    void testDefaultDecorators()
    {
        final Resilience4jDecorationStrategy strategy = new Resilience4jDecorationStrategy();
        final String s = strategy.executeSupplier("foo"::toString, ResilienceConfiguration.of("testDefaultDecorators"));
        assertThat(s).isEqualTo("foo");
    }

    @Test
    void testEmptyDecorators()
    {
        final Resilience4jDecorationStrategy emptyStrategy = Resilience4jDecorationStrategy.builder().build();

        // configuration does not allow time being wasted
        final ResilienceConfiguration configuration =
            ResilienceConfiguration
                .of("testEmptyDecorators")
                .timeLimiterConfiguration(ResilienceConfiguration.TimeLimiterConfiguration.of(Duration.ZERO));

        // run wasteful operation on strategy without attached decorators
        final String s = emptyStrategy.executeSupplier(() -> {
            Try.run(() -> Thread.sleep(100));
            return "foo";
        }, configuration);

        // the time limiter did not throw an exception for wasteful operation
        assertThat(s).isEqualTo("foo");
    }

    @Test
    void testCustomDecorators()
    {
        // create custom, trackable decorator
        final GenericDecorator customDecorator = spy(GenericDecorator.class);
        doAnswer(( req ) -> req.getArgument(0)).when(customDecorator).decorateCallable(any(), any());

        final Resilience4jDecorationStrategy emptyStrategy =
            Resilience4jDecorationStrategy.builder().decorator(customDecorator).build();

        final ResilienceConfiguration configuration = ResilienceConfiguration.of("testCustomDecorators");
        final String s = emptyStrategy.executeSupplier("foo"::toString, configuration);

        assertThat(s).isEqualTo("foo");
        verify(customDecorator, times(1)).decorateCallable(any(), any(ResilienceConfiguration.class));
    }

    @Test
    void testCombinedDecorators()
    {
        // create custom, trackable decorators
        final GenericDecorator customDecoratorFirst = spy(GenericDecorator.class);
        doAnswer(( req ) -> req.getArgument(0)).when(customDecoratorFirst).decorateCallable(any(), any());

        final GenericDecorator customDecoratorLast = spy(GenericDecorator.class);
        doAnswer(( req ) -> req.getArgument(0)).when(customDecoratorLast).decorateCallable(any(), any());

        final Resilience4jDecorationStrategy combinedStrategy =
            Resilience4jDecorationStrategy
                .builder()
                .decorator(customDecoratorFirst)
                .defaultDecorators()
                .decorator(customDecoratorLast)
                .build();

        final ResilienceConfiguration configuration = ResilienceConfiguration.of("testCombinedDecorators");
        final String s = combinedStrategy.executeSupplier("foo"::toString, configuration);

        assertThat(s).isEqualTo("foo");

        final InOrder inOrder = inOrder(customDecoratorFirst, customDecoratorLast);
        inOrder.verify(customDecoratorFirst, times(1)).decorateCallable(any(), any(ResilienceConfiguration.class));
        inOrder.verify(customDecoratorLast, times(1)).decorateCallable(any(), any(ResilienceConfiguration.class));
    }
}
