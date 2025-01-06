/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A name-value pair representing a header (for example, an HTTP header).
 */
@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class Header
{
    @Nonnull
    private final String name;

    @Nullable
    private final String value;
}
