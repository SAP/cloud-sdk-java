package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.Credentials;

import io.vavr.control.Option;
import lombok.Data;

/**
 * Proxy configuration.
 */
@Data
public class ProxyConfiguration
{
    @Nonnull
    private final URI uri;

    @Nullable
    private final Credentials credentials;

    /**
     * Creates a configuration based on the given URI and without any credentials.
     *
     * @param uri
     *            The URI to initiate the configuration with.
     */
    public ProxyConfiguration( @Nonnull final URI uri )
    {
        this(uri, null);
    }

    /**
     * Creates a configuration based on the given URI and credentials.
     *
     * @param uri
     *            The URI to initiate the configuration with.
     * @param credentials
     *            The credentials that can be used to connect to a remote.
     */
    public ProxyConfiguration( @Nonnull final URI uri, @Nullable final Credentials credentials )
    {
        this.uri = uri;
        this.credentials = credentials;
    }

    /**
     * Getter for the credentials, wrapped in an {@link Option}, as it may be null.
     *
     * @return The credentials of this configuration.
     */
    @Nonnull
    public Option<Credentials> getCredentials()
    {
        return Option.of(credentials);
    }

    /**
     * Static factory to create a configuration based on an URI and credentials.
     *
     * @param uri
     *            The URI to initiate the configuration with.
     * @param credentials
     *            The credentials that can be used to connect to a remote.
     *
     * @return A configuration based on the given parameter.
     */
    @Nonnull
    public static ProxyConfiguration of( @Nonnull final URI uri, @Nullable final Credentials credentials )
    {
        return new ProxyConfiguration(uri, credentials);
    }

    /**
     * Static factory to create a configuration solely based on an URI.
     *
     * @param uri
     *            The URI to initiate the configuration with.
     *
     * @return A configuration based on the given URI.
     */
    @Nonnull
    public static ProxyConfiguration of( @Nonnull final URI uri )
    {
        return new ProxyConfiguration(uri);
    }

    /**
     * Static factory to create a configuration based on an URI (as a String) and credentials.
     *
     * @param uri
     *            The URI string to initiate the configuration with.
     * @param credentials
     *            The credentials that can be used to connect to a remote.
     *
     * @return A configuration based on the given parameter.
     *
     * @throws IllegalArgumentException
     *             if the URI String cannot be parsed as an URI.
     */
    @Nonnull
    public static ProxyConfiguration of( @Nonnull final String uri, @Nullable final Credentials credentials )
        throws IllegalArgumentException
    {
        return new ProxyConfiguration(URI.create(uri), credentials);
    }

    /**
     * Static factory to create a configuration bsaed on an URI (as a String).
     *
     * @param uri
     *            The URI string to initiate the configuration with.
     *
     * @return A configuration based on the given URI.
     *
     * @throws IllegalArgumentException
     *             if the URI String cannot be parsed as an URI.
     */
    @Nonnull
    public static ProxyConfiguration of( @Nonnull final String uri )
        throws IllegalArgumentException
    {
        return new ProxyConfiguration(URI.create(uri));
    }
}
