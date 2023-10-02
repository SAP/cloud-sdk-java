/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.CsrfTokenRetrievalException;

/**
 * Retrieves a CSRF token from a given remote system.
 */
public interface CsrfTokenRetriever
{
    /**
     * The disabled {@link CsrfTokenRetriever} throws an {@link IllegalStateException} in case it is invoked.
     */
    CsrfTokenRetriever DISABLED_CSRF_TOKEN_RETRIEVER = new CsrfTokenRetriever()
    {
        @Nonnull
        @Override
        public CsrfToken retrieveCsrfToken( @Nonnull final HttpClient httpClient, @Nonnull final String servicePath )
        {
            throw new IllegalStateException("The disabled CSRF token retriever cannot retrieve an CSRF token.");
        }

        @Nonnull
        @Override
        public CsrfToken retrieveCsrfToken(
            @Nonnull final HttpClient httpClient,
            @Nonnull final String servicePath,
            @Nonnull final Map<String, Collection<String>> headers )
        {
            throw new IllegalStateException("The disabled CSRF token retriever cannot retrieve an CSRF token.");
        }

        @Override
        public boolean isEnabled()
        {
            return false;
        }
    };

    /**
     * Retrieves an CSRF Token from a remote system by issuing an CSRF token request.
     *
     * @param httpClient
     *            The {@link HttpClient} to be used to issue the CSRF token request.
     * @param servicePath
     *            The service path (which would be appended to the destination URI) to be used to issue the CSRF token
     *            request.
     * @return The fetched {@link CsrfToken}.
     * @throws CsrfTokenRetrievalException
     *             When CSRF Token could not be fetched.
     */
    @Nonnull
    CsrfToken retrieveCsrfToken( @Nonnull final HttpClient httpClient, @Nonnull final String servicePath );

    /**
     * Retrieves an CSRF Token from a remote system by issuing an CSRF token request.
     *
     * @param httpClient
     *            The {@link HttpClient} to be used to issue the CSRF token request.
     * @param servicePath
     *            The service path (which would be appended to the destination URI) to be used to issue the CSRF token
     *            request.
     * @param headers
     *            Additional headers to use for the CSRF token request.
     * @return The fetched {@link CsrfToken}.
     * @throws CsrfTokenRetrievalException
     *             When CSRF Token could not be fetched.
     */
    @Nonnull
    CsrfToken retrieveCsrfToken(
        @Nonnull final HttpClient httpClient,
        @Nonnull final String servicePath,
        @Nonnull final Map<String, Collection<String>> headers );

    /**
     * Indicates if CSRF token retrieval is enabled.
     *
     * @return Flag indicating if CSRF token retrieval is enabled
     */
    default boolean isEnabled()
    {
        return true;
    }
}
