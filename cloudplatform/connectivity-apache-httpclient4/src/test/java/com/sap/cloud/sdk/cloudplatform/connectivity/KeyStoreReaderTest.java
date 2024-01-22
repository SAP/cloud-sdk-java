/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileReader;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;

import org.junit.jupiter.api.Test;

class KeyStoreReaderTest
{
    private static final String RES =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName();
    private static final String CRT_PATH = RES + "/client-cert.crt";
    private static final String KEY_PATH = RES + "/client-cert.key";

    @Test
    void testPem()
        throws Exception
    {
        final String ALIAS = "1";
        final FileReader certs = new FileReader(CRT_PATH), key = new FileReader(KEY_PATH);
        final KeyStore createdKeystore = KeyStoreReader.createKeyStore(ALIAS, new char[0], certs, key);

        assertThat(createdKeystore.getType()).isEqualTo("JKS");
        assertThat(createdKeystore.getProvider()).isNotNull();

        assertThat(createdKeystore.getCertificateChain(ALIAS)).hasSize(1);
        assertThat(createdKeystore.getCertificate(ALIAS))
            .isInstanceOf(X509Certificate.class)
            .extracting(c -> ((X509Certificate) c).getSubjectX500Principal())
            .hasToString("CN=localhost, EMAILADDRESS=cloudsdk@sap.com, O=Potsdam, ST=Brandenburg, C=DE");

        assertThat(createdKeystore.getKey(ALIAS, new char[0])).isInstanceOf(RSAPrivateCrtKey.class); // no password
    }
}
