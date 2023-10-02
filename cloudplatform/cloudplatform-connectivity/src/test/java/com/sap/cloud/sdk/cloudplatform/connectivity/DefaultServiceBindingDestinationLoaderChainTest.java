package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import io.vavr.control.Try;

public class DefaultServiceBindingDestinationLoaderChainTest
{
    private static final ServiceBinding TEST_BINDING;
    private static final ServiceBindingDestinationOptions TEST_OPTIONS;

    static {
        TEST_BINDING = mock(ServiceBinding.class);
        when(TEST_BINDING.getServiceIdentifier())
            .thenReturn(Optional.of(ServiceIdentifier.of(UUID.randomUUID().toString())));

        TEST_OPTIONS = ServiceBindingDestinationOptions.forService(TEST_BINDING).build();
    }

    @Test
    public void testTryGetDestinationWithoutDelegateLoaders()
    {
        final DefaultServiceBindingDestinationLoaderChain sut =
            new DefaultServiceBindingDestinationLoaderChain(Collections.emptyList());

        final Try<HttpDestination> result = sut.tryGetDestination(TEST_OPTIONS);
        assertThat(result.isFailure()).isTrue();
        assertThat(result)
            .isSameAs(DefaultServiceBindingDestinationLoaderChain.NoDelegateLoadersExceptionHolder.NO_DELEGATE_LOADERS);
    }

    @Test
    public void testTryGetDestinationWithNoResultFromDelegateLoader()
    {
        final DestinationNotFoundException expectedCause = new DestinationNotFoundException();

        final ServiceBindingDestinationLoader loader = mock(ServiceBindingDestinationLoader.class);
        when(loader.tryGetDestination(any())).thenReturn(Try.failure(expectedCause));

        final DefaultServiceBindingDestinationLoaderChain sut =
            new DefaultServiceBindingDestinationLoaderChain(Collections.singletonList(loader));

        final Try<HttpDestination> result = sut.tryGetDestination(TEST_OPTIONS);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isExactlyInstanceOf(DestinationNotFoundException.class);
    }

    @Test
    public void testTryGetDestinationWithDestinationAccessExceptionFromLoader()
    {
        final DestinationAccessException expectedCause = new DestinationAccessException();

        final ServiceBindingDestinationLoader loader = mock(ServiceBindingDestinationLoader.class);
        when(loader.tryGetDestination(any())).thenReturn(Try.failure(expectedCause));

        final DefaultServiceBindingDestinationLoaderChain sut =
            new DefaultServiceBindingDestinationLoaderChain(Collections.singletonList(loader));

        final Try<HttpDestination> result = sut.tryGetDestination(TEST_OPTIONS);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause()).isSameAs(expectedCause);
    }

    @Test
    public void testTryGetDestinationWithIllegalStateExceptionFromLoader()
    {
        final IllegalStateException expectedCause = new IllegalStateException();

        final ServiceBindingDestinationLoader loader = mock(ServiceBindingDestinationLoader.class);
        when(loader.tryGetDestination(any())).thenReturn(Try.failure(expectedCause));

        final DefaultServiceBindingDestinationLoaderChain sut =
            new DefaultServiceBindingDestinationLoaderChain(Collections.singletonList(loader));

        final Try<HttpDestination> result = sut.tryGetDestination(TEST_OPTIONS);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isExactlyInstanceOf(DestinationAccessException.class)
            .cause()
            .isSameAs(expectedCause);
    }

    @Test
    public void testTryGetDestinationReturnsFirstMatchingResult()
    {
        final ServiceBindingDestinationLoader firstLoader = mock(ServiceBindingDestinationLoader.class);
        final Try<HttpDestination> firstSuccess = Try.success(mock(HttpDestination.class));
        when(firstLoader.tryGetDestination(any())).thenReturn(firstSuccess);

        final ServiceBindingDestinationLoader secondLoader = mock(ServiceBindingDestinationLoader.class);
        final Try<HttpDestination> secondSuccess = Try.success(mock(HttpDestination.class));
        when(secondLoader.tryGetDestination(any())).thenReturn(secondSuccess);

        final DefaultServiceBindingDestinationLoaderChain sut =
            new DefaultServiceBindingDestinationLoaderChain(Arrays.asList(firstLoader, secondLoader));

        final Try<HttpDestination> result = sut.tryGetDestination(TEST_OPTIONS);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result).isSameAs(firstSuccess);
        assertThat(result).isNotSameAs(secondSuccess);

        verify(firstLoader, times(1)).tryGetDestination(any());
        verify(secondLoader, times(0)).tryGetDestination(any());
    }
}
