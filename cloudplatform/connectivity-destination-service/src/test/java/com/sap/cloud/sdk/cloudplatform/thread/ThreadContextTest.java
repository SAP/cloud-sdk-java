package com.sap.cloud.sdk.cloudplatform.thread;

import static com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS;
import static com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener.PROPERTY_PRINCIPAL;
import static com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener.PROPERTY_TENANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.security.BasicAuthenticationThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;

import lombok.SneakyThrows;

@SuppressWarnings( "DanglingJavadoc" ) // we are using JavaDoc-style comments (with a different color!) to better indicate lambda/thread beginnings and ends
class ThreadContextTest
{
    private static final int TEST_TIMEOUT = 300_000; // 5 minutes
    private static final String VALUE = "value";

    private final RequestHeaderContainer mockedRequestHeaders = mock(RequestHeaderContainer.class);
    private final Tenant mockedTenant = mock(Tenant.class);
    private final Principal mockedPrincipal = mock(Principal.class);

    @Nonnull
    private ExecutorService executorService;

    @BeforeEach
    void setUp()
    {
        executorService = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    void tearDown()
    {
        executorService.shutdown();
    }

    @Test
    void testAvailableThreadContextListeners()
    {
        final List<Class<?>> availableListenerTypes =
            DefaultThreadContextListenerChain
                .getDefaultListeners()
                .stream()
                .map(Object::getClass)
                .collect(Collectors.toList());

        // these are the listeners we are safe to assume are present in each new ThreadContextExecutor by default
        assertThat(availableListenerTypes)
            .contains(
                RequestHeaderThreadContextListener.class,
                TenantThreadContextListener.class,
                PrincipalThreadContextListener.class,
                BasicAuthenticationThreadContextListener.class);
    }

    @Test
    void testThreadContextCanBeSwitchedInSameThread()
    {
        final ThreadContext rootContext = newThreadContextWithMockedProperties();

        ThreadContextExecutor.using(rootContext).withoutDefaultListeners().execute(() -> {
            // make sure the parent context is currently active
            final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
            assertThat(parentContext).isEqualTo(rootContext);
            assertThat(parentContext).isNotSameAs(rootContext);
            final long parentThreadId = Thread.currentThread().getId();

            ThreadContextExecutor.fromCurrentContext().withoutDefaultListeners().execute(() -> {
                // this is still running in the same thread
                assertThat(Thread.currentThread().getId()).isEqualTo(parentThreadId);

                // but the active thread context was switched
                final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                assertThat(childContext).isNotSameAs(parentContext);
                assertThat(childContext).isEqualTo(parentContext);
            });

            // active thread context is back to parent
            assertThat(ThreadContextAccessor.getCurrentContext()).isSameAs(parentContext);
        });
    }

    /*
     * Test Case:
     * - Create a new explicit parent ThreadContext
     * - Explicitly share parent ThreadContext with child thread
     * - Parent thread outlives the child thread
     * - Parent ThreadContext is not cleared after the child thread stopped
     *
     * ┌─────────────┐
     * │             │
     * │ Root-Thread │
     * │             │
     * └──────┬──────┘
     *        │
     *        │ 1. Create a new ThreadContextExecutor with new ThreadContext
     *        │ => Listeners will be called and properties will be initialized
     *        │
     *        │ 2. Create child ThreadContextExecutor using the root ThreadContext
     *        │
     *        │ 3. Start asynchronous operation
     *        │
     *        │
     *        │                  ┌────────────┐
     *        │    start         │            │
     *        ├─────────────────►│ Sub-Thread │
     *        │                  │            │
     *        │                  └─────┬──────┘
     *        │                        │
     *        │                        │ 1. Run childThreadContextExecutor.execute(...)
     *        │                        │ => Triggers all registered Listeners. Properties are not modified.
     *        │                        │
     *        │                        │ 2. Read property from child ThreadContext
     *        │                        │ => Success: The properties are still present
     *        │                        │
     *        │                        │
     *        │                    ────┴────
     *        │
     *        │ // The Sub-Thread has stopped
     *        │
     *        │ 4. Read property from root ThreadContext
     *        │ => Success: The properties are still present
     *        │
     *
     * fancy ascii art created with: https://asciiflow.com/#/
     */
    @SneakyThrows
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    void testSharedContextIsNotClearedAfterChildFinished()
    {
        final SoftAssertions softly = new SoftAssertions();
        final CountDownLatch parentContextLatch = new CountDownLatch(1);
        final CountDownLatch childContextLatch = new CountDownLatch(1);

        submitAndCountDown(softly, parentContextLatch, () -> {
            /** >>> BEGINNING OF ROOT THREAD **/
            final long rootThreadId = Thread.currentThread().getId();

            final ThreadContext rootContext = newThreadContextWithMockedProperties();
            ThreadContextExecutor.using(rootContext).withoutDefaultListeners().execute(() -> {
                /** >>> BEGINNING OF ROOT THREAD CONTEXT EXECUTOR **/
                softly.assertThat(Thread.currentThread().getId()).isEqualTo(rootThreadId);
                final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
                softly.assertThat(parentContext).isNotSameAs(rootContext);
                // make sure all properties are still there
                softly.assertThat(parentContext).isEqualTo(rootContext);

                final ThreadContextExecutor childContextExecutor =
                    ThreadContextExecutor.using(parentContext).withoutDefaultListeners();
                submitAndCountDown(softly, childContextLatch, childContextExecutor, () -> {
                    /** >>> BEGINNING OF CHILD THREAD **/
                    softly.assertThat(Thread.currentThread().getId()).isNotEqualTo(rootThreadId);

                    // make sure the parent thread context has been propagated
                    final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                    softly.assertThat(childContext).isNotSameAs(parentContext);
                    // make sure all properties are still there
                    softly.assertThat(childContext).isEqualTo(parentContext);

                    /** <<< END OF CHILD THREAD **/
                });

                tryAwait(softly, childContextLatch); // wait until child thread is done

                // parent thread context is still active
                softly.assertThat(ThreadContextAccessor.getCurrentContext()).isSameAs(parentContext);
                // make sure all properties are still there
                softly.assertThat(ThreadContextAccessor.getCurrentContext()).isEqualTo(rootContext);

                /** <<< END OF ROOT THREAD CONTEXT EXECUTOR **/
            });
            /** <<< END OF ROOT THREAD **/
        });

        tryAwait(softly, parentContextLatch);
        softly.assertAll();
    }

    /*
     * Test Case:
     * - Create a new root ThreadContext (implicit)
     * - Implicitly duplicate values of root ThreadContext to child ThreadContext
     * - Parent thread is stopped BEFORE the child thread has started
     * - Child ThreadContext contains copied properties
     *
     * ┌─────────────┐
     * │             │
     * │ Root-Thread │
     * │             │
     * └──────┬──────┘
     *        │
     *        │ 1. Create a new ThreadContextExecutor (without new ThreadContext!)
     *        │ => Initializes a new root ThreadContext from values by Listeners
     *        │
     *        │ 2. Create child ThreadContextExecutor with new ThreadContext
     *        │ => Listeners will be called and properties should be inherited
     *        │
     *        │ 3. Start asynchronous operation before stopping
     *        │
     *        │
     *        │                  ┌────────────┐
     *        │    start         │            │
     *        ├─────────────────►│ Sub-Thread │
     *        │                  │            │
     *        │                  └─────┬──────┘
     *        │                        │
     *    ────┴────                    │ // The Root-Thread has already stopped
     *                                 │
     *                                 │ 1. Run childThreadContextExecutor.execute(...)
     *                                 │ => Triggers all registered Listeners
     *                                 │
     *                                 │ 2. Read property from child ThreadContext
     *                                 │ => Success: The property duplication happened
     *                                 │    when the parent ThreadContext was still filled
     *                                 │
     *
     * fancy ascii art created with: https://asciiflow.com/#/
     */
    @SneakyThrows
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    void testSuperLateExecutionOfExplicitDuplication()
    {
        final SoftAssertions softly = new SoftAssertions();
        final CountDownLatch parentContextLatch = new CountDownLatch(1);
        final CountDownLatch childContextLatch = new CountDownLatch(1);

        submitAndCountDown(softly, parentContextLatch, () -> {
            /** >>> BEGINNING OF ROOT THREAD **/
            final long rootThreadId = Thread.currentThread().getId();

            // crucially this does not use .withThreadContext() to pass in an existing context with values
            // instead, this is how the root context is created, where values are populated via the listeners
            ThreadContextExecutor.fromNewContext().withListeners(new MyListener(VALUE)).execute(() -> {
                /** >>> BEGINNING OF ROOT THREAD CONTEXT EXECUTOR **/
                softly.assertThat(Thread.currentThread().getId()).isEqualTo(rootThreadId);

                // verify the value was populated
                final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
                softly.assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                final ThreadContextExecutor childContextExecutor =
                    ThreadContextExecutor.fromCurrentContext().withListeners(new MyListener()); // pretend as this listener was picked up automatically through the service locator pattern
                submitAndCountDown(softly, childContextLatch, () -> {
                    /** >>> BEGINNING OF CHILD THREAD **/
                    softly.assertThat(ThreadContextAccessor.tryGetCurrentContext().isFailure()).isTrue();
                    // make sure the parent thread is done BEFORE the child ThreadContextExecutor#execute is being invoked
                    tryAwait(softly, parentContextLatch);

                    childContextExecutor.execute(() -> {
                        /** >>> BEGINNING OF CHILD THREAD CONTEXT EXECUTOR **/
                        softly.assertThat(Thread.currentThread().getId()).isNotEqualTo(rootThreadId);

                        // make sure the child context is available here
                        final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                        softly.assertThat(parentContext).isNotSameAs(childContext);

                        // check that parent context is not cleaned up
                        softly
                            .assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get())
                            .isEqualTo(VALUE);

                        // check that the child context has duplicated properties defined
                        softly
                            .assertThat(childContext.getPropertyValue(MyListener.PROPERTY_KEY).get())
                            .isEqualTo(VALUE);
                        /** <<< END OF CHILD THREAD CONTEXT EXECUTOR **/
                    });
                    softly.assertThat(ThreadContextAccessor.tryGetCurrentContext().isFailure()).isTrue();
                    /** <<< END OF CHILD THREAD **/
                });
                /** <<< END OF ROOT THREAD CONTEXT EXECUTOR **/
            });
            /** <<< END OF ROOT THREAD **/
        });

        tryAwait(softly, childContextLatch);
        softly.assertAll();
    }

    /*
     * Test Case:
     * - Create a new root ThreadContext (implicit)
     * - Implicitly duplicate values of root ThreadContext to child ThreadContext
     * - Parent thread is stopped AFTER the child thread has started (still running)
     * - Child ThreadContext contains copied properties
     *
     * ┌─────────────┐
     * │             │
     * │ Root-Thread │
     * │             │
     * └──────┬──────┘
     *        │
     *        │ 1. Create a new ThreadContextExecutor (without new ThreadContext!)
     *        │ => Initializes a new root ThreadContext from values by Listeners
     *        │
     *        │ 2. Create child ThreadContextExecutor with new ThreadContext
     *        │ => Listeners will be called and properties should be inherited
     *        │
     *        │ 3. Start asynchronous operation before stopping
     *        │
     *        │
     *        │                  ┌────────────┐
     *        │    start         │            │
     *        ├─────────────────►│ Sub-Thread │
     *        │                  │            │
     *        │                  └─────┬──────┘
     *        │                        │
     *        │                        │ 1. Run childThreadContextExecutor.execute(...)
     *        │                        │ => Triggers als registered Listeners
     *        │                        │
     *    ────┴────                    │ // Now the Root-Thread has stopped
     *                                 │ => The root ThreadContext has been cleared
     *                                 │
     *                                 │ 2. Read property from child ThreadContext
     *                                 │ => Success: The property duplication happened
     *                                 │    when the parent ThreadContext was still filled
     *                                 │
     *
     * fancy ascii art created with: https://asciiflow.com/#/
     */
    @SneakyThrows
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    void testLateExecutionOfExplicitDuplication()
    {
        final SoftAssertions softly = new SoftAssertions();
        final CountDownLatch parentContextLatch = new CountDownLatch(1);
        final CountDownLatch childContextLatch = new CountDownLatch(1);
        final CountDownLatch childContextStartedLatch = new CountDownLatch(1);

        submitAndCountDown(softly, parentContextLatch, () -> {
            /** >>> BEGINNING OF ROOT THREAD **/
            final long rootThreadId = Thread.currentThread().getId();

            ThreadContextExecutor.fromNewContext().withListeners(new MyListener(VALUE)).execute(() -> {
                /** >>> BEGINNING OF ROOT THREAD CONTEXT EXECUTOR **/
                softly.assertThat(Thread.currentThread().getId()).isEqualTo(rootThreadId);

                final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();

                // verify the value was populated
                softly.assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                final ThreadContextExecutor childContextExecutor =
                    ThreadContextExecutor.fromCurrentContext().withListeners(new MyListener()); // pretend as this listener was picked up automatically through the service locator pattern
                submitAndCountDown(softly, childContextLatch, childContextExecutor, () -> {
                    /** >>> BEGINNING OF CHILD THREAD **/
                    softly.assertThat(Thread.currentThread().getId()).isNotEqualTo(rootThreadId);

                    // make sure this point is reached BEFORE the parent context dies
                    softly.assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                    // signal the parent thread that it can be stopped now
                    childContextStartedLatch.countDown();

                    // make sure the parent thread is done
                    tryAwait(softly, parentContextLatch);

                    // parent context has not been cleared
                    softly.assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                    // make sure the child context is available here
                    final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                    softly.assertThat(parentContext).isNotSameAs(childContext);

                    // the child still inherited the value from the parent
                    softly.assertThat(childContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                    /** <<< END OF CHILD THREAD **/
                });

                tryAwait(softly, childContextStartedLatch); // we do not "destroy" the parent until the child has started
                /** <<< END OF ROOT THREAD CONTEXT EXECUTOR **/
            });
            /** <<< END OF ROOT THREAD **/
        });

        tryAwait(softly, childContextLatch);
        softly.assertAll();
    }

    /*
     * Test Case:
     * - Create a new root ThreadContext (implicit)
     * - Explicitly share ThreadContext with child thread
     * - Parent thread is stopped BEFORE the child thread has started
     * - Child ThreadContext is not empty
     *
     * ┌─────────────┐
     * │             │
     * │ Root-Thread │
     * │             │
     * └──────┬──────┘
     *        │
     *        │ 1. Create a new ThreadContextExecutor (without new ThreadContext!)
     *        │ => Initializes a new root ThreadContext from values by Listeners
     *        │
     *        │ 2. Create child ThreadContextExecutor using the parent context
     *        │
     *        │ 3. Start asynchronous operation before stopping
     *        │
     *        │
     *        │                  ┌────────────┐
     *        │    start         │            │
     *        ├─────────────────►│ Sub-Thread │
     *        │                  │            │
     *        │                  └─────┬──────┘
     *        │                        │
     *    ────┴────                    │ // The Root-Thread has already stopped
     *                                 │
     *                                 │ 1. Run childThreadContextExecutor.execute(...)
     *                                 │ => Listeners will be called but no properties will be changed (since already present)
     *                                 │
     *                                 │ 2. Read property from child ThreadContext
     *                                 │ => Success since parent context is immutable
     *                                 │
     *
     * fancy ascii art created with: https://asciiflow.com/#/
     */
    @SneakyThrows
    @Test
    @Timeout( value = TEST_TIMEOUT, unit = TimeUnit.MILLISECONDS )
    void testLateExecutionOfExplicitSharing()
    {
        final SoftAssertions softly = new SoftAssertions();
        final CountDownLatch parentContextLatch = new CountDownLatch(1);
        final CountDownLatch childContextLatch = new CountDownLatch(1);

        submitAndCountDown(softly, parentContextLatch, () -> {
            /** >>> BEGINNING OF ROOT THREAD **/
            final long rootThreadId = Thread.currentThread().getId();

            ThreadContextExecutor.fromNewContext().withListeners(new MyListener(VALUE)).execute(() -> {
                /** >>> BEGINNING OF ROOT THREAD CONTEXT EXECUTOR **/
                softly.assertThat(Thread.currentThread().getId()).isEqualTo(rootThreadId);

                // verify the value was populated
                final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
                softly.assertThat(parentContext.getPropertyValue(MyListener.PROPERTY_KEY).get()).isEqualTo(VALUE);

                final ThreadContextExecutor childContextExecutor =
                    ThreadContextExecutor.fromCurrentContext().withListeners(new MyListener()); // pretend as this listener was picked up automatically through the service locator pattern
                submitAndCountDown(softly, childContextLatch, () -> {
                    /** >>> BEGINNING OF CHILD THREAD **/
                    // make sure the parent thread is done BEFORE the child ThreadContextExecutor#execute is being invoked
                    tryAwait(softly, parentContextLatch);

                    childContextExecutor.execute(() -> {
                        /** >>> BEGINNING OF CHILD THREAD CONTEXT EXECUTOR **/
                        softly.assertThat(Thread.currentThread().getId()).isNotEqualTo(rootThreadId);

                        // make sure the child context is available here
                        final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                        softly.assertThat(parentContext).isNotSameAs(childContext);

                        // check that the child context still contains the property
                        softly
                            .assertThat(childContext.getPropertyValue(MyListener.PROPERTY_KEY).get())
                            .isEqualTo(VALUE);
                        /** <<< END OF CHILD THREAD CONTEXT EXECUTOR **/
                    });
                    /** <<< END OF CHILD THREAD **/
                });
                /** <<< END OF ROOT THREAD CONTEXT EXECUTOR **/
            });
            /** <<< END OF ROOT THREAD **/
        });

        tryAwait(softly, childContextLatch);
        softly.assertAll();
    }

    private void submitAndCountDown(
        @Nonnull final SoftAssertions softly,
        @Nonnull final CountDownLatch latch,
        @Nonnull final ThreadContextExecutor threadContextExecutor,
        @Nonnull final Executable task )
    {
        submitAndCountDown(softly, latch, () -> threadContextExecutor.execute(task));
    }

    private void submitAndCountDown(
        @Nonnull final SoftAssertions softly,
        @Nonnull final CountDownLatch latch,
        @Nonnull final Executable task )
    {
        executorService.submit(() -> {
            softly.assertThat(ThreadContextAccessor.tryGetCurrentContext()).isEmpty();
            try {
                task.execute();
            }
            catch( final Exception e ) {
                softly.fail(String.format("Asynchronous task threw an unexpected exception: %s", e.getMessage()), e);
            }
            finally {
                softly.assertThat(ThreadContextAccessor.tryGetCurrentContext()).isEmpty();
                latch.countDown();
            }
        });
    }

    private void tryAwait( @Nonnull final SoftAssertions softly, @Nonnull final CountDownLatch latch )
    {
        try {
            softly
                .assertThat(latch.await(TEST_TIMEOUT, TimeUnit.MILLISECONDS))
                .isTrue()
                .withFailMessage("Expected thread to finish within the time limit, but it failed to do so.");
        }
        catch( final Exception e ) {
            softly.fail("Exception while awaiting latch.", e);
        }
    }

    @Nonnull
    private ThreadContext newThreadContextWithMockedProperties()
    {
        final ThreadContext threadContext = new DefaultThreadContext();
        threadContext.setPropertyIfAbsent(PROPERTY_REQUEST_HEADERS, Property.of(mockedRequestHeaders));
        threadContext.setPropertyIfAbsent(PROPERTY_TENANT, Property.of(mockedTenant));
        threadContext.setPropertyIfAbsent(PROPERTY_PRINCIPAL, Property.of(mockedPrincipal));

        return threadContext;
    }

    private static class MyListener implements ThreadContextListener
    {
        private static final String PROPERTY_KEY = "mykey";

        @Nullable
        private final String value;

        public MyListener()
        {
            value = null;
        }

        public MyListener( @Nonnull final String value )
        {
            this.value = value;
        }

        @Override
        public int getPriority()
        {
            return 0;
        }

        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            if( value != null ) {
                threadContext.setPropertyIfAbsent(PROPERTY_KEY, Property.of(value));
            }
        }
    }
}
