/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.security.config.ClientCertificate;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.mtls.SSLContextFactory;

/**
 * Factory for creating Apache HttpClient 5 instances from {@link ClientIdentity}.
 * <p>
 * This factory handles the creation of HttpClient 5 instances configured with the appropriate SSL context based on the
 * provided client identity, supporting both client secret and client certificate authentication.
 */
class HttpClient5Factory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient5Factory.class);
    private static final char[] NO_PASSWORD = "".toCharArray();

    /**
     * Creates a new {@link CloseableHttpClient} configured with the given {@link ClientIdentity}.
     *
     * @param identity
     *            The client identity containing credentials (either client secret or client certificate).
     * @return A configured {@link CloseableHttpClient} instance.
     * @throws HttpClient5FactoryException
     *             If there is an error creating the HTTP client.
     */
    @Nonnull
    static CloseableHttpClient create( @Nonnull final ClientIdentity identity )
        throws HttpClient5FactoryException
    {
        return create(identity, null);
    }

    /**
     * Creates a new {@link CloseableHttpClient} configured with the given {@link ClientIdentity} and optional
     * {@link KeyStore}.
     *
     * @param identity
     *            The client identity containing credentials (either client secret or client certificate).
     * @param keyStore
     *            Optional KeyStore to use for mTLS. If provided, this takes precedence over the identity's certificate.
     * @return A configured {@link CloseableHttpClient} instance.
     * @throws HttpClient5FactoryException
     *             If there is an error creating the HTTP client.
     */
    @Nonnull
    static CloseableHttpClient create( @Nonnull final ClientIdentity identity, @Nullable final KeyStore keyStore )
        throws HttpClient5FactoryException
    {
        try {
            final HttpClientConnectionManager connectionManager = createConnectionManager(identity, keyStore);
            return HttpClients.custom().useSystemProperties().setConnectionManager(connectionManager).build();
        }
        catch( final GeneralSecurityException | IOException e ) {
            throw new HttpClient5FactoryException("Failed to create HttpClient5 for ClientIdentity", e);
        }
    }

    @Nonnull
    private static
        HttpClientConnectionManager
        createConnectionManager( @Nonnull final ClientIdentity identity, @Nullable final KeyStore keyStore )
            throws GeneralSecurityException,
                IOException
    {
        final TlsSocketStrategy tlsSocketStrategy = createTlsSocketStrategy(identity, keyStore);

        return PoolingHttpClientConnectionManagerBuilder.create().setTlsSocketStrategy(tlsSocketStrategy).build();
    }

    @Nonnull
    private static
        TlsSocketStrategy
        createTlsSocketStrategy( @Nonnull final ClientIdentity identity, @Nullable final KeyStore keyStore )
            throws GeneralSecurityException,
                IOException
    {
        final SSLContext sslContext = createSSLContext(identity, keyStore);
        return new DefaultClientTlsStrategy(sslContext);
    }

    @Nonnull
    private static
        SSLContext
        createSSLContext( @Nonnull final ClientIdentity identity, @Nullable final KeyStore keyStore )
            throws GeneralSecurityException,
                IOException
    {
        // If a KeyStore is provided directly (e.g., for ZTIS), use it for mTLS
        if( keyStore != null ) {
            LOGGER.debug("Creating SSL context with provided KeyStore for identity: {}", identity.getId());
            return createSSLContextFromKeyStore(keyStore);
        }

        // If the identity is certificate-based and has valid certificate data, use SSLContextFactory
        if( identity.isCertificateBased() && hasCertificateData(identity) ) {
            LOGGER.debug("Creating SSL context with client certificate for identity: {}", identity.getId());
            return SSLContextFactory.getInstance().create(identity);
        }

        // For non-certificate-based identities or identities without certificate data, return a default SSL context
        LOGGER.debug("Creating default SSL context for identity: {}", identity.getId());
        return SSLContext.getDefault();
    }

    /**
     * Checks if the identity has valid certificate data that can be used by SSLContextFactory. This excludes identities
     * that have a KeyStore directly (like ZtisClientIdentity) which should be handled separately.
     */
    private static boolean hasCertificateData( @Nonnull final ClientIdentity identity )
    {
        // ZtisClientIdentity has a KeyStore but no PEM certificate data - it should be handled separately
        // by passing the KeyStore directly to HttpClient5Factory.create(identity, keyStore)
        if( identity instanceof SecurityLibWorkarounds.ZtisClientIdentity ) {
            return false;
        }

        // Check for PEM string certificate data
        if( identity instanceof ClientCertificate clientCertificate ) {
            final String cert = clientCertificate.getCertificate();
            final String key = clientCertificate.getKey();
            if( cert != null && !cert.isBlank() && key != null && !key.isBlank() ) {
                return true;
            }
        }

        // Check for pre-parsed certificate chain and private key
        if( identity.getCertificateChain() != null && identity.getPrivateKey() != null ) {
            return true;
        }

        // Check if identity has certificate/key via the base interface methods
        // This handles cases where the identity is not a ClientCertificate but still has certificate data
        final String cert = identity.getCertificate();
        final String key = identity.getKey();
        if( cert != null && !cert.isBlank() && key != null && !key.isBlank() ) {
            return true;
        }

        return false;
    }

    @Nonnull
    private static SSLContext createSSLContextFromKeyStore( @Nonnull final KeyStore keyStore )
        throws GeneralSecurityException
    {
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, NO_PASSWORD);
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    /**
     * Exception thrown when there is an error creating an HttpClient5 instance.
     */
    static class HttpClient5FactoryException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        HttpClient5FactoryException( final String message, final Throwable cause )
        {
            super(message, cause);
        }
    }
}
