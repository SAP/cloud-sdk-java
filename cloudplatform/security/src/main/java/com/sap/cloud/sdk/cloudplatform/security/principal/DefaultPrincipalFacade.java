package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;

/**
 * Default implementation of {@link PrincipalFacade} encapsulating the logic to access {@link Principal} information.
 */
public class DefaultPrincipalFacade implements PrincipalFacade
{
    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        return ThreadContextAccessor
            .tryGetCurrentContext()
            .flatMap(c -> c.getPropertyValue(PrincipalThreadContextListener.PROPERTY_PRINCIPAL));
    }

    @Nullable
    <T> T executeWithPrincipal( @Nonnull final Principal principal, @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        return ThreadContextExecutor
            .fromCurrentOrNewContext()
            .withListeners(new PrincipalThreadContextListener(principal))
            .execute(callable);
    }
}
