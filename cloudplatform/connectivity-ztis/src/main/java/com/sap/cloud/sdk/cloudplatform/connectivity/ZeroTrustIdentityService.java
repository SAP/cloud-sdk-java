package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.ZeroTrustIdentityOptions;

import java.io.IOException;
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
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.svid.x509svid.X509Svid;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.DefaultX509Source.X509SourceOptions;
import io.spiffe.workloadapi.X509Source;
import io.vavr.Lazy;
import io.vavr.control.Try;

@SuppressWarnings( "UnstableApiUsage" )
@Beta
public class ZeroTrustIdentityService implements ServiceBindingDestinationLoader
{
    static final ServiceIdentifier ZTIS_IDENTIFIER = ServiceIdentifier.of("zero-trust-identity");
    private static final Lazy<X509Source> source = Lazy.of(ZeroTrustIdentityService::initX509Source);
    private static final String SPIFFE_SOCKET_PATH = "unix:///tmp/spire-agent/public/api.sock";
    private static final Duration DEFAULT_SOCKET_TIMEOUT = Duration.ofSeconds(10);
    private static KeyStoreCache keyStoreCache = null;

    @SuppressWarnings( "UseOfObsoleteDateTimeApi" ) // required by Java security API
    private record KeyStoreCache( KeyStore keyStore, Date lastUpdated )
    {
    }

    // implicitly synchronized via VAVR's Lazy
    private static X509Source initX509Source()
    {
        final X509SourceOptions x509SourceOptions =
            X509SourceOptions
                .builder()
                .spiffeSocketPath(SPIFFE_SOCKET_PATH)
                .initTimeout(DEFAULT_SOCKET_TIMEOUT)
                .build();
        try {
            return DefaultX509Source.newSource(x509SourceOptions);
        }
        catch( final SocketEndpointAddressException | X509SourceException e ) {
            throw new DestinationAccessException(e);
        }
    }

    private static synchronized KeyStore getKeyStore()
    {
        final X509Svid svid = getX509Svid();
        final KeyStore.Entry privateKeyEntry = new PrivateKeyEntry(svid.getPrivateKey(), svid.getChainArray());

        if( keyStoreCache != null && !svid.getLeaf().getNotBefore().after(keyStoreCache.lastUpdated()) ) {
            return keyStoreCache.keyStore();
        }
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null);
            keyStore.setEntry("spiffe", privateKeyEntry, new KeyStore.PasswordProtection(new char[0]));
        }
        catch( final KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e ) {
            throw new RuntimeException(e);
        }
        keyStoreCache = new KeyStoreCache(keyStore, Date.from(Instant.now()));
        return keyStore;
    }

    public static X509Svid getX509Svid()
    {
        return source.get().getX509Svid();
    }

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull ServiceBindingDestinationOptions options )
    {
        if( !ZTIS_IDENTIFIER.equals(options.getServiceBinding().getServiceIdentifier().orElse(null)) ) {
            return Try.failure(new DestinationNotFoundException());
        }
        final Try<KeyStore> maybeKeyStore = Try.of(ZeroTrustIdentityService::getKeyStore);
        if( maybeKeyStore.isFailure() ) {
            return Try
                .failure(
                    new DestinationAccessException(
                        "Failed to load KeyStore for Zero Trust Identity Certificate.",
                        maybeKeyStore.getCause()));
        }

        return options
            .getOption(ZeroTrustIdentityOptions.class)
            .toTry(
                () -> new IllegalStateException(
                    "No Destination for ZeroTrust to enhance given in ServiceBindingDestinationOptions."))
            .map(DefaultHttpDestination::fromDestination)
            .map(builder -> builder.keyStore(maybeKeyStore.get()))
            .map(DefaultHttpDestination.Builder::build);
    }
}
