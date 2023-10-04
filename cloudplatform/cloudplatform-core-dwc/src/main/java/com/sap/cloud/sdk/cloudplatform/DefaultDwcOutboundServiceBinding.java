/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import lombok.Builder;
import lombok.Value;

/**
 * Default implementation of the {@link DwcOutboundServiceBinding} interface.
 *
 * @deprecated Deprecated in favor of {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding}.
 *             Refer to
 *             {@link com.sap.cloud.sdk.cloudplatform.connectivity.MegacliteServiceBinding#forService(com.sap.cloud.environment.servicebinding.api.ServiceIdentifier)}
 *             for detailed usage instructions.
 */
@Deprecated
@Value
@Builder
public class DefaultDwcOutboundServiceBinding implements DwcOutboundServiceBinding
{
    String name;
    String version;

    @Builder.Default
    String outboundProxyVersion = "v1";
}
