/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.util.concurrent.Callable;

/**
 * Abstract parent class to allow for generic check of the {@link #executeWithAuthToken(AuthToken, Callable)} method in
 * the {@link AuthTokenAccessor#executeWithAuthToken(AuthToken, Callable)}.
 */
abstract class ExecutableAuthTokenFacade implements AuthTokenFacade
{
    /**
     * Executes the given {@code callable} in the context of the given {@code authToken}.
     *
     * @param authToken
     *            The token to be used in the execution of the given {@code callable}.
     * @param callable
     *            The {@link Callable} to be executed with the given {@code authToken}.
     * @param <T>
     *            The type of the return value of the {@code callable}.
     * @return The value returned by executing the given {@code callable} in the context of the given {@code AuthToken}.
     */
    protected abstract <T> T executeWithAuthToken( final AuthToken authToken, final Callable<T> callable );
}
