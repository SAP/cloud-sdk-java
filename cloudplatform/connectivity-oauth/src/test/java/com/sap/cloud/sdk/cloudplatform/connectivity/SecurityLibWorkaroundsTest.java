package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;
import com.sap.cloud.security.config.CredentialType;

import lombok.SneakyThrows;

class SecurityLibWorkaroundsTest
{
    @Test
    void testZtisClientIdentityImplementsEquals()
    {
        final ZtisClientIdentity sut = new ZtisClientIdentity("id", mock(KeyStore.class));

        assertThat(sut).isEqualTo(new ZtisClientIdentity("id", mock(KeyStore.class)));
        assertThat(sut).hasSameHashCodeAs(new ZtisClientIdentity("id", mock(KeyStore.class)));
        assertThat(sut).isNotEqualTo(new ZtisClientIdentity("id2", mock(KeyStore.class)));
        assertThat(sut).doesNotHaveSameHashCodeAs(new ZtisClientIdentity("id2", mock(KeyStore.class)));
    }

    @SneakyThrows
    @Test
    void testZtisClientIdentityEqualityWithRealCertificates()
    {
        // Two keystores loaded with different certificates must be considered unequal.
        // This is what drives cache-key differentiation in OAuth2Service.tokenServiceCache
        // when the SVID cert rotates.
        final KeyPair keyPair = generateKeyPair();
        final Certificate certA = generateCertificate(keyPair, "CN=cert-a");
        final Certificate certB = generateCertificate(keyPair, "CN=cert-b");

        final KeyStore ksA = buildKeyStore(keyPair, certA);
        final KeyStore ksB = buildKeyStore(keyPair, certB);
        final KeyStore ksSameAsA = buildKeyStore(keyPair, certA);

        final ZtisClientIdentity identityA = new ZtisClientIdentity("client", ksA);
        final ZtisClientIdentity identityB = new ZtisClientIdentity("client", ksB);
        final ZtisClientIdentity identitySameAsA = new ZtisClientIdentity("client", ksSameAsA);

        // Different cert → unequal: a new tokenServiceCache entry would be created after rotation
        assertThat(identityA).isNotEqualTo(identityB);
        assertThat(identityA).doesNotHaveSameHashCodeAs(identityB);

        // Same cert content → equal: tokenServiceCache hit, HttpClient is reused (expected behaviour)
        assertThat(identityA).isEqualTo(identitySameAsA);
        assertThat(identityA).hasSameHashCodeAs(identitySameAsA);
    }

    @SneakyThrows
    @Test
    void testOAuth2ServiceTokenCacheKeyChangesWhenCertRotates()
    {
        // This test documents the self-healing behaviour of OAuth2Service.tokenServiceCache:
        // when the SVID cert rotates, getZtisIdentity() produces a new ZtisClientIdentity with a
        // different certificate, which hashes to a different cache key, causing tokenServiceCache
        // to create a new OAuth2TokenService (and thus a new HttpClient with a fresh SSLContext).
        //
        // PRECONDITION for this to work: tryGetDestination() (and therefore getZtisIdentity())
        // must be called again after cert rotation. If the HttpDestination is cached at a higher
        // level and re-used indefinitely, the ZtisClientIdentity inside OAuth2Service is never
        // refreshed and the cache key never changes.
        final KeyPair keyPair = generateKeyPair();
        final Certificate certBeforeRotation = generateCertificate(keyPair, "CN=before-rotation");
        final Certificate certAfterRotation = generateCertificate(keyPair, "CN=after-rotation");

        final KeyStore ksBefore = buildKeyStore(keyPair, certBeforeRotation);
        final KeyStore ksAfter = buildKeyStore(keyPair, certAfterRotation);

        final ZtisClientIdentity identityBefore = new ZtisClientIdentity("client", ksBefore);
        final ZtisClientIdentity identityAfter = new ZtisClientIdentity("client", ksAfter);

        // Simulate OAuth2Service.getTokenService() cache key computation
        final com.sap.cloud.sdk.cloudplatform.cache.CacheKey keyBefore =
            com.sap.cloud.sdk.cloudplatform.cache.CacheKey.fromIds(null, null).append(identityBefore);
        final com.sap.cloud.sdk.cloudplatform.cache.CacheKey keyAfter =
            com.sap.cloud.sdk.cloudplatform.cache.CacheKey.fromIds(null, null).append(identityAfter);

        // Cache keys must differ → cache miss → new HttpClient built with rotated cert
        assertThat(keyBefore).isNotEqualTo(keyAfter);
    }

    @SneakyThrows
    private static KeyPair generateKeyPair()
    {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    @SneakyThrows
    private static Certificate generateCertificate( final KeyPair keyPair, final String subject )
    {
        final long now = System.currentTimeMillis();
        final X500Name name = new X500Name(subject);
        final BigInteger serial = new BigInteger(Long.toString(now));
        final Date startDate = new Date(now);
        final Date endDate = new Date(now + 3_600_000L);

        final JcaX509v3CertificateBuilder certBuilder =
            new JcaX509v3CertificateBuilder(name, serial, startDate, endDate, name, keyPair.getPublic());
        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(true));

        final Provider prov = new BouncyCastleProvider();
        Security.addProvider(prov);
        final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().setProvider(prov).getCertificate(certBuilder.build(contentSigner));
    }

    @SneakyThrows
    private static KeyStore buildKeyStore( final KeyPair keyPair, final Certificate cert )
    {
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null);
        ks.setKeyEntry("spiffe", keyPair.getPrivate(), new char[0], new Certificate[] { cert });
        return ks;
    }

    @Test
    void testGetCredentialType()
    {
        assertThat(CredentialType.from("binding-secret")).isEqualTo(CredentialType.BINDING_SECRET);
        assertThat(CredentialType.from("Binding-Secret")).isEqualTo(CredentialType.BINDING_SECRET);
        assertThat(CredentialType.from("instance-secret")).isEqualTo(CredentialType.INSTANCE_SECRET);
        assertThat(CredentialType.from("Instance-Secret")).isEqualTo(CredentialType.INSTANCE_SECRET);
        assertThat(CredentialType.from("X509_GENERATED")).isEqualTo(CredentialType.X509_GENERATED);
        assertThat(CredentialType.from("x509_generated")).isEqualTo(CredentialType.X509_GENERATED);
        assertThat(CredentialType.from("X509_ATTESTED")).isEqualTo(CredentialType.X509_ATTESTED);
        assertThat(CredentialType.from("x509_attested")).isEqualTo(CredentialType.X509_ATTESTED);
        assertThat(CredentialType.from("X509_PROVIDED")).isEqualTo(CredentialType.X509_PROVIDED);
        assertThat(CredentialType.from("x509_provided")).isEqualTo(CredentialType.X509_PROVIDED);
        assertThat(CredentialType.from("X509")).isEqualTo(CredentialType.X509);
        assertThat(CredentialType.from("x509")).isEqualTo(CredentialType.X509);
        assertThat(CredentialType.from("foo")).isNull();
    }
}
