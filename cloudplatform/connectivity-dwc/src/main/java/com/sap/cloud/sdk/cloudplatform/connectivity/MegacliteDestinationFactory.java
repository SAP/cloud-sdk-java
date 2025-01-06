/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MegacliteDestinationFactory
{
    @Nonnull
    @Getter
    private static final MegacliteDestinationFactory instance = new MegacliteDestinationFactory();

    @Nonnull
    private final DwcConfiguration dwcConfig;

    private MegacliteDestinationFactory()
    {
        this(DwcConfiguration.getInstance());
    }

    @Nonnull
    HttpDestination getMegacliteDestination()
    {
        return getMegacliteDestination(null);
    }

    @Nonnull
    HttpDestination getMegacliteDestination( @Nullable final String path )
    {
        final URI baseUrl;
        try {
            baseUrl = dwcConfig.megacliteUrl();
        }
        catch( final CloudPlatformException e ) {
            throw new DestinationAccessException("Failed to derive the megaclite base URL from the environment", e);
        }
        final URI destinationUrl = Option.of(path).map(baseUrl::resolve).getOrElse(baseUrl);

        return DefaultHttpDestination
            .builder(destinationUrl)
            .securityConfiguration(SecurityConfigurationStrategy.FROM_PLATFORM)
            .headerProviders(DwcHeaderProvider.limitedHeaderProviderForDestinationAccess())
            .build();
    }
}
