/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContextBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;

import com.sap.cloud.sdk.cloudplatform.PlatformSslContextProvider;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Try;

/**
 * Abstract implementation that creates an {@link SSLContext} based on an X509 certificate and private key.
 */
abstract class AbstractX509SslContextProvider implements PlatformSslContextProvider
{
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

        final Certificate[] clientCertificates;
        final PrivateKey privateKey;

        try {
            clientCertificates = loadCertificates(certReader);
        }
        catch( final Exception e ) {
            return Try.failure(new CloudPlatformException("Failed to load platform certificate", e));
        }

        try {
            privateKey = loadPrivateKey(keyReader);
        }
        catch( final Exception e ) {
            return Try.failure(new CloudPlatformException("Failed to load platform key", e));
        }

        return Try
            .of(() -> KeyStore.getInstance("JKS"))
            .andThenTry(k -> k.load(null))
            .andThenTry(
                k -> k.setKeyEntry("instance-identity", privateKey, "changeit".toCharArray(), clientCertificates))
            .mapTry(k -> sslContextBuilder.loadKeyMaterial(k, "changeit".toCharArray()))
            .mapTry(SSLContextBuilder::build);
    }

    @Nonnull
    static Certificate[] loadCertificates( @Nonnull final Reader certReader )
        throws CertificateException,
            IOException
    {
        final List<Certificate> certs = new ArrayList<>();
        final CertificateFactory factory = CertificateFactory.getInstance("X509");

        try( PEMParser pemParser = new PEMParser(certReader) ) {
            PemObject object;
            while( (object = pemParser.readPemObject()) != null ) {
                if( !object.getType().equals("CERTIFICATE") ) {
                    continue;
                }
                certs.add(factory.generateCertificate(new ByteArrayInputStream(object.getContent())));
            }
        }
        if( certs.isEmpty() ) {
            throw new CloudPlatformException("Provided certificate data did not contain any valid X.509 certificates.");
        }
        return certs.toArray(new Certificate[0]);
    }

    @Nonnull
    static PrivateKey loadPrivateKey( @Nonnull final Reader keyReader )
        throws IOException
    {
        try( PEMParser pemParser = new PEMParser(keyReader) ) {
            final PEMKeyPair keyPair = (PEMKeyPair) pemParser.readObject();
            if( keyPair == null ) {
                throw new CloudPlatformException("Provided key data did not contain a valid PEM key.");
            }
            return new JcaPEMKeyConverter().getKeyPair(keyPair).getPrivate();
        }
    }
}
