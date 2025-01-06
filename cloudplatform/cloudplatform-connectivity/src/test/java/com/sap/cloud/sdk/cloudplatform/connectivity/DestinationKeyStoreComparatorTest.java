/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.INITIAL_HASH_CODE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
    void testResolveKeyStoreHashCode()
    {
        final KeyPair keyPair = generateKeyPair();
        final Certificate cert = generateCertificate(keyPair, "a");
        final SecretKey secretKey = generateSecretKey();

        // empty JKS, not loaded -> DEFAULT HASH
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");

            final int result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isEqualTo(INITIAL_HASH_CODE);
        }
        // empty JKS, loaded -> DEFAULT HASH
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);

            final int result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isEqualTo(INITIAL_HASH_CODE);
        }
        // JKS with single certificate -> DYNAMIC HASH
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setCertificateEntry("a", cert);

            final int result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isNotEqualTo(INITIAL_HASH_CODE);
        }
        // JKS with single certificate+key -> DYNAMIC HASH
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setKeyEntry("a", keyPair.getPrivate(), new char[0], new Certificate[] { cert });

            final int result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isNotEqualTo(INITIAL_HASH_CODE);
        }
        // JCEKS with single key -> DEFAULT HASH
        {
            final KeyStore keyStore = KeyStore.getInstance("JCEKS"); // JKS doesn't allow for keys without certs
            keyStore.load(null);
            final KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection("pass".toCharArray());
            final KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry("a", entry, param);

            final int result = DestinationKeyStoreComparator.resolveKeyStoreHashCode(keyStore);
            assertThat(result).isEqualTo(INITIAL_HASH_CODE);
        }
    }

    @SneakyThrows
    @Test
    void testResolveCertificatesOnly()
    {
        final KeyPair keyPair = generateKeyPair();
        final Certificate cert = generateCertificate(keyPair, "a");
        final SecretKey secretKey = generateSecretKey();

        // empty JKS, not loaded -> NO ELEMENTS
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");

            final Certificate[] result = DestinationKeyStoreComparator.resolveCertificatesOnly(keyStore);
            assertThat(result).isEmpty();
        }
        // empty JKS, loaded -> NO ELEMENTS
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);

            final Certificate[] result = DestinationKeyStoreComparator.resolveCertificatesOnly(keyStore);
            assertThat(result).isEmpty();
        }
        // JKS with single certificate -> ONE ELEMENT
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setCertificateEntry("a", cert);

            final Certificate[] result = DestinationKeyStoreComparator.resolveCertificatesOnly(keyStore);
            assertThat(result).containsExactly(cert);
        }
        // JKS with single certificate+key -> ONE ELEMENT
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setKeyEntry("a", keyPair.getPrivate(), new char[0], new Certificate[] { cert });

            final Certificate[] result = DestinationKeyStoreComparator.resolveCertificatesOnly(keyStore);
            assertThat(result).containsExactly(cert);
        }
        // JCEKS with single key -> NO ELEMENTS
        {
            final KeyStore keyStore = KeyStore.getInstance("JCEKS"); // JKS doesn't allow for keys without certs
            keyStore.load(null);
            final KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection("pass".toCharArray());
            final KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry("a", entry, param);

            final Certificate[] result = DestinationKeyStoreComparator.resolveCertificatesOnly(keyStore);
            assertThat(result).isEmpty();
        }
    }

    @Test // sanity-check
    void testEqualsBehavior()
    {
        final KeyPair keyPair = generateKeyPair();
        final Certificate cert1 = generateCertificate(keyPair, "a");
        final Certificate cert2 = generateCertificate(keyPair, "b");

        assertThat(cert1).isNotEqualTo(cert2);
    }

    @SneakyThrows
    static KeyPair generateKeyPair()
    {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    @SneakyThrows
    static SecretKey generateSecretKey()
    {
        final KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        return kg.generateKey();
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
