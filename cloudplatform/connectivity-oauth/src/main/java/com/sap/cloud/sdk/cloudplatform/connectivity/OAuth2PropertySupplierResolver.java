/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A resolver type for {@link OAuth2PropertySupplier} to allow for matching and instantiating.
 */
@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class OAuth2PropertySupplierResolver
{
    @Nonnull
    private final Predicate<ServiceBindingDestinationOptions> matcher;

    @Nonnull
    private final Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> resolver;

    /**
     * Create an {@link OAuth2PropertySupplierResolver} for the given {@link ServiceIdentifier}. Using this API is
     * discouraged since "service identifier" may not be available or correctly set in all cases.
     *
     * @param identifier
     *            The service binding identifier, e.g. "destination".
     * @param resolver
     *            The resolver to use.
     * @return A new instance of {@link OAuth2PropertySupplierResolver}.
     */
    static OAuth2PropertySupplierResolver forServiceIdentifier(
        @Nonnull final ServiceIdentifier identifier,
        @Nonnull final Function<ServiceBindingDestinationOptions, OAuth2PropertySupplier> resolver )
    {
        return new OAuth2PropertySupplierResolver(
            opts -> identifier.equals(opts.getServiceBinding().getServiceIdentifier().orElse(null)),
            resolver);
    }

    boolean matches( @Nonnull final ServiceBindingDestinationOptions options )
    {
        return matcher.test(options);
    }

    @Nullable
    OAuth2PropertySupplier resolve( @Nonnull final ServiceBindingDestinationOptions options )
    {
        return resolver.apply(options);
    }
}
