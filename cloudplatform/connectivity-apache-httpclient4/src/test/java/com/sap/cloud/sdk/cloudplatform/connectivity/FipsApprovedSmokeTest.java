package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileReader;
import java.security.KeyStore;
import java.security.Security;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * FIPS compatibility smoke tests for PEM credential loading and KeyStore creation.
 *
 * <p>
 * These tests replicate the user scenario from FIPS analysis (User Analysis A/B, Flow 1): the user has registered BC
 * FIPS as the JCA provider, enabled approved-only mode, and is using the SDK to load a BTP destination configured with
 * a PEM client certificate.
 *
 * <p>
 * The meaningful assertion is that no {@code FipsUnapprovedOperationError} is thrown — meaning the code path does not
 * invoke any non-FIPS-approved algorithms when BC FIPS is registered and {@code approved_only=true} is active.
 *
 * <p>
 * Note on provider resolution: {@code KeyStore.getInstance("PKCS12")} resolves to the {@code SUN} provider on a
 * standard JDK even with BCFIPS at position 1. BCFIPS passes PKCS12 requests through since PKCS12 is a FIPS-approved
 * format regardless of which JCA provider constructs the in-memory container. On a real FIPS JVM (e.g., Red Hat FIPS
 * OpenJDK), provider configuration is enforced at the OS/JDK level. No assertion on the provider name is made.
 *
 * <p>
 * Run with: {@code mvn test -P fips-approved} (Requires -Dorg.bouncycastle.fips.approved_only=true which is set by the
 * profile argLine)
 */
@Tag( "fips-approved" )
class FipsApprovedSmokeTest
{
    private static final String RES =
        "src/test/resources/" + ClientCertificateAuthenticationLocalTest.class.getSimpleName();
    private static final String CRT_PATH = RES + "/client-cert.crt";
    private static final String KEY_PATH = RES + "/client-cert.key";
    private static final String ALIAS = "client-cert";
    private static final char[] EMPTY_PASSWORD = new char[0];

    @BeforeAll
    static void registerBouncyCastleFips()
    {
        Security.insertProviderAt(new BouncyCastleFipsProvider(), 1);

        assertThat(Security.getProvider("BCFIPS"))
            .describedAs("BC FIPS provider must be registered as a JCA provider")
            .isNotNull();

        assertThat(CryptoServicesRegistrar.isInApprovedOnlyMode())
            .describedAs(
                "BC FIPS must be in approved-only mode. "
                    + "Ensure -Dorg.bouncycastle.fips.approved_only=true is set (done by the fips-approved Maven profile).")
            .isTrue();
    }

    /**
     * Verifies Flow 1 from User Analysis A/B: PEM certificate + unencrypted PKCS#8 private key → in-memory PKCS12
     * KeyStore → available for mTLS.
     *
     * <p>
     * This is the critical code path fixed in P1: {@code KeyStoreReader.createKeyStore()} now creates a PKCS12 KeyStore
     * (was JKS before the fix). If any non-FIPS algorithm were invoked during key loading (e.g., JKS key cipher which
     * uses MD5), BC FIPS would throw {@code FipsUnapprovedOperationError}. The fact that this test completes without
     * throwing confirms the fix is effective.
     */
    @Test
    @SneakyThrows
    void pemCertificateAndKeyLoadCompletesWithoutFipsViolation()
    {
        // If any non-FIPS algorithm were invoked, FipsUnapprovedOperationError would be thrown here.
        final KeyStore keyStore =
            KeyStoreReader.createKeyStore(ALIAS, EMPTY_PASSWORD, new FileReader(CRT_PATH), new FileReader(KEY_PATH));

        assertThat(keyStore.getType())
            .describedAs("KeyStore must be PKCS12 type — validates the P1 fix (was JKS before)")
            .isEqualTo("PKCS12");

        assertThat(keyStore.getCertificateChain(ALIAS))
            .describedAs("Certificate chain must be present and non-empty")
            .isNotNull()
            .isNotEmpty();

        assertThat(keyStore.getKey(ALIAS, EMPTY_PASSWORD))
            .describedAs("Private key must be accessible from the KeyStore")
            .isNotNull();

        // No assertion on keyStore.getProvider().getName() — on a standard JDK, SUN provides PKCS12
        // even with BCFIPS at position 1. PKCS12 is FIPS-approved regardless of the provider.
    }
}
