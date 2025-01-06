package com.sap.cloud.sdk.cloudplatform.thread;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

/**
 * Enables decorating a callable before it is scheduled for asynchronous execution by the
 * {@link DefaultThreadContextExecutorService}. This enables passing on ThreadLocal values to newly created threads.
 */
public interface ThreadContextDecorator
{
    /**
     * Defines the default priorities for decorators. Smaller priorities are applied first.
     */
    class DefaultPriorities
    {
        /**
         * The priority for the security context decorator.
         */
        public static final int SCP_CF_SECURITY_CONTEXT_DECORATOR = -1;
        /**
         * The priority for the custom decorator.
         */
        public static final int CUSTOM_DECORATOR = 0;
    }

    /**
     * Returns the priority that defines the order in which decorators are applied. Smaller priorities are applied
     * first. Negative number must not be used as they are reserved for internal use.
     *
     * @return The priority indicator.
     */
    int getPriority();

    /**
     * Enhance an operation with specific behaviour to be applied around its execution. Decoration means returning a new
     * callable which itself will eventually call the given callable.
     *
     * @param callable
     *            The callable to be decorated.
     * @param <T>
     *            Callable generic return type.
     * @return The new, enhanced callable.
     */
    @Nonnull
    <T> Callable<T> decorateCallable( @Nonnull final Callable<T> callable );
}
