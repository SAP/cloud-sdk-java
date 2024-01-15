/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

import org.apache.hc.core5.ssl.SSLContextBuilder;

import com.sap.cloud.sdk.cloudplatform.PlatformSslContextProvider;

import io.vavr.control.Try;

/**
 * Abstract implementation that creates an {@link SSLContext} based on an X509 certificate and private key.
 */
abstract class AbstractX509SslContextProvider implements PlatformSslContextProvider
{
    private static final KeyStoreReader KEY_STORE_READER = new KeyStoreReader();

    /**
     * Convenience for {@code tryGetContext(new StringReader(cert), new StringReader(key))}
     *
     * @see AbstractX509SslContextProvider#tryGetContext(Reader, Reader)
     *
     * @param cert
     *            A string containing the certificates.
     * @param key
     *            A string containing the key
     * @return A {@link Try} containing either the derived SSL context or a parsing failure.
     */
    @Nonnull
    Try<SSLContext> tryGetContext( @Nonnull final String cert, @Nonnull final String key )
    {
        return tryGetContext(new StringReader(cert), new StringReader(key));
    }

    /**
     * Creates an {@link SSLContext} based on an X509 certificate and private key.
     *
     * @param certReader
     *            {@link Reader} containing the certificate(s).
     * @param keyReader
     *            {@link Reader} containing the private key.
     * @return A {@link Try} containing either the derived SSL context or a parsing failure.
     */
    @Nonnull
    Try<SSLContext> tryGetContext( @Nonnull final Reader certReader, @Nonnull final Reader keyReader )
    {
        final SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
        return Try
            .of(() -> KEY_STORE_READER.createKeyStore(certReader, keyReader))
            .mapTry(k -> sslContextBuilder.loadKeyMaterial(k, KEY_STORE_READER.password))
            .mapTry(SSLContextBuilder::build);
    }
}
