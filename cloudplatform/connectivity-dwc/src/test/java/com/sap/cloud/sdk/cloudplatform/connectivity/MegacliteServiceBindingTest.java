/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;

import org.junit.Test;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;

public class MegacliteServiceBindingTest
{
    static {
        MegacliteServiceBinding.dwcConfiguration =
            new DwcConfiguration(URI.create("megaclite.com"), "provider-tenant-id");
    }

    @Test
    public void testPropertiesOfTheOpenSourceServiceBinding()
    {
        final ServiceBinding binding =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.DESTINATION)
                .providerConfiguration()
                .name("destination-paas")
                .version("v1")
                .build();

        assertThat(binding.getKeys()).isEmpty();
        assertThat(binding.getName()).isEmpty();
        assertThat(binding.getServiceName()).contains("destination");
        assertThat(binding.getServicePlan()).isEmpty();
        assertThat(binding.getTags()).isEmpty();
        assertThat(binding.getCredentials().get("tenantid")).isEqualTo("provider-tenant-id");
    }

    @Test
    public void testBuilderThrowsExceptionWhenAddingAnExistingMandate()
    {
        final MegacliteServiceBinding.Builder4 builder =
            MegacliteServiceBinding
                .forService(ServiceIdentifier.DESTINATION)
                .providerConfiguration()
                .name("destination-paas")
                .version("v1");

        assertThatThrownBy(() -> builder.and().providerConfiguration())
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
