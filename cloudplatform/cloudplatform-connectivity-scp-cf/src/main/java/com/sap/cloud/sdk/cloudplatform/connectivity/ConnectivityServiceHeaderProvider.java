/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public class ConnectivityServiceHeaderProvider implements DestinationHeaderProvider
{
    private static final String SAP_CONNECTIVITY_SCC_LOCATION_ID_HEADER = "SAP-Connectivity-SCC-Location_ID";
    private static final ConnectivityService DEFAULT_CONNECTIVITY_SERVICE = new ConnectivityService();
    @Nonnull
    private final ConnectivityService connectivityService;

    public ConnectivityServiceHeaderProvider()
    {
        connectivityService = DEFAULT_CONNECTIVITY_SERVICE;
    }

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final HttpDestination destination = requestContext.getDestination();

        if( !destination.getProxyType().contains(ProxyType.ON_PREMISE) ) {
            return Collections.emptyList();
        }
        log
            .debug(
                "Treating destination with ProxyType {}, hence obtaining HTTP headers for On-Premise system.",
                ProxyType.ON_PREMISE);
        assertTenantRemainedConsistent(destination);
        return getOnPremiseProxyHeaders(destination, destination.getAuthenticationType());
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
            throw new IllegalStateException(
                "Tenant ID of destination '"
                    + destination.get(DestinationProperty.NAME).getOrNull()
                    + "' does not match the current tenant ID. Destination was created specifically for tenant '"
                    + destinationTenantId
                    + "', but the current tenant is '"
                    + currentTenantId
                    + "'.");
        }
    }

    // to be changed by https://github.com/SAP/cloud-sdk-java-backlog/issues/275
    @Nonnull
    private List<Header> getOnPremiseProxyHeaders(
        @Nonnull final DestinationProperties destination,
        @Nonnull final AuthenticationType authType )
        throws DestinationAccessException
    {
        final List<Header> headers = new ArrayList<>();

        if( destination.get(DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID).isDefined() ) {
            log
                .debug(
                    "Destination property {} defined, hence HTTP header with key {} added.",
                    DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID.getKeyName(),
                    SAP_CONNECTIVITY_SCC_LOCATION_ID_HEADER);
            headers
                .add(
                    new Header(
                        SAP_CONNECTIVITY_SCC_LOCATION_ID_HEADER,
                        destination.get(DestinationProperty.CLOUD_CONNECTOR_LOCATION_ID).get()));
        }

        final PrincipalPropagationStrategy strategy = PrincipalPropagationStrategy.of(destination, authType);

        headers.addAll(connectivityService.getHeadersForOnPremiseSystem(false, strategy));
        return headers;
    }
}
