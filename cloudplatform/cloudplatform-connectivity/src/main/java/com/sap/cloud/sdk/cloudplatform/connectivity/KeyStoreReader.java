/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.io.pem.PemObject;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor( access = AccessLevel.PRIVATE )
class KeyStoreReader
{
    private static final String MSG_CERT = "Provided certificate data did not contain any valid X.509 certificates.";
    private static final String MSG_KEY = "Provided key data did not contain a valid PEM key";

    @Nonnull
    static KeyStore createKeyStore(
        @Nonnull final String alias,
        @Nonnull final char[] password,
        @Nonnull final Reader certReader,
        @Nonnull final Reader keyReader )
        throws KeyStoreException,
            CertificateException,
            IOException,
            NoSuchAlgorithmException
    {
        final Certificate[] clientCertificates =
            Try.of(() -> loadCertificates(certReader)).getOrElseThrow(e -> new DestinationAccessException(MSG_CERT, e));
        final PrivateKey privateKey =
            Try.of(() -> loadKey(keyReader, password)).getOrElseThrow(e -> new DestinationAccessException(MSG_KEY, e));
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null);
        keyStore.setKeyEntry(alias, privateKey, password, clientCertificates);
        return keyStore;
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
            throw new IllegalArgumentException(
                "Provided certificate data did not contain any valid X.509 certificates.");
        }
        return certs.toArray(new Certificate[0]);
    }

    @Nonnull
    static PrivateKey loadKey( @Nonnull final Reader keyReader, @Nullable final char[] password )
        throws IOException,
            OperatorCreationException,
            PKCSException
    {
        try( PEMParser pemParser = new PEMParser(keyReader) ) {
            final Object raw = pemParser.readObject();
            if( raw instanceof PEMKeyPair ) {
                return new JcaPEMKeyConverter().getKeyPair((PEMKeyPair) raw).getPrivate();
            }
            if( raw instanceof PrivateKey ) {
                return (PrivateKey) raw;
            }
            if( raw instanceof PKCS8EncryptedPrivateKeyInfo ) {
                final InputDecryptorProvider c = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password);
                final PrivateKeyInfo privateKeyInfo = ((PKCS8EncryptedPrivateKeyInfo) raw).decryptPrivateKeyInfo(c);
                return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
            }
            if( raw instanceof PrivateKeyInfo ) {
                return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) raw);
            }
            throw new IllegalArgumentException("Provided key data did not contain a valid PEM key.");
        }
    }
}
