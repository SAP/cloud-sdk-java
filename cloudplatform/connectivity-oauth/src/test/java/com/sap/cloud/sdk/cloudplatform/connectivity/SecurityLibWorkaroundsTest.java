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
