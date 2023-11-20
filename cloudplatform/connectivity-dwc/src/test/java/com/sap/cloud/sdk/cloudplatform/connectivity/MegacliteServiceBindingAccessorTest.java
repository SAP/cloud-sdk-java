/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBindingAccessor.CONNECTIVITY_BINDING;
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
    void testGetServiceBindingsIsNotEmptyByDefault()
    {
        final MegacliteServiceBindingAccessor sut = new MegacliteServiceBindingAccessor();
        assertThat(sut.getServiceBindings()).hasSize(1).containsExactly(CONNECTIVITY_BINDING);
    }

    @Test
    void testGetServiceBindingsReturnsStaticServiceBindings()
    {
        final MegacliteServiceBindingAccessor sut = new MegacliteServiceBindingAccessor();
        assertThat(sut.getServiceBindings()).containsExactlyInAnyOrder(CONNECTIVITY_BINDING);

        final MegacliteServiceBinding serviceBinding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.DESTINATION)
                .providerConfiguration()
                .name("destination")
                .version("v1")
                .build();
        MegacliteServiceBindingAccessor.registerServiceBinding(serviceBinding);

        assertThat(sut.getServiceBindings()).containsExactlyInAnyOrder(serviceBinding, CONNECTIVITY_BINDING);
    }
}
