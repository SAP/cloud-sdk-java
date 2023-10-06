/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class DestinationRetrieval
{
    @Nonnull
    private final Supplier<ScpCfDestinationServiceV1Response> result;

    @Nonnull
    private final OnBehalfOf onBehalfOf;

    ScpCfDestinationServiceV1Response get()
    {
        return getResult().get();
    }
}
