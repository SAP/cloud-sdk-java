/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * ERP type.
 *
 * @param <T>
 *            The generic sub class type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public interface ErpType<T extends ErpType<T>> extends Serializable
{
    /**
     * Get the type converter for the ERP type.
     *
     * @return The type converter.
     */
    @Nonnull
    ErpTypeConverter<T> getTypeConverter();
}
