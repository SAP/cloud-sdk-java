package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.BtpServiceOptions.ZeroTrustIdentityOptions;
import static com.sap.cloud.sdk.cloudplatform.connectivity.ZeroTrustIdentityService.ZTIS_IDENTIFIER;

import java.security.KeyStore;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

@Beta
public class ZeroTrustIdentityDestinationEnhancer implements ServiceBindingDestinationLoader
{
    private final ZeroTrustIdentityService service;

    public ZeroTrustIdentityDestinationEnhancer()
    {
        service = ZeroTrustIdentityService.getInstance();
    }

    ZeroTrustIdentityDestinationEnhancer( ZeroTrustIdentityService service )
    {
        this.service = service;
    }

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull ServiceBindingDestinationOptions options )
    {
        if( !ZTIS_IDENTIFIER.equals(options.getServiceBinding().getServiceIdentifier().orElse(null)) ) {
            return Try.failure(new DestinationNotFoundException());
        }
        final Try<KeyStore> maybeKeyStore = Try.of(service::getOrCreateKeyStore);
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
                () -> new DestinationAccessException(
                    "No Destination for ZeroTrust to enhance given in ServiceBindingDestinationOptions."))
            .map(DefaultHttpDestination::fromDestination)
            .map(builder -> builder.keyStore(maybeKeyStore.get()))
            .map(DefaultHttpDestination.Builder::build);
    }
}
