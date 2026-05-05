package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class CsrfTokenInterceptor implements HttpRequestInterceptor
{
    static final String X_CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    private static final String X_CSRF_TOKEN_FETCH_VALUE = "fetch";

    private static final Set<String> MUTATING_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");
    private static final String NON_PRINTABLE_CHARS = "[^ -~]";

    @Nonnull
    private final HttpClient httpClient;

    @Override
    public
        void
        process( @Nonnull final HttpRequest request, final EntityDetails entityDetails, final HttpContext context )
            throws HttpException,
                IOException
    {
        if( !MUTATING_METHODS.contains(request.getMethod().toUpperCase()) ) {
            return;
        }

        if( request.containsHeader(X_CSRF_TOKEN_HEADER_KEY) ) {
            log.debug("CSRF token already present in request, skipping retrieval.");
            return;
        }

        final URI requestUri;
        try {
            requestUri = request.getUri();
        }
        catch( final Exception e ) {
            log.debug("Failed to determine request URI for CSRF token fetch, skipping.", e);
            return;
        }

        final HttpHead headRequest = new HttpHead(requestUri);
        headRequest.addHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_FETCH_VALUE);

        try {
            final String token = httpClient.execute(headRequest, response -> {
                final Header header = response.getFirstHeader(X_CSRF_TOKEN_HEADER_KEY);
                if( header == null || header.getValue() == null ) {
                    log
                        .debug(
                            "Target system did not respond with a {} header. "
                                + "The subsequent request may fail if a CSRF token is required.",
                            X_CSRF_TOKEN_HEADER_KEY);
                    return null;
                }
                return header.getValue().replaceAll(NON_PRINTABLE_CHARS, "");
            });

            if( token != null ) {
                log.debug("Successfully retrieved CSRF token, adding to request.");
                request.addHeader(X_CSRF_TOKEN_HEADER_KEY, token);
            }
        }
        catch( final Exception e ) {
            log
                .debug(
                    "CSRF token retrieval failed: the HEAD request was not successful. "
                        + "The subsequent request may fail if a CSRF token is required.",
                    e);
        }
    }

    /**
     * Returns the request methods for which this interceptor will attempt to fetch a CSRF token. Used for testing.
     */
    static Set<String> getMutatingMethods()
    {
        return MUTATING_METHODS;
    }
}
