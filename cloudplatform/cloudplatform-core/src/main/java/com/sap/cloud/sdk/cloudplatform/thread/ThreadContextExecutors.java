/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Convenience class, giving static access to the functionality of a {@link ThreadContextExecutorService}, using a
 * configurable instance.
 * <p>
 * For example:
 *
 * <pre>
 * <code>final Future&lt;Try&lt;Tenant&gt;&gt; tenant =
 *     ThreadContextExecutors.submit(
 *         // code that is executed within the ThreadContext created by DefaultThreadContextExecutorService
 *         TenantAccessor::tryGetCurrentTenant);
 * </code>
 * </pre>
 */
public class ThreadContextExecutors
{
    private static ThreadContextExecutorService executor = newDefaultThreadContextExecutorService();

    private static ThreadContextExecutorService newDefaultThreadContextExecutorService()
    {
        return DefaultThreadContextExecutorService
            .of(
                Executors
                    .newCachedThreadPool(
                        new ThreadFactoryBuilder()
                            .setNameFormat("cloudsdk-executor-%d")
                            .setDaemon(true)
                            .setPriority(Thread.MAX_PRIORITY)
                            .build()));
    }

    /**
     * Get the unique executor of this class.
     *
     * @return The unique customized executor instance.
     */
    @Nonnull
    public static ThreadContextExecutorService getExecutor()
    {
        return executor;
    }

    /**
     * Set the unique executor of this class or reset it if the given executor is null.
     *
     * @param executor
     *            the executor of this class
     */
    public static void setExecutor( @Nullable final ThreadContextExecutorService executor )
    {
        ThreadContextExecutors.executor = executor == null ? newDefaultThreadContextExecutorService() : executor;
    }

    /**
     * Attach a {@link ThreadContext} to the given command and execute it.
     *
     * @param command
     *            the runnable task
     * @see java.util.concurrent.Executor#execute(Runnable)
     */
    public static void execute( @Nonnull final Runnable command )
    {
        getExecutor().execute(command);
    }

    /**
     * Attach a {@link ThreadContext} to the given command and execute it.
     *
     * @param command
     *            the runnable task
     * @param threadContext
     *            the {@link ThreadContext} to attach to the command
     * @see java.util.concurrent.Executor#execute(Runnable)
     */
    public static void execute( @Nonnull final Runnable command, @Nonnull final ThreadContext threadContext )
    {
        getExecutor().execute(command, threadContext);
    }

    /**
     * Attach a {@link ThreadContextExecutor} to the given command and execute it.
     *
     * @param command
     *            the runnable task
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the command
     * @see java.util.concurrent.Executor#execute(Runnable)
     */
    public static
        void
        execute( @Nonnull final Runnable command, @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        getExecutor().execute(command, threadContextExecutor);
    }

    /**
     * Attach a {@link ThreadContext} to the given task and execute it.
     *
     * @param task
     *            the task to submit
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     * @see java.util.concurrent.ExecutorService#submit(Callable)
     */
    @Nonnull
    public static <T> Future<T> submit( @Nonnull final Callable<T> task )
    {
        return getExecutor().submit(task);
    }

    /**
     * Attach a {@link ThreadContext} to the given task and execute it.
     *
     * @param task
     *            the task to submit
     * @param threadContext
     *            the {@link ThreadContext} to attach to the task
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     * @see java.util.concurrent.ExecutorService#submit(Callable)
     */
    @Nonnull
    public static <T> Future<T> submit( @Nonnull final Callable<T> task, @Nonnull final ThreadContext threadContext )
    {
        return getExecutor().submit(task, threadContext);
    }

    /**
     * Attach a {@link ThreadContextExecutor} to the given task and execute it.
     *
     * @param task
     *            the task to submit
     * @param threadContextExecutor
     *            the {@link ThreadContextExecutor} to attach to the task
     * @return a Future representing pending completion of the task
     * @param <T>
     *            the type of the task's result
     * @see java.util.concurrent.ExecutorService#submit(Callable)
     */
    @Nonnull
    public static <T> Future<T> submit(
        @Nonnull final Callable<T> task,
        @Nonnull final ThreadContextExecutor threadContextExecutor )
    {
        return getExecutor().submit(task, threadContextExecutor);
    }
}
