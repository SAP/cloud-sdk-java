/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
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
    private final Supplier<DestinationServiceV1Response> result;

    @Nonnull
    private final OnBehalfOf onBehalfOf;

    DestinationServiceV1Response get()
    {
        return getResult().get();
    }
}
