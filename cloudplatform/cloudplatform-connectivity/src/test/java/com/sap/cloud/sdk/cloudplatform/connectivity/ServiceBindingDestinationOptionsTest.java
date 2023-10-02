/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Test;

import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.sdk.cloudplatform.connectivity.ServiceBindingDestinationOptions.Options;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;

public class ServiceBindingDestinationOptionsTest
{
    private static final ServiceBinding TEST_BINDING;

    static {
        TEST_BINDING = mock(ServiceBinding.class);
        when(TEST_BINDING.getServiceIdentifier()).thenReturn(Optional.of(ServiceIdentifier.DESTINATION));
    }

    @After
    public void cleanup()
    {
        DefaultServiceBindingAccessor.setInstance(null);
    }

    @Test
    public void testCanBeCreatedWithoutServiceIdentifier()
    {
        final ServiceBinding mock = mock(ServiceBinding.class);
        when(mock.getServiceIdentifier()).thenReturn(Optional.empty());

        // service bindings without service identifier are allowed opposed to previous implementation
        assertThatNoException().isThrownBy(() -> ServiceBindingDestinationOptions.forService(mock).build());
    }

    @Test
    public void testCanBeCreatedWithoutExplicitOnBehalfOf()
    {
        final ServiceBindingDestinationOptions sut = ServiceBindingDestinationOptions.forService(TEST_BINDING).build();

        assertThat(sut.getServiceBinding()).isSameAs(TEST_BINDING);
        assertThat(sut.getOnBehalfOf())
            .describedAs(
                "If no on-behalf-of is given, the default should be " + OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT)
            .isEqualTo(OnBehalfOf.TECHNICAL_USER_CURRENT_TENANT);
    }

    @Test
    public void testGetModifiedBehalf()
    {
        final ServiceBindingDestinationOptions sut =
            ServiceBindingDestinationOptions
                .forService(TEST_BINDING)
                .onBehalfOf(OnBehalfOf.NAMED_USER_CURRENT_TENANT)
                .build();

        assertThat(sut.getServiceBinding()).isSameAs(TEST_BINDING);
        assertThat(sut.getOnBehalfOf()).isSameAs(OnBehalfOf.NAMED_USER_CURRENT_TENANT);
    }

    @Test
    public void testGetDestinationWithProxy()
    {
        final HttpDestination mock = mock(HttpDestination.class);

        final ServiceBindingDestinationOptions sut =
            ServiceBindingDestinationOptions
                .forService(TEST_BINDING)
                .withOption(Options.ProxyOptions.destinationToBeProxied(mock))
                .withOption(Options.ResilienceOptions.of(ResilienceConfiguration.of("")))
                .build();

        assertThat(sut.getOption(Options.ProxyOptions.class)).contains(mock);
    }

    @Test
    public void testServiceBindingIsLoadedIfNotGiven()
    {
        DefaultServiceBindingAccessor.setInstance(() -> Collections.singletonList(TEST_BINDING));
        final ServiceBindingDestinationOptions sut =
            ServiceBindingDestinationOptions.forService(ServiceIdentifier.DESTINATION).build();

        assertThat(sut.getServiceBinding()).isSameAs(TEST_BINDING);
    }

    @Test
    public void testServiceBindingLoadingFails()
    {
        assertThatThrownBy(() -> ServiceBindingDestinationOptions.forService(ServiceIdentifier.DESTINATION).build())
            .isExactlyInstanceOf(DestinationAccessException.class)
            .hasMessageContaining("Could not find any");
    }

    @Test
    public void testIllegalOptionInputs()
    {
        assertThatThrownBy(
            () -> ServiceBindingDestinationOptions.forService(mock(ServiceBinding.class)).withOption(() -> "foo"))
            .isExactlyInstanceOf(IllegalArgumentException.class);

        final Supplier<String> s = () -> "foo";
        assertThatThrownBy(
            () -> ServiceBindingDestinationOptions.forService(mock(ServiceBinding.class)).withOption(s::get))
            .isExactlyInstanceOf(IllegalArgumentException.class);

        final ServiceBindingDestinationOptions.OptionsEnhancer<String> optionsEnhancer =
            new ServiceBindingDestinationOptions.OptionsEnhancer<String>()
            {
                @Nonnull
                @Override
                public String getValue()
                {
                    return "foo";
                }
            };
        assertThatThrownBy(
            () -> ServiceBindingDestinationOptions.forService(mock(ServiceBinding.class)).withOption(optionsEnhancer))
            .isExactlyInstanceOf(IllegalArgumentException.class);

        final HttpDestination mock = mock(HttpDestination.class);
        assertThatThrownBy(
            () -> ServiceBindingDestinationOptions
                .forService(mock(ServiceBinding.class))
                .withOption(Options.ProxyOptions.destinationToBeProxied(mock))
                .withOption(Options.ProxyOptions.destinationToBeProxied(mock)))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
