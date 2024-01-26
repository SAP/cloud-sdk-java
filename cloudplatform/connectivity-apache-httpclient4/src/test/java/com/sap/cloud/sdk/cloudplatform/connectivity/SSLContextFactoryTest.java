/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.util.Enumeration;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.PlatformSslContextProvider;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Try;

class SSLContextFactoryTest
{
    @ParameterizedTest
    @EnumSource( AuthenticationType.class )
    void testDestinationWithDefaultSettings( AuthenticationType authenticationType )
        throws GeneralSecurityException,
            IOException
    {
        final SSLContextBuilder sslContextBuilder = Mockito.spy(SSLContextBuilder.class);

        final DefaultHttpDestination destination =
            DefaultHttpDestination.builder("uri").authenticationType(authenticationType).build();

        final SSLContextFactory sslContextFactory = new SSLContextFactory(sslContextBuilder);

        sslContextFactory.createSSLContext(destination);

        Mockito.verify(sslContextBuilder, Mockito.never()).setProtocol(ArgumentMatchers.anyString());
        Mockito
            .verify(sslContextBuilder, Mockito.never())
            .loadKeyMaterial(ArgumentMatchers.any(KeyStore.class), ArgumentMatchers.any(char[].class));
        Mockito.verify(sslContextBuilder, Mockito.never()).loadTrustMaterial(TrustAllStrategy.INSTANCE);
        Mockito
            .verify(sslContextBuilder)
            .loadTrustMaterial(ArgumentMatchers.any(KeyStore.class), ArgumentMatchers.isNull());
    }

    @Test
    void testDestinationsWithSpecifiedTlsVersion()
        throws GeneralSecurityException,
            IOException
    {
        final String tlsVersion = "TLSv1.2";

        final SSLContextBuilder sslContextBuilder = Mockito.spy(SSLContextBuilder.class);

        final DefaultHttpDestination destination = DefaultHttpDestination.builder("uri").tlsVersion(tlsVersion).build();

        new SSLContextFactory(sslContextBuilder).createSSLContext(destination);

        Mockito.verify(sslContextBuilder).setProtocol(tlsVersion);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testDestinationWithSpecifiedKeyStore()
        throws GeneralSecurityException,
            IOException
    {
        final SSLContextBuilder sslContextBuilder = Mockito.mock(SSLContextBuilder.class);

        final KeyStoreSpi keyStoreSpiMock = Mockito.spy(KeyStoreSpi.class);
        final KeyStore keyStoreMock = new MyKeyStore(keyStoreSpiMock);
        keyStoreMock.load(null); // this is important to put the internal flag "initialized" to true

        final Enumeration<String> keyStoreAliases = (Enumeration<String>) Mockito.mock(Enumeration.class);

        Mockito.when(keyStoreSpiMock.engineAliases()).thenReturn(keyStoreAliases);
        Mockito.when(keyStoreAliases.hasMoreElements()).thenReturn(Boolean.FALSE);

        final DefaultHttpDestination destination =
            DefaultHttpDestination.builder("uri").keyStore(keyStoreMock).keyStorePassword("password").build();

        new SSLContextFactory(sslContextBuilder).createSSLContext(destination);

        Mockito.verify(sslContextBuilder).loadKeyMaterial(keyStoreMock, "password".toCharArray());
    }

    @Test
    void testDestinationsWhichTrustsAllCertificates()
        throws GeneralSecurityException,
            IOException
    {
        final SSLContextBuilder sslContextBuilder = Mockito.spy(SSLContextBuilder.class);

        final DefaultHttpDestination destination = DefaultHttpDestination.builder("uri").trustAllCertificates().build();

        new SSLContextFactory(sslContextBuilder).createSSLContext(destination);

        Mockito.verify(sslContextBuilder).loadTrustMaterial(TrustAllStrategy.INSTANCE);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testDestinationsWithSpecifiedTrustStore()
        throws GeneralSecurityException,
            IOException
    {
        final SSLContextBuilder sslContextBuilder = Mockito.spy(SSLContextBuilder.class);

        final KeyStoreSpi keyStoreSpiMock = Mockito.spy(KeyStoreSpi.class);
        final KeyStore trustStore = new MyKeyStore(keyStoreSpiMock);
        trustStore.load(null); // this is important to put the internal flag "initialized" to true

        final Enumeration<String> keyStoreAliases = (Enumeration<String>) Mockito.mock(Enumeration.class);

        Mockito.when(keyStoreSpiMock.engineAliases()).thenReturn(keyStoreAliases);
        Mockito.when(keyStoreAliases.hasMoreElements()).thenReturn(Boolean.FALSE);

        final DefaultHttpDestination destination = DefaultHttpDestination.builder("uri").trustStore(trustStore).build();

        new SSLContextFactory(sslContextBuilder).createSSLContext(destination);

        Mockito.verify(sslContextBuilder).loadTrustMaterial(trustStore, null);
    }

    @Test
    void testGetSecuritySettingsFromCloudPlatformSucceeds()
        throws GeneralSecurityException,
            IOException
    {
        final PlatformSslContextProvider provider = mockAndGetSSLContextSuccessfully();

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("uri")
                .securityConfiguration(SecurityConfigurationStrategy.FROM_PLATFORM)
                .build();

        final SSLContextFactory sut = new SSLContextFactory(provider);
        final SSLContext sslContext = sut.createSSLContext(destination);

        assertThat(provider.tryGetContext().get()).isSameAs(sslContext);
    }

    @Test
    void testGetSecuritySettingsFromCloudPlatformFailing()
    {
        final PlatformSslContextProvider provider = mockAndGetSSLContextSFailing();

        final SSLContextFactory sut = new SSLContextFactory(provider);

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder("uri")
                .securityConfiguration(SecurityConfigurationStrategy.FROM_PLATFORM)
                .build();

        assertThatExceptionOfType(CloudPlatformException.class).isThrownBy(() -> sut.createSSLContext(destination));
    }

    private PlatformSslContextProvider mockAndGetSSLContextSuccessfully()
    {
        final PlatformSslContextProvider sslContextProvider = Mockito.spy(PlatformSslContextProvider.class);

        final SSLContext sslContext = Mockito.mock(SSLContext.class);

        Mockito.doReturn(Try.success(sslContext)).when(sslContextProvider).tryGetContext();

        return sslContextProvider;
    }

    private PlatformSslContextProvider mockAndGetSSLContextSFailing()
    {
        final PlatformSslContextProvider sslContextProvider = Mockito.spy(PlatformSslContextProvider.class);

        Mockito
            .doReturn(Try.failure(new CloudPlatformException("Failing on purpose")))
            .when(sslContextProvider)
            .tryGetContext();

        return sslContextProvider;
    }

    private static class MyKeyStore extends KeyStore
    {
        MyKeyStore( final KeyStoreSpi keyStoreSpiMock )
        {
            super(keyStoreSpiMock, null, "test");
        }
    }
}
