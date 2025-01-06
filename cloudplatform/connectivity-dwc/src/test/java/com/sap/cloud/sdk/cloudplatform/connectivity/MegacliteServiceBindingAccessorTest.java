/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

class MegacliteServiceBindingAccessorTest
{
    @BeforeEach
    @AfterEach
    void resetStaticServiceBindings()
    {
        MegacliteServiceBindingAccessor.clearServiceBindings();
    }

    @Test
    void testClassIsPickedUpAsServiceBindingAccessor()
    {
        final List<ServiceBindingAccessor> accessors = ServiceBindingAccessor.getInstancesViaServiceLoader();
        assertThat(accessors.stream().filter(MegacliteServiceBindingAccessor.class::isInstance).count()).isEqualTo(1);
    }

    @Test
    void testGetServiceBindingsIsEmptyByDefault()
    {
        final MegacliteServiceBindingAccessor sut = new MegacliteServiceBindingAccessor();
        assertThat(sut.getServiceBindings()).isEmpty();
    }

    @Test
    void testGetServiceBindingsReturnsStaticServiceBindings()
    {
        final MegacliteServiceBindingAccessor sut = new MegacliteServiceBindingAccessor();

        final MegacliteServiceBinding serviceBinding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.DESTINATION)
                .providerConfiguration()
                .name("destination")
                .version("v1")
                .build();
        MegacliteServiceBindingAccessor.registerServiceBinding(serviceBinding);

        assertThat(sut.getServiceBindings()).containsExactlyInAnyOrder(serviceBinding);
    }
}
