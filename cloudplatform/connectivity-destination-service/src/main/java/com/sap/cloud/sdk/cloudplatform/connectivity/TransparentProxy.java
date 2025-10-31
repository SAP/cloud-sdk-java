package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Try;

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
public class TransparentProxy implements DestinationLoader
{
    private static final Integer DEFAULT_PORT = 80;
    private static final String SCHEME_SEPARATOR = "://";
    private static final String HTTP_SCHEME = org.apache.http.HttpHost.DEFAULT_SCHEME_NAME + SCHEME_SEPARATOR;
    private static final String PATH_SEPARATOR = "/";
    private static final String PORT_SEPARATOR = ":";
    private static final String HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE =
        "Host '%s' contains a path '%s'. Paths are not allowed in host registration.";
    static String uri;
    static NetworkValidator networkValidator = new DefaultNetworkValidator();

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

        validateHostHasNoPath(host);

        final String normalizedHost = normalizeHostWithScheme(host);
        final String hostForVerification = extractHostForValidation(normalizedHost);

        verifyHostConnectivity(hostForVerification, port);

        uri = String.format("%s%s%d", normalizedHost, PORT_SEPARATOR, port);
        DestinationAccessor.prependDestinationLoader(new TransparentProxy());
    }

    @Nonnull
    private static String normalizeHostWithScheme( @Nonnull final String host )
    {
        if( host.contains(SCHEME_SEPARATOR) ) {
            return host;
        }
        return HTTP_SCHEME + host;
    }

    private static String extractHostForValidation( @Nonnull final String normalizedHost )
    {
        try {
            final URI uri = new URI(normalizedHost);
            return uri.getHost();
        }
        catch( final URISyntaxException e ) {
            String host = normalizedHost;
            if( host.contains(SCHEME_SEPARATOR) ) {
                host = host.substring(host.indexOf(SCHEME_SEPARATOR) + 3);
            }
            if( host.contains(PORT_SEPARATOR) ) {
                host = host.substring(0, host.indexOf(PORT_SEPARATOR));
            }
            return host;
        }
    }

    private static void verifyHostConnectivity( @Nonnull final String host, final int port )
    {
        networkValidator.verifyHostConnectivity(host, port);
    }

    private static void validateHostHasNoPath( @Nonnull final String host )
    {
        try {
            final URI uri = new URI(host.contains(SCHEME_SEPARATOR) ? host : HTTP_SCHEME + host);
            final String path = uri.getPath();
            if( path != null && !path.isEmpty() ) {
                throw new DestinationAccessException(
                    String.format(HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE, host, path));
            }
        }
        catch( final URISyntaxException e ) {
            final String hostToCheck =
                host.contains(SCHEME_SEPARATOR) ? host.substring(host.indexOf(SCHEME_SEPARATOR) + 3) : host;
            if( hostToCheck.contains(PATH_SEPARATOR) ) {
                final String pathPart = hostToCheck.substring(hostToCheck.indexOf(PATH_SEPARATOR));
                throw new DestinationAccessException(
                    String.format(HOST_CONTAINS_PATH_ERROR_MESSAGE_TEMPLATE, host, pathPart),
                    e);
            }
        }
    }

    @Nonnull
    @Override
    public Try<Destination> tryGetDestination( @Nonnull final String destinationName )
    {
        return Try.success(TransparentProxyDestination.gateway(destinationName, uri).build());
    }

    @Nonnull
    @Override
    public
        Try<Destination>
        tryGetDestination( @Nonnull final String destinationName, @Nonnull DestinationOptions options )
    {
        return Try.success(TransparentProxyDestination.gateway(destinationName, uri).build());
    }

}
