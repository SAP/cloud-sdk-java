/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;

import io.vavr.control.Try;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to get all SAP Deploy with Confidence related headers from the current request.
 */
@Slf4j
public class DwcHeaderProvider implements DestinationHeaderProvider
{
    @Getter
    private final int cardinality = 1;

    @Getter
    private static final DwcHeaderProvider instance = new DwcHeaderProvider();

    @Nonnull
    private final Collection<String> limiters;

    /**
     * Default constructor.
     */
    public DwcHeaderProvider()
    {
        this(List.of());
    }

    DwcHeaderProvider( @Nonnull final Collection<String> limiters )
    {
        this.limiters = limiters.stream().map(String::toLowerCase).toList();
    }

    /**
     * The HA Proxy on CF imposes a limit on header size. When accessing the destination service via megaclite not all
     * DwC headers are required.
     *
     * @return A header provider configured to only forward dwc headers required for destination service access.
     */
    static DwcHeaderProvider limitedHeaderProviderForDestinationAccess()
    {
        return new DwcHeaderProvider(
            List
                .of(
                    "dwc-tenant",
                    "dwc-subdomain",
                    "dwc-jwt",
                    "dwc-ias-jwt",
                    "dwc-megaclite-xsuaa-authorities",
                    "dwc-operation-id"));
    }

    @Nonnull
    @Override
    public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
    {
        final Try<RequestHeaderContainer> maybeHeaders = RequestHeaderAccessor.tryGetHeaderContainer();
        if( maybeHeaders.isFailure() ) {
            log
                .warn(
                    "Failed to access request headers in current ThreadContext; unable to pass on DwC headers to outgoing requests.");
            log.debug("Access to request headers failed:", maybeHeaders.getCause());
            return Collections.emptyList();
        }

        final RequestHeaderContainer headerContainer = maybeHeaders.get();
        final List<Header> headers =
            headerContainer
                .getHeaderNames()
                .stream()
                .filter(name -> name.toLowerCase(Locale.ENGLISH).startsWith("dwc-"))
                .filter(name -> limiters.isEmpty() || limiters.contains(name.toLowerCase(Locale.ENGLISH)))
                .flatMap(name -> headerContainer.getHeaderValues(name).stream().map(value -> new Header(name, value)))
                .collect(Collectors.toList());
        if( headers.isEmpty() ) {
            log
                .warn(
                    "Unable to pass on DwC headers to outgoing requests: Did not find any DwC headers in the set of request headers: {}",
                    headerContainer.getHeaderNames());
        }
        return headers;
    }
}
