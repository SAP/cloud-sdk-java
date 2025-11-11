package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

/**
 * Abstract class for validating network connectivity to remote hosts and ports.
 *
 * <p>
 * This abstract class defines the contract for network validation implementations used by transparent proxy components
 * to ensure that registered hosts are reachable before completing the registration process.
 *
 * <p>
 * Implementations should perform actual network connectivity tests to verify that the specified host and port
 * combination is accessible from the current environment. This validation helps prevent registration of unreachable
 * endpoints that would cause runtime failures.
 *
 * <p>
 * <strong>Usage Context:</strong> This abstract class is primarily used by {@link TransparentProxy} during host
 * registration to validate network connectivity before accepting the registration.
 *
 * @see DefaultNetworkValidator
 * @see TransparentProxy
 * @since 5.24.0
 */
abstract class NetworkValidator
{
    /**
     * Validates connectivity to the specified host and port combination.
     *
     * <p>
     * This method should attempt to establish a network connection to the given host and port to verify that the
     * endpoint is reachable and accepting connections. The validation should complete within a reasonable timeout
     * period.
     *
     * <p>
     * If the host cannot be resolved or the connection cannot be established, this method should throw a
     * {@link DestinationAccessException} with descriptive error information.
     *
     * @param host
     *            the hostname or IP address to validate connectivity to. Must not be null or empty
     * @param port
     *            the port number to test connectivity on. Should be a valid port number (1-65535)
     * @throws DestinationAccessException
     *             if the host cannot be resolved, the connection cannot be established, or any other network-related
     *             validation failure occurs
     * @throws IllegalArgumentException
     *             if host is null or port is invalid
     */
    abstract void verifyHostConnectivity( @Nonnull String host, int port )
        throws DestinationAccessException;
}
