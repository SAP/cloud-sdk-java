/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representation of the <i>Megaclite</i> service as provided in <i>Deploy with Confidence</i> landscapes. This class
 * can be used to convert {@link MegacliteServiceBinding} instances into {@link HttpDestination} that use Megaclite as
 * an egress proxy.
 *
 * @see MegacliteServiceBinding
 * @see MegacliteServiceBindingAccessor
 * @since 4.17.0
 */
@Beta
@Slf4j
public final class MegacliteServiceBindingDestinationLoader implements ServiceBindingDestinationLoader
{
    @Setter( AccessLevel.PACKAGE )
    private DwcConfiguration dwcConfig = DwcConfiguration.getInstance();
    @Setter( AccessLevel.PACKAGE )
    private MegacliteDestinationFactory destinationFactory = MegacliteDestinationFactory.getInstance();
    @Setter( AccessLevel.PACKAGE )
    private MegacliteConnectivityProxyInformationResolver connectivityResolver =
        MegacliteConnectivityProxyInformationResolver.getInstance();

    @Nonnull
    @Override
    public Try<HttpDestination> tryGetDestination( @Nonnull final ServiceBindingDestinationOptions options )
    {
        final MegacliteServiceBinding megacliteServiceBinding;

        try {
            megacliteServiceBinding = getBindingOrThrow(options);
        }
        catch( final DestinationNotFoundException e ) {
            return Try.failure(e);
        }
        final OnBehalfOf onBehalfOf = options.getOnBehalfOf();

        if( ServiceIdentifier.CONNECTIVITY.equals(options.getServiceBinding().getServiceIdentifier().orElse(null)) ) {
            return tryGetConnectivityDestination(onBehalfOf, options);
        }

        return Try
            .of(() -> getMandateConfigurationOrThrow(megacliteServiceBinding, onBehalfOf))
            .map(
                config -> String
                    .format("/%s/%s/%s/", config.getMegacliteVersion(), config.getName(), config.getVersion()))
            .map(destinationFactory::getMegacliteDestination);
    }

    @Nonnull
    private Try<HttpDestination> tryGetConnectivityDestination(
        @Nonnull final OnBehalfOf onBehalfOf,
        @Nonnull final ServiceBindingDestinationOptions options )
    {
        if( onBehalfOf == OnBehalfOf.TECHNICAL_USER_PROVIDER ) {
            return Try
                .failure(
                    new DestinationAccessException(
                        "Accessing an onpremise system on behalf of the provider tenant explicitly is currently not supported."));
        }
        return options
            .getOption(Options.ProxyOptions.class)
            .toTry(
                () -> new DestinationAccessException(
                    "Using the connectivity proxy requires a base destination to add the proxy behaviour to. "
                        + "Please provide a base destination using the ServiceBindingDestinationOptions like so: "
                        + "ServiceBindingDestinationOptions.forService(BindableService.CONNECTIVITY).withOption(ServiceBindingDestinationOptions.Options.ProxyOptions.destinationToBeProxied(myDestination)).build();"))
            .flatMap(this::toProxiedDestination);
    }

    private Try<HttpDestination> toProxiedDestination( @Nonnull final HttpDestination base )
    {
        final Try<URI> proxyUrl = Try.of(connectivityResolver::getProxyUrl);
        if( proxyUrl.isFailure() ) {
            return Try
                .failure(
                    new DestinationAccessException("Failed to resolve on-premise proxy URL.", proxyUrl.getCause()));
        }
        final DefaultHttpDestination.Builder builder =
            DefaultHttpDestination
                .fromDestination(base)
                // be sure to use exactly this instance of connectivityResolver since it has a cache attached
                .headerProviders(connectivityResolver);

        return proxyUrl.map(builder::proxy).map(DefaultHttpDestination.Builder::buildInternal);
    }

    @Nonnull
    MegacliteServiceBinding.MandateConfiguration getMandateConfigurationOrThrow(
        @Nonnull final MegacliteServiceBinding binding,
        @Nonnull final OnBehalfOf behalf )
        throws DestinationAccessException
    {
        final boolean useProviderMandate;
        if( behalf == OnBehalfOf.TECHNICAL_USER_PROVIDER ) {
            useProviderMandate = true;
        } else {
            //  NAMED_USER_CURRENT_TENANT and OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT behave identical here
            useProviderMandate = currentTenantIsProvider();
        }

        final MegacliteServiceBinding.MandateConfiguration mandateConfiguration =
            useProviderMandate ? binding.getProviderConfiguration() : binding.getSubscriberConfiguration();
        if( mandateConfiguration != null ) {
            return mandateConfiguration;
        }
        final String msg =
            "Unable to transform the service binding for service '%s' into a Megaclite based destination. "
                + "The service binding has not been configured to be accessible from the %s account. "
                + "Please make sure your %s configuration is correct.";
        final String formattedMsg =
            String
                .format(
                    msg,
                    binding.getService(),
                    useProviderMandate ? "provider" : "subscriber",
                    MegacliteServiceBinding.class);
        throw new DestinationAccessException(formattedMsg);
    }

    private MegacliteServiceBinding getBindingOrThrow( @Nonnull final ServiceBindingDestinationOptions options )
    {
        final ServiceBinding serviceBinding = options.getServiceBinding();
        if( serviceBinding instanceof MegacliteServiceBinding ) {
            return (MegacliteServiceBinding) serviceBinding;
        }
        final String msg =
            "Unable to transform the provided service binding for service '%s' into a Megaclite based destination. Expected the service binding to be of type '%s' but got a '%s' instead.";
        final String formattedMsg =
            String
                .format(
                    msg,
                    options.getServiceBinding().getServiceIdentifier(),
                    serviceBinding.getClass(),
                    MegacliteServiceBinding.class);
        throw new DestinationNotFoundException(null, formattedMsg);
    }

    private boolean currentTenantIsProvider()
        throws DestinationAccessException
    {
        try {
            final String providerId = dwcConfig.providerTenant();
            return TenantAccessor.getCurrentTenant().getTenantId().equalsIgnoreCase(providerId);
        }
        catch( final Exception e ) {
            throw new DestinationAccessException(
                "Unable to determine the current or provider tenant. Please inspect the cause of this exception for further details.",
                e);
        }
    }
}
