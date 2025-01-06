package com.sap.cloud.sdk.cloudplatform.requestheader;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;

/**
 * Default implementation of the {@link RequestHeaderFacade} interface.
 */
public class DefaultRequestHeaderFacade implements RequestHeaderFacade
{
    @Nonnull
    @Override
    public Try<RequestHeaderContainer> tryGetRequestHeaders()
    {
        return ThreadContextAccessor
            .tryGetCurrentContext()
            .flatMap(c -> c.getPropertyValue(RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS));
    }

    @Nullable
    <T> T executeWithHeaderContainer(
        @Nonnull final RequestHeaderContainer headers,
        @Nonnull final Callable<T> callable )
    {
        return ThreadContextExecutor
            .fromCurrentOrNewContext()
            .withListeners(new RequestHeaderThreadContextListener(headers))
            .execute(callable);
    }
}
