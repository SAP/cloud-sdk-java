/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

class DestinationLoaderChainTest
{
    private static final String DESTINATION_NAME_1 = "some-destination";
    private static final String DESTINATION_NAME_2 = "some-other-destination";
    private final Destination mockedDestination_1 = mock(Destination.class);
    private final Destination mockedDestination_2 = mock(Destination.class);

    private DestinationLoader loaderEmpty;
    private DestinationLoader loaderFull_1;
    private DestinationLoader loaderFull_2;
    private DestinationLoader loaderErr;

    @BeforeEach
    void initializeMockedLoaders()
    {
        loaderEmpty = mock(DestinationLoader.class);
        loaderFull_1 = mock(DestinationLoader.class);
        loaderFull_2 = mock(DestinationLoader.class);
        loaderErr = mock(DestinationLoader.class);

        when(loaderEmpty.tryGetDestination(any(), any())).thenReturn(Try.failure(new DestinationNotFoundException()));
        when(loaderFull_1.tryGetDestination(eq(DESTINATION_NAME_1), any()))
            .thenReturn(Try.of(() -> mockedDestination_1));
        when(loaderFull_2.tryGetDestination(eq(DESTINATION_NAME_2), any()))
            .thenReturn(Try.of(() -> mockedDestination_2));
        when(loaderErr.tryGetDestination(any(), any()))
            .thenReturn(Try.failure(new DestinationAccessException(new IllegalArgumentException())));
    }

    @Test
    void testDestinationPresentInAllLoaders()
    {
        final DestinationLoader chain =
            DestinationLoaderChain.builder(loaderFull_1).append(loaderFull_2).append(loaderEmpty).build();

        VavrAssertions.assertThat(chain.tryGetDestination(DESTINATION_NAME_1)).contains(mockedDestination_1);

        verify(loaderFull_1, only()).tryGetDestination(eq(DESTINATION_NAME_1), any());
        verify(loaderFull_2, never()).tryGetDestination(any(), any());
        verify(loaderEmpty, never()).tryGetDestination(any(), any());
    }

    @Test
    void testDestinationPresentOnlyInLastLoader()
    {
        final DestinationLoader chain =
            DestinationLoaderChain.builder(loaderEmpty).append(loaderEmpty).append(loaderFull_1).build();

        VavrAssertions.assertThat(chain.tryGetDestination(DESTINATION_NAME_1)).contains(mockedDestination_1);

        verify(loaderEmpty, times(2)).tryGetDestination(eq(DESTINATION_NAME_1), any());
        verify(loaderFull_1, only()).tryGetDestination(eq(DESTINATION_NAME_1), any());
    }

    @Test
    void testConstructionOnEmptyList()
    {
        Assertions
            .assertThatThrownBy(() -> new DestinationLoaderChain(Collections.emptyList()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNoDestinationOnAnyLoader()
    {
        final DestinationLoader chain = DestinationLoaderChain.builder(loaderEmpty).build();

        VavrAssertions
            .assertThat(chain.tryGetDestination(DESTINATION_NAME_2))
            .isFailure()
            .failBecauseOf(DestinationNotFoundException.class);

        verify(loaderEmpty).tryGetDestination(eq(DESTINATION_NAME_2), any());
    }

    @Test
    void testPropagationOfOtherExceptions()
    {
        final DestinationLoaderChain chain =
            DestinationLoaderChain.builder(loaderErr).append(loaderFull_1).append(loaderErr).build();
        final Try<Destination> shouldBeFailure = chain.tryGetDestination(DESTINATION_NAME_2);

        VavrAssertions.assertThat(shouldBeFailure).isFailure();
        assertThatThrownBy(shouldBeFailure::get)
            .isInstanceOf(DestinationAccessException.class)
            .hasRootCauseExactlyInstanceOf(IllegalArgumentException.class);

        verify(loaderErr, only()).tryGetDestination(eq(DESTINATION_NAME_2), any());
        verify(loaderFull_1, never()).tryGetDestination(eq(DESTINATION_NAME_2), any());
    }

    @Test
    void testExceptionInLastLoader()
    {
        final DestinationLoaderChain chain = DestinationLoaderChain.builder(loaderEmpty).append(loaderErr).build();
        final Try<Destination> shouldBeFailure = chain.tryGetDestination(DESTINATION_NAME_2);

        VavrAssertions.assertThat(shouldBeFailure).isFailure();
        assertThatThrownBy(shouldBeFailure::get)
            .isInstanceOf(DestinationAccessException.class)
            .hasRootCauseExactlyInstanceOf(IllegalArgumentException.class);
    }
}
