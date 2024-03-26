package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.KeyStore;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;

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
}
