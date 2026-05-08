package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.SecurityLibWorkarounds.ZtisClientIdentity;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.security.config.CredentialType;

class SecurityLibWorkaroundsTest
{
    @Test
    void testZtisClientIdentityImplementsEquals()
    {
        // equals/hashCode are based only on id, not on the keyStoreSource (which rotates dynamically)
        final ZtisClientIdentity sut = new ZtisClientIdentity("id", () -> mock(KeyStore.class));

        assertThat(sut).isEqualTo(new ZtisClientIdentity("id", () -> mock(KeyStore.class)));
        assertThat(sut).hasSameHashCodeAs(new ZtisClientIdentity("id", () -> mock(KeyStore.class)));
        assertThat(sut).isNotEqualTo(new ZtisClientIdentity("id2", () -> mock(KeyStore.class)));
        assertThat(sut).doesNotHaveSameHashCodeAs(new ZtisClientIdentity("id2", () -> mock(KeyStore.class)));
    }

    @Test
    void testZtisClientIdentityGetKeyStore()
    {
        final KeyStore expectedKeyStore = mock(KeyStore.class);
        final ZtisClientIdentity sut = new ZtisClientIdentity("id", () -> expectedKeyStore);

        assertThat(sut.getKeyStore()).isSameAs(expectedKeyStore);
    }

    @Test
    void testZtisClientIdentityGetKeyStoreWrapsException()
    {
        final RuntimeException cause = new RuntimeException("ZTIS failure");
        final ZtisClientIdentity sut = new ZtisClientIdentity("id", () -> {
            throw cause;
        });

        assertThatThrownBy(sut::getKeyStore)
            .isInstanceOf(CloudPlatformException.class)
            .hasMessageContaining("X509_ATTESTED")
            .hasCause(cause);
    }

    @Test
    void testZtisClientIdentitySupplierCalledLazily()
    {
        final AtomicInteger callCount = new AtomicInteger(0);
        final KeyStore ks = mock(KeyStore.class);
        final Supplier<KeyStore> supplier = () -> {
            callCount.incrementAndGet();
            return ks;
        };

        // Construction must NOT call the supplier
        final ZtisClientIdentity sut = new ZtisClientIdentity("id", supplier);
        assertThat(callCount.get()).isZero();

        // Each call to getKeyStore() invokes the supplier
        sut.getKeyStore();
        assertThat(callCount.get()).isEqualTo(1);

        sut.getKeyStore();
        assertThat(callCount.get()).isEqualTo(2);
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
