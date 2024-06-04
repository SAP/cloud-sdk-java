package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveCertificatesOnly;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveKeyStoreHashCode;

import java.security.KeyStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.config.CredentialType;

import lombok.AllArgsConstructor;
import lombok.Getter;

final class SecurityLibWorkarounds
{
    private static final String X509_GENERATED = "X509_GENERATED";
    static final String X509_ATTESTED = "X509_ATTESTED";
    static final String X509_PROVIDED = "X509_PROVIDED";

    private SecurityLibWorkarounds()
    {
        throw new IllegalStateException("This utility class should never be instantiated.");
    }

    @Nullable
    static CredentialType getCredentialType( @Nonnull final String rawType )
    {
        final CredentialType maybeType = CredentialType.from(rawType);
        if( maybeType != null ) {
            return maybeType;
        }
        // Workaround for the Security Client Lib <= 3.3.5 which does not recognise X509_GENERATED, X509_PROVIDED and X509_ATTESTED.
        if( rawType.equalsIgnoreCase(X509_GENERATED)
            || rawType.equalsIgnoreCase(X509_ATTESTED)
            || rawType.equalsIgnoreCase(X509_PROVIDED) ) {
            return CredentialType.X509;
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    static class ZtisClientIdentity implements ClientIdentity
    {
        @Nonnull
        private final String id;
        @Nonnull
        private final KeyStore keyStore;

        @Override
        public boolean isCertificateBased()
        {
            return true;
        }

        // The identity will be used as cache key, so it's important we correctly implement equals/hashCode
        @Override
        public boolean equals( final Object obj )
        {
            if( this == obj ) {
                return true;
            }

            if( obj == null || getClass() != obj.getClass() ) {
                return false;
            }

            final ZtisClientIdentity that = (ZtisClientIdentity) obj;
            return new EqualsBuilder()
                .append(id, that.id)
                .append(resolveCertificatesOnly(keyStore), resolveCertificatesOnly(that.keyStore))
                .isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(41, 71).append(id).append(resolveKeyStoreHashCode(keyStore)).build();
        }
    }
}
