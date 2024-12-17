/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.RequestHeadersAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Executable;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Accessor for retrieving the {@link RequestHeaderContainer} of the current context.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
public final class RequestHeaderAccessor
{
    @Nonnull
    @Getter
    private static RequestHeaderFacade headerFacade = new DefaultRequestHeaderFacade();

    @Nullable
    @Getter
    @Setter
    private static Supplier<RequestHeaderContainer> fallbackHeaderContainer;

    /**
     * Sets the {@link RequestHeaderFacade} to use.
     *
     * @param requestHeaderFacade
     *            The {@link RequestHeaderFacade} to use, or {@code null} if the default {@link RequestHeaderFacade}
     *            should be used.
     */
    public static void setHeaderFacade( @Nullable final RequestHeaderFacade requestHeaderFacade )
    {
        headerFacade = Objects.requireNonNullElseGet(requestHeaderFacade, DefaultRequestHeaderFacade::new);
    }

    /**
     * Returns the current {@link RequestHeaderContainer}.
     *
     * @return The {@link RequestHeaderContainer} for the current context.
     * @throws RequestHeadersAccessException
     *             If the current {@link RequestHeaderContainer} cannot be accessed.
     */
    @Nonnull
    public static RequestHeaderContainer getHeaderContainer()
        throws RequestHeadersAccessException
    {
        return tryGetHeaderContainer().getOrElseThrow(failure -> {
            if( failure instanceof RequestHeadersAccessException ) {
                throw (RequestHeadersAccessException) failure;
            } else {
                throw new RequestHeadersAccessException("Failed to get current request headers.", failure);
            }
        });
    }

    /**
     * Tries to get the current {@link RequestHeaderContainer}.
     *
     * @return A {@link Try} that might contain the current {@link RequestHeaderContainer}.
     */
    @Nonnull
    public static Try<RequestHeaderContainer> tryGetHeaderContainer()
    {
        final Try<RequestHeaderContainer> requestHeadersTry = headerFacade.tryGetRequestHeaders();

        if( requestHeadersTry.isFailure() && fallbackHeaderContainer != null ) {
            return requestHeadersTry.recover(failure -> {
                final RequestHeaderContainer fallback = fallbackHeaderContainer.get();
                log.warn("Recovering with fallback request: {}.", fallback, failure);
                return fallback;
            });
        }

        return requestHeadersTry;
    }

    /**
     * Runs the given {@code executable} with the given {@code headers} available.
     *
     * @param headers
     *            The HTTP headers (name-value pairs) that should be available within the {@code executable}.
     * @param executable
     *            The {@link Executable} to execute.
     * @throws ThreadContextExecutionException
     *             If there is an issue when running the {@code executable}.
     */
    public static
        void
        executeWithHeaderContainer( @Nonnull final Map<String, String> headers, @Nonnull final Executable executable )
            throws ThreadContextExecutionException
    {
        executeWithHeaderContainer(DefaultRequestHeaderContainer.fromSingleValueMap(headers), executable);
    }

    /**
     * Runs the given {@code executable} with the given {@code headers} available.
     *
     * @param headers
     *            The {@link RequestHeaderContainer} that should be available within the {@code executable}.
     * @param executable
     *            The {@link Runnable} to execute.
     * @throws ThreadContextExecutionException
     *             If there is an issue when running the {@code executable}.
     */
    public static void executeWithHeaderContainer(
        @Nonnull final RequestHeaderContainer headers,
        @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithHeaderContainer(headers, () -> {
            executable.execute();
            return null;
        });
    }

    /**
     * Runs the given {@code callable} with the given {@code headers} available and returns the result.
     *
     * @param headers
     *            The HTTP headers (name-value pairs) that should be available within the {@code callable}.
     * @param callable
     *            The {@link Callable} to run.
     * @param <T>
     *            The result type of the given {@code callable}.
     * @return The result of the {@code callable}.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the given {@code callable}.
     */
    @Nullable
    public static <T> T executeWithHeaderContainer(
        @Nonnull final Map<String, String> headers,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        return executeWithHeaderContainer(DefaultRequestHeaderContainer.fromSingleValueMap(headers), callable);
    }

    /**
     * Runs the given {@code callable} with the given {@code headers} available and returns the result.
     *
     * @param headers
     *            The {@link RequestHeaderContainer} that should be available within the {@code callable}.
     * @param callable
     *            The {@link Callable} to run.
     * @param <T>
     *            The result type of the given {@code callable}.
     * @return The result of the {@code callable}.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the given {@code callable}.
     */
    @Nullable
    public static <T> T executeWithHeaderContainer(
        @Nonnull final RequestHeaderContainer headers,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final RequestHeaderFacade facade = getHeaderFacade();
        if( !DefaultRequestHeaderFacade.class.isAssignableFrom(facade.getClass()) ) {
            throw new ThreadContextExecutionException(String.format("""
                The 'executeWith...' API is currently supported only when using the Cloud SDK's 'DefaultFacade'\
                 implementations (e.g. '%s'). This is an issue especially when using the CAP integration\
                 ('cds-integration-cloud-sdk'). To workaround this shortcoming, please refer to the CAP\
                 documentation about how to manipulate the request context: \
                https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext.\
                """, DefaultRequestHeaderFacade.class.getName()));
        }

        return ((DefaultRequestHeaderFacade) facade).executeWithHeaderContainer(headers, callable);
    }

    /**
     * Runs the given {@code executable} with the given {@code headers} as a fallback option that should be used in case
     * there is no current {@link RequestHeaderContainer}.
     *
     * @param headers
     *            The fallback {@link RequestHeaderContainer}.
     * @param executable
     *            The {@link Executable} to run.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the given {@code executable}.
     */
    public static void executeWithFallbackHeaderContainer(
        @Nonnull final Supplier<RequestHeaderContainer> headers,
        @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithFallbackHeaderContainer(headers, () -> {
            executable.execute();
            return null;
        });
    }

    /**
     * Runs the given {@code callable} with the given {@code headers} as a fallback option that should be used in case
     * there is no current {@link RequestHeaderContainer}.
     *
     * @param headers
     *            The fallback {@link RequestHeaderContainer}.
     * @param callable
     *            The {@link Callable} to run.
     * @param <T>
     *            The return type of the given {@code callable}.
     * @return The result of the given {@code callable}.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the given {@code callable}.
     */
    @Nullable
    public static <T> T executeWithFallbackHeaderContainer(
        @Nonnull final Supplier<RequestHeaderContainer> headers,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final Try<RequestHeaderContainer> requestHeadersTry = tryGetHeaderContainer();

        if( requestHeadersTry.isSuccess() ) {
            try {
                return callable.call();
            }
            catch( final Exception e ) {
                throw new ThreadContextExecutionException(e);
            }
        }

        return executeWithHeaderContainer(headers.get(), callable);
    }
}
