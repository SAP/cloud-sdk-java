/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;

/**
 * Represents a user defined service binding for a service that is reachable through the DwC outbound proxy service.
 *
 * @deprecated Deprecated in favor of {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding}.
 *             Refer to
 *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding#forService(com.sap.cloud.environment.servicebinding.api.ServiceIdentifier)}
 *             for detailed usage instructions.
 */
@Deprecated
public interface DwcOutboundServiceBinding
{
    /**
     * Returns the name of this user defined service binding.
     *
     * @return The name of this user defined service binding.
     */
    @Nonnull
    String getName();

    /**
     * Returns the version of this user defined service binding.
     *
     * @return The version of this user defined service binding.
     */
    @Nonnull
    String getVersion();

    /**
     * Returns the version of the outbound proxy through which the bound service is reachable.
     *
     * @return The version of the outbound proxy through which the bound service is reachable.
     */
    @Nonnull
    String getOutboundProxyVersion();
}
