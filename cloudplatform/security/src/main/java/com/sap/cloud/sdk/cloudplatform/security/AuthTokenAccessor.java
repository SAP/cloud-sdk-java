/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.ObjectLookupFailedException;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Executable;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Accessor for retrieving the current {@link AuthToken}.
 * <p>
 * This accessor delegates the logic to implementations of the {@link AuthTokenFacade} interface, as determined by the
 * {@link java.util.ServiceLoader} pattern.
 * <p>
 * If more than one implementation of the {@code AuthTokenFacade} could be found an Exception is thrown. If no
 * implementation could be found a default implementation will be used, that throws an Exception on every invocation.
 *
 * @see AuthTokenFacade#NULL
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
public final class AuthTokenAccessor
{
    /**
     * The {@link AuthTokenFacade} instance.
     */
    @Getter
    @Nonnull
    private static AuthTokenFacade authTokenFacade = getDefaultAuthTokenFacade();

    /**
     * Global fallback {@link AuthToken}. By default, no fallback is used, i.e., the fallback is {@code null}. A global
     * fallback can be useful to ensure a safe fallback or to ease testing with a mocked token.
     */
    @Getter
    @Setter
    @Nullable
    private static Supplier<AuthToken> fallbackToken = null;

    /**
     * Replaces the default {@link AuthTokenFacade} instance.
     *
     * @param requestFacade
     *            An instance of {@link AuthTokenFacade}. Use {@code null} to reset the facade.
     */
    public static void setAuthTokenFacade( @Nullable final AuthTokenFacade requestFacade )
    {
        if( requestFacade == null ) {
            AuthTokenAccessor.authTokenFacade = getDefaultAuthTokenFacade();
        } else {
            AuthTokenAccessor.authTokenFacade = requestFacade;
        }
    }

    @Nonnull
    private static AuthTokenFacade getDefaultAuthTokenFacade()
    {
        final Collection<AuthTokenFacade> facades = FacadeLocator.getFacades(AuthTokenFacade.class);
        if( facades.size() > 1 ) {
            final String classes = facades.stream().map(f -> f.getClass().getName()).collect(Collectors.joining(", "));
            throw new ObjectLookupFailedException(
                String
                    .format(
                        "Too many implementations of %s found. Make sure to only have a single platform specific implementation of the interface on your classpath: %s",
                        AuthTokenFacade.class.getName(),
                        classes));
        }
        if( facades.size() == 1 ) {
            return facades.iterator().next();
        }
        return authTokenFacade = new DefaultAuthTokenFacade();
    }

    /**
     * Returns the current {@link AuthToken}.
     *
     * @return The current {@link AuthToken}.
     *
     * @throws AuthTokenAccessException
     *             If there is an issue while trying to access the {@link AuthToken}. For instance, an {@link AuthToken}
     *             is not available if no request is available or the request does not contain an "Authorization"
     *             header.
     */
    @Nonnull
    public static AuthToken getCurrentToken()
        throws AuthTokenAccessException
    {
        return tryGetCurrentToken().getOrElseThrow(failure -> {
            if( failure instanceof AuthTokenAccessException ) {
                throw (AuthTokenAccessException) failure;
            } else {
                throw new AuthTokenAccessException("Failed to get current authorization token.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of the current {@link AuthToken}, or, if the {@link Try} is a failure, the global fallback.
     * An {@link AuthToken} is not available if no request is available or the request does not contain an
     * "Authorization" header.
     *
     * @return A {@link Try} of the current {@link AuthToken}
     */
    @Nonnull
    public static Try<AuthToken> tryGetCurrentToken()
    {
        final Try<AuthToken> authTokenTry = authTokenFacade.tryGetCurrentToken();

        if( authTokenTry.isFailure() && fallbackToken != null ) {
            return authTokenTry.recover(failure -> {
                final AuthToken fallback = fallbackToken.get();
                log.warn("Recovering with fallback token: {}.", fallback, failure);
                return fallback;
            });
        }

        return authTokenTry;
    }

    /**
     * Execute the given {@link Callable} with a given token.
     *
     * @param authToken
     *            The token to execute with.
     * @param callable
     *            The callable to execute.
     *
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code with the token.
     */
    @Nullable
    public static <T> T executeWithAuthToken( @Nonnull final AuthToken authToken, @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final AuthTokenFacade facade = getAuthTokenFacade();
        if( !ExecutableAuthTokenFacade.class.isAssignableFrom(facade.getClass()) ) {
            throw new ThreadContextExecutionException(
                String
                    .format(
                        "The 'executeWith...' API is currently supported only when using the Cloud SDK's 'DefaultFacade'"
                            + " implementations (e.g. '%s'). This is an issue especially when using the CAP integration"
                            + " ('cds-integration-cloud-sdk'). To workaround this shortcoming, please refer to the CAP"
                            + " documentation about how to manipulate the request context:"
                            + " https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext.",
                        ExecutableAuthTokenFacade.class.getName()));
        }

        return ((ExecutableAuthTokenFacade) facade).executeWithAuthToken(authToken, callable);
    }

    /**
     * Execute the given {@link Executable} with a given token.
     *
     * @param authToken
     *            The token to execute with.
     * @param executable
     *            The executable to execute.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code with the token.
     */
    public static void executeWithAuthToken( @Nonnull final AuthToken authToken, @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithAuthToken(authToken, () -> {
            executable.execute();
            return null;
        });
    }

    /**
     * Execute the given {@link Callable}, using the given token as fallback if there is no other token available.
     *
     * @param fallbackAuthToken
     *            The token to fall back to.
     * @param callable
     *            The callable to execute.
     *
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code with the token.
     */
    @Nullable
    public static <T> T executeWithFallbackAuthToken(
        @Nonnull final Supplier<AuthToken> fallbackAuthToken,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final Try<AuthToken> tokenTry = tryGetCurrentToken();

        if( tokenTry.isSuccess() ) {
            try {
                return callable.call();
            }
            catch( final ThreadContextExecutionException e ) {
                throw e;
            }
            catch( final Exception e ) {
                throw new ThreadContextExecutionException(e);
            }
        }

        return executeWithAuthToken(fallbackAuthToken.get(), callable);
    }

    /**
     * Execute the given {@link Executable}, using the given token as fallback if there is no other token available.
     *
     * @param fallbackAuthToken
     *            The token to fall back to.
     * @param executable
     *            The executable to execute.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code with the token.
     */
    public static void executeWithFallbackAuthToken(
        @Nonnull final Supplier<AuthToken> fallbackAuthToken,
        @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithFallbackAuthToken(fallbackAuthToken, () -> {
            executable.execute();
            return null;
        });
    }
}
