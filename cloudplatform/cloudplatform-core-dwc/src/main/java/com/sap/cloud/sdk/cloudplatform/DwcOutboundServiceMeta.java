/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nullable;

/**
 * Represents a set of meta information for a service binding that is reachable through the DwC outbound proxy service.
 *
 * @deprecated Deprecated in favor of {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding}.
 *             Refer to
 *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding#forService(com.sap.cloud.environment.servicebinding.api.ServiceIdentifier)}
 *             for detailed usage instructions.
 */
@Deprecated
public interface DwcOutboundServiceMeta
{
    /**
     * The service kind which is targeted.
     */
    enum TargetService
    {
        /**
         * A destination service.
         */
        DESTINATION
    }

    /**
     * The mandate for which the target is accessed.
     */
    enum TargetMandate
    {
        /**
         * Provider.
         */
        PROVIDER,
        /**
         * Subscriber.
         */
        SUBSCRIBER
    }

    /**
     * Check whether the service binding is for a reuse service.
     *
     * @return Whether the service binding is for a reuse service.
     */
    boolean isReuseService();

    /**
     * The service kind which is targeted.
     *
     * @return The service kind which is targeted.
     */
    @Nullable
    TargetService getTargetService();

    /**
     * The mandate for which the target is accessed.
     *
     * @return The mandate for which the target is accessed.
     */
    @Nullable
    TargetMandate getTargetMandate();
}
