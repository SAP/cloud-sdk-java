package com.sap.cloud.sdk.cloudplatform.thread;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * Implementation of {@link ThreadContextFacade} that internally uses {@link ThreadLocal} to provide access to the
 * respective {@link ThreadContext}.
 * <p>
 * <strong>Important:</strong> This implementation should only be used when relying on container-managed threads only.
 * In case you use a framework that manages threads, you have to rely on an implementation that is adjusted to your
 * framework.
 */
public class ThreadLocalThreadContextFacade implements ThreadContextFacade
{
    /**
     * Static thread-local variable to store the current thread context.<br>
     * <br>
     * <strong>Note:</strong> The type ensures that the accessible content is safe from other threads. That means two
     * foreign threads have their own independent data in this static field value.
     */
    private static final ThreadLocal<ThreadContext> currentThreadContext = new ThreadLocal<>();

    @Nonnull
    @Override
    public Try<ThreadContext> tryGetCurrentContext()
    {
        return Option
            .of(getCurrentContextOrNull())
            .toTry(
                () -> new ThreadContextAccessException(
                    String
                        .format(
                            "No %s available for thread id=%s.",
                            ThreadContext.class.getSimpleName(),
                            Thread.currentThread().getId())));
    }

    @Override
    public void setCurrentContext( @Nonnull final ThreadContext threadContext )
    {
        currentThreadContext.set(threadContext);
    }

    @Override
    public void removeCurrentContext()
    {
        currentThreadContext.remove();
    }

    @Override
    @Nullable
    public ThreadContext getCurrentContextOrNull()
    {
        return currentThreadContext.get();
    }
}
