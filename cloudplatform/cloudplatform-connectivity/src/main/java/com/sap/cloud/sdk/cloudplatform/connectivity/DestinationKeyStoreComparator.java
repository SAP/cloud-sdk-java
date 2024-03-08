/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DestinationKeyStoreComparator
{
    static int INITIAL_HASH_CODE = 17;

    /**
     * Calculate the SAP Cloud SDK compatible KeyStore hash code. The method may return a static number
     * {@link DestinationKeyStoreComparator#INITIAL_HASH_CODE} in case the key-store was not initialized or contains
     * non-certificate based elements.
     *
     * @param ks
     *            The KeyStore to calculate the hash code for.
     * @return The key-store hash code dynamically computed on behalf of stored certificates.
     */
    static int resolveKeyStoreHashCode( @Nonnull final KeyStore ks )
    {
        final HashCodeBuilder out = new HashCodeBuilder(INITIAL_HASH_CODE, 37);
        final Certificate[] certificates = resolveCertificatesOnly(ks);
        out.append(certificates);
        return out.toHashCode();
    }

    /**
     * Resolve certificates-only from a KeyStore.
     *
     * @param ks
     *            The KeyStore to iterate.
     * @return An array with certificates, or empty in case of error or non-certificate based keystore entries.
     */
    @Nonnull
    static Certificate[] resolveCertificatesOnly( @Nonnull final KeyStore ks )
    {
        final ArrayList<Certificate> out = new ArrayList<>();
        try {
            final Enumeration<String> aliases = ks.aliases();
            while( aliases.hasMoreElements() ) {
                final String alias = aliases.nextElement();
                if( ks.getCertificate(alias) == null ) {
                    return new Certificate[0];
                }
                out.add(ks.getCertificate(alias));
            }
        }
        catch( final Exception e ) {
            log.debug("Error while resolving certificates from KeyStore", e);
            return new Certificate[0];
        }
        return out.toArray(new Certificate[0]);
    }
}
