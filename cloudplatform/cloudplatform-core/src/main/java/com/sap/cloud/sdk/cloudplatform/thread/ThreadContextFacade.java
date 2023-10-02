/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.thread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Try;

/**
 * This class provides an abstraction for accessing the current {@link ThreadContext}.
 */
public interface ThreadContextFacade
{
    /**
     * Returns a {@link Try} of the current {@link ThreadContext}.
     *
     * @return A {@link Try} of the current {@link ThreadContext}.
     */
    @Nonnull
    Try<ThreadContext> tryGetCurrentContext();

    /**
     * Sets the current {@link ThreadContext}.
     *
     * @param threadContext
     *            The {@link ThreadContext} to set.
     */
    void setCurrentContext( @Nonnull final ThreadContext threadContext );

    /**
     * Removes the current {@link ThreadContext}.
     */
    void removeCurrentContext();

    /**
     * Returns an instance {@link ThreadContext} or null.
     *
     * @return A {@link ThreadContext} or null.
     */
    @Nullable
    default ThreadContext getCurrentContextOrNull()
    {
        return tryGetCurrentContext().getOrNull();
    }
}
