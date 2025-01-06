/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents the default implementation of {@link ThreadContextExecutorService}.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class DefaultThreadContextExecutorService implements ThreadContextExecutorService
{
    @Nonnull
    private final ExecutorService executor;

    /**
     * Static factory method that enables the given {@link ExecutorService} to attach a {@link ThreadContext} to its
     * tasks. The properties will be inherited <b>only</b> if the current parent {@link Thread} contains a
     * {@link ThreadContext}.
     *
     * @param executorService
     *            The executor service instance.
     * @return The customized executor service.
     */
    @Nonnull
    public static DefaultThreadContextExecutorService of( @Nonnull final ExecutorService executorService )
    {
        log.debug("Instantiating DefaultThreadContextExecutorService using: {}", executorService);
        return new DefaultThreadContextExecutorService(executorService);
    }

    @Nonnull
    private <T> Callable<T> decorate( @Nonnull final Callable<T> task )
    {
        log.debug("Creating new ThreadContextExecutor for: {}", task);
        return decorate(task, ThreadContextExecutor.fromCurrentOrNewContext());
    }

    @Nonnull
    private <T> Callable<T> decorate( @Nonnull final Callable<T> task, @Nonnull final ThreadContext threadContext )
    {
        log.debug("Creating new ThreadContextExecutor with ThreadContext: {} for: {}", threadContext, task);
        return decorate(task, ThreadContextExecutor.using(threadContext));
    }

    @Nonnull
    private <T> Callable<T> decorate(
        @Nonnull final Callable<T> task,
        @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        // SDK is innermost decorator. This enables accessing data set by other decorators
        // e.g. SDK listeners can access the CDS request context
        Callable<T> result = () -> threadContextExecutor.execute(task);

        for( final ThreadContextDecorator decorator : DefaultThreadContextDecoratorChain.getDefaultDecorators() ) {
            log.debug("Decorating callable for async execution with decorator {}.", decorator.getClass().getName());

            result = decorator.decorateCallable(result);
        }

        return result;
    }

    @Nonnull
    private Runnable decorate( @Nonnull final Runnable task )
    {
        log.debug("Creating new ThreadContextExecutor for: {}", task);
        return decorate(task, ThreadContextExecutor.fromCurrentOrNewContext());
    }

    @Nonnull
    private Runnable decorate( @Nonnull final Runnable task, @Nonnull final ThreadContext threadContext )
    {
        log.debug("Creating new ThreadContextExecutor with ThreadContext: {} for: {}", threadContext, task);
        return decorate(task, ThreadContextExecutor.using(threadContext));
    }

    @Nonnull
    private
        Runnable
        decorate( @Nonnull final Runnable task, @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        final Callable<Object> callable = decorate(() -> {
            task.run();
            return null;
        }, threadContextExecutor);
        return () -> {
            try {
                callable.call();
            }
            catch( final ThreadContextExecutionException e ) {
                throw e;
            }
            catch( final Exception e ) {
                throw new ThreadContextExecutionException(e);
            }
        };
    }

    @Override
    public void execute( @Nonnull final Runnable command )
    {
        executor.execute(decorate(command));
    }

    @Override
    public void execute( @Nonnull final Runnable command, @Nonnull final ThreadContext threadContext )
    {
        executor.execute(decorate(command, threadContext));
    }

    @Override
    public void execute( @Nonnull final Runnable command, @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        executor.execute(decorate(command, threadContextExecutor));
    }

    @Override
    public void shutdown()
    {
        log.debug("Shutting down {}", executor);
        executor.shutdown();
    }

    @Nonnull
    @Override
    public List<Runnable> shutdownNow()
    {
        log.debug("Shutting down now {}", executor);
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown()
    {
        return executor.isShutdown();
    }

    @Override
    public boolean isTerminated()
    {
        return executor.isTerminated();
    }

    @Override
    public boolean awaitTermination( final long timeout, @Nonnull final TimeUnit unit )
        throws InterruptedException
    {
        log.debug("Awaiting termination on {} with timeout {} {}", executor, timeout, unit);
        return executor.awaitTermination(timeout, unit);
    }

    @Nonnull
    @Override
    public <T> Future<T> submit( @Nonnull final Callable<T> task )
    {
        return executor.submit(decorate(task));
    }

    @Nonnull
    @Override
    public <T> Future<T> submit( @Nonnull final Callable<T> task, @Nonnull final ThreadContext threadContext )
    {
        return executor.submit(decorate(task, threadContext));
    }

    @Nonnull
    @Override
    public <T> Future<T> submit(
        @Nonnull final Callable<T> task,
        @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        return executor.submit(decorate(task, threadContextExecutor));
    }

    @Nonnull
    @Override
    public <T> Future<T> submit( @Nonnull final Runnable task, @Nonnull final T result )
    {
        return executor.submit(decorate(task), result);
    }

    @Nonnull
    @Override
    public <
        T>
        Future<T>
        submit( @Nonnull final Runnable task, @Nonnull final T result, @Nonnull final ThreadContext threadContext )
    {
        return executor.submit(decorate(task, threadContext), result);
    }

    @Nonnull
    @Override
    public <T> Future<T> submit(
        @Nonnull final Runnable task,
        @Nonnull final T result,
        @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        return executor.submit(decorate(task, threadContextExecutor), result);
    }

    @Nonnull
    @Override
    public Future<?> submit( @Nonnull final Runnable task )
    {
        return executor.submit(decorate(task));
    }

    @Nonnull
    @Override
    public Future<?> submit( @Nonnull final Runnable task, @Nonnull final ThreadContext threadContext )
    {
        return executor.submit(decorate(task, threadContext));
    }

    @Nonnull
    @Override
    public Future<?> submit( @Nonnull final Runnable task, @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        return executor.submit(decorate(task, threadContextExecutor));
    }

    @Nonnull
    @Override
    public <T> List<Future<T>> invokeAll( @Nonnull final Collection<? extends Callable<T>> tasks )
        throws InterruptedException
    {
        return executor.invokeAll(tasks.stream().map(this::decorate).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public <T> List<Future<T>> invokeAll(
        @Nonnull final Collection<? extends Callable<T>> tasks,
        final long timeout,
        @Nonnull final TimeUnit unit )
        throws InterruptedException
    {
        return executor.invokeAll(tasks.stream().map(this::decorate).collect(Collectors.toList()), timeout, unit);
    }

    @Nonnull
    @Override
    public <T> T invokeAny( @Nonnull final Collection<? extends Callable<T>> tasks )
        throws InterruptedException,
            ExecutionException
    {
        return executor.invokeAny(tasks.stream().map(this::decorate).collect(Collectors.toList()));
    }

    @Nonnull
    @Override
    public <T> T invokeAny(
        @Nonnull final Collection<? extends Callable<T>> tasks,
        final long timeout,
        @Nonnull final TimeUnit unit )
        throws InterruptedException,
            ExecutionException,
            TimeoutException
    {
        return executor.invokeAny(tasks.stream().map(this::decorate).collect(Collectors.toList()), timeout, unit);
    }
}
