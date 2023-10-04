/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

public class DestinationAccessorTest
{
    @Before
    @After
    public void resetLoader()
    {
        // reset accessor to the default loader
        DestinationAccessor.setLoader(null);
    }

    @Test
    public void testDefaultDestinationLoader()
    {
        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
    }

    @Test
    public void testLoaderSetterWorks()
    {
        final DestinationLoader mockedLoader = mock(DestinationLoader.class);

        DestinationAccessor.setLoader(mockedLoader);

        assertThat(DestinationAccessor.getLoader()).isEqualTo(mockedLoader);
    }

    @Test
    public void testLoaderSetterFallsBackToDefaultOnNullParameter()
    {
        final DestinationLoader mockedLoader = mock(DestinationLoader.class);
        final DestinationLoader defaultLoader = DestinationAccessor.getLoader();

        DestinationAccessor.setLoader(mockedLoader);
        DestinationAccessor.setLoader(null);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(defaultLoader.getClass()).isNotEqualTo(mockedLoader);
    }

    @Test
    public void testGetDestination()
    {
        final String someDestinationName = "Some Destination Name";
        final Destination someDestination = DefaultDestination.builder().build();

        final DestinationLoader mockedLoader = mock(DestinationLoader.class);

        when(mockedLoader.tryGetDestination(eq(someDestinationName))).thenReturn(Try.of(() -> someDestination));

        DestinationAccessor.setLoader(mockedLoader);

        final Try<Destination> loadedDestination = DestinationAccessor.tryGetDestination(someDestinationName);

        VavrAssertions.assertThat(loadedDestination).contains(someDestination);
    }

    @Test
    public void testAppendDestinationLoader()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("dest1").build();
        final DefaultHttpDestination dest2 = DefaultHttpDestination.builder("").name("dest2").build();

        final DefaultDestinationLoader loader1 = new DefaultDestinationLoader().registerDestination(dest1);
        final DefaultDestinationLoader loader2 = new DefaultDestinationLoader().registerDestination(dest2);

        DestinationAccessor.setLoader(loader1);

        DestinationAccessor.appendDestinationLoader(loader2);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("dest1").asHttp()).isEqualTo(dest1);
        assertThat(DestinationAccessor.getDestination("dest2").asHttp()).isEqualTo(dest2);
    }

    @Test
    public void testPrependDestinationLoader()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("dest1").build();
        final DefaultHttpDestination dest2 = DefaultHttpDestination.builder("").name("dest2").build();

        final DefaultDestinationLoader loader1 = new DefaultDestinationLoader().registerDestination(dest1);
        final DefaultDestinationLoader loader2 = new DefaultDestinationLoader().registerDestination(dest2);

        DestinationAccessor.setLoader(loader1);

        DestinationAccessor.prependDestinationLoader(loader2);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("dest1").asHttp()).isEqualTo(dest1);
        assertThat(DestinationAccessor.getDestination("dest2").asHttp()).isEqualTo(dest2);
    }

    @Test
    public void testAppendDestinationLoaderWithRedundantDestinations()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("destination").build();
        final DefaultHttpDestination dest2 =
            DefaultHttpDestination.builder("http://some.uri").name("destination").build();

        final DefaultDestinationLoader loader1 = new DefaultDestinationLoader().registerDestination(dest1);
        final DefaultDestinationLoader loader2 = new DefaultDestinationLoader().registerDestination(dest2);

        DestinationAccessor.appendDestinationLoader(loader1);
        DestinationAccessor.appendDestinationLoader(loader2);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("destination").asHttp()).isEqualTo(dest1);
    }

    @Test
    public void testPrependDestinationLoaderWithRedundantDestinations()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("destination").build();
        final DefaultHttpDestination dest2 =
            DefaultHttpDestination.builder("http://some.uri").name("destination").build();

        final DefaultDestinationLoader loader1 = new DefaultDestinationLoader().registerDestination(dest1);
        final DefaultDestinationLoader loader2 = new DefaultDestinationLoader().registerDestination(dest2);

        DestinationAccessor.prependDestinationLoader(loader1);
        DestinationAccessor.prependDestinationLoader(loader2);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("destination").asHttp()).isEqualTo(dest2);
    }

    @Test
    public void testAppendingTheCurrentDestinationLoader()
    {
        final DestinationLoader currentLoader = DestinationAccessor.getLoader();

        DestinationAccessor.appendDestinationLoader(currentLoader);

        assertThat(DestinationAccessor.getLoader()).isSameAs(currentLoader);
    }

    @Test
    public void testPrependingTheCurrentDestinationLoader()
    {
        final DestinationLoader currentLoader = DestinationAccessor.getLoader();

        DestinationAccessor.prependDestinationLoader(currentLoader);

        assertThat(DestinationAccessor.getLoader()).isSameAs(currentLoader);
    }

    @Test
    public void appendDestinationLoaderShouldAddChainToAlreadyExistingChain()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("destination").build();
        final DefaultHttpDestination dest2 =
            DefaultHttpDestination.builder("http://some.uri").name("other destination").build();

        final DestinationLoader initialLoader =
            DestinationLoaderChain.builder(new DefaultDestinationLoader().registerDestination(dest1)).build();
        DestinationAccessor.setLoader(initialLoader);

        final DestinationLoader additionalLoader =
            DestinationLoaderChain.builder(new DefaultDestinationLoader().registerDestination(dest2)).build();
        DestinationAccessor.appendDestinationLoader(additionalLoader);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("destination").asHttp()).isEqualTo(dest1);
        assertThat(DestinationAccessor.getDestination("other destination").asHttp()).isEqualTo(dest2);
    }

    @Test
    public void prependDestinationLoaderShouldAddChainToAlreadyExistingChain()
    {
        final DefaultHttpDestination dest1 = DefaultHttpDestination.builder("").name("destination").build();
        final DefaultHttpDestination dest2 =
            DefaultHttpDestination.builder("http://some.uri").name("other destination").build();

        final DestinationLoader initialLoader =
            DestinationLoaderChain.builder(new DefaultDestinationLoader().registerDestination(dest1)).build();
        DestinationAccessor.setLoader(initialLoader);

        final DestinationLoader additionalLoader =
            DestinationLoaderChain.builder(new DefaultDestinationLoader().registerDestination(dest2)).build();
        DestinationAccessor.prependDestinationLoader(additionalLoader);

        assertThat(DestinationAccessor.getLoader()).isInstanceOf(DestinationLoaderChain.class);
        assertThat(DestinationAccessor.getDestination("destination").asHttp()).isEqualTo(dest1);
        assertThat(DestinationAccessor.getDestination("other destination").asHttp()).isEqualTo(dest2);
    }

    @Test
    public void testDestinationNotFound()
    {
        final Try<Destination> shouldThrow = DestinationAccessor.tryGetDestination("Unknown");

        VavrAssertions.assertThat(shouldThrow).isFailure();
        assertThatThrownBy(shouldThrow::get).isInstanceOf(DestinationNotFoundException.class);
    }
}
