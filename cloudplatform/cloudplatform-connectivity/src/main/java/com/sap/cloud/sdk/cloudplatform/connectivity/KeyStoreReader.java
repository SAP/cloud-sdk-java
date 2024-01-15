/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
class KeyStoreReader
{
    @Builder.Default
    @Nonnull
    final String alias = "instance-identity";

    @Builder.Default
    @Nullable
    final char[] password = "changeit".toCharArray();

    @Builder.Default
    @Nonnull
    final Function<Throwable, Certificate[]> fallbackCertificates = ( e ) -> {
        throw new CloudPlatformException("Provided certificate data did not contain any valid X.509 certificates.", e);
    };

    @Builder.Default
    @Nonnull
    final Function<Throwable, PrivateKey> fallbackPrivateKey = ( e ) -> {
        throw new CloudPlatformException("Provided key data did not contain a valid PEM key.", e);
    };

    @Nonnull
    KeyStore createKeyStore( @Nonnull final Reader certReader, @Nonnull final Reader keyReader )
        throws Exception
    {
        final Certificate[] clientCertificates =
            Try.of(() -> loadCertificates(certReader)).getOrElseGet(fallbackCertificates);
        final PrivateKey privateKey = Try.of(() -> loadPrivateKey(keyReader)).getOrElseGet(fallbackPrivateKey);
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
