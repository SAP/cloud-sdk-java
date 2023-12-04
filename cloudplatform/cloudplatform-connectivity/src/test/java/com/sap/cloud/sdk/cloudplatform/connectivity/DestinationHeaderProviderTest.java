/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

class DestinationHeaderProviderTest
{
    private static final String DESTINATION_NAME = "TestDestination";
    private static final Header TEST_HEADER = new Header("Expected-Header", "present");

    @Test
    void testHeaderProvidersAreInvoked()
    {
        final String uri = "some-uri";

        final Header firstHeader = new Header("foo1", "bar1");
        final Header secondHeader = new Header("foo2", "bar2");
        final Header thirdHeader = new Header("foo3", "bar3");
        final Header fourthHeader = new Header("foo4", "bar4");

        final DestinationHeaderProvider firstHeaderProvider = ( any ) -> Collections.singletonList(firstHeader);
        final DestinationHeaderProvider secondHeaderProvider = ( any ) -> Arrays.asList(firstHeader, secondHeader);
        final DestinationHeaderProvider thirdHeaderProvider = ( any ) -> Collections.singletonList(thirdHeader);
        final DestinationHeaderProvider fourthHeaderProvider =
            ( context ) -> context.getRequestUri().getPath().endsWith("foo")
                ? Collections.singletonList(fourthHeader)
                : Collections.emptyList();

        final DefaultHttpDestination destination =
            DefaultHttpDestination
                .builder(uri)
                .headerProviders(firstHeaderProvider, secondHeaderProvider, thirdHeaderProvider, fourthHeaderProvider)
                .build();

        final Collection<Header> destinationHeaders = destination.getHeaders();

        assertThat(destinationHeaders).containsExactlyInAnyOrder(firstHeader, firstHeader, secondHeader, thirdHeader);

        final Collection<Header> destinationHeadersWithRequestUri = destination.getHeaders(URI.create("/foo"));

        assertThat(destinationHeadersWithRequestUri)
            .containsExactlyInAnyOrder(firstHeader, firstHeader, secondHeader, thirdHeader, fourthHeader);
    }

    @Test
    void testProvidersAreLoaded()
    {
        final DefaultHttpDestination destination = DefaultHttpDestination.builder("bar").name(DESTINATION_NAME).build();

        final Collection<Header> actualHeader = destination.getHeaders();

        assertThat(actualHeader).contains(TEST_HEADER);
    }

    public static class TestDestinationHeaderProvider implements DestinationHeaderProvider
    {
        @Nonnull
        @Override
        public List<Header> getHeaders( @Nonnull final DestinationRequestContext requestContext )
        {
            final Option<String> maybeName = requestContext.getDestination().get(DestinationProperty.NAME);

            if( maybeName.isDefined() && maybeName.get().equals(DESTINATION_NAME) ) {
                final List<Header> result = new ArrayList<>();

                result.add(TEST_HEADER);

                return result;
            }

            return Collections.emptyList();
        }
    }
}
