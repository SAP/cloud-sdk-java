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
    private static final String DEFAULT_ALIAS = "instance-identity";
    private static final char[] DEFAULT_PASSWORD = "changeit".toCharArray();

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
            .of(() -> KeyStoreReader.createKeyStore(DEFAULT_ALIAS, DEFAULT_PASSWORD, certReader, keyReader))
            .mapTry(k -> sslContextBuilder.loadKeyMaterial(k, DEFAULT_PASSWORD))
            .mapTry(SSLContextBuilder::build);
    }
}
