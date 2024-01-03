/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Default implementation of {@link PrincipalFacade} encapsulating the logic to access {@link Principal} information.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public class DefaultPrincipalFacade implements PrincipalFacade
{
    private static final List<PrincipalExtractor> DEFAULT_PRINCIPAL_EXTRACTORS =
        Arrays.asList(new OAuth2AuthTokenPrincipalExtractor(), new OidcAuthTokenPrincipalExtractor());

    private final List<PrincipalExtractor> principalExtractors;

    /**
     * Creates a new instance of this facade.
     */
    public DefaultPrincipalFacade()
    {
        this(DEFAULT_PRINCIPAL_EXTRACTORS);
    }

    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        final Try<Principal> principalFromThreadContextTry =
            ThreadContextAccessor
                .tryGetCurrentContext()
                .flatMap(c -> c.getPropertyValue(PrincipalThreadContextListener.PROPERTY_PRINCIPAL));

        if( principalFromThreadContextTry.isSuccess() ) {
            return principalFromThreadContextTry;
        }

        final List<Throwable> throwables = new ArrayList<>();
        throwables.add(principalFromThreadContextTry.getCause());

        return principalExtractors
            .stream()
            .map(PrincipalExtractor::tryGetCurrentPrincipal)
            .filter(principalTry -> principalTry.onFailure(throwables::add).isSuccess())
            .findFirst()
            .orElseGet(() -> createFallbackException(throwables));
    }

    private Try<Principal> createFallbackException( @Nonnull final List<? extends Throwable> throwables )
    {
        final PrincipalAccessException resultingException =
            new PrincipalAccessException("Could not read a principal from thread context, JWT, nor Basic Auth header.");

        throwables.forEach(resultingException::addSuppressed);

        return Try.failure(resultingException);
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
