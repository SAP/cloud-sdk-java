/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceIdentifier;
import com.sap.cloud.environment.servicebinding.api.exception.ServiceBindingAccessException;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link ServiceBindingAccessor} interface that keeps track of {@link MegacliteServiceBinding}
 * instances.
 * <p>
 * <b>Important Hint:</b> All instances of this class will always return the same set of {@link ServiceBinding}
 * instances. These instances <b>must</b> be registered manually (using
 * {@link #registerServiceBinding(MegacliteServiceBinding)}).
 * </p>
 *
 * @since 4.17.0
 */
@Slf4j
public class MegacliteServiceBindingAccessor implements ServiceBindingAccessor
{
    /**
     * The {@link MegacliteServiceBinding} for the connectivity service. In case you want to access on-premise systems
     * via Megaclite, you need to register this service binding manually using
     * {@link #registerServiceBinding(MegacliteServiceBinding)}.
     *
     * @since 5.0.0
     */
    @Nonnull
    public static final MegacliteServiceBinding CONNECTIVITY_BINDING =
        MegacliteServiceBinding
            .forService(ServiceIdentifier.CONNECTIVITY)
            .subscriberConfiguration()
            .name("connectivity")
            .version("v1")
            .build();

    @Nonnull
    private static final Set<MegacliteServiceBinding> serviceBindings = new HashSet<>();

    /**
     * Adds the provided {@code serviceBinding} to the <b>statically</b> stored list of all tracked
     * {@link MegacliteServiceBinding} instances. The added {@code serviceBinding} will be included in the response of
     * {@link #getServiceBindings()} of <b>any</b> instance of the {@link MegacliteServiceBindingAccessor} class.
     *
     * @param serviceBinding
     *            The {@link MegacliteServiceBinding} to add.
     */
    public static synchronized void registerServiceBinding( @Nonnull final MegacliteServiceBinding serviceBinding )
    {
        serviceBindings.add(serviceBinding);
    }

    static synchronized void clearServiceBindings()
    {
        log.warn("Clearing the registered Dwc Service Bindings. This should never happen outside of testing!");
        serviceBindings.clear();
    }

    @Nonnull
    @Override
    public List<ServiceBinding> getServiceBindings()
        throws ServiceBindingAccessException
    {
        return new ArrayList<>(serviceBindings);
    }
}
