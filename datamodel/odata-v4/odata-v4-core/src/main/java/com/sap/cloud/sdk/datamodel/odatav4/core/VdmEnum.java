/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface to manage the different properties an Edm.Enum literal contains.
 */
public interface VdmEnum
{
    /**
     * Get the name property of the Edm.Enum literal
     *
     * @return The name property.
     */
    @Nonnull
    default String getName()
    {
        return toString();
    }

    /**
     * Get the value property of the Edm.Enum literal
     *
     * @return The value property.
     */
    @Nullable
    default Long getValue()
    {
        return null;
    }

    /**
     * Helper function to resolve enum constant from given type reference and String identifier.
     *
     * @param enumType
     *            The enum type reference.
     * @param identifier
     *            The enum constant identifier.
     * @param <T>
     *            The generic enum type.
     * @return A
     */
    @Nullable
    static <T extends VdmEnum> T getConstant( @Nonnull final Class<T> enumType, @Nullable final String identifier )
    {
        final T[] enumConstants = enumType.getEnumConstants();
        if( enumConstants == null ) {
            return null;
        }
        return Arrays
            .stream(enumConstants)
            .filter(member -> Objects.equals(member.getName(), identifier))
            .findFirst()
            .orElse(null);
    }
}
