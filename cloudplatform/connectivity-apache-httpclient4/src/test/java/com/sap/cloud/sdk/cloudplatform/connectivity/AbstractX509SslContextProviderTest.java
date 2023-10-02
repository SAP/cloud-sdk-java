/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.PlatformSslContextProvider;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

public class AbstractX509SslContextProviderTest
{
    @Test
    public void testCertificateParsing()
        throws IOException,
            CertificateException
    {
        final FileReader cert = new FileReader(getTestFile("valid_cert.pem"));
        final Certificate[] certificates = AbstractX509SslContextProvider.loadCertificates(cert);

        assertThat(certificates).isNotEmpty();
        assertThat(certificates[0].getType()).isEqualTo("X.509");
        final String certAsString = certificates[0].toString();
        assertThat(certAsString).contains("C=DE");
        assertThat(certAsString).contains("O=SAP Sandbox");
        assertThat(certAsString).contains("CN=SAP Sandbox CA");
    }

    @Test
    public void testCertificateParsingFailure()
        throws FileNotFoundException
    {
        final FileReader cert = new FileReader(getTestFile("invalid_cert"));
        assertThatThrownBy(() -> AbstractX509SslContextProvider.loadCertificates(cert))
            .isInstanceOf(CloudPlatformException.class);
    }

    @Test
    public void testKeyParsing()
        throws IOException
    {
        final FileReader key = new FileReader(getTestFile("valid_key.pem"));
        final PrivateKey privateKey = AbstractX509SslContextProvider.loadPrivateKey(key);

        assertThat(privateKey.getAlgorithm()).containsIgnoringCase("RSA");
    }

    @Test
    public void testKeyParsingFailure()
        throws FileNotFoundException
    {
        final FileReader key = new FileReader(getTestFile("invalid_key"));
        assertThatThrownBy(() -> AbstractX509SslContextProvider.loadPrivateKey(key))
            .isInstanceOf(CloudPlatformException.class);
    }

    @Test
    public void testContextCreation()
    {
        final PlatformSslContextProvider cut =
            new TestImplementation(getTestFile("valid_cert.pem"), getTestFile("valid_key.pem"));

        final Try<SSLContext> shouldBeSuccess = cut.tryGetContext();

        VavrAssertions.assertThat(shouldBeSuccess).isSuccess();
    }

    private File getTestFile( String fileName )
    {
        return new File(
            Objects
                .requireNonNull(
                    getClass()
                        .getClassLoader()
                        .getResource(AbstractX509SslContextProviderTest.class.getSimpleName() + "/" + fileName))
                .getFile());
    }

    @RequiredArgsConstructor
    private static final class TestImplementation extends AbstractX509SslContextProvider
    {

        final File certFile;
        final File keyFile;

        @Nonnull
        @Override
        public Try<SSLContext> tryGetContext()
        {
            final FileReader certReader;
            final FileReader keyReader;
            try {
                certReader = new FileReader(certFile);
                keyReader = new FileReader(keyFile);
            }
            catch( final FileNotFoundException e ) {
                return Try.failure(new CloudPlatformException("Failed to read test files.", e));
            }
            return super.tryGetContext(certReader, keyReader);
        }
    }
}
