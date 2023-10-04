/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

/**
 * Allows the execution of {@link Runnable} and {@link Callable} within a {@link ThreadContext} within a
 * {@link ExecutorService}.
 * <p>
 * The {@link ThreadContext} properties will be automatically propagated to the asynchronous task (either
 * {@link Runnable} or {@link Callable}) <b>if</b> the current {@link Thread} already contains a {@link ThreadContext}.
 * <p>
 * For example:
 *
 * <pre>
 * <code>final DefaultThreadContextExecutorService executor =
 *     DefaultThreadContextExecutorService.of(Executors.newCachedThreadPool());
 * final Future&lt;Try&lt;Tenant&gt;&gt; tenant =
 *     executor.submit(
 *         // code that is executed within the ThreadContext created by DefaultThreadContextExecutorService
 *         TenantAccessor::tryGetCurrentTenant);
 * </code>
 * </pre>
 *
 * @see ThreadContextExecutors
 */
public interface ThreadContextExecutorService extends ExecutorService
{
    /**
     * Attach a {@link ThreadContext} to the given command and execute it.
     *
     * @see #execute(Runnable)
     * @param command
     *            the runnable task
     * @param threadContext
     *            the {@link ThreadContext} to attach to the command
     */
    void execute( @Nonnull final Runnable command, @Nonnull final ThreadContext threadContext );

    /**
     * Attach a {@link ThreadContextExecutor} to the given command and execute it.
     *
     * @see #execute(Runnable)
     * @param command
     *            the runnable task
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the command
     */
    void execute( @Nonnull final Runnable command, @Nonnull final ThreadContextExecutor threadContextExecutor );

    /**
     * Attach a {@link ThreadContext} to the given task and execute it.
     *
     * @see #submit(Callable)
     * @param task
     *            the task to submit
     * @param threadContext
     *            the {@link ThreadContext} to attach to the task
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     */
    @Nonnull
    <T> Future<T> submit( @Nonnull final Callable<T> task, @Nonnull final ThreadContext threadContext );

    /**
     * Attach a {@link ThreadContextExecutor} to the given task and execute it.
     *
     * @see #submit(Callable)
     * @param task
     *            the task to submit
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the task
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     */
    @Nonnull
    <T> Future<T> submit( @Nonnull final Callable<T> task, @Nonnull final ThreadContextExecutor threadContextExecutor );

    /**
     * Attach a {@link ThreadContext} to the given task and execute it.
     *
     * @see #submit(Runnable, Object)
     * @param task
     *            the task to submit
     * @param threadContext
     *            the {@link ThreadContext} to attach to the task
     * @param result
     *            the result to return
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     */
    @Nonnull
    <
        T>
        Future<T>
        submit( @Nonnull final Runnable task, @Nonnull final T result, @Nonnull final ThreadContext threadContext );

    /**
     * Attach a {@link ThreadContextExecutor} to the given task and execute it.
     *
     * @see #submit(Runnable, Object)
     * @param task
     *            the task to submit
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the task
     * @param result
     *            the result to return
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     */
    @Nonnull
    <T> Future<T> submit(
        @Nonnull final Runnable task,
        @Nonnull final T result,
        @Nonnull final ThreadContextExecutor threadContextExecutor );

    /**
     * Attach a {@link ThreadContext} to the given task and execute it.
     *
     * @see #submit(Runnable)
     * @param task
     *            the task to submit
     * @param threadContext
     *            the {@link ThreadContext} to attach to the task
     * @return a Future representing pending completion of the task
     */
    @Nonnull
    Future<?> submit( @Nonnull final Runnable task, @Nonnull final ThreadContext threadContext );

    /**
     * Attach a {@link ThreadContextExecutor} to the given task and execute it.
     *
     * @see #submit(Runnable)
     * @param task
     *            the task to submit
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the task
     * @return a Future representing pending completion of the task
     */
    @Nonnull
    Future<?> submit( @Nonnull final Runnable task, @Nonnull final ThreadContextExecutor threadContextExecutor );
}
