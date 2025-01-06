/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static java.util.Collections.singletonList;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class OAuth2HeaderProvider implements DestinationHeaderProvider
{
    @Nonnull
    private final OAuth2Service oauth2service;
    @Nonnull
    private final String authHeaderName;

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final DestinationProperties destination = requestContext.getDestination();
        assertTenantRemainedConsistent(destination);

        final String accessToken = oauth2service.retrieveAccessToken();

        return singletonList(new Header(authHeaderName, "Bearer " + accessToken));
    }

    private void assertTenantRemainedConsistent( @Nonnull final DestinationProperties destination )
    {
        final Option<String> maybeTenantId = destination.get(DestinationProperty.TENANT_ID);
        if( maybeTenantId.isEmpty() ) {
            return;
        }
        final String destinationTenantId = maybeTenantId.get();
        final String currentTenantId = TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId).getOrElse("");

        if( !destinationTenantId.equals(currentTenantId) ) {
            final String msg =
                "Tenant ID of destination '%s' does not match the current tenant ID. Destination was created specifically for tenant '%s', but the current tenant is '%s'.";
            final String destinationName = destination.get(DestinationProperty.NAME).getOrNull();
            throw new IllegalStateException(String.format(msg, destinationName, destinationTenantId, currentTenantId));
        }
    }
}
