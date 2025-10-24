package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.spiffe.bundle.x509bundle.X509Bundle;
import io.spiffe.exception.X509SvidException;
import io.spiffe.spiffeid.TrustDomain;
import io.spiffe.svid.x509svid.X509Svid;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.DefaultX509Source.X509SourceOptions;
import io.spiffe.workloadapi.X509Source;
import io.vavr.Lazy;
import io.vavr.NotImplementedError;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation to access X.509 certificates provided by the Zero Trust Identity Service (ZTIS).
 *
 * @since 5.7.0
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class ZeroTrustIdentityService
{
    static final ServiceIdentifier ZTIS_IDENTIFIER = ServiceIdentifier.of("zero-trust-identity");
    private static final String DEFAULT_SOCKET_PATH = "unix:///tmp/spire-agent/public/api.sock";
    private static final String SOCKET_ENVIRONMENT_VARIABLE = "SPIFFE_ENDPOINT_SOCKET";
    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(10);
    @Getter
    private static final ZeroTrustIdentityService instance = new ZeroTrustIdentityService();
    private final Lazy<X509Source> source = Lazy.of(this::initX509Source);
    private final Lazy<TypedMapView> credentials;
    private KeyStoreCache keyStoreCache = null;

    private record KeyStoreCache( KeyStore keyStore, X509Svid svid )
    {
    }

    private ZeroTrustIdentityService()
    {
        credentials = Lazy.of(ZeroTrustIdentityService::loadBinding);
    }

    ZeroTrustIdentityService( @Nonnull final ServiceBinding binding )
    {
        credentials = Lazy.of(() -> TypedMapView.ofCredentials(binding));
    }

    // Do not evaluate this prematurely, it requires the service binding to be present
    // implicitly synchronized via VAVR's Lazy
    private static TypedMapView loadBinding()
    {
        return DefaultServiceBindingAccessor
            .getInstance()
            .getServiceBindings()
            .stream()
            .filter(it -> ZTIS_IDENTIFIER.equals(it.getServiceIdentifier().orElse(null)))
            .map(TypedMapView::ofCredentials)
            .findFirst()
            .orElseThrow(() -> new ServiceBindingAccessException("No Zero Trust Identity Service binding found."));
    }

    // Do not evaluate this prematurely, it requires:
    // - the service binding to be present
    // - the SPIRE agent to be running and the OS specific dependencies are available at runtime
    // implicitly synchronized via VAVR's Lazy
    // package private for testing
    X509Source initX509Source()
    {
        // this throws if there is no binding
        // we enforce a binding to always be present, even though we don't necessarily need it for loading from the socket
        // but the sidecar only works if the binding is present, so it's fine to throw here
        final TypedMapView mapView = credentials.get();

        // for local testing the certificate can be provided on the file system
        if( mapView.containsKey("certPath") && mapView.containsKey("keyPath") ) {
            final String message =
                """
                    Found 'certPath' and 'keyPath' in Zero Trust Identity Service binding. Loading X509 SVID from the given file system location. \
                    This is intended for local testing only, since the certificates will not be rotated automatically.\
                    """;
            log.info(message);
            return new FileSystemX509Source();
        }

        final String socketPath = Option.of(System.getenv(SOCKET_ENVIRONMENT_VARIABLE)).getOrElse(DEFAULT_SOCKET_PATH);
        log.info("Using socket path {} for ZTIS agent.", socketPath);

        final X509SourceOptions x509SourceOptions =
            X509SourceOptions.builder().spiffeSocketPath(socketPath).initTimeout(DEFAULT_SOCKET_TIMEOUT).build();
        try {
            return DefaultX509Source.newSource(x509SourceOptions);
        }
        catch( final Exception e ) {
            throw new CloudPlatformException("Failed to load the certificate from the unix socket: " + socketPath, e);
        }
    }

    @Nonnull
    X509Svid getX509Svid()
    {
        try {
            final X509Svid svid = source.get().getX509Svid();
            return Objects.requireNonNull(svid, "X.509 certificate must not be null.");
        }
        catch( Exception e ) {
            final String message =
                "Failed to load a certificate using the Zero Trust Identity service. "
                    + "Please ensure your application is configured correctly according to: "
                    + "https://sap.github.io/cloud-sdk/docs/java/features/connectivity/mtls";
            throw new CloudPlatformException(message, e);
        }
    }

    /**
     * Returns the SPIFFE ID assigned to the app via the Zero Trust Identity service binding. This should always be
     * equivalent to calling {@link #getX509Svid()} with {@link X509Svid#getSpiffeId()}.
     *
     * @return The SPIFFE ID.
     * @since 5.7.0
     */
    @Nonnull
    public String getSpiffeId()
    {
        return credentials.get().getMapView("workload").getString("spiffeID");
    }

    /**
     * Returns the app identifier assigned to the app via the Zero Trust Identity service binding.
     *
     * @return The app identifier.
     * @since 5.7.0
     */
    @Nonnull
    public Option<String> getAppIdentifier()
    {
        final TypedMapView mapView = credentials.get();
        return Option
            .some(mapView)
            .filter(m -> m.containsKey("parameters"))
            .map(m -> m.getMapView("parameters"))
            .filter(m -> m.containsKey("app-identifier"))
            .map(m -> m.getString("app-identifier"))
            .filter(s -> !s.isBlank());
    }

    /**
     * Returns a KeyStore containing the X.509 certificate and key provided by the Zero Trust Identity Service. The
     * KeyStore is cached and will be reloaded if the certificate expires.
     *
     * @return a KeyStore containing the X.509 certificate and key.
     * @since 5.7.0
     */
    @Nonnull
    public KeyStore getOrCreateKeyStore()
    {
        final X509Svid svid = getX509Svid();

        if( isKeyStoreCached(svid) ) {
            return keyStoreCache.keyStore();
        }
        // double-checked locking
        synchronized( this ) {
            if( isKeyStoreCached(svid) ) {
                return keyStoreCache.keyStore();
            }
            assertSvidNotExpired(svid);
            final KeyStore keyStore = loadKeyStore(svid);
            keyStoreCache = new KeyStoreCache(keyStore, svid);
            return keyStore;
        }
    }

    private void assertSvidNotExpired( @Nonnull final X509Svid svid )
    {
        if( svid.getLeaf().getNotAfter().before(Date.from(Instant.now())) ) {
            throw new IllegalStateException(
                "The provided X509 SVID has expired. The expiry date was " + svid.getLeaf().getNotAfter() + ".");
        }
    }

    @Nonnull
    KeyStore loadKeyStore( @Nonnull final X509Svid svid )
    {
        log.debug("Creating new KeyStore for SVID with expiration date {}", svid.getLeaf().getNotAfter());
        final KeyStore.Entry privateKeyEntry = new PrivateKeyEntry(svid.getPrivateKey(), svid.getChainArray());
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setEntry("spiffe", privateKeyEntry, new KeyStore.PasswordProtection(new char[0]));
        }
        catch( final KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e ) {
            throw new CloudPlatformException(
                "Failed to load private key entry with X.509 certificate into a KeyStore.",
                e);
        }
        return keyStore;
    }

    boolean isKeyStoreCached( @Nonnull final X509Svid svid )
    {
        // X509Svid does implement equals, so we don't have to manually compare the certificates
        return keyStoreCache != null && svid.equals(keyStoreCache.svid());
    }

    @RequiredArgsConstructor
    private class FileSystemX509Source implements X509Source
    {
        private final Lazy<X509Svid> svid = Lazy.of(this::loadFromFileSystem);

        private X509Svid loadFromFileSystem()
        {
            final Path certPath = Path.of(credentials.get().getString("certPath"));
            final Path keyPath = Path.of(credentials.get().getString("keyPath"));

            try {
                return X509Svid.load(certPath, keyPath);
            }
            catch( X509SvidException e ) {
                throw new CloudPlatformException("Failed to load X509 SVID from file system.", e);
            }
        }

        @Nonnull
        @Override
        public X509Svid getX509Svid()
        {
            return svid.get();
        }

        @Nonnull
        @Override
        public X509Bundle getBundleForTrustDomain( @Nonnull final TrustDomain trustDomain )
        {
            throw new NotImplementedError();
        }

        @Override
        public void close()
        {

        }
    }
}
