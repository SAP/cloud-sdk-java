package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.ZTIS_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingBuilder;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.spiffe.svid.x509svid.X509Svid;

class ZeroTrustIdentityServiceTest
{
    private static final ServiceBinding binding = mockBinding();

    private ZeroTrustIdentityService sut;
    private X509Svid svidMock;

    @BeforeEach
    void setUp()
    {
        sut = spy(new ZeroTrustIdentityService(binding));
        mockSvid(Instant.now().plusSeconds(300));
        doReturn(mock(KeyStore.class)).when(sut).loadKeyStore(any());
    }

    @Test
    void testLazyInitialization()
    {
        // it's important here to spy using the class, not on an already existing instance
        // otherwise the method reference stored in Lazy.of(this::initX509Source) would not point to the mock
        sut = spy(ZeroTrustIdentityService.class);
        verify(sut, never()).initX509Source();

        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(CloudPlatformException.class);
        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(CloudPlatformException.class);

        // an initialization failure must not be cached
        verify(sut, times(2)).initX509Source();
    }

    @Test
    void testKeyStoreCache()
    {
        assertThat(sut.isKeyStoreCached(svidMock)).isFalse();

        sut.getOrCreateKeyStore();
        assertThat(sut.isKeyStoreCached(svidMock)).isTrue();

        sut.getOrCreateKeyStore();
        verify(sut, times(1)).loadKeyStore(svidMock);

        // actually the time doesn't matter here, as the logic depends on the equals comparison of the X509Svid
        // however, equals cannot be mocked, so equals compares the mock objects, which would be different even with the same times
        // we could make this test more realistic by using real certificate + key objects
        mockSvid(Instant.now().plusSeconds(20));

        assertThat(sut.isKeyStoreCached(svidMock)).isFalse();
        sut.getOrCreateKeyStore();
        assertThat(sut.isKeyStoreCached(svidMock)).isTrue();
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
        mockSvid(Instant.now().minusSeconds(20));
        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(IllegalStateException.class);
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

        sut = new ZeroTrustIdentityService(emptyBinding);
        assertThat(sut.getAppIdentifier()).isEmpty();

        final DefaultServiceBinding emptyValue =
            new DefaultServiceBindingBuilder()
                .withServiceIdentifier(ZTIS_IDENTIFIER)
                .withCredentials(Map.of("parameters", Map.of("app-identifier", "")))
                .build();

        sut = new ZeroTrustIdentityService(emptyValue);
        assertThat(sut.getAppIdentifier()).isEmpty();
    }

    @Test
    void testCertOnFileSystem()
    {
        final ServiceBinding binding =
            new DefaultServiceBindingBuilder()
                .withServiceIdentifier(ZTIS_IDENTIFIER)
                .withCredentials(
                    Map
                        .of(
                            "certPath",
                            "src/test/resources/ZeroTrustIdentityServiceTest/cert.pem",
                            "keyPath",
                            "src/test/resources/ZeroTrustIdentityServiceTest/key.pem"))
                .build();

        final ZeroTrustIdentityService sut = new ZeroTrustIdentityService(binding);
        final X509Svid svid = sut.getX509Svid();

        assertThat(svid.getLeaf().getSubjectX500Principal().getName()).contains("OU=Zero Trust Identity Service");
        assertThat(svid.getPrivateKey()).isNotNull();
        assertThat(svid.getSpiffeId().getTrustDomain().getName()).isEqualTo("0trust.net.sap");

        assertThat(svid)
            .describedAs("Our cache relies on the equals implementation of SVIDs")
            .isEqualTo(sut.getX509Svid());

        assertThatThrownBy(sut::getOrCreateKeyStore)
            .describedAs("KeyStore creation should fail since the cert has expired")
            .isInstanceOf(IllegalStateException.class);
    }

    private void mockSvid( Instant notAfter )
    {
        final X509Svid svid = mock(X509Svid.class);
        final X509Certificate certificate = mock(X509Certificate.class);
        doReturn(Date.from(notAfter)).when(certificate).getNotAfter();
        doReturn(certificate).when(svid).getLeaf();
        doReturn(svid).when(sut).getX509Svid();
        svidMock = svid;
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
