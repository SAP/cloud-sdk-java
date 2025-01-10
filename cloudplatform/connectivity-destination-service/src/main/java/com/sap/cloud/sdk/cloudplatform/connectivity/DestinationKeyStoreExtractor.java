package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.KEY_STORE_LOCATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.KEY_STORE_PASSWORD;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.TRUST_STORE_LOCATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.TRUST_STORE_PASSWORD;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationServiceV1Response.DestinationCertificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
@SuppressWarnings( "PMD.TooManyStaticImports" )
class DestinationKeyStoreExtractor
{
    // See the supported key store file extensions:
    // https://help.sap.com/viewer/cca91383641e40ffbe03bdc78f00f681/Cloud/en-US/df1bb55a526942b9bee78fea2ebb3162.html
    // Mapping file extension to key store types
    private static final Map<String, BiFunction<String, String, KeyStore>> SUPPORTED_KEY_STORES =
        ImmutableMap
            .<String, BiFunction<String, String, KeyStore>> builder()
            .put("pfx", ( ks, pw ) -> retrieveExistingKeyStore(ks, pw, "PKCS12"))
            .put("p12", ( ks, pw ) -> retrieveExistingKeyStore(ks, pw, "PKCS12"))
            .put("jks", ( ks, pw ) -> retrieveExistingKeyStore(ks, pw, "JKS"))
            .put("pem", DestinationKeyStoreExtractor::createNewKeyStoreFromPem)
            .build();

    //Check out supported trust store file extensions here:
    //https://help.sap.com/viewer/cca91383641e40ffbe03bdc78f00f681/Cloud/en-US/df1bb55a526942b9bee78fea2ebb3162.html
    //Mapping file extension to key store types
    static final Map<String, String> SUPPORTED_KEY_STORE_TYPES_AS_TRUST_STORE = ImmutableMap.of("jks", "JKS");

    static final List<String> SUPPORTED_CERT_FILE_EXTENSIONS_AS_TRUST_STORE = ImmutableList.of("crt", "cer", "der");

    @Nonnull
    private final PropertyKeyExtractor destination;

    interface PropertyKeyExtractor
    {
        @Nonnull
        <T> Option<T> get( @Nonnull final DestinationPropertyKey<T> key );
    }

    DestinationKeyStoreExtractor( @Nonnull final DestinationProperties destination )
    {
        this(destination::get);
    }

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
        if( destination.get(TRUST_STORE_LOCATION).isEmpty() ) {
            return Option.none();
        }

        if( log.isDebugEnabled() ) {
            log
                .debug(
                    "Properties {} and {} found for destination {}",
                    TRUST_STORE_LOCATION.getKeyName(),
                    TRUST_STORE_PASSWORD.getKeyName(),
                    destination.get(DestinationProperty.NAME).getOrElse("without name"));
        }

        final DestinationCertificate cert = getDestinationCertificateFromProperty(TRUST_STORE_LOCATION);
        final String keyStorePassword = destination.get(TRUST_STORE_PASSWORD).getOrNull();
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
        if( destination.get(KEY_STORE_LOCATION).isEmpty() || !authTypeRequiresLoadingKeyMaterial(destination) ) {
            return Option.none();
        }

        if( log.isDebugEnabled() ) {
            log
                .debug(
                    "Properties {} and {} found for destination {}",
                    KEY_STORE_LOCATION.getKeyName(),
                    KEY_STORE_PASSWORD.getKeyName(),
                    destination.get(DestinationProperty.NAME).getOrElse("without name"));
        }

        final DestinationCertificate certificate = getDestinationCertificateFromProperty(KEY_STORE_LOCATION);

        final BiFunction<String, String, KeyStore> storeTransformer =
            getKeyStoreTransformerByFileName(certificate.getName());
        final String storePassword = destination.get(KEY_STORE_PASSWORD).getOrNull();
        return Option.some(storeTransformer.apply(certificate.getContent(), storePassword));
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

        if( certificate.isEmpty() ) {
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
    private static BiFunction<String, String, KeyStore> getKeyStoreTransformerByFileName( @Nonnull final String name )
    {
        final String fileExtension = FilenameUtils.getExtension(name.toLowerCase());
        final BiFunction<String, String, KeyStore> typeOfFileExt = SUPPORTED_KEY_STORES.get(fileExtension);
        if( typeOfFileExt == null ) {
            final String message =
                "Could not create Key Store with file extension: %s. Supported extensions: "
                    + SUPPORTED_KEY_STORES.keySet();
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
            // password cannot be `null`, since PKCS#12 requires a password to access certificates (e.g. mTLS).
            final char[] password = keyStorePassword == null ? new char[0] : keyStorePassword.toCharArray();
            ks.load(is, password);
            return ks;
        }
        catch( final IOException | NoSuchAlgorithmException | CertificateException e ) {
            throw new DestinationAccessException("Failed to load key store.", e);
        }
    }

    @Nonnull
    static KeyStore createNewKeyStoreFromPem( @Nonnull final String data, @Nullable final String password )
    {
        try {
            final String decoded = new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8).trim();

            final Pattern pattern = Pattern.compile("-+BEGIN CERTIFICATE-+.*-+END CERTIFICATE-+", Pattern.DOTALL);
            final Matcher match = pattern.matcher(decoded);
            if( !match.find() ) {
                throw new IllegalArgumentException("PEM format cannot be parsed: No certificate entry found.");
            }
            final String key = (decoded.substring(0, match.start()) + decoded.substring(match.end())).trim();
            if( key.isEmpty() ) {
                throw new IllegalArgumentException("PEM format cannot be parsed: No private key entry found.");
            }

            final String alias = "1";
            final char[] pw = Strings.isNullOrEmpty(password) ? new char[0] : password.toCharArray();
            return KeyStoreReader.createKeyStore(alias, pw, new StringReader(match.group()), new StringReader(key));
        }
        catch( final Exception e ) {
            throw new DestinationAccessException("Failed to instantiate new KeyStore.", e);
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

    private static boolean authTypeRequiresLoadingKeyMaterial( @Nonnull final PropertyKeyExtractor destination )
    {
        final AuthenticationType authenticationType =
            destination.get(DestinationProperty.AUTH_TYPE).getOrElse(AuthenticationType.NO_AUTHENTICATION);
        return switch( authenticationType ) {
            case OAUTH2_SAML_BEARER_ASSERTION, SAML_ASSERTION -> false;
            default -> true;
        };
    }
}
