package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;
import java.security.KeyStore;
import java.util.Collection;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.security.BasicCredentials;

import io.vavr.control.Option;

/**
 * Adds HTTP relevant fields to the "generic" destination.
 */
public interface HttpDestinationProperties extends Destination
{
    /**
     * Getter for the {@link URI} to communicate with.
     * <p>
     * This is a mandatory property of a HttpDestination.
     *
     * @return The {@code URI} to be used with this destination.
     */
    @Nonnull
    URI getUri();

    /**
     * Getter for a collection of headers that should be added to the outgoing request for this destination.
     *
     * @param requestUri
     *            The target URI of a request to which HTTP headers should be added.
     * @return A collection with all headers to be used when communicating with the target of the destination.
     */
    @Nonnull
    Collection<Header> getHeaders( @Nonnull final URI requestUri );

    /**
     * Convenience method to get the headers for a request against the destination's URI.
     *
     * @return the headers of this destination.
     * @see #getHeaders(URI)
     */
    @Nonnull
    default Collection<Header> getHeaders()
    {
        return getHeaders(getUri());
    }

    /**
     * The TLS version to be used when communicating over HTTP.
     *
     * @return An {@link Option} wrapping the TLS version to use, if any.
     */
    @Nonnull
    Option<String> getTlsVersion();

    /**
     * The {@link ProxyConfiguration} to be used when communicating over HTTP.
     *
     * @return An {@link Option} wrapping the {@code ProxyConfiguration} to use, if any.
     */
    @Nonnull
    Option<ProxyConfiguration> getProxyConfiguration();

    /**
     * The KeyStore to be used when communicating over HTTP.
     *
     * @return An {@link Option} wrapping the KeyStore to use, if any.
     */
    @Nonnull
    Option<KeyStore> getKeyStore();

    /**
     * The password for the Key Store to be used when communicating over HTTP.
     *
     * @return An {@link Option} wrapping the password to use, if any.
     */
    @Nonnull
    Option<String> getKeyStorePassword();

    /**
     * Indicates whether all server certificates should be accepted when communicating over HTTP.
     *
     * @return {@code true} if all certificates should be accepted, {@code false} otherwise.
     */
    boolean isTrustingAllCertificates();

    /**
     * Returns the basic credentials to be used for authentication at the remote system.
     *
     * @return An {@link Option} wrapping the {@link BasicCredentials} to use, if any.
     */
    @Nonnull
    Option<BasicCredentials> getBasicCredentials();

    /**
     * Returns the authentication type to be expected when authenticating at the remote system.
     *
     * @return This destination authentication type.
     */
    @Nonnull
    AuthenticationType getAuthenticationType();

    /**
     * Returns the {@link ProxyType} that is configured for this destination.
     *
     * @return An {@link Option} wrapping the {@link ProxyType} to use, if any.
     */
    @Nonnull
    Option<ProxyType> getProxyType();

    /**
     * Returns the optional trust store of the destination.
     *
     * @return The optional trust store of the destination.
     * @throws DestinationAccessException
     *             If there is an issue accessing the trust store.
     */
    @Nonnull
    Option<KeyStore> getTrustStore();

    /**
     * Returns the optional trust store password of the destination.
     *
     * @return The optional trust store password of the destination.
     */
    @Nonnull
    Option<String> getTrustStorePassword();

    /**
     * Defines from how the {@link javax.net.ssl.SSLContext} for outbound HTTP calls via this destination is determined
     * from.
     *
     * @return The optional {@link SecurityConfigurationStrategy} of the destination
     */
    @Nonnull
    default SecurityConfigurationStrategy getSecurityConfigurationStrategy()
    {
        return SecurityConfigurationStrategy.getDefault();
    }
}
