package com.sap.cloud.sdk.cloudplatform.tenant;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;

/**
 * Abstract base class for {@link TenantFacade}s.
 */
public class DefaultTenantFacade implements TenantFacade
{
    @Nonnull
    @Override
    public Try<Tenant> tryGetCurrentTenant()
    {
        return ThreadContextAccessor
            .tryGetCurrentContext()
            .flatMap(c -> c.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT));
    }

    @Nullable
    <T> T executeWithTenant( @Nonnull final Tenant tenant, @Nonnull final Callable<T> callable )
    {
        return ThreadContextExecutor
            .fromCurrentOrNewContext()
            .withListeners(new TenantThreadContextListener(tenant))
            .execute(callable);
    }
}
