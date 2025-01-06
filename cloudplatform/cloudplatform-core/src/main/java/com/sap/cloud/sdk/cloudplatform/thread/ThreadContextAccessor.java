/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Accessor for the current {@link ThreadContext}.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class ThreadContextAccessor
{
    @Nonnull
    private static Try<ThreadContextFacade> threadContextFacade = FacadeLocator.getFacade(ThreadContextFacade.class);

    /**
     * Returns the {@link ThreadContextFacade} instance.
     *
     * @return The {@link ThreadContextFacade} instance, or {@code null}.
     */
    @Nullable
    public static ThreadContextFacade getThreadContextFacade()
    {
        return threadContextFacade.getOrNull();
    }

    /**
     * Returns a {@link Try} of the {@link ThreadContextFacade} instance.
     *
     * @return A {@link Try} of the {@link ThreadContextFacade} instance.
     */
    @Nonnull
    public static Try<ThreadContextFacade> tryGetThreadContextFacade()
    {
        return threadContextFacade;
    }

    /**
     * Replaces the default {@link ThreadContextFacade} instance.
     *
     * @param threadContextFacade
     *            An instance of {@link ThreadContextFacade}. Use {@code null} to reset the facade.
     */
    public static void setThreadContextFacade( @Nullable final ThreadContextFacade threadContextFacade )
    {
        if( threadContextFacade == null ) {
            ThreadContextAccessor.threadContextFacade = FacadeLocator.getFacade(ThreadContextFacade.class);
        } else {
            ThreadContextAccessor.threadContextFacade = Try.success(threadContextFacade);
        }
    }

    /**
     * Returns the current {@link ThreadContext}.
     *
     * @return The current {@link ThreadContext}.
     * @throws ThreadContextAccessException
     *             If the current {@link ThreadContext} cannot be accessed.
     */
    @Nonnull
    public static ThreadContext getCurrentContext()
        throws ThreadContextAccessException
    {
        return tryGetCurrentContext().getOrElseThrow(failure -> {
            if( failure instanceof ThreadContextAccessException ) {
                throw (ThreadContextAccessException) failure;
            } else {
                throw new ThreadContextAccessException("Failed to get current thread context.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of the current {@link ThreadContext}.
     *
     * @return A {@link Try} of the current {@link ThreadContext}.
     */
    @Nonnull
    public static Try<ThreadContext> tryGetCurrentContext()
    {
        return threadContextFacade.flatMap(ThreadContextFacade::tryGetCurrentContext);
    }

    /**
     * Returns the current {@link ThreadContext} or null, if this is not possible.
     *
     * @since 4.3.0
     *
     * @return The current {@link ThreadContext} or null.
     */
    @Nullable
    public static ThreadContext getCurrentContextOrNull()
    {
        return threadContextFacade.map(ThreadContextFacade::getCurrentContextOrNull).getOrNull();
    }
}
