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
     * Derives the service root URI from the full request URI by truncating the path at the first OData resource
     * segment. This matches the HC4 behavior where the CSRF token HEAD request was always sent to the service path root
     * rather than the specific resource path.
     * <p>
     * The service root is identified as the path up to and including the trailing slash before the first resource
     * segment. Example: {@code http://host/service/$batch} → {@code http://host/service/},
     * {@code http://host/service/Entity} → {@code http://host/service/}
     */
    @Nonnull
    static URI deriveServiceRootUri( @Nonnull final URI requestUri )
    {
        final String path = requestUri.getRawPath();
        // Service root is everything up to and including the trailing slash before the first resource segment.
        // Find the last '/' that is followed by at least one more character (i.e., there is a resource segment).
        final int lastSlash = path.lastIndexOf('/');
        // If the path ends with '/' already (e.g. "/service/"), use it as-is.
        // Otherwise, strip the last segment (e.g. "/service/Entity" -> "/service/", "/service/$batch" -> "/service/").
        final String servicePath =
            (lastSlash >= 0 && lastSlash < path.length() - 1) ? path.substring(0, lastSlash + 1) : path;
        try {
            return new URI(requestUri.getScheme(), requestUri.getAuthority(), servicePath, null, null);
        }
        catch( final Exception e ) {
            log.debug("Failed to derive service root URI, falling back to full request URI.", e);
            return requestUri;
        }
    }
}
