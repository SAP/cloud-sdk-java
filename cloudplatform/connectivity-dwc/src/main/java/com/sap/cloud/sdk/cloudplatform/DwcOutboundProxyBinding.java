/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import java.net.URI;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Builder;
import lombok.Value;

/**
 * Represents an user defined service binding that connects the DwC outbound proxy service to the DwC application.
 */
@Beta
@Value
@Builder
public class DwcOutboundProxyBinding
{
    /**
     * The default name of the service binding.
     */
    public static final String DEFAULT_SERVICE_BINDING_NAME = "megaclite";

    @Builder.Default
    @Nonnull
    String name = DEFAULT_SERVICE_BINDING_NAME;

    @Nonnull
    URI uri;
}
