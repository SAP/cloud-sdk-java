/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
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
    private static final DwcHeaderProvider instance = new DwcHeaderProvider();

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
