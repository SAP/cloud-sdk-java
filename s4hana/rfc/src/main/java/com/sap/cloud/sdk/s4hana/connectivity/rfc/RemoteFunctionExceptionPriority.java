/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class RemoteFunctionExceptionPriority implements Comparable<RemoteFunctionExceptionPriority>
{
    @Getter
    private final int value;

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public int compareTo( @Nonnull final RemoteFunctionExceptionPriority o )
    {
        return Integer.compare(getValue(), o.getValue());
    }
}
