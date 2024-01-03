/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;

class CustomResilienceStrategyTest
{
    private static final ThreadLocal<String> storage = new ThreadLocal<>();

    @AfterEach
    void resetDecorationStrategy()
    {
        ResilienceDecorator.setDecorationStrategy(new Resilience4jDecorationStrategy());
    }

    @BeforeEach
    void resetStorage()
    {
        storage.set(null);
    }

    @Test
    void testThreadLocalWithDefaultDecorationStrategy()
    {
        storage.set("foo");

        final ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of("CustomAdapters-0");
        final String foo = ResilienceDecorator.executeSupplier(storage::get, resilienceConfiguration);
        assertThat(foo).isNull();
    }

    @Test
    void testThreadLocalWithCustomDecorationStrategy()
    {
        // setup custom resilience strategy with changed decorators
        final GenericDecorator customDecorator =
            createAccessorDecorator(
                CustomResilienceStrategyTest.storage::get,
                CustomResilienceStrategyTest.storage::set);

        // set default decoration strategy
        ResilienceDecorator
            .setDecorationStrategy(
                Resilience4jDecorationStrategy.builder().decorator(customDecorator).defaultDecorators().build());

        storage.set("foo");

        // actual application code
        final ResilienceConfiguration resilienceConfiguration = ResilienceConfiguration.of("CustomAdapters-1");
        final String foo = ResilienceDecorator.executeSupplier(storage::get, resilienceConfiguration);

        assertThat(foo).isEqualTo("foo");
    }

    public static <ValueT> GenericDecorator createAccessorDecorator(
        @Nonnull final Supplier<ValueT> getter,
        @Nonnull final Consumer<ValueT> setter )
    {
        return new GenericDecorator()
        {
            @Nonnull
            @Override
            public <T> Callable<T> decorateCallable(
                @Nonnull final Callable<T> callable,
                @Nonnull final ResilienceConfiguration configuration )
            {
                final ValueT storedValue = getter.get();
                return () -> {
                    setter.accept(storedValue);
                    return callable.call();
                };
            }
        };
    }
}
