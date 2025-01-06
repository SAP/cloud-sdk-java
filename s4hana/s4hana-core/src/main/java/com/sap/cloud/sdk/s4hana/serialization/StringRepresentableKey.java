/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

/**
 * Represents a key that can be represented as a String.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface StringRepresentableKey
{
    /**
     * Returns the String representation of this key.
     *
     * @return This key as a String.
     */
    @Nonnull
    String getKeyAsString();
}
