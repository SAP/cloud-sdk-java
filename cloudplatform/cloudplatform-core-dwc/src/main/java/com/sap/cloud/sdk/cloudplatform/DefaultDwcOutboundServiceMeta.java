/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nullable;

import lombok.Value;

/**
 * Default implementation of the {@link DwcOutboundServiceMeta} interface.
 *
 * @deprecated Deprecated in favor of {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding}.
 *             Refer to
 *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding#forService(com.sap.cloud.environment.servicebinding.api.ServiceIdentifier)}
 *             for detailed usage instructions.
 */
@Deprecated
@Value
class DefaultDwcOutboundServiceMeta implements DwcOutboundServiceMeta
{
    boolean reuseService;

    @Nullable
    TargetService targetService;

    @Nullable
    TargetMandate targetMandate;
}
