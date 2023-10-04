/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The destination and request specific context.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public class DestinationRequestContext
{
    @Nonnull
    private final HttpDestination destination;

    @Nonnull
    private final URI requestUri;
}
