package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.security.KeyStore;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import lombok.EqualsAndHashCode;

import com.sap.cloud.security.config.ClientIdentity;

import lombok.ToString;
import lombok.Value;

final class SecurityLibWorkarounds
{
    private SecurityLibWorkarounds()
    {
        throw new IllegalStateException("This utility class should never be instantiated.");
    }

    @Value
    static class ZtisClientIdentity implements ClientIdentity
    {
        @Nonnull
        String id;

        // Exclude certificates from equals & hash code since they rotate dynamically at runtime
        // Instead, the OAuth2Service cache explicitly checks for outdated KeyStores
        @Nonnull
        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        Supplier<KeyStore> keyStoreSource;

        @Override
        public boolean isCertificateBased()
        {
            return true;
        }

        @Nonnull
        KeyStore getKeyStore()
        {
            try {
                return keyStoreSource.get();
            }
            catch( final Exception e ) {
                throw new CloudPlatformException("Failed to load X509 certificate for credential type X509_ATTESTED.", e);
            }
        }
    }
}
