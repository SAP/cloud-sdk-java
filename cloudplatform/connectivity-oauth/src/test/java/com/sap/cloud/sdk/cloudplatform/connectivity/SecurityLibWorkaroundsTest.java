package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.KeyStore;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;
import com.sap.cloud.security.config.CredentialType;

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

    @Test
    void testGetCredentialType()
    {
        assertThat(SecurityLibWorkarounds.getCredentialType("binding-secret")).isEqualTo(CredentialType.BINDING_SECRET);
        assertThat(SecurityLibWorkarounds.getCredentialType("Binding-Secret")).isEqualTo(CredentialType.BINDING_SECRET);
        assertThat(SecurityLibWorkarounds.getCredentialType("instance-secret"))
            .isEqualTo(CredentialType.INSTANCE_SECRET);
        assertThat(SecurityLibWorkarounds.getCredentialType("Instance-Secret"))
            .isEqualTo(CredentialType.INSTANCE_SECRET);
        assertThat(SecurityLibWorkarounds.getCredentialType("X509_GENERATED")).isEqualTo(CredentialType.X509_GENERATED);
        assertThat(SecurityLibWorkarounds.getCredentialType("x509_generated")).isEqualTo(CredentialType.X509_GENERATED);
        assertThat(SecurityLibWorkarounds.getCredentialType("X509_ATTESTED")).isEqualTo(CredentialType.X509_ATTESTED);
        assertThat(SecurityLibWorkarounds.getCredentialType("x509_attested")).isEqualTo(CredentialType.X509_ATTESTED);
        assertThat(SecurityLibWorkarounds.getCredentialType("X509_PROVIDED")).isEqualTo(CredentialType.X509_PROVIDED);
        assertThat(SecurityLibWorkarounds.getCredentialType("x509_provided")).isEqualTo(CredentialType.X509_PROVIDED);
        assertThat(SecurityLibWorkarounds.getCredentialType("X509")).isEqualTo(CredentialType.X509);
        assertThat(SecurityLibWorkarounds.getCredentialType("x509")).isEqualTo(CredentialType.X509);
        assertThat(SecurityLibWorkarounds.getCredentialType("foo")).isNull();
    }
}
