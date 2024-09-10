package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveCertificatesOnly;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationKeyStoreComparator.resolveKeyStoreHashCode;

import java.security.KeyStore;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.sap.cloud.security.config.ClientIdentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

final class SecurityLibWorkarounds
{
    private SecurityLibWorkarounds()
    {
        throw new IllegalStateException("This utility class should never be instantiated.");
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
