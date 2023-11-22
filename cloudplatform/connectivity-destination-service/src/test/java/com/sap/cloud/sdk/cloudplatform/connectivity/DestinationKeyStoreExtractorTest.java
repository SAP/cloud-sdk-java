package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationCertificate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;

import io.vavr.control.Option;

class DestinationKeyStoreExtractorTest
{
    private static final String MOCKED_DESTINATION_NAME = "Testname";
    private static final URI VALID_URI = URI.create("https://www.sap.de");

    private static final String JKS_FILE_KEY_STORE_LOCATION = "trustcert.jks";
    private static final String JKS_FILE_KEY_STORE_CONTENT =
        "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";
    private static final String KEY_STORE_PASSWORD = "Initial1";

    private static final String PKCS12_FILE_KEY_STORE_CONTENT_NO_PASSWORD =
        "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";
    private static final String PKCS12_FILE_KEY_STORE_LOCATION = "secret-store.p12";
    private static final String PKCS12_FILE_KEY_STORE_CONTENT =
        "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";
    private static final String PKCS12_KEY_STORE_PASSWORD = "cca-password";

    //taken from the Destination Service response on Cloud Foundry
    //represents the certificate extracted from a certificate file (file extension cer, crt, der)
    private static final String CERTIFICATE_FILE_CONTENT =
        "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tDQpNSUlGdURDQ0E2Q2dBd0lCQWdJVWI4dWxFSE9nVHZPaW8xaFV2bE1mT0pscWhld3dEUVlKS29aSWh2Y05BUUVMDQpCUUF3Z1lFeEN6QUpCZ05WQkFZVEFsVkxNUTh3RFFZRFZRUUhEQVpNYjI1a2IyNHhGVEFUQmdOVkJBb01ERU52DQphVzVUWTJsbGJtTmxjekVUTUJFR0ExVUVDd3dLVFhWc2RHbERhR0ZwYmpFMU1ETUdBMVVFQXd3c2MyVnNabk5wDQpaMjVsWkhSbGMzUXVkMlZ6ZEdWMWNtOXdaUzVqYkc5MVpHRndjQzVoZW5WeVpTNWpiMjB3SUJjTk1Ua3hNakEyDQpNVE16TmpNMVdoZ1BNakV4T1RFeE1USXhNek0yTXpWYU1JR0JNUXN3Q1FZRFZRUUdFd0pWU3pFUE1BMEdBMVVFDQpCd3dHVEc5dVpHOXVNUlV3RXdZRFZRUUtEQXhEYjJsdVUyTnBaVzVqWlhNeEV6QVJCZ05WQkFzTUNrMTFiSFJwDQpRMmhoYVc0eE5UQXpCZ05WQkFNTUxITmxiR1p6YVdkdVpXUjBaWE4wTG5kbGMzUmxkWEp2Y0dVdVkyeHZkV1JoDQpjSEF1WVhwMWNtVXVZMjl0TUlJQ0lqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FnOEFNSUlDQ2dLQ0FnRUF5SEs5DQphSUZqeTZGUlBiUFgzTFZyR1pvZnlmam51eXZjZjQyakpLeXhwVmZhcDVrVDFFc3llcGdlSUk1eCtHVmdENFNuDQp1eFNtZmlwSGtZOFpZUnQ1YWJEQ1hhamo5VHJ5cmRhK0s4OEhsWVlNSG54OUxJUjFzQ0ZyZEVsQkRTcHVLckxkDQpURktZa2gyUFpnSVVtMHJyZTcvbXJSOFFXSjNNYXpHNkp0YndNV0RzbkUvdlk0Tmg5Z1VrdHA2ZWxMVlZqRTZqDQo3VHBzYXhvSkVZb1QwOE9ub2RQZmFwUEdqYXpuVXg5UTdqSUNNYzFOQ1BRNng1cTdJYk5WVkZTK2xaQ1FERmttDQo4UEhtU0lUSFhYeldrOFR3dVg4MElUSGtVa0s5YUEyR1drdFVWV2FGbzN3Q2V2OHhNcHlzRHJXc1R2cTVCOE85DQpFOGpIZVVBUXVRRGg0cVhuZ0pHR2pqZkV6WjhvS1R2dld5RXNJTlJHbzJqc3BsUFNteUpMSGVLMjFYd29OVklYDQpEYmdrbDVqODIvV0N5TWF1bWZNb2krUXdYbXhqT0p4MkI5SFNxYUNBV21tQVdOcjgxOExybzhUa3BIVEJMOEpODQpJM3hkZllYelhzRDZEUzRtamJXbGlrSE1ja3ExeGJUdVdsTEFHWVM0TkRjWHlDNkNuS0VnMlpMaHpFMlc2aHAyDQo2TW5iNk4vZG5MSWJrSVZhNmxTRE9UTHEyZTBQR0owRXZ6di9COXVmbXg0akVQK2sxQWJqbGtybHFMR1huVm5MDQorNGcwdDU1UlVyS1RHdVY2RmxRZERXZFdCcU1pMTJQVC9RVUJFa2hleGZkMExTSnJBZWlQeDQ3RnVXZEpzeWlSDQo4RGk0aWE1OTRZZFpYSy9JLzBlR1ppSWVPWjF1Yk9iRno0Q0V6YjhDQXdFQUFhTWtNQ0l3Q3dZRFZSMFBCQVFEDQpBZ1F3TUJNR0ExVWRKUVFNTUFvR0NDc0dBUVVGQndNQk1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQ0FRQ3QyK0d0DQpTaU5OY2paMEJTb1IzR0NGT0xtM2dpUzNMdHR1SWpHMEhoNGd0ejdKUEFZc0VqMGNlVzFGdTB4T04xaDFrbzNHDQpldGgxT1pWWCtLZjJoY0R4T0wxMTd4MmpJdHdtTHIyRjJKZytRNitJYUFKbkpxY05WVElsVExRTUJ3WlZQNGNVDQo4MEh4L3ZPVkxERkExVTdiOTM0am96WjdKTHdmK2VkTkkrdFZHbnpnbHhOZXMyZEJxemxpakllNjdQb2NUVk9kDQpoWFpKRXRIenZZWGdKVmp6VTNRc3R0UU9zTWp6cVZJVWNtTVRpR1NGQ1piOURWU3ZWUlNXeE8vSXhaNk1KcTZCDQpuamcwbVphZGljT0ppeDFxSGVFelhoVUJMTW9OY1ZBL09SU3k0aEVUTWdZU29MMEJyTWFCVWZXTnlUMVlxMTBQDQpQUkMxejNNdDBqWmQzUjBXVnhYWDJOc3RHTURqMDI4T3hHVTgyNitxckpqWjFMTG1nNm52bk9ad0FYejd2c1Z0DQpiZENMU09RV0FQZ0hob0ZHcjh3L05PYTcrRDBXSXlYbUpaZTludUZicklKSjFmS3lCbFc3eG1JS1NoZXBpZjJLDQp4UUZWemwwT2Nibmp5RGsvK212WTZyNnhCU2NHNjZBeEVDZ09VYjlPSUp1eDdzd0RQcHl0TWVWNlpzU3p6MFE5DQpQVGFDempzTXZhYm9kaS9CcTdOSmJ2ZlJZSjhxdlkwazJoY2toRURUUXd0dWEwc21Dai9UMHFSdWRUdC90Q0pEDQovSUtRSDduUUhRRkg2dlUwR2tJa0lMTm5ES0dCV3RqaTRTOWo1TjQvY0ZoaEw3QnE4dThCRGU4UkswS0pCMzIwDQpLRHFUYXhGODloenk3Y0lWVVY3U3dIS1o3bUsxdkZVampXa1BWUT09DQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tDQo=";
    private static final String CERTIFICATE_FILE_LOCATION_WITH_CRT_EXTENSION = "trustcert.crt";
    private static final String CERTIFICATE_FILE_LOCATION_WITH_CER_EXTENSION = "trustcert.cer";
    private static final String CERTIFICATE_FILE_LOCATION_WITH_DER_EXTENSION = "trustcert.der";

    private static final String TRUST_STORE_FILE_WITH_UNSUPPORTED_EXTENSION = "trustcert.jpg";

    @RegisterExtension
    TokenRule token = TokenRule.createXsuaa();

    @Test
    void testMissingCertificateInformation()
    {
        final DestinationServiceAdapter destinationService = mock(DestinationServiceAdapter.class);
        final DestinationService loader = new DestinationService(destinationService);
        DestinationService.Cache.enableChangeDetection();

        final Map<String, String> destinationConfiguration =
            ImmutableMap
                .<String, String> builder()
                .put("KeyStorePassword", PKCS12_KEY_STORE_PASSWORD)
                .put("audience", "www.successfactors.com")
                .put("authnContextClassRef", "urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession")
                .put("WebIDEEnabled", "true")
                .put("tokenServiceUrl", "https://apisalesdemo2.successfactors.eu:443/oauth/token")
                .put("KeyStore", "key-store")
                .put("URL", "https://apisalesdemo2.successfactors.eu:443")
                .put("Name", "sfapi_dest")
                .put("Type", "HTTP")
                .put("companyId", "comepany-id")
                .put("XFSystemName", "SFSF_SalesDemo")
                .put("KeyStoreLocation", PKCS12_FILE_KEY_STORE_LOCATION)
                .put("clientKey", "client-key")
                .put("Authentication", "OAuth2SAMLBearerAssertion")
                .put("nameIdFormat", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")
                .put("ProxyType", "Internet")
                .build();
        final Map<String, String> certificate1 =
            ImmutableMap
                .<String, String> builder()
                .put("Name", PKCS12_FILE_KEY_STORE_LOCATION)
                .put("Content", PKCS12_FILE_KEY_STORE_CONTENT)
                //.put("Type", "Certificate") // <-- test condition: missing certificate.type
                .build();
        final Map<String, Object> destinationData =
            ImmutableMap
                .<String, Object> builder()
                .put("destinationConfiguration", destinationConfiguration)
                .put("certificates", org.assertj.core.util.Lists.newArrayList(certificate1))
                .build();
        doReturn(new Gson().toJson(destinationData))
            .when(destinationService)
            .getConfigurationAsJsonWithUserToken(eq("/destinations/ABC"), any(OnBehalfOf.class));

        final Principal p1 = new DefaultPrincipal("P1");
        final Destination destination =
            PrincipalAccessor.executeWithPrincipal(p1, () -> loader.tryGetDestination("ABC").get());
        final HttpDestination dest = destination.asHttp();
        assertThat(dest.getKeyStore()).isNotNull();
        assertThat(dest.get(DestinationProperty.CERTIFICATES)).isNotEmpty();

        @SuppressWarnings( "unchecked" )
        final List<DestinationCertificate> certs =
            (List<DestinationCertificate>) dest.get(DestinationProperty.CERTIFICATES).get();
        assertThat(certs).isNotEmpty();
        assertThat(certs.get(0).getExpiryTimestamp()).isNotNull();
    }

    @Test
    void testGetKeyStoreWithJksFile()
        throws KeyStoreException
    {
        final String fileLocation = JKS_FILE_KEY_STORE_LOCATION;
        final String fileContent = JKS_FILE_KEY_STORE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                .build();

        final KeyStore actualKeyStore =
            new DestinationKeyStoreExtractor(testDestination)
                .getKeyStore(DestinationProperty.TRUST_STORE_LOCATION, DestinationProperty.TRUST_STORE_PASSWORD)
                .get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromJksFile();

        assertThat(actualKeyStore.getCertificate(fileLocation))
            .isEqualTo(expectedKeyStore.getCertificate(fileLocation));
    }

    @Test
    void testGetKeyStoreWithPkcs12File()
        throws KeyStoreException
    {
        final String fileLocation = PKCS12_FILE_KEY_STORE_LOCATION;
        final String fileContent = PKCS12_FILE_KEY_STORE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.KEY_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.KEY_STORE_PASSWORD, PKCS12_KEY_STORE_PASSWORD)
                .property(DestinationProperty.CERTIFICATES, createCertificateJson(fileLocation, fileContent, null))
                .build();

        final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getKeyStore().get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromPkcsFile();

        final String cert = "1";
        assertThat(actualKeyStore.getCertificate(cert)).isEqualTo(expectedKeyStore.getCertificate(cert)).isNotNull();
    }

    @Test
    void testGetTrustStoreFromCrtFile()
        throws KeyStoreException
    {
        final String fileLocation = CERTIFICATE_FILE_LOCATION_WITH_CRT_EXTENSION;
        final String fileContent = CERTIFICATE_FILE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                .build();

        final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getTrustStore().get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromCertificateFile(fileLocation);

        assertThat(actualKeyStore.getCertificate(fileLocation))
            .isEqualTo(expectedKeyStore.getCertificate(fileLocation));
    }

    @Test
    void testGetTrustStoreFromCerFile()
        throws KeyStoreException
    {
        final String fileLocation = CERTIFICATE_FILE_LOCATION_WITH_CER_EXTENSION;
        final String fileContent = CERTIFICATE_FILE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                .build();

        final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getTrustStore().get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromCertificateFile(fileLocation);

        assertThat(actualKeyStore.getCertificate(fileLocation))
            .isEqualTo(expectedKeyStore.getCertificate(fileLocation));
    }

    @Test
    void testGetTrustStoreFromJksFile()
        throws KeyStoreException
    {
        final String fileLocation = JKS_FILE_KEY_STORE_LOCATION;
        final String fileContent = JKS_FILE_KEY_STORE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                .build();

        final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getTrustStore().get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromJksFile();

        assertThat(actualKeyStore.getCertificate(fileLocation))
            .isEqualTo(expectedKeyStore.getCertificate(fileLocation));
    }

    @Test
    void testGetKeyStoreWithPkcs12FileNoPassword()
    {
        final String fileContent = PKCS12_FILE_KEY_STORE_CONTENT_NO_PASSWORD;

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.KEY_STORE_LOCATION, PKCS12_FILE_KEY_STORE_LOCATION)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(PKCS12_FILE_KEY_STORE_LOCATION, fileContent, null))
                .build();

        final Option<KeyStore> keyStore = new DestinationKeyStoreExtractor(destination).getKeyStore();
        assertThat(keyStore).isNotEmpty();
    }

    @Test
    void testGetKeyStoreWithPkcs12FileEmptyPassword()
    {
        final String fileContent = PKCS12_FILE_KEY_STORE_CONTENT_NO_PASSWORD;

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.KEY_STORE_LOCATION, PKCS12_FILE_KEY_STORE_LOCATION)
                .property(DestinationProperty.KEY_STORE_PASSWORD, "")
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(PKCS12_FILE_KEY_STORE_LOCATION, fileContent, null))
                .build();

        final Option<KeyStore> keyStore = new DestinationKeyStoreExtractor(destination).getKeyStore();
        assertThat(keyStore).isNotEmpty();
    }

    @Test
    void testGetTrustStoreFromUnsupportedFileExtension()
    {
        assertThatExceptionOfType(DestinationAccessException.class).isThrownBy(() -> {
            final String fileLocation = TRUST_STORE_FILE_WITH_UNSUPPORTED_EXTENSION;
            final String fileContent = JKS_FILE_KEY_STORE_CONTENT;

            final DefaultHttpDestination testDestination =
                DefaultHttpDestination
                    .builder(VALID_URI)
                    .name(MOCKED_DESTINATION_NAME)
                    .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                    .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                    .property(
                        DestinationProperty.CERTIFICATES,
                        createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                    .build();

            final AuthTokenHeaderProvider propertyFactoryUnderTest = new AuthTokenHeaderProvider();

            final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getTrustStore().get();
        });
    }

    @Test
    void testGetTrustStoreFromDerFile()
        throws KeyStoreException
    {
        final String fileLocation = CERTIFICATE_FILE_LOCATION_WITH_DER_EXTENSION;
        final String fileContent = CERTIFICATE_FILE_CONTENT;

        final DefaultHttpDestination testDestination =
            DefaultHttpDestination
                .builder(VALID_URI)
                .name(MOCKED_DESTINATION_NAME)
                .property(DestinationProperty.TRUST_STORE_LOCATION, fileLocation)
                .property(DestinationProperty.TRUST_STORE_PASSWORD, KEY_STORE_PASSWORD)
                .property(
                    DestinationProperty.CERTIFICATES,
                    createCertificateJson(fileLocation, fileContent, "CERTIFICATE"))
                .build();

        final KeyStore actualKeyStore = new DestinationKeyStoreExtractor(testDestination).getTrustStore().get();

        final KeyStore expectedKeyStore = createKeyStoreObjectFromCertificateFile(fileLocation);

        assertThat(actualKeyStore.getCertificate(fileLocation))
            .isEqualTo(expectedKeyStore.getCertificate(fileLocation));
    }

    // helper methods

    private List<DestinationCertificate> createCertificateJson(
        @Nonnull final String fileLocation,
        @Nonnull final String fileContent,
        @Nullable final String fileType )
    {
        return new Gson()
            .fromJson(
                String
                    .format(
                        "[{ \"Name\": \"%s\", \"Content\": \"%s\", \"Type\": %s }]",
                        fileLocation,
                        fileContent,
                        fileType != null ? "\"" + fileType + "\"" : null),
                new TypeToken<List<DestinationCertificate>>()
                {
                }.getType());
    }

    private KeyStore createKeyStoreObjectFromJksFile()
    {
        final byte[] bytes = Base64.getDecoder().decode(JKS_FILE_KEY_STORE_CONTENT);

        try( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(is, KEY_STORE_PASSWORD.toCharArray());
            return ks;
        }
        catch( final IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
            throw new ShouldNotHappenException(e);
        }
    }

    private KeyStore createKeyStoreObjectFromCertificateFile( final String fileLocatioh )
    {
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            final byte[] bytes = Base64.getDecoder().decode(CERTIFICATE_FILE_CONTENT);

            try( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
                final Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(is);
                keyStore.load(null, "doesnotmatter".toCharArray());
                keyStore.setCertificateEntry(fileLocatioh, certificate);
                return keyStore;
            }
            catch( final KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e ) {
                throw new ShouldNotHappenException(e);
            }
        }
        catch( final KeyStoreException e ) {
            throw new ShouldNotHappenException(e);
        }
    }

    private KeyStore createKeyStoreObjectFromPkcsFile()
    {
        final byte[] bytes = Base64.getDecoder().decode(PKCS12_FILE_KEY_STORE_CONTENT);

        try( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
            final KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(is, PKCS12_KEY_STORE_PASSWORD.toCharArray());
            return ks;
        }
        catch( final IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e ) {
            throw new ShouldNotHappenException(e);
        }
    }
}
