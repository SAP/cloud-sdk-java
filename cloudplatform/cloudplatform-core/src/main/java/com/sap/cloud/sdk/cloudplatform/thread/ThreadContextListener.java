package com.sap.cloud.sdk.cloudplatform.thread;

import javax.annotation.Nonnull;

/**
 * Enables listening to lifecycle events of a {@link ThreadContext}. {@link ThreadContextListener}s are invoked by a
 * {@link ThreadContextExecutor}.
 */
public interface ThreadContextListener extends Comparable<ThreadContextListener>
{
    /**
     * The priorities used to order the default listeners provided by the SDK.
     */
    class DefaultPriorities
    {
        /**
         * The default priority for the RequestHeaderThreadContextListener
         */
        public static final int REQUEST_HEADER_LISTENER = -7;
        /**
         * The default priority for the AuthTokenThreadContextListener
         */
        public static final int AUTH_TOKEN_LISTENER = -6;
        /**
         * The default priority for the SecurityContextThreadContextDecorator
         */
        public static final int SCP_CF_SECURITY_CONTEXT_DECORATOR = -5;
        /**
         * The default priority for the BasicAuthenticationThreadContextListener
         */
        public static final int BASIC_AUTH_LISTENER = -4;
        /**
         * The default priority for the PrincipalThreadContextListener
         */
        public static final int PRINCIPAL_LISTENER = -3;
        /**
         * The default priority for the TenantThreadContextListener
         */
        public static final int TENANT_LISTENER = -2;
        /**
         * The default priority for custom listeners
         */
        public static final int CUSTOM_LISTENER = 0;
    }

    /**
     * Returns the priority that defines the order in which listeners are invoked. Smaller priorities are invoked
     * earlier during context initialization and invoked later (reversed initialization order) during context
     * destruction. Negative number must not be used as they are reserved for internal use.
     *
     * @return The priority of this listener implementation.
     */
    int getPriority();

    /**
     * Invoked before the current {@link ThreadContext} is initialized and set. This method can be used, for example, to
     * inherit properties from the parent context.
     * <p>
     * {@link ThreadContextAccessor#getCurrentContext()} returns the old {@link ThreadContext} reference.
     *
     * @param threadContext
     *            The {@link ThreadContext} that is initialized and will be set.
     */
    default void beforeInitialize( @Nonnull final ThreadContext threadContext )
    {
    }

    /**
     * Invoked after the current {@link ThreadContext} is initialized and set.
     *
     * @param threadContext
     *            The {@link ThreadContext} that was initialized is now set.
     */
    default void afterInitialize( @Nonnull final ThreadContext threadContext )
    {
    }

    @Override
    default int compareTo( @Nonnull final ThreadContextListener other )
    {
        return Integer.compare(getPriority(), other.getPriority());
    }
}
