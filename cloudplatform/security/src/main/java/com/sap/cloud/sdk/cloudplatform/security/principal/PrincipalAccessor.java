/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
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
 * Accessor for retrieving the current {@link Principal}.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
public final class PrincipalAccessor
{
    @Nonnull
    private static PrincipalFacade principalFacade = loadFacadeOrDefault();

    /**
     * Global fallback {@link Principal}. By default, no fallback is used, i.e., the fallback is {@code null}. A global
     * fallback can be useful to ensure a safe fallback or to ease testing with a mocked principal.
     */
    @Getter
    @Setter
    @Nullable
    private static Supplier<Principal> fallbackPrincipal = null;

    /**
     * Returns the {@link PrincipalFacade} instance.
     *
     * @return The {@link PrincipalFacade} instance.
     */
    @Nonnull
    public static PrincipalFacade getPrincipalFacade()
    {
        return principalFacade;
    }

    /**
     * Returns a {@link Try} of the {@link PrincipalFacade} instance.
     *
     * @return A {@link Try} of the {@link PrincipalFacade} instance.
     */
    @Nonnull
    public static Try<PrincipalFacade> tryGetPrincipalFacade()
    {
        return Try.success(principalFacade);
    }

    /**
     * Replaces the default {@link PrincipalFacade} instance.
     *
     * @param principalFacade
     *            An instance of {@link PrincipalFacade}. Use {@code null} to reset the facade to default.
     */
    public static void setPrincipalFacade( @Nullable final PrincipalFacade principalFacade )
    {
        if( principalFacade == null ) {
            PrincipalAccessor.principalFacade = loadFacadeOrDefault();
        } else {
            PrincipalAccessor.principalFacade = principalFacade;
        }
    }

    @Nonnull
    private static PrincipalFacade loadFacadeOrDefault()
    {
        return FacadeLocator.getFacade(PrincipalFacade.class).getOrElseGet(e -> {
            log.debug("No PrincipalFacade found via FacadeLocator. Falling back to DefaultPrincipalFacade.");
            return new DefaultPrincipalFacade();
        });
    }

    /**
     * Returns the current {@code Principal}.
     *
     * @return The current {@code Principal}.
     *
     * @throws PrincipalAccessException
     *             If there is an issue while accessing the {@code Principal}.
     */
    @Nonnull
    public static Principal getCurrentPrincipal()
        throws PrincipalAccessException
    {
        return tryGetCurrentPrincipal().getOrElseThrow(failure -> {
            if( failure instanceof PrincipalAccessException ) {
                throw (PrincipalAccessException) failure;
            } else {
                throw new PrincipalAccessException("Failed to get current principal.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of the current {@link Principal}, or, if the {@link Try} is a failure, the global fallback.
     *
     * @return A {@link Try} of the current {@link Principal}.
     */
    @Nonnull
    public static Try<Principal> tryGetCurrentPrincipal()
    {
        final Try<Principal> principalTry = principalFacade.tryGetCurrentPrincipal();
        if( principalTry.isSuccess() || fallbackPrincipal == null ) {
            return principalTry;
        }

        @Nullable
        final Principal fallback = fallbackPrincipal.get();
        if( fallback == null ) {
            return Try.failure(new PrincipalAccessException());
        }

        return principalTry.recover(failure -> {
            log.warn("Recovering with fallback principal: {}.", fallback, failure);
            return fallback;
        });
    }

    /**
     * Execute the given {@link Callable} on behalf of a given principal.
     *
     * @param principal
     *            The principal to execute on behalf of.
     * @param callable
     *            The callable to execute.
     *
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the principal.
     */
    @Nullable
    public static <T> T executeWithPrincipal( @Nonnull final Principal principal, @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final PrincipalFacade maybeFacade = getPrincipalFacade();
        if( maybeFacade == null || !DefaultPrincipalFacade.class.isAssignableFrom(maybeFacade.getClass()) ) {
            throw new ThreadContextExecutionException(
                String
                    .format(
                        "The 'executeWith...' API is currently supported only when using the Cloud SDK's 'DefaultFacade'"
                            + " implementations (e.g. '%s'). This is an issue especially when using the CAP integration"
                            + " ('cds-integration-cloud-sdk'). To workaround this shortcoming, please refer to the CAP"
                            + " documentation about how to manipulate the request context: "
                            + "https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext.",
                        DefaultPrincipalFacade.class.getName()));
        }

        return ((DefaultPrincipalFacade) maybeFacade).executeWithPrincipal(principal, callable);
    }

    /**
     * Execute the given {@link Executable} on behalf of a given principal.
     *
     * @param principal
     *            The principal to execute on behalf of.
     * @param executable
     *            The executable to execute.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the principal.
     */
    public static void executeWithPrincipal( @Nonnull final Principal principal, @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithPrincipal(principal, () -> {
            executable.execute();
            return null;
        });
    }

    /**
     * Execute the given {@link Callable}, using the given principal as fallback if there is no other principal
     * available.
     *
     * @param fallbackPrincipal
     *            The principal to fall back to.
     * @param callable
     *            The callable to execute.
     *
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the principal.
     */
    @Nullable
    public static <T> T executeWithFallbackPrincipal(
        @Nonnull final Supplier<Principal> fallbackPrincipal,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final Try<Principal> principalTry = tryGetCurrentPrincipal();

        if( principalTry.isSuccess() ) {
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

        return executeWithPrincipal(fallbackPrincipal.get(), callable);
    }

    /**
     * Execute the given {@link Executable}, using the given principal as fallback if there is no other principal
     * available.
     *
     * @param fallbackPrincipal
     *            The principal to fall back to.
     * @param executable
     *            The executable to execute.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the principal.
     */
    public static void executeWithFallbackPrincipal(
        @Nonnull final Supplier<Principal> fallbackPrincipal,
        @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithFallbackPrincipal(fallbackPrincipal, () -> {
            executable.execute();
            return null;
        });
    }
}
