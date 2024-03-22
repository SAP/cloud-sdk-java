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

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.TypedMapView;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.spiffe.bundle.x509bundle.X509Bundle;
import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.exception.X509SvidException;
import io.spiffe.spiffeid.TrustDomain;
import io.spiffe.svid.x509svid.X509Svid;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.DefaultX509Source.X509SourceOptions;
import io.spiffe.workloadapi.X509Source;
import io.vavr.Lazy;
import io.vavr.NotImplementedError;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation to access X.509 certificates provided by the Zero Trust Identity Service (ZTIS).
 */
@Beta
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class ZeroTrustIdentityService
{
    static final ServiceIdentifier ZTIS_IDENTIFIER = ServiceIdentifier.of("zero-trust-identity");
    private static final String DEFAULT_SOCKET_PATH = "unix:///tmp/spire-agent/public/api.sock";
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

    ZeroTrustIdentityService( ServiceBinding binding )
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
            String message =
                """
                    Found 'certPath' and 'keyPath' in Zero Trust Identity Service binding. Loading X509 SVID from the given file system location. \
                    This is intended for local testing only, since the certificates will not be rotated automatically.\
                    """;
            log.info(message);
            return new FileSystemX509Source();
        }

        final X509SourceOptions x509SourceOptions =
            X509SourceOptions
                .builder()
                .spiffeSocketPath(DEFAULT_SOCKET_PATH)
                .initTimeout(DEFAULT_SOCKET_TIMEOUT)
                .build();
        try {
            return DefaultX509Source.newSource(x509SourceOptions);
        }
        catch( final SocketEndpointAddressException | X509SourceException e ) {
            final String msg =
                """
                    Failed to initialize the Zero Trust Identity Service X509 source. \
                    Double-check that the SPIRE agent is running and the io.spiffe dependencies required for your operating system are present. \
                    For Cloud Foundry, the agent is typically provided by the zero_trust_sidecar_buildpack and the required dependencies are io.spiffe:grpc-netty-linux. \
                    """;
            throw new CloudPlatformException(msg, e);
        }
    }

    @Nonnull
    X509Svid getX509Svid()
    {
        return source.get().getX509Svid();
    }

    /**
     * Returns the SPIFFE ID assigned to the app via the Zero Trust Identity service binding. This should always be
     * equivalent to calling {@link #getX509Svid()} with {@link X509Svid#getSpiffeId()}.
     *
     * @return The SPIFFE ID.
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
     */
    @Nonnull
    public String getAppIdentifier()
    {
        return credentials.get().getMapView("parameters").getString("app-identifier");
    }

    /**
     * Returns a KeyStore containing the X.509 certificate and key provided by the Zero Trust Identity Service. The
     * KeyStore is cached and will be reloaded if the certificate expires.
     *
     * @return a KeyStore containing the X.509 certificate and key.
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

    private void assertSvidNotExpired( X509Svid svid )
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
            throw new RuntimeException(e);
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
        public X509Bundle getBundleForTrustDomain( @NonNull TrustDomain trustDomain )
        {
            throw new NotImplementedError();
        }

        @Override
        public void close()
        {

        }
    }
}
