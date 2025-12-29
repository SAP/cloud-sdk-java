package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import lombok.extern.slf4j.Slf4j;

/**
 * NetworkVerifier that performs TCP socket-based connectivity verification.
 *
 * <p>
 * This implementation uses standard Java socket connections to verify that a remote host and port combination is
 * reachable and accepting connections. It establishes a temporary TCP connection to the target endpoint and immediately
 * closes it upon successful establishment.
 *
 * <p>
 * <strong>Verification Process:</strong>
 * <ol>
 * <li>Creates a new TCP socket</li>
 * <li>Attempts to connect to the specified host and port with a timeout</li>
 * <li>Immediately closes the connection if successful</li>
 * <li>Throws appropriate exceptions for failures</li>
 * </ol>
 *
 * <p>
 * <strong>Timeout Configuration:</strong> The verification uses a fixed timeout of {@value #HOST_REACH_TIMEOUT}
 * milliseconds to prevent indefinite blocking on unreachable endpoints.
 *
 * <p>
 * <strong>Error Handling:</strong>
 * <ul>
 * <li>{@link UnknownHostException} - Host cannot be resolved to an IP address</li>
 * <li>{@link IOException} - Network connectivity issues or connection refused</li>
 * </ul>
 *
 * @see TransparentProxy
 * @since 5.24.0
 */
@Slf4j
class NetworkVerifier
{
    private static final int HOST_REACH_TIMEOUT = 5000;

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation creates a TCP socket connection to the specified host and port to verify connectivity. The
     * connection is immediately closed after successful establishment.
     *
     * @param host
     *            {@inheritDoc}
     * @param port
     *            {@inheritDoc}
     * @throws DestinationAccessException
     *             {@inheritDoc}
     *             <p>
     *             Specific error conditions:
     *             <ul>
     *             <li>Host resolution failure - when DNS lookup fails</li>
     *             <li>Connection failure - when host is unreachable or port is closed</li>
     *             <li>Network timeouts - when connection attempt exceeds timeout</li>
     *             </ul>
     */
    void verifyHostConnectivity( @Nonnull final String host, final int port )
        throws DestinationAccessException
    {
        log.info("Verifying that transparent proxy host is reachable on {}:{}", host, port);
        try( Socket socket = new Socket() ) {
            socket.connect(new InetSocketAddress(host, port), HOST_REACH_TIMEOUT);
            log.info("Successfully verified that transparent proxy host is reachable on {}:{}", host, port);
        }
        catch( final UnknownHostException e ) {
            throw new DestinationAccessException(
                String.format("Host [%s] could not be resolved. Caused by: %s", host, e.getMessage()),
                e);
        }
        catch( final IOException e ) {
            throw new DestinationAccessException(
                String.format("Host [%s] on port [%d] is not reachable. Caused by: %s", host, port, e.getMessage()),
                e);
        }
    }
}
