package com.sap.cloud.sdk.cloudplatform.connectivity;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.spiffe.svid.x509svid.X509Svid;

class ZeroTrustIdentityServiceTest
{
    private ZeroTrustIdentityService sut;
    private X509Svid svidMock;

    @BeforeEach
    void setUp()
    {
        sut = spy(new ZeroTrustIdentityService());
        mockSvid(Instant.now().plusSeconds(300));
        doReturn(mock(KeyStore.class)).when(sut).loadKeyStore(any());
    }

    @Test
    void testLazyInitialization()
    {
        verify(sut, never()).initX509Source();
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
    void testCheckForInvalidCertificate()
    {
        mockSvid(Instant.now().minusSeconds(20));
        assertThatThrownBy(sut::getOrCreateKeyStore).isInstanceOf(IllegalStateException.class);
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
}
