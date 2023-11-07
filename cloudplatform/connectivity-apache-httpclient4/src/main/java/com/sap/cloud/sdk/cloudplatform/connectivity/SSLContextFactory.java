/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;

import com.sap.cloud.sdk.cloudplatform.PlatformSslContextProvider;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SSLContextFactory
{
    private static final KeyStoreMetadata JDK_TRUST_STORE_METADATA;
    @Nonnull
    private final PlatformSslContextProvider sslContextProvider;

    static {
        final File trustStoreFile =
            Option
                .of(System.getProperty("javax.net.ssl.trustStore"))
                .map(File::new)
                .getOrElse(() -> new File(System.getProperty("java.home"), "/lib/security/cacerts"));

        @Nullable
        final String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");

        JDK_TRUST_STORE_METADATA = new KeyStoreMetadata(trustStoreFile, trustStorePassword);
    }

    private final SSLContextBuilder sslContextBuilder;

    SSLContextFactory(
        @Nullable final SSLContextBuilder sslContextBuilder,
        @Nullable final PlatformSslContextProvider sslContextProvider )
    {
        this.sslContextBuilder = sslContextBuilder != null ? sslContextBuilder : SSLContextBuilder.create();
        this.sslContextProvider = sslContextProvider != null ? sslContextProvider : new CfPlatformSslContextProvider();
    }

    SSLContextFactory( @Nullable final PlatformSslContextProvider sslContextProvider )
    {
        this(null, sslContextProvider);
    }

    SSLContextFactory( @Nonnull final SSLContextBuilder sslContextBuilder )
    {
        this(sslContextBuilder, null);
    }

    SSLContextFactory()
    {
        this(null, null);
    }

    @Nonnull
    SSLContext createSSLContext( @Nonnull final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        switch( destination.getSecurityConfigurationStrategy() ) {
            case FROM_PLATFORM:
                return getPlatformSslContext();
            case FROM_DESTINATION:
            default:
                return getSSLContextFromDestination(destination);
        }
    }

    private SSLContext getPlatformSslContext()
        throws CloudPlatformException
    {
        return sslContextProvider
            .tryGetContext()
            .getOrElseThrow(e -> new CloudPlatformException("Failed to create default SSL context", e));
    }

    private SSLContext getSSLContextFromDestination( @Nonnull final HttpDestinationProperties destination )
        throws GeneralSecurityException,
            IOException
    {
        setTlsVersion(destination, sslContextBuilder);

        configureTrustSettings(destination, sslContextBuilder);

        configureKeySettings(destination, sslContextBuilder);

        return sslContextBuilder.build();
    }

    private
        void
        setTlsVersion( @Nonnull final HttpDestinationProperties destination, final SSLContextBuilder sslContextBuilder )
    {
        destination.getTlsVersion().peek(tlsVersion -> {
            log.debug("Using TLS protocol version \"{}\".", tlsVersion);
            sslContextBuilder.setProtocol(tlsVersion);
        });
    }

    /**
     * Logic is as follows: - If the Trust All property is set on the destination, we blindly trust all server
     * certificates. - Otherwise, if a dedicated trust store is set on the destination, we use that one. - Otherwise, we
     * fall back to the JDK default trust store
     */
    private void configureTrustSettings(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final SSLContextBuilder sslContextBuilder )
        throws GeneralSecurityException,
            IOException
    {
        if( destination.isTrustingAllCertificates() ) {
            log.debug("Trusting all certificates.");
            sslContextBuilder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
        } else {
            final Option<KeyStore> maybeTrustStore = destination.getTrustStore();

            if( maybeTrustStore.isDefined() ) {
                log.debug("Using trust store of destination.");
                sslContextBuilder.loadTrustMaterial(maybeTrustStore.get(), null);
            } else {
                log.debug("Using JDK default trust store.");

                final KeyStore jdkTrustStore = loadTrustStore(JDK_TRUST_STORE_METADATA);

                sslContextBuilder.loadTrustMaterial(jdkTrustStore, null);
            }
        }
    }

    @Nonnull
    private KeyStore loadTrustStore( @Nonnull final KeyStoreMetadata keyStoreMetadata )
        throws IOException,
            GeneralSecurityException
    {
        final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        final File canonicalFile = keyStoreMetadata.getFilePath().getCanonicalFile();
        final Option<String> maybePassword = keyStoreMetadata.getPassword();
        final char[] keyStorePassword = maybePassword.map(String::toCharArray).getOrNull();

        try( InputStream is = Files.newInputStream(canonicalFile.toPath()) ) {
            keyStore.load(is, keyStorePassword);
        }

        return keyStore;
    }

    private void configureKeySettings(
        @Nonnull final HttpDestinationProperties destination,
        @Nonnull final SSLContextBuilder sslContextBuilder )
        throws GeneralSecurityException
    {
        final Option<KeyStore> maybeKeyStore = destination.getKeyStore();
        if( !maybeKeyStore.isDefined() ) {
            return;
        }
        if( !authTypeRequiresLoadingKeyMaterial(destination.getAuthenticationType()) ) {
            return;
        }

        final KeyStore keyStore = maybeKeyStore.get();
        if( log.isDebugEnabled() ) {
            log.debug("Using key store of destination with aliases: {}", Collections.list(keyStore.aliases()));
        }

        final char[] keyStorePassword =
            destination
                .getKeyStorePassword()
                .map(String::toCharArray)
                .onEmpty(() -> log.debug("Using key store without password."))
                .getOrElse(() -> new char[0]);

        sslContextBuilder.loadKeyMaterial(keyStore, keyStorePassword);
    }

    private boolean authTypeRequiresLoadingKeyMaterial( final AuthenticationType authenticationType )
    {
        return authenticationType != AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION
            && authenticationType != AuthenticationType.SAML_ASSERTION;
    }

    @Value
    private static class KeyStoreMetadata
    {
        File filePath;
        @Nullable
        String password;

        Option<String> getPassword()
        {
            return Option.of(password);
        }
    }
}
