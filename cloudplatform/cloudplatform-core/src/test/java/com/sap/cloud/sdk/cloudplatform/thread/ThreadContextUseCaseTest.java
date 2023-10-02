/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

public class ThreadContextUseCaseTest
{
    @Data
    private static class TestInformation
    {
        private String value;
    }

    @Slf4j
    private static class TestController
    {
        private TestInformation getValue()
        {
            final TestContextListener instance = TestContextListener.getInstance();
            return ThreadContextAccessor
                .tryGetCurrentContext()
                .flatMap(c -> c.<TestInformation> getPropertyValue(instance.getPropertyName()))
                .filter(Objects::nonNull)
                .onFailure(e -> log.warn("Failed to get property from current thread context.", e))
                .getOrElse(instance::createPropertyValue);
        }
    }

    @Before
    public void addListener()
    {
        DefaultThreadContextListenerChain.registerDefaultListener(TestContextListener.getInstance());
    }

    @After
    public void removeListener()
    {
        DefaultThreadContextListenerChain.unregisterDefaultListener(TestContextListener.getInstance().getPriority());
    }

    /*
     * Test without ThreadContextExecutor
     * Assertion is focusing on values NOT being equal to each other.
     */
    @Test
    public void testWithoutThreadContext()
        throws Exception
    {
        final TestController impl = new TestController();

        final TestInformation testValue = impl.getValue();
        testValue.setValue("foo");

        final SoftAssertions softly = new SoftAssertions();

        // critical thread-dependent code
        final Runnable callback = () -> softly.assertThat(new TestController().getValue()).isNotEqualTo(testValue);

        // check trivial assumption: working in same thread where declaration happened
        callback.run();

        // check advanced assumption: working in different thread
        final Thread thread = new Thread(callback);
        thread.start();
        thread.join();

        softly.assertAll();
    }

    /*
     * Test reference implementation using the ThreadContext API of Cloud SDK: implement a custom ThreadContextListener.
     * Like most other components of the Cloud SDK, it requires the current application runtime to be inside a ThreadContext already.
     * Any operation of newly spawned threads must be run with ThreadContextExecutor again,
     * such that the properties of current ThreadContext will be inherited, e.g. happening in Resilience4jDecorationStrategy.
     */
    @Test
    public void testWithThreadContext()
    {
        final SoftAssertions softly = new SoftAssertions();

        // simulate active thread context as provided by application at runtime
        ThreadContextExecutor.fromNewContext().execute(() -> {

            final TestController impl = new TestController();

            final TestInformation testValue = impl.getValue();
            testValue.setValue("bar");

            // critical thread-dependent code
            final Executable callback = () -> softly.assertThat(new TestController().getValue()).isEqualTo(testValue);

            // check trivial assumption: working in same thread where declaration happened
            callback.execute();

            final ThreadContextExecutor executor = ThreadContextExecutor.fromCurrentContext();

            // check advanced assumption: working in different thread
            final Thread thread = new Thread(() -> executor.execute(callback));
            thread.start();
            thread.join();

            // verify the parent context is not destroyed when the child thread dies
            callback.execute();
        });

        softly.assertAll();
    }

    @Slf4j
    @NoArgsConstructor( access = AccessLevel.PROTECTED )
    private static class TestContextListener extends GenericContextListener<TestInformation>
    {
        @Getter
        public static final TestContextListener instance = new TestContextListener();

        @Getter
        private final String propertyName = TestContextListener.class.getName() + ":Custom";

        @Getter
        public int priority = 10; // customize

        @Nonnull
        @Override
        protected TestInformation createPropertyValue()
        {
            log.debug("Instantiated new instance of TestInformation.");
            return new TestInformation();
        }
    }

    private abstract static class GenericContextListener<T> implements ThreadContextListener
    {
        @Nonnull
        protected abstract String getPropertyName();

        @Nonnull
        protected abstract T createPropertyValue()
            throws Exception;

        @Override
        public void afterInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext.setPropertyIfAbsent(getPropertyName(), Property.decorateCallable(this::createPropertyValue));
        }
    }
}
