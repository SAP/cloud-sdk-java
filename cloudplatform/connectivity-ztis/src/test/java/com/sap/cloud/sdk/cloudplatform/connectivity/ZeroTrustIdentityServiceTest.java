package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.spiffe.workloadapi.X509Source;
import io.vavr.Lazy;

class ZeroTrustIdentityServiceTest
{
    private X509Source mock;
    private ZeroTrustIdentityService sut;

    @BeforeEach
    void setUp()
    {
        mock = mock(X509Source.class);
        sut = spy(new ZeroTrustIdentityService(Lazy.of(() -> mock)));
    }

    @Test
    void test()
    {
        sut = spy(new ZeroTrustIdentityService());
        verify(sut, never()).initX509Source();
    }
}
