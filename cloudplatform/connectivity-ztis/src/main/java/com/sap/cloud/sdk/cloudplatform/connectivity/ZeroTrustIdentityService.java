package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;

import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.svid.x509svid.X509Svid;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.DefaultX509Source.X509SourceOptions;
import io.spiffe.workloadapi.X509Source;
import io.vavr.Lazy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

@Beta
@Slf4j
public class ZeroTrustIdentityService
{
    static final ServiceIdentifier ZTIS_IDENTIFIER = ServiceIdentifier.of("zero-trust-identity");
    @Getter( AccessLevel.PUBLIC )
    private static final ZeroTrustIdentityService instance = new ZeroTrustIdentityService();
    private static final String DEFAULT_SOCKET_PATH = "unix:///tmp/spire-agent/public/api.sock";
    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(10);
    private final Lazy<X509Source> source = Lazy.of(this::initX509Source);
    private KeyStoreCache keyStoreCache = null;

    public X509Svid getX509Svid()
    {
        return source.get().getX509Svid();
    }

    private record KeyStoreCache( KeyStore keyStore, X509Svid svid )
    {
    }

    // Do not evaluate this prematurely, it only works if the SPIRE agent is running and the OS specific dependencies are available at runtime
    // implicitly synchronized via VAVR's Lazy
    X509Source initX509Source()
    {
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
            throw new DestinationAccessException(e);
        }
    }

    @Nonnull
    KeyStore getOrCreateKeyStore()
    {
        final X509Svid svid = getX509Svid();

        if( isKeyStoreCached(svid) ) {
            return keyStoreCache.keyStore();
        }
        // double-checked locking
        synchronized (this) {
            if( isKeyStoreCached(svid) ) {
                return keyStoreCache.keyStore();
            }
            assertSvidNotExpired(svid);
            final KeyStore keyStore = loadKeyStore(svid);
            keyStoreCache = new KeyStoreCache(keyStore, svid);
            return keyStore;
        }
    }

    private void assertSvidNotExpired(X509Svid svid) {
        if( svid.getLeaf().getNotAfter().before(Date.from(Instant.now())) ) {
            throw new IllegalStateException("The provided X509 SVID has expired. The expiry date was " + svid.getLeaf().getNotAfter() + ".");
        }
    }

    @Nonnull
    KeyStore loadKeyStore(@Nonnull final X509Svid svid)
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
}
