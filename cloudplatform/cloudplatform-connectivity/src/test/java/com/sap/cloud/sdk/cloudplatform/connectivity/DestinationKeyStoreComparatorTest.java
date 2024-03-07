/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.OptionalInt;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class DestinationKeyStoreComparatorTest
{

    @SneakyThrows
    @Test
    void testHashCodeEmptyKeyStore()
    {
        final KeyPair keyPair = generateKeyPair();

        // empty jks, not loaded
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            final OptionalInt result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isEmpty();
        }
        // empty jks, loaded
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            final OptionalInt result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).hasValue(17);
        }
        // jks with single certificate
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setCertificateEntry("a", generateCertificate(keyPair, "a"));
            final OptionalInt result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isNotEmpty().isNotEqualTo(OptionalInt.of(17));
        }
        // jks with single certificate+key
        {
            final Certificate cert = generateCertificate(keyPair, "a");
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setKeyEntry("a", keyPair.getPrivate(), new char[0], new Certificate[] { cert });
            final OptionalInt result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isNotEmpty().isNotEqualTo(OptionalInt.of(17));
        }
    }

    @SneakyThrows
    static KeyPair generateKeyPair()
    {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    @SneakyThrows
    static Certificate generateCertificate( final KeyPair keyPair, final String subject )
    {
        final long now = System.currentTimeMillis();
        final X500Name name = new X500Name("CN=" + subject);
        final BigInteger serial = new BigInteger(Long.toString(now));
        final Date startDate = new Date(now);
        final Date endDate = new Date(now + 3600_000);

        final JcaX509v3CertificateBuilder certBuilder =
            new JcaX509v3CertificateBuilder(name, serial, startDate, endDate, name, keyPair.getPublic());
        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));

        final Provider prov = new BouncyCastleProvider();
        Security.addProvider(prov);
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().setProvider(prov).getCertificate(certBuilder.build(contentSigner));
    }
}
