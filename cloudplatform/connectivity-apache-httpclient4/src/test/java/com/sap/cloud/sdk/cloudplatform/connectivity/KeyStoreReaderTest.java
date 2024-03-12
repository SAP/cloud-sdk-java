/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.KeyStoreReader.createKeyStore;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

import java.io.FileReader;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

class KeyStoreReaderTest
{
    private static final String RES =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName();
    private static final String CRT_PATH = RES + "/client-cert.crt";
    private static final String KEY_PATH = RES + "/client-cert.key";
    private static final String ALIAS = "1";
    private static final char[] PASS = new char[0];

    @SneakyThrows
    @Test
    void testPem()
    {
        final FileReader certs = new FileReader(CRT_PATH), key = new FileReader(KEY_PATH);
        final KeyStore createdKeystore = createKeyStore(ALIAS, PASS, certs, key);

        assertThat(createdKeystore.getType()).isEqualTo("JKS");
        assertThat(createdKeystore.getProvider()).isNotNull();

        assertThat(createdKeystore.getCertificateChain(ALIAS)).hasSize(1);
        assertThat(createdKeystore.getCertificate(ALIAS))
            .isInstanceOf(X509Certificate.class)
            .extracting(c -> ((X509Certificate) c).getSubjectX500Principal())
            .hasToString("CN=localhost, EMAILADDRESS=cloudsdk@sap.com, O=Potsdam, ST=Brandenburg, C=DE");

        assertThat(createdKeystore.getKey(ALIAS, new char[0])).isInstanceOf(RSAPrivateCrtKey.class); // no password
    }

    @SneakyThrows
    @Test
    void testKeyStoreSanity() // sanity checks
    {
        final KeyStore ks1 = createKeyStore(ALIAS, PASS, new FileReader(CRT_PATH), new FileReader(KEY_PATH));
        final KeyStore ks2 = createKeyStore(ALIAS, PASS, new FileReader(CRT_PATH), new FileReader(KEY_PATH));

        assertThat(ks1).isNotEqualTo(ks2); // KeyStore class does not support equals
        assertThat(ks1).doesNotHaveSameHashCodeAs(ks2); // ... nor hashCode
        assertThat(ks1.aliases()).isNotEqualTo(ks2.aliases()); // Enumeration "aliases" does not support equals

        assertThat(ks1.aliases()).extracting(Collections::list, as(LIST)).containsExactly(ALIAS);
        assertThat(ks2.aliases()).extracting(Collections::list, as(LIST)).containsExactly(ALIAS);

        assertThat(ks1.getCertificate(ALIAS)).isEqualTo(ks2.getCertificate(ALIAS)); // certificates support equals
        assertThat(ks1.getCertificate(ALIAS)).hasSameHashCodeAs(ks2.getCertificate(ALIAS)); // ... and hashCode

        assertThat(ks1.getKey(ALIAS, PASS)).isEqualTo(ks2.getKey(ALIAS, PASS)); // keys support equals
        assertThat(ks1.getKey(ALIAS, PASS)).hasSameHashCodeAs(ks2.getKey(ALIAS, PASS)); // ... and hashCode
    }
}
