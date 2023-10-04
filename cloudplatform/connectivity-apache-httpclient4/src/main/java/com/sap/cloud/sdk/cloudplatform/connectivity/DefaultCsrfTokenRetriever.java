/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.CsrfTokenRetrievalException;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation for retrieving a CSRF token from a given remote system.
 */
@Slf4j
@EqualsAndHashCode
public class DefaultCsrfTokenRetriever implements CsrfTokenRetriever
{
    /**
     * The HTTP header name for the loading the CSRF token.
     */
    public static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_HEADER_FETCH_VALUE = "fetch";

    @Override
    @Nonnull
    public CsrfToken retrieveCsrfToken( @Nonnull final HttpClient httpClient, @Nonnull final String servicePath )
    {
        return retrieveCsrfToken(httpClient, servicePath, Collections.emptyMap());
    }

    @Override
    @Nonnull
    public CsrfToken retrieveCsrfToken(
        @Nonnull final HttpClient httpClient,
        @Nonnull final String servicePath,
        @Nonnull final Map<String, Collection<String>> headers )
    {
        try {
            final Header csrfHeader = retrieveCsrfTokenResponseHeader(httpClient, servicePath, headers);
            final CsrfToken token = convertHeaderToCsrfToken(csrfHeader);
            log.debug("Successfully retrieved CSRF token {} from service path {}", token, servicePath);
            return token;
        }
        catch( final NoSuchElementException e ) {
            final String msgErrorFormat = "CSRF token retrieval failed: The server did not respond with the %s header.";
            final String msgError = String.format(msgErrorFormat, X_CSRF_TOKEN_HEADER_KEY);
            final String msgDebug =
                "{} The subsequent request to service path {} is bound to fail if the OData service requires a CSRF token in request header.";
            log.debug(msgDebug, msgError, servicePath);
            throw new CsrfTokenRetrievalException(msgError, e);
        }
        catch( final Exception e ) {
            final String msgError = "CSRF token retrieval failed: The HTTP request was not successful.";
            log.debug("{} The affected service path is: {}", msgError, servicePath, e);
            throw new CsrfTokenRetrievalException(msgError, e);
        }
    }

    @Nonnull
    private Header retrieveCsrfTokenResponseHeader(
        @Nonnull final HttpClient httpClient,
        @Nonnull final String servicePath,
        @Nonnull final Map<String, Collection<String>> headers )
        throws NoSuchElementException,
            IOException
    {
        final HttpUriRequest csrfTokenRequest = new HttpHead(URI.create(servicePath));

        // Required header
        csrfTokenRequest.setHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_HEADER_FETCH_VALUE);

        // Additional headers
        headers.forEach(( k, values ) -> values.forEach(v -> csrfTokenRequest.addHeader(k, v)));

        //The service path gets appended to the destination URI inside the execute request
        final HttpResponse csrfResponse = httpClient.execute(csrfTokenRequest);
        final Header header = csrfResponse.getFirstHeader(X_CSRF_TOKEN_HEADER_KEY);
        if( header == null || header.getValue() == null ) {
            throw new NoSuchElementException("No CSRF token could be found in the response header.");
        }
        return header;
    }

    @Nonnull
    private CsrfToken convertHeaderToCsrfToken( @Nonnull final Header csrfHeader )
    {
        final String printableCsrfToken = new AsciiUtils().removeNonPrintableCharacters(csrfHeader.getValue());
        return new CsrfToken(printableCsrfToken);
    }
}
