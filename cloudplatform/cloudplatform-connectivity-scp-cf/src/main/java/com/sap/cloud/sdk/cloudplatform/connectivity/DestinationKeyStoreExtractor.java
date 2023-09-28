/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfDestinationServiceV1Response.DestinationCertificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class DestinationKeyStoreExtractor
{
    // @formatter:off
    // See the supported key store file extensions:
    // https://help.sap.com/viewer/cca91383641e40ffbe03bdc78f00f681/Cloud/en-US/df1bb55a526942b9bee78fea2ebb3162.html
    //Mapping file extension to key store types
    private static final Map<String, String> SUPPORTED_KEY_STORE_TYPES_AS_KEY_STORE = ImmutableMap.of(
            "pfx", "PKCS12",
            "p12", "PKCS12",
            "jks", "JKS"
    );

    //Check out supported trust store file extensions here:
    //https://help.sap.com/viewer/cca91383641e40ffbe03bdc78f00f681/Cloud/en-US/df1bb55a526942b9bee78fea2ebb3162.html
    //Mapping file extension to key store types
    static final Map<String, String> SUPPORTED_KEY_STORE_TYPES_AS_TRUST_STORE = ImmutableMap.of("jks", "JKS");

    static final List<String> SUPPORTED_CERT_FILE_EXTENSIONS_AS_TRUST_STORE = ImmutableList.of("crt", "cer", "der");
    // @formatter:on

    @Nonnull
    private final DestinationProperties destination;

    /*
     *  The destination configuration on Cloud Foundry allows to upload a trust store.
     *  In the JDK, this concept is realized by the class KeyStore.
     *  In order to avoid confusion why a trust store is fetched using the method getKeyStore,
     *  we introduce the method getTrustStore which delegates to getKeyStore.
     */
    @Nonnull
    Option<KeyStore> getTrustStore()
        throws DestinationAccessException
    {
        return getTrustStore(DestinationProperty.TRUST_STORE_LOCATION, DestinationProperty.TRUST_STORE_PASSWORD);
    }

    @Nonnull
    Option<KeyStore> getTrustStore(
        @Nonnull final DestinationPropertyKey<String> locationKey,
        @Nonnull final DestinationPropertyKey<String> passwordKey )
    {
        if( !destination.get(locationKey).isDefined() ) {
            return Option.none();
        }

        if( log.isDebugEnabled() ) {
            log
                .debug(
                    "Properties {} and {} found for destination {}",
                    locationKey.getKeyName(),
                    passwordKey.getKeyName(),
                    destination.get(DestinationProperty.NAME).getOrElse("without name"));
        }

        final DestinationCertificate cert = getDestinationCertificateFromProperty(locationKey);
        final String keyStorePassword = destination.get(passwordKey).getOrNull();
        final String fileExtension = FilenameUtils.getExtension(cert.getName().toLowerCase());

        // happy path: existing key-store:
        final String keyStoreType = SUPPORTED_KEY_STORE_TYPES_AS_TRUST_STORE.get(fileExtension);
        if( keyStoreType != null ) {
            log.debug("Creating a key store of type {} with file extension {}.", keyStoreType, fileExtension);
            final KeyStore keyStore = retrieveExistingKeyStore(cert.getContent(), keyStorePassword, keyStoreType);
            return Option.some(keyStore);
        }

        // happy-path: create key-store with existing certificate
        if( SUPPORTED_CERT_FILE_EXTENSIONS_AS_TRUST_STORE.contains(fileExtension) ) {
            final KeyStore keyStore = createKeyStoreFromCertificate(cert.getContent(), cert.getName());
            return Option.some(keyStore);
        }

        // unhappy: unsupported file extension
        final String extensions =
            Joiner
                .on(", ")
                .join(SUPPORTED_KEY_STORE_TYPES_AS_TRUST_STORE.keySet(), SUPPORTED_CERT_FILE_EXTENSIONS_AS_TRUST_STORE);
        final String message = "Could not create Trust Store from file \"%s\". Supported file extensions: %s";
        throw new DestinationAccessException(String.format(message, cert.getName(), extensions));
    }

    @Nonnull
    Option<KeyStore> getKeyStore()
        throws DestinationAccessException
    {
        return getKeyStore(DestinationProperty.KEY_STORE_LOCATION, DestinationProperty.KEY_STORE_PASSWORD);
    }

    @Nonnull
    Option<KeyStore> getKeyStore(
        @Nonnull final DestinationPropertyKey<String> locationKey,
        @Nonnull final DestinationPropertyKey<String> passwordKey )
    {
        if( !destination.get(locationKey).isDefined() ) {
            return Option.none();
        }

        if( log.isDebugEnabled() ) {
            log
                .debug(
                    "Properties {} and {} found for destination {}",
                    locationKey.getKeyName(),
                    passwordKey.getKeyName(),
                    destination.get(DestinationProperty.NAME).getOrElse("without name"));
        }

        final DestinationCertificate certificate = getDestinationCertificateFromProperty(locationKey);

        final String storeType = getKeyStoreTypeByFileName(certificate.getName());
        final String storePassword = destination.get(passwordKey).getOrNull();
        final KeyStore store = retrieveExistingKeyStore(certificate.getContent(), storePassword, storeType);
        return Option.some(store);
    }

    @Nonnull
    private DestinationCertificate getDestinationCertificateFromProperty(
        @Nonnull final DestinationPropertyKey<String> locationKey )
    {
        final String keyStoreLocation = destination.get(locationKey).get();

        final List<?> certs = destination.get(DestinationProperty.CERTIFICATES).getOrElse(Collections::emptyList);
        log
            .debug(
                "Considering Key Store Location {}. Found {} key store certificates.",
                keyStoreLocation,
                certs.size());

        final Optional<DestinationCertificate> certificate =
            certs
                .stream()
                .filter(DestinationCertificate.class::isInstance)
                .map(DestinationCertificate.class::cast)
                .filter(cert -> hasCertificateName(keyStoreLocation, cert.getName()))
                .filter(cert -> hasCertificateContent(cert.getContent()))
                .findFirst();

        if( !certificate.isPresent() ) {
            throw new DestinationAccessException(
                String
                    .format(
                        "Failed to resolve key store '%s' in destination '%s' as no matching certificate was found.",
                        keyStoreLocation,
                        destination.get(DestinationProperty.NAME).getOrElse("without name")));
        }

        log.trace("Loaded destination certificate: {}", certificate);
        return certificate.get();
    }

    @Nonnull
    private static String getKeyStoreTypeByFileName( @Nonnull final String name )
    {
        final String fileExtension = FilenameUtils.getExtension(name.toLowerCase());
        final String typeOfFileExt = SUPPORTED_KEY_STORE_TYPES_AS_KEY_STORE.get(fileExtension);
        if( typeOfFileExt == null ) {
            final String message =
                "Could not create Key Store with file extension: %s. Supported extensions: "
                    + SUPPORTED_KEY_STORE_TYPES_AS_KEY_STORE.keySet();
            throw new DestinationAccessException(String.format(message, fileExtension));
        }
        return typeOfFileExt;
    }

    @Nonnull
    private static
        KeyStore
        createKeyStoreFromCertificate( @Nonnull final String fileContent, final String fileLocation )
    {
        final String keyStoreType = KeyStore.getDefaultType();
        log.debug("Creating a key store of type {}.", keyStoreType);
        final KeyStore ks =
            Try
                .of(() -> KeyStore.getInstance(keyStoreType))
                .getOrElseThrow(e -> new DestinationAccessException("Failed to load key store.", e));

        final byte[] bytes = Base64.getDecoder().decode(fileContent);

        try( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
            final Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(is);
            ks.load(null, null);
            ks.setCertificateEntry(fileLocation, certificate);
            return ks;
        }
        catch( final KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e ) {
            throw new DestinationAccessException("Failed to load key store.", e);
        }
    }

    @Nonnull
    private static KeyStore retrieveExistingKeyStore(
        @Nonnull final String fileContent,
        @Nullable final String keyStorePassword,
        @Nonnull final String keyStoreType )
    {
        log.debug("Creating a key store of type {}.", keyStoreType);
        final KeyStore ks =
            Try
                .of(() -> KeyStore.getInstance(keyStoreType))
                .getOrElseThrow(e -> new DestinationAccessException("Failed to load key store.", e));

        final byte[] bytes = Base64.getDecoder().decode(fileContent);

        try( ByteArrayInputStream is = new ByteArrayInputStream(bytes) ) {
            ks.load(is, Strings.isNullOrEmpty(keyStorePassword) ? null : keyStorePassword.toCharArray());
            return ks;
        }
        catch( final IOException | NoSuchAlgorithmException | CertificateException e ) {
            throw new DestinationAccessException("Failed to load key store.", e);
        }
    }

    private static boolean hasCertificateName( final String keyStoreLocation, @Nullable final String name )
    {
        return name != null && name.equals(keyStoreLocation);
    }

    private static boolean hasCertificateContent( @Nullable final String content )
    {
        return content != null;
    }
}
