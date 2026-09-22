package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.regex.Pattern;

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
    private static final Pattern NON_PRINTABLE_CHARS = Pattern.compile("[^ -~]");

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

        final URI csrfFetchUri = deriveServiceRootUri(requestUri);
        final HttpHead headRequest = new HttpHead(csrfFetchUri);
        headRequest.addHeader(X_CSRF_TOKEN_HEADER_KEY, X_CSRF_TOKEN_FETCH_VALUE);

        try {
            final String token = httpClient.execute(headRequest, response -> {
                final Header header = response.getFirstHeader(X_CSRF_TOKEN_HEADER_KEY);
                if( header == null || header.getValue() == null ) {
                    log
                        .warn(
                            "Target system did not respond with a {} header. "
                                + "The subsequent request may fail if a CSRF token is required.",
                            X_CSRF_TOKEN_HEADER_KEY);
                    return null;
                }
                return NON_PRINTABLE_CHARS.matcher(header.getValue()).replaceAll("");
            });

            if( token != null ) {
                log.debug("Successfully retrieved CSRF token, adding to request.");
                request.addHeader(X_CSRF_TOKEN_HEADER_KEY, token);
            }
        }
        catch( final Exception e ) {
            log
                .warn(
                    "CSRF token retrieval failed: the HEAD request was not successful. "
                        + "The subsequent request may fail if a CSRF token is required.",
                    e);
        }
    }

    /**
     * Derives the service root URI from the full request URI to send the CSRF token HEAD request.
     * <p>
     * The service root is the path prefix up to and including the slash that precedes the first OData resource segment.
     * A resource segment is identified by the presence of a key predicate ({@code (}) — the slash immediately before
     * the first {@code (} marks the boundary between the service path and the first entity set name. For paths without
     * a key predicate the last path segment is stripped instead.
     * <p>
     * Examples:
     * <ul>
     * <li>{@code /service/Entity} → {@code /service/}
     * <li>{@code /service/$batch} → {@code /service/}
     * <li>{@code /service/Entity('key')} → {@code /service/}
     * <li>{@code /service/Entity('key')/NavigationProperty} → {@code /service/}
     * <li>{@code /service/Entity('key')/NavigationProperty(42)} → {@code /service/}
     * </ul>
     */
    @Nonnull
    static URI deriveServiceRootUri( @Nonnull final URI requestUri )
    {
        final String path = requestUri.getRawPath();
        final String servicePath;

        final int firstParen = path.indexOf('(');
        if( firstParen > 0 ) {
            // A key predicate is present. The service root ends at the slash immediately before the first '(',
            // i.e. before the entity set name. This correctly handles navigation property paths such as
            // /service/Entity('key')/NavProp and /service/Entity('key')/NavProp(42).
            final int slashBeforeEntity = path.lastIndexOf('/', firstParen);
            servicePath = slashBeforeEntity >= 0 ? path.substring(0, slashBeforeEntity + 1) : path;
        } else {
            // No key predicate — strip the last path segment.
            // Handles /service/Entity -> /service/ and /service/$batch -> /service/.
            // Also handles paths that already end with '/' (e.g. /service/) by leaving them unchanged.
            final int lastSlash = path.lastIndexOf('/');
            servicePath = (lastSlash >= 0 && lastSlash < path.length() - 1) ? path.substring(0, lastSlash + 1) : path;
        }

        try {
            return new URI(requestUri.getScheme(), requestUri.getAuthority(), servicePath, null, null);
        }
        catch( final Exception e ) {
            log.debug("Failed to derive service root URI, falling back to full request URI.", e);
            return requestUri;
        }
    }
}
