package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalThreadContextListener;

public interface PrincipalContextApi extends TestContextApi
{
    /**
     * Set {@code default-test-principal} as value for the principal for the current thread.
     *
     * @return the principal
     */
    default Principal setPrincipal()
    {
        return setPrincipal("default-test-principal");
    }

    /**
     * Set the given principal as value for the principal for the current thread.
     *
     * @param principal
     *            the principal to use
     * @return the principal
     */
    default Principal setPrincipal( @Nonnull final String principal )
    {
        return setPrincipal(new DefaultPrincipal(principal));
    }

    /**
     * Set the given principal as value for the principal for the current thread.
     *
     * @param principal
     *            the principal to use. If {@code null}, the principal will be cleared.
     * @return the principal
     */
    default Principal setPrincipal( @Nullable final Principal principal )
    {
        setProperty(PrincipalThreadContextListener.PROPERTY_PRINCIPAL, principal);
        return principal;
    }

    /**
     * Clear the principal for the current thread.
     */
    default void clearPrincipal()
    {
        setPrincipal((Principal) null);
    }
}
