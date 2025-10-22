package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * A transparent proxy loader that enables routing traffic through a single registered gateway host.
 *
 * <p>
 * This class provides a mechanism to register a proxy gateway that will handle all destination requests transparently.
 * Once registered, all destination lookups will be routed through the configured gateway host and port.
 *
 * <p>
 * <strong>Key Features:</strong>
 * <ul>
 * <li>Single gateway registration - only one proxy can be registered at a time</li>
 * <li>Host validation - ensures hosts don't contain paths and are reachable</li>
 * <li>Automatic scheme normalization - defaults to HTTP if no scheme provided</li>
 * <li>Network connectivity validation before registration</li>
 * </ul>
 *
 * <p>
 * <strong>Usage Example:</strong>
 *
 * <pre>{@code
 * // Register with default port 80
 * TransparentProxy.register("gateway.svc.cluster.local");
 *
 * // Register with custom port
 * TransparentProxy.register("http://gateway.svc.cluster.local", 8080);
 * }</pre>
 *
 * <p>
 * <strong>Thread Safety:</strong> This class uses static state and is not thread-safe. Registration should be performed
 * during application initialization.
 *
 * @since 5.24.0
 */
@Slf4j
public class TransparentProxy implements DestinationLoader
{
    private static final String X_ERROR_INTERNAL_CODE_HEADER = "x-error-internal-code";
    private static final Integer DEFAULT_PORT = 80;
    private static final String SCHEME_SEPARATOR = "://";
    private static final String HTTP_SCHEME = org.apache.http.HttpHost.DEFAULT_SCHEME_NAME + SCHEME_SEPARATOR;
    private static final String PORT_SEPARATOR = ":";
    private static final String HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE =
        "Host '%s' contains a path '%s'. Paths are not allowed in host registration.";
    static String uri;
    static NetworkVerifier networkVerifier = new NetworkVerifier();

    /**
     * Registers a transparent proxy gateway using the default port 80.
     *
     * <p>
     * This method registers the specified host as a transparent proxy gateway that will handle all subsequent
     * destination requests. The host will be validated for reachability and must not contain any path components.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:80}
     *
     * @param host
     *            the gateway host to register (e.g., "gateway.svc.cluster.local") Must not contain paths or be null
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on port 80
     * @throws IllegalArgumentException
     *             if host is null
     * @see #register(String, Integer)
     */
    public static void register( @Nonnull final String host )
    {
        registerLoader(host, DEFAULT_PORT);
    }

    /**
     * Registers a transparent proxy gateway with a specified port.
     *
     * <p>
     * This method registers the specified host and port as a transparent proxy gateway that will handle all subsequent
     * destination requests. The host will be validated for reachability on the specified port and must not contain any
     * path components.
     *
     * <p>
     * If no scheme is provided, HTTP will be used by default. The final URI will be constructed as:
     * {@code <normalized-host>:<port>}
     *
     * @param host
     *            the gateway host to register (e.g., "gateway" or "<a href="http://gateway">...</a>") Must not contain
     *            paths or be null
     * @param port
     *            the port number to use for the gateway connection. Must not be null and should be a valid port number
     *            (1-65535)
     * @throws DestinationAccessException
     *             if the proxy is already registered, the host contains a path, or the host is not reachable on the
     *             specified port
     * @throws IllegalArgumentException
     *             if host or port is null
     * @see #register(String)
     */
    public static void register( @Nonnull final String host, @Nonnull final Integer port )
    {
        registerLoader(host, port);
    }

    private static void registerLoader( @Nonnull final String host, final Integer port )
    {
        if( uri != null ) {
            throw new DestinationAccessException(
                "TransparentProxy is already registered. Only one registration is allowed.");
        }

        try {
            final String normalizedHost = normalizeHostWithScheme(host);
            final String hostForVerification = getHostForVerification(host, normalizedHost);

            verifyHostConnectivity(hostForVerification, port);

            uri = String.format("%s%s%d", normalizedHost, PORT_SEPARATOR, port);
            DestinationAccessor.prependDestinationLoader(new TransparentProxy());

        }
        catch( final URISyntaxException e ) {
            throw new DestinationAccessException(
                String.format("Invalid host format: [%s]. Caused by: %s", host, e.getMessage()),
                e);
        }
    }

    @Nonnull
    private static String getHostForVerification( @Nonnull final String host, final String normalizedHost )
        throws URISyntaxException
    {
        final URI parsedUri = new URI(normalizedHost);

        final String path = parsedUri.getPath();
        if( path != null && !path.isEmpty() ) {
            throw new DestinationAccessException(String.format(HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE, host, path));
        }

        final String hostForVerification = parsedUri.getHost();
        if( hostForVerification == null ) {
            throw new DestinationAccessException(String.format("Invalid host format: [%s]", host));
        }
        return hostForVerification;
    }

    @Nonnull
    private static String normalizeHostWithScheme( @Nonnull final String host )
    {
        if( host.contains(SCHEME_SEPARATOR) ) {
            return host;
        }
        return HTTP_SCHEME + host;
    }

    private static void verifyHostConnectivity( @Nonnull final String host, final int port )
    {
        networkVerifier.verifyHostConnectivity(host, port);
    }

    /**
     * Verifies if the destination is found by making a HEAD HTTP request.
     *
     * @param destination
     *            the destination to use
     * @param destinationName
     *            the name of the destination to check
     * @return true if the destination is not found, false otherwise
     */
    private static
        boolean
        isDestinationNotFound( @Nonnull final TransparentProxyDestination destination, final String destinationName )
    {
        final HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);
        final URI destinationUri = destination.getUri();
        final HttpHead headRequest = new HttpHead(destinationUri);
        log.debug("Making HEAD request to check if destination {} is found...", destinationName);
        final HttpResponse response;
        try {
            response = httpClient.execute(headRequest);
        }
        catch( IOException e ) {
            log.debug("HEAD request to destination {} failed with exception: {}", destinationName, e.getMessage(), e);
            return false;
        }
        final int statusCode = response.getStatusLine().getStatusCode();

        boolean destinationNotFound = false;
        if( statusCode == HttpStatus.SC_BAD_GATEWAY ) {
            final String errorInternalCode = getHeaderValue(response, X_ERROR_INTERNAL_CODE_HEADER);
            if( Integer.toString(HttpStatus.SC_NOT_FOUND).equals(errorInternalCode) ) {
                destinationNotFound = true;
            }
        }

        log
            .debug(
                "HEAD request to destination {} returned status code: {}, x-error-internal-code: {}, found: {}",
                destinationName,
                statusCode,
                getHeaderValue(response, X_ERROR_INTERNAL_CODE_HEADER),
                !destinationNotFound);

        return destinationNotFound;
    }

    /**
     * Helper method to extract header value from HTTP message.
     *
     * @param message
     *            the HTTP message
     * @param headerName
     *            the name of the header to extract
     * @return the header value if present, "" otherwise
     */
    private static String getHeaderValue( @Nonnull final HttpMessage message, @Nonnull final String headerName )
    {
        if( message.containsHeader(headerName) ) {
            return message.getFirstHeader(headerName).getValue();
        }
        return "";
    }

    @Nonnull
    @Override
    public Try<Destination> tryGetDestination( @Nonnull final String destinationName )
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(destinationName, uri).build();

        if( isDestinationNotFound(destination, destinationName) ) {
            return Try.failure(new DestinationAccessException("Destination not found: " + destinationName));
        }

        return Try.success(destination);
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull DestinationOptions options )
    {
        final TransparentProxyDestination destination =
            TransparentProxyDestination.gateway(destinationName, uri).build();

        if( isDestinationNotFound(destination, destinationName) ) {
            return Try.failure(new DestinationAccessException("Destination not found: " + destinationName));
        }

        return Try.success(destination);
    }

}
