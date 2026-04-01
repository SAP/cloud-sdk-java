package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.SVID_EXPIRY_SAFETY_MARGIN;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.ZTIS_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.spiffe.svid.x509svid.X509Svid;
import lombok.SneakyThrows;

class ZeroTrustIdentityServiceTest
{
    private static final String SPIFFE_ID = "spiffe://example.org/workload";
    private static final ServiceBinding BINDING = mockBinding();

    private static KeyPair keyPair;

    private ZeroTrustIdentityService sut;

    @BeforeAll
    @SneakyThrows
    static void setUpClass()
    {
        Security.addProvider(new BouncyCastleProvider());
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);
        keyPair = kpg.generateKeyPair();
    }

    @BeforeEach
    void setUp()
    {
        sut = spy(new ZeroTrustIdentityService(BINDING));
    }

    @Test
    void testLazyInitialization()
    {
        // it's important here to spy using the class, not on an already existing instance
        // otherwise the method reference stored in Lazy.of(this::initX509Source) would not point to the mock
        final ZeroTrustIdentityService sut = spy(ZeroTrustIdentityService.class);
        verify(sut, never()).initX509Source();

        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(CloudPlatformException.class);
        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(CloudPlatformException.class);

        // an initialization failure must not be cached
        verify(sut, times(2)).initX509Source();
    }

    @Test
    void testKeyStoreCache()
    {
        // Cache is keyed by SpiffeId, not by SVID object reference.
        // Two SVIDs with the same SpiffeId and a valid expiry share the same cache entry.
        final X509Svid svid = newSvid(Instant.now().plus(SVID_EXPIRY_SAFETY_MARGIN).plusSeconds(300));
        doReturn(svid).when(sut).getX509Svid();

        assertThat(sut.isKeyStoreCached(svid)).isFalse();

        sut.getOrCreateKeyStore();
        assertThat(sut.isKeyStoreCached(svid)).isTrue();

        // Second call with the same SVID reference → cache hit, loadKeyStore not called again
        sut.getOrCreateKeyStore();
        verify(sut, times(1)).loadKeyStore(svid);

        // New SVID object with the same SpiffeId (simulates SPIRE delivering a rotated cert with the same identity)
        // → still a cache HIT as long as the cert is well within the expiry margin.
        // The expiry margin is the sole invalidation mechanism; SpiffeId equality means "same workload".
        final X509Svid rotated =
            newSvid(Instant.now().plus(ZeroTrustIdentityService.SVID_EXPIRY_SAFETY_MARGIN).plusSeconds(300));
        doReturn(rotated).when(sut).getX509Svid();

        assertThat(sut.isKeyStoreCached(rotated)).isTrue();
        sut.getOrCreateKeyStore();
        // loadKeyStore was not called a second time — cached KeyStore is reused
        verify(sut, times(1)).loadKeyStore(any());
    }

    @Test
    void testThrowsWithoutBinding()
    {
        assertThatThrownBy(ZeroTrustIdentityService.getInstance()::getX509Svid)
            .isInstanceOf(CloudPlatformException.class)
            .hasRootCauseInstanceOf(ServiceBindingAccessException.class);
    }

    @Test
    void testCheckForInvalidCertificate()
    {
        final X509Svid expired = newSvid(Instant.now().minusSeconds(20));
        doReturn(expired).when(sut).getX509Svid();
        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testCachedKeyStoreIsRejectedWhenCachedSvidHasExpired()
    {
        final X509Svid svid = newSvid(Instant.now().plus(SVID_EXPIRY_SAFETY_MARGIN).plusSeconds(300));
        doReturn(svid).when(sut).getX509Svid();

        sut.getOrCreateKeyStore();
        assertThat(sut.isKeyStoreCached(svid)).isTrue();

        // Simulate SPIRE being slow: a new SVID arrives with the same SpiffeId but its cert is already past notAfter.
        final X509Svid expiredSvid = newSvid(Instant.now().plus(SVID_EXPIRY_SAFETY_MARGIN).minusSeconds(1));
        doReturn(expiredSvid).when(sut).getX509Svid();

        assertThat(sut.isKeyStoreCached(expiredSvid)).isFalse();
    }

    @Test
    void testCachedKeyStoreIsRejectedWhenSvidIsAboutToExpire()
    {
        // SVID expiring within the safety margin must not be considered cached,
        // even when it is the same object reference
        final X509Svid nearExpiry = newSvid(Instant.now().plus(SVID_EXPIRY_SAFETY_MARGIN).minus(1, ChronoUnit.HOURS));
        doReturn(nearExpiry).when(sut).getX509Svid();

        // getOrCreateKeyStore() fills the cache (assertSvidNotExpired does not fire — cert is still valid)
        sut.getOrCreateKeyStore();

        // isKeyStoreCached must return false: expiry margin kicks in
        assertThat(sut.isKeyStoreCached(nearExpiry)).isFalse();
    }

    @Test
    void testSpiffeId()
    {
        assertThat(sut.getSpiffeId()).isEqualTo("spiffe://example.org");
    }

    @Test
    void testAppIdentifier()
    {
        assertThat(sut.getAppIdentifier()).contains("test-app");

        final DefaultServiceBinding emptyBinding =
            new DefaultServiceBindingBuilder().withServiceIdentifier(ZTIS_IDENTIFIER).build();

        sut = spy(new ZeroTrustIdentityService(emptyBinding));
        assertThat(sut.getAppIdentifier()).isEmpty();

        final DefaultServiceBinding emptyValue =
            new DefaultServiceBindingBuilder()
                .withServiceIdentifier(ZTIS_IDENTIFIER)
                .withCredentials(Map.of("parameters", Map.of("app-identifier", "")))
                .build();

        sut = spy(new ZeroTrustIdentityService(emptyValue));
        assertThat(sut.getAppIdentifier()).isEmpty();
    }

    // -- helpers --

    /**
     * Generates a SPIFFE-compliant X.509 certificate with the given validity window and parses it into an
     * {@link X509Svid}. Uses a shared EC key pair so only the certificate (and its expiry) differs between calls.
     */
    @SneakyThrows
    @Nonnull
    static X509Svid newSvid( @Nonnull final Instant notAfter )
    {
        final long now = System.currentTimeMillis();
        final X500Name subject = new X500Name("CN=test-svid");
        final JcaX509v3CertificateBuilder certBuilder =
            new JcaX509v3CertificateBuilder(
                subject,
                BigInteger.valueOf(now),
                new Date(now - 1000),
                Date.from(notAfter),
                subject,
                keyPair.getPublic());

        // SPIFFE URI SAN — required by X509Svid.parse()
        certBuilder
            .addExtension(
                Extension.subjectAlternativeName,
                false,
                new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, SPIFFE_ID)));

        // leaf certificate requirements imposed by X509Svid.parse():
        //   - digitalSignature key usage
        //   - no keyCertSign / cRLSign
        //   - BasicConstraints CA=false
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        certBuilder
            .addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        final Provider bc = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        final ContentSigner signer =
            new JcaContentSignerBuilder("SHA256withECDSA").setProvider(bc).build(keyPair.getPrivate());
        final X509Certificate cert =
            new JcaX509CertificateConverter().setProvider(bc).getCertificate(certBuilder.build(signer));

        // SPIFFE library expects PKCS#8 PEM ("PRIVATE KEY"), not SEC1 ("EC PRIVATE KEY")
        final byte[] certPem = toPem("CERTIFICATE", cert.getEncoded());
        final byte[] keyPem = toPem("PRIVATE KEY", keyPair.getPrivate().getEncoded());
        return X509Svid.parse(certPem, keyPem);
    }

    @Nonnull
    private static byte[] toPem( @Nonnull final String type, @Nonnull final byte[] der )
    {
        final String base64 = java.util.Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(der);
        final String pem = "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----\n";
        return pem.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static ServiceBinding mockBinding()
    {
        return new DefaultServiceBindingBuilder()
            .withServiceIdentifier(ZTIS_IDENTIFIER)
            .withCredentials(
                Map
                    .of(
                        "workload",
                        Map.of("spiffeID", "spiffe://example.org"),
                        "parameters",
                        Map.of("app-identifier", "test-app")))
            .build();
    }
}
