/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter( AccessLevel.PACKAGE )
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class Parameter
{
    @Nonnull
    private final ParameterKind parameterKind;

    @Nonnull
    private final Value<?> parameterValue;
}
