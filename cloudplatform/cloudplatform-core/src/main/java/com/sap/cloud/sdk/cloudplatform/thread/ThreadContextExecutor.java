/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Executes a {@code Callable} or {@code Executable} within a {@link ThreadContext}.
 * <p>
 * For example:
 *
 * <pre>
 * <code>ThreadContextExecutor.fromNewContext().execute(() -> {
 *     // code that is executed within the ThreadContext created by ThreadContextExecutor
 *     new MyODataService().getAllEntities().top(10).execute();
 * });
 * </code>
 * </pre>
 * <p>
 * <strong>Note:</strong> Please be aware that if you pass a {@link ThreadContext} to the executor, you have to make
 * sure that this context is not destroyed during execution, for example, within asynchronous tasks. You should
 * therefore only pass a {@link ThreadContext} for which you manage the lifecycle yourself.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class ThreadContextExecutor
{
    @Nonnull
    private ThreadContext threadContext;

    @Nonnull
    private final DefaultThreadContextListenerChain listenerChain = new DefaultThreadContextListenerChain();

    /**
     * Create a {@link ThreadContextExecutor} using the current {@link ThreadContext}, that is being resolved from
     * {@link ThreadContextAccessor} and duplicated with all existing properties. If no {@link ThreadContext} can be
     * determined, then an exception will be thrown.
     *
     * @return A new instance of {@link ThreadContextExecutor}.
     * @throws ThreadContextAccessException
     *             if a {@link ThreadContext} cannot be resolved.
     */
    @Nonnull
    public static ThreadContextExecutor fromCurrentContext()
        throws ThreadContextAccessException
    {
        final ThreadContext initialContext = ThreadContextAccessor.getCurrentContext();
        return new ThreadContextExecutor(initialContext.duplicate());
    }

    /**
     * Create a {@link ThreadContextExecutor} using the current {@link ThreadContext}, if one is available. Otherwise, a
     * new {@link DefaultThreadContext} is created.
     *
     * @return A new instance of {@link ThreadContextExecutor}.
     * @throws ThreadContextAccessException
     *             if a {@link ThreadContext} cannot be resolved.
     *
     * @see #fromCurrentContext()
     * @see #fromNewContext()
     */
    @Nonnull
    public static ThreadContextExecutor fromCurrentOrNewContext()
    {
        return Try.of(ThreadContextExecutor::fromCurrentContext).getOrElse(ThreadContextExecutor::fromNewContext);
    }

    /**
     * Create a {@link ThreadContextExecutor} using a new instance of {@link ThreadContext} with empty properties.
     *
     * @return A new instance of {@link ThreadContextExecutor}.
     */
    @Nonnull
    public static ThreadContextExecutor fromNewContext()
    {
        return new ThreadContextExecutor(new DefaultThreadContext());
    }

    /**
     * Create a {@link ThreadContextExecutor} using a duplicate of the provided {@link ThreadContext} with preset
     * properties.
     *
     * @param context
     *            An existing instance of {@link ThreadContext}.
     * @return A new instance of {@link ThreadContextExecutor}.
     */
    @Nonnull
    public static ThreadContextExecutor using( @Nonnull final ThreadContext context )
    {
        return new ThreadContextExecutor(context.duplicate());
    }

    /**
     * Removes all SDK provided listeners on the classpath from the executor call.
     * <p>
     * The various libraries of the SDK provide several implementations of the {@code ThreadContextListener} which are
     * loaded automatically if their libraries are part of the classpath. To fine tune the used listeners this method
     * allows to ignore those automatically loaded implementation.
     *
     * @return This ThreadContextExecutor to provide a fluent interface.
     */
    @Nonnull
    public ThreadContextExecutor withoutDefaultListeners()
    {
        listenerChain.removeDefaultListeners();
        return this;
    }

    /**
     * Adds the specified listeners to the life-cycle of the {@code ThreadContext}.
     *
     * @param listeners
     *            The listeners to use during creation and destruction of the {@code ThreadContext}.
     *
     * @return This ThreadContextExecutor to provide a fluent interface.
     */
    @Nonnull
    public ThreadContextExecutor withListeners( @Nonnull final ThreadContextListener... listeners )
    {
        return withListeners(Arrays.asList(listeners));
    }

    /**
     * Adds the specified listeners to the life-cycle of the {@code ThreadContext}.
     *
     * @param listeners
     *            The listeners to use during creation and destruction of the {@code ThreadContext}.
     *
     * @return This ThreadContextExecutor to provide a fluent interface.
     */
    @Nonnull
    public ThreadContextExecutor withListeners( @Nonnull final Iterable<ThreadContextListener> listeners )
    {
        for( final ThreadContextListener listener : listeners ) {
            listenerChain.addListener(listener);
        }

        return this;
    }

    /**
     * Removes the listeners with the given priorities from the life-cycle of the {@code ThreadContext}.
     *
     * @param listenerPriorities
     *            The priorities of the listeners to remove.
     *
     * @return This ThreadContextExecutor to provide a fluent interface.
     */
    @Nonnull
    public ThreadContextExecutor withoutListeners( @Nonnull final Integer... listenerPriorities )
    {
        return withoutListeners(Arrays.asList(listenerPriorities));
    }

    /**
     * Removes the listeners with the given priorities from the life-cycle of the {@code ThreadContext}.
     *
     * @param listenerPriorities
     *            The priorities of the listeners to remove.
     *
     * @return This ThreadContextExecutor to provide a fluent interface.
     */
    @Nonnull
    public ThreadContextExecutor withoutListeners( @Nonnull final Iterable<Integer> listenerPriorities )
    {
        for( final int listenerPriority : listenerPriorities ) {
            listenerChain.removeListener(listenerPriority);
        }

        return this;
    }

    /**
     * Returns {@link ThreadContextListener}s by their priority.
     *
     * @return The list of listeners.
     */
    @Nonnull
    public List<ThreadContextListener> getListenersOrderedByPriority()
    {
        return listenerChain.getListenersOrderedByPriority();
    }

    /**
     * Get a {@link ThreadContextListener} by its priority.
     *
     * @return The expected listener, or {@code null}.
     *
     * @param listenerPriority
     *            The priority of the listeners.
     * @param <T>
     *            The type of listener
     */
    @Nonnull
    public <T extends ThreadContextListener> Option<T> getListener( final int listenerPriority )
    {
        @SuppressWarnings( "unchecked" )
        final Try<T> lookup = Try.of(() -> (T) listenerChain.getListener(listenerPriority));
        return lookup
            .onFailure(e -> log.error("Listener with priority {} could not be cast to target type", listenerPriority))
            .toOption()
            .filter(Objects::nonNull);
    }

    /**
     * Executes the given {@code Callable} as if it was called inside a {@code ThreadContext}.
     *
     * @param callable
     *            The {@code Callable} to execute inside a {@code ThreadContext}.
     * @param <T>
     *            The return type of the given {@code Callable}.
     *
     * @return The value computed by the specified {@code Callable}.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while computing the value.
     */
    @Nullable
    public <T> T execute( @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        try {
            return call(callable);
        }
        catch( final ThreadContextExecutionException e ) {
            throw e;
        }
        catch( final Exception e ) {
            throw new ThreadContextExecutionException(e);
        }
    }

    /**
     * Executes the given {@code Executable} as if it was called inside a {@code ThreadContext}.
     *
     * @param executable
     *            The {@code Executable} to execute inside a {@code ThreadContext}.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the {@code Executable}.
     */
    public void execute( @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        try {
            call(() -> {
                executable.execute();
                return null;
            });
        }
        catch( final ThreadContextExecutionException e ) {
            throw e;
        }
        catch( final Exception e ) {
            throw new ThreadContextExecutionException(e);
        }
    }

    @Nullable
    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" ) // callable.call() throws Exception. We're not catching it here.
    private <T> T call( @Nonnull final Callable<T> callable )
        throws Exception
    {
        final ThreadContextFacade facade =
            ThreadContextAccessor
                .tryGetThreadContextFacade()
                .getOrElseThrow(
                    failure -> new ThreadContextAccessException(
                        "No " + ThreadContextFacade.class.getSimpleName() + " defined.",
                        failure));

        final ThreadContext initialContext = facade.getCurrentContextOrNull();
        final ThreadContext executionThreadContext = threadContext.duplicate();

        notifyBeforeInitialize(executionThreadContext);
        try {
            setCurrentContext(executionThreadContext, facade);
            notifyAfterInitialize(executionThreadContext);

            return callable.call();
        }
        finally {
            resetCurrentContext(facade, initialContext);
        }
    }

    private void notifyBeforeInitialize( @Nonnull final ThreadContext executionThreadContext )
    {
        log.debug("Notify before initialize: {}", executionThreadContext);

        for( final ThreadContextListener listener : listenerChain.getListenersOrderedByPriority() ) {
            log.debug("Invoking beforeInitialize() on listener: {}", listener.getClass().getName());

            listener.beforeInitialize(executionThreadContext);
        }
    }

    private void notifyAfterInitialize( @Nonnull final ThreadContext executionThreadContext )
    {
        log.debug("Notify after initialize: {}", executionThreadContext);

        for( final ThreadContextListener listener : listenerChain.getListenersOrderedByPriority() ) {
            log.debug("Invoking afterInitialize() on listener: {}", listener.getClass().getName());

            listener.afterInitialize(executionThreadContext);
        }
    }

    private void setCurrentContext(
        @Nonnull final ThreadContext executionThreadContext,
        @Nonnull final ThreadContextFacade facade )
    {
        log.debug("Setting current thread context to {}.", executionThreadContext);

        facade.setCurrentContext(executionThreadContext);
    }

    private
        void
        resetCurrentContext( @Nonnull final ThreadContextFacade facade, @Nullable final ThreadContext initialContext )
    {
        if( initialContext != null ) {
            log.debug("Resetting current thread context to initial {}.", initialContext);

            facade.setCurrentContext(initialContext);
        } else {
            log.debug("Removing current thread context.");

            facade.removeCurrentContext();
        }
    }
}
