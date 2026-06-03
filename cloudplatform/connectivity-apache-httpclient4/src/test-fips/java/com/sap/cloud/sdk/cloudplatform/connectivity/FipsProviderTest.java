package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.FileReader;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * Regression guard for the P1 fix: asserts that {@code KeyStoreReader.createKeyStore()} produces a PKCS12 keystore. Run
 * with {@code mvn test -P fips-approved}.
 */
@Tag( "fips-approved" )
class FipsProviderTest
{
    private static final String RES = "src/test/resources/ClientCertificateAuthenticationLocalTest";
    private static final String CRT_PATH = RES + "/client-cert.crt";
    private static final String KEY_PATH = RES + "/client-cert.key";
    private static final String ALIAS = "client-cert";
    private static final char[] EMPTY_PASSWORD = new char[0];

    @AfterAll
    static void removeBouncyCastleFips()
    {
        Security.removeProvider("BCFIPS");
    }

    @BeforeAll
    static void registerBouncyCastleFips()
    {
        Security.insertProviderAt(new BouncyCastleFipsProvider(), 1);

        assertThat(Security.getProvider("BCFIPS"))
            .describedAs("BC FIPS provider must be registered as a JCA provider")
            .isNotNull();

        assertThat(CryptoServicesRegistrar.isInApprovedOnlyMode())
            .describedAs("BC FIPS must be in approved-only mode. ")
            .isTrue();
    }

    @Test
    @SneakyThrows
    void keystoreTypeIsP12()
    {
        final KeyStore keyStore =
            KeyStoreReader.createKeyStore(ALIAS, EMPTY_PASSWORD, new FileReader(CRT_PATH), new FileReader(KEY_PATH));

        assertThat(keyStore.getType()).isEqualTo("pkcs12");
    }

    @Test
    void md5IsRejectedInApprovedOnlyMode()
    {
        assertThatThrownBy(() -> MessageDigest.getInstance("MD5", "BCFIPS"))
            .isInstanceOf(NoSuchAlgorithmException.class);

    }
}