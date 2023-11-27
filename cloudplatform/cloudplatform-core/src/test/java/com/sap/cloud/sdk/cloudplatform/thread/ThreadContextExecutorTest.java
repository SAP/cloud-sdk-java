/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.assertj.core.api.SoftAssertions;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

class ThreadContextExecutorTest
{
    private static final String PROPERTY_NAME = "MyProperty";
    private static final int TIMEOUT = 300;

    @RequiredArgsConstructor
    private static class MyThreadContextListener implements ThreadContextListener
    {
        private final String propertyName;
        private final Property<?> property;

        MyThreadContextListener( @Nonnull final Property<?> property )
        {
            this(PROPERTY_NAME, property);
        }

        @Override
        public int getPriority()
        {
            return 0;
        }

        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext.setPropertyIfAbsent(propertyName, property);
        }
    }

    private static void assertCurrentContextContains(
        @Nonnull final SoftAssertions softly,
        @Nonnull final Property<?> expectedProperty )
    {
        final ThreadContext currentThreadContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentThreadContext == null ) {
            softly.fail("Cannot get current context");
            return;
        }
        softly.assertThat(currentThreadContext.getPropertyValue(PROPERTY_NAME)).isEqualTo(expectedProperty.getValue());
    }

    @SafeVarargs
    private static void assertCurrentContextContains(
        @Nonnull final SoftAssertions softly,
        @Nonnull final Map.Entry<String, Property<?>>... expectedEntries )
    {
        final ThreadContext currentThreadContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentThreadContext == null ) {
            softly.fail("Cannot get current context");
            return;
        }
        for( final Map.Entry<String, Property<?>> property : expectedEntries ) {
            softly
                .assertThat(currentThreadContext.getPropertyValue(property.getKey()))
                .isEqualTo(property.getValue().getValue());
        }
    }

    private static void assertCurrentContextDoesNotContain(
        @Nonnull final SoftAssertions softly,
        @Nonnull final Property<?> expectedProperty )
    {
        final ThreadContext currentThreadContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentThreadContext == null ) {
            softly.fail("Cannot get current context");
            return;
        }
        softly.assertThat(currentThreadContext.getPropertyValue(PROPERTY_NAME).isEmpty()).isTrue();
    }

    @SafeVarargs
    private static void assertCurrentContextDoesNotContain(
        @Nonnull final SoftAssertions softly,
        @Nonnull final Map.Entry<String, Property<?>>... expectedEntries )
    {
        final ThreadContext currentThreadContext = ThreadContextAccessor.getCurrentContextOrNull();
        if( currentThreadContext == null ) {
            softly.fail("Cannot get current context");
            return;
        }
        for( final Map.Entry<String, Property<?>> property : expectedEntries ) {
            softly.assertThat(currentThreadContext.getPropertyValue(property.getKey()).isEmpty()).isTrue();
        }
    }

    @Test
    void testContextNesting()
    {
        final SoftAssertions softly = new SoftAssertions();

        VavrAssertions.assertThat(ThreadContextAccessor.tryGetCurrentContext()).isFailure();

        final String keyParentOfParent = "keyParentOfParent";
        final String keyParent = "keyParent";
        final String key = "key";
        final Property<?> propertyParentOfParent = Property.of("valueParentOfParent");
        final Property<?> propertyParent = Property.of("valueParent");
        final Property<?> property = Property.of("value");

        ThreadContextExecutor
            .fromNewContext()
            .withListeners(new MyThreadContextListener(keyParentOfParent, propertyParentOfParent))
            .execute(() -> {
                assertCurrentContextContains(softly, entry(keyParentOfParent, propertyParentOfParent));

                ThreadContextExecutor
                    .fromCurrentContext()
                    .withListeners(new MyThreadContextListener(keyParent, propertyParent))
                    .execute(() -> {
                        assertCurrentContextContains(
                            softly,
                            entry(keyParentOfParent, propertyParentOfParent),
                            entry(keyParent, propertyParent));

                        ThreadContextExecutor
                            .fromCurrentContext()
                            .withListeners(new MyThreadContextListener(key, property))
                            .execute(
                                () -> assertCurrentContextContains(
                                    softly,
                                    entry(keyParentOfParent, propertyParentOfParent),
                                    entry(keyParent, propertyParent),
                                    entry(key, property)));
                    });
            });

        softly.assertAll();
    }

    @Test
    void testExistingPropertyIsKept()
    {
        final SoftAssertions softly = new SoftAssertions();

        VavrAssertions.assertThat(ThreadContextAccessor.tryGetCurrentContext()).isFailure();

        final Property<?> property1 = Property.of("value1");
        final Property<?> property2 = Property.of("value2");

        ThreadContextExecutor.fromNewContext().withListeners(new MyThreadContextListener(property1)).execute(() -> {
            assertCurrentContextContains(softly, property1);

            ThreadContextExecutor
                .fromCurrentContext()
                .withListeners(new MyThreadContextListener(property2)) // listener DOES NOT overwrite context value
                .execute(() -> assertCurrentContextContains(softly, property1));
        });

        softly.assertAll();
    }

    @Test
    void testExecutorServicePropagatedContext()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> property = Property.of("value");

        ThreadContextExecutor.fromNewContext().withListeners(new MyThreadContextListener(property)).execute(() -> {

            final ExecutorService executor = ThreadContextExecutors.getExecutor();

            executor.submit(() -> assertCurrentContextContains(softly, property)).get(TIMEOUT, TimeUnit.SECONDS);
        });

        softly.assertAll();
    }

    @Test
    @SneakyThrows
    void testExecutorServicePropagatedThreadContextExecutor()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> property = Property.of("value");

        final ThreadContextExecutorService executor =
            DefaultThreadContextExecutorService.of(Executors.newCachedThreadPool());
        final ThreadContextExecutor executor1 =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .withListeners(new MyThreadContextListener(property));
        final ThreadContextExecutor executor2 =
            ThreadContextExecutor
                .fromNewContext()
                .withoutDefaultListeners()
                .withListeners(new MyThreadContextListener(property));

        executor.execute(() -> assertCurrentContextContains(softly, property), executor1);
        executor.submit(() -> assertCurrentContextContains(softly, property), executor2);

        executor.shutdown();
        assertThat(executor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)).isTrue();

        softly.assertAll();
    }

    @Test
    @SneakyThrows
    void testExecutorServiceWithoutPropagatedContext()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> property = Property.of("value");

        final ExecutorService executor = ThreadContextExecutors.getExecutor();
        executor.submit(() -> assertCurrentContextDoesNotContain(softly, property)).get(TIMEOUT, TimeUnit.SECONDS);

        softly.assertAll();
    }

    @Test
    void testExecutorServiceShutdown()
    {
        final ExecutorService delegate = Executors.newCachedThreadPool();
        final ExecutorService executor = DefaultThreadContextExecutorService.of(delegate);
        executor.submit(ThreadContextAccessor::tryGetCurrentContext);
        executor.shutdown();
        assertThat(executor.isShutdown()).isTrue();
        assertThat(delegate.isShutdown()).isTrue();

        final ExecutorService delegate2 = Executors.newCachedThreadPool();
        final ExecutorService executor2 = DefaultThreadContextExecutorService.of(delegate2);
        executor2.submit(ThreadContextAccessor::tryGetCurrentContext);
        executor2.shutdownNow();
        assertThat(executor2.isShutdown()).isTrue();
        assertThat(delegate2.isShutdown()).isTrue();
    }

    @Test
    void testExecutorServiceMultipleThreadsPropagatedContext()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> property = Property.of("value");

        ThreadContextExecutor.fromNewContext().withListeners(new MyThreadContextListener(property)).execute(() -> {

            final Callable<Try<ThreadContext>> assertCurrentContextContains = () -> {
                assertCurrentContextContains(softly, property);
                return ThreadContextAccessor.tryGetCurrentContext();
            };

            final List<Callable<Try<ThreadContext>>> getThreadContexts = new ArrayList<>();
            getThreadContexts.add(assertCurrentContextContains);
            getThreadContexts.add(assertCurrentContextContains);

            final ExecutorService executor = ThreadContextExecutors.getExecutor();
            final List<Future<Try<ThreadContext>>> threadContexts = executor.invokeAll(getThreadContexts);

            final ThreadContext threadContext1 = threadContexts.get(0).get(TIMEOUT, TimeUnit.SECONDS).get();
            final ThreadContext threadContext2 = threadContexts.get(1).get(TIMEOUT, TimeUnit.SECONDS).get();
            assertThat(threadContext1).isEqualTo(threadContext2);
            assertThat(threadContext1).isNotSameAs(threadContext2);
        });

        softly.assertAll();
    }

    @Test
    void testExecutorServiceNotLeakingContext()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> parentProperty = Property.of("parentValue");

        // Create parent ThreadContext with parentProperty
        ThreadContextExecutor
            .fromNewContext()
            .withListeners(new MyThreadContextListener(parentProperty))
            .execute(() -> {

                // This executor will reuse the same thread
                final ThreadContextExecutorService executor =
                    DefaultThreadContextExecutorService.of(Executors.newSingleThreadExecutor());

                final Map.Entry<String, Property<?>> childAProperty = entry("childAkey", Property.of("childAValue"));
                final ThreadContext contextA = ThreadContextAccessor.getCurrentContext().duplicate();
                contextA.setProperty(childAProperty.getKey(), childAProperty.getValue());

                // Create child from parent with childAProperty
                executor.submit((Callable<?>) () -> {
                    assertCurrentContextContains(softly, parentProperty);
                    assertCurrentContextContains(softly, childAProperty);
                    return null;
                }, contextA).get(TIMEOUT, TimeUnit.SECONDS);

                // Check that ChildA did not leak their childAProperty in the parent
                assertCurrentContextDoesNotContain(softly, childAProperty);

                final Map.Entry<String, Property<?>> childBProperty = entry("childBkey", Property.of("childBValue"));
                final ThreadContext contextB = ThreadContextAccessor.getCurrentContext().duplicate();
                contextB.setProperty(childBProperty.getKey(), childBProperty.getValue());

                // Create child from parent with childBProperty
                executor.submit((Callable<?>) () -> {
                    assertCurrentContextContains(softly, parentProperty);
                    assertCurrentContextContains(softly, childBProperty);
                    // Check that ChildA did not leak their childAProperty in the childB
                    assertCurrentContextDoesNotContain(softly, childAProperty);
                    return null;
                }, contextB).get(TIMEOUT, TimeUnit.SECONDS);
            });

        softly.assertAll();
    }

    @Test
    void testExecutorContextCorrectlySwitch()
    {
        final SoftAssertions softly = new SoftAssertions();
        final Property<?> parentProperty = Property.of("parentValue");

        // Create parent ThreadContext with parentProperty
        ThreadContextExecutor
            .fromNewContext()
            .withListeners(new MyThreadContextListener(parentProperty))
            .execute(() -> {

                // switch to a new context
                try {
                    ThreadContextExecutor.fromNewContext().execute(() -> {
                        assertCurrentContextDoesNotContain(softly, parentProperty);
                        // stop the execution before the end
                        throw new RuntimeException();
                    });
                }
                catch( final Exception ignored ) {
                }

                // Check that the context has been properly reset
                assertCurrentContextContains(softly, parentProperty);
            });

        softly.assertAll();
    }

    @Test
    void testExecutorServiceCallableException()
    {
        final Future<?> result = ThreadContextExecutors.getExecutor().submit((Callable<?>) () -> {
            throw new NullPointerException("test");
        });

        assertThatThrownBy(result::get)
            .isInstanceOf(ExecutionException.class)
            .cause()
            .isInstanceOf(ThreadContextExecutionException.class)
            .cause()
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("test");
    }

    @Test
    void testExecutorServiceRunnableException()
    {
        final Future<?> result = ThreadContextExecutors.getExecutor().submit((Runnable) () -> {
            throw new NullPointerException("test");
        });

        assertThatThrownBy(result::get)
            .isInstanceOf(ExecutionException.class)
            .cause()
            .isInstanceOf(ThreadContextExecutionException.class)
            .cause()
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("test");
    }

    private static class IncrementThreadContextListener implements ThreadContextListener
    {
        private static int n = 1;

        @Override
        public int getPriority()
        {
            return 0;
        }

        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext.setPropertyIfAbsent(PROPERTY_NAME, Property.of(n));
            n++;
        }
    }

    @Test
    void testSubsequentExecuteCallsWillInvokeListenersWithInitialThreadContext()
    {
        final SoftAssertions softly = new SoftAssertions();

        // The specified listener will set a property with an increasing number everytime beforeInitialize is called
        final ThreadContextExecutor threadContextExecutor =
            ThreadContextExecutor.fromNewContext().withListeners(new IncrementThreadContextListener());

        threadContextExecutor.execute(() -> assertCurrentContextContains(softly, Property.of(1)));
        // We are expecting the listener to be called on the initial ThreadContext (or a duplicate of that).
        // Which is why setting the property is expected to work (since we are using #setPropertyIfAbsent).
        threadContextExecutor.execute(() -> assertCurrentContextContains(softly, Property.of(2)));

        softly.assertAll();
    }

}
