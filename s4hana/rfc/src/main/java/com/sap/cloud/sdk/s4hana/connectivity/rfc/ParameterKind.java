/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * Parameter kind for remote functions.
 */
enum ParameterKind
{
    EXPORTING(10),
    IMPORTING(20),
    TABLES(30),
    CHANGING(40);

    @Getter
    private final int identifier;

    ParameterKind( final int identifier )
    {
        this.identifier = identifier;
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Override
    public String toString()
    {
        return name();
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    public static ParameterKind ofIdentifier( final int identifier )
        throws IllegalArgumentException
    {
        for( final ParameterKind parameterKind : values() ) {
            if( parameterKind.getIdentifier() == identifier ) {
                return parameterKind;
            }
        }

        throw new IllegalArgumentException(
            "Unknown " + ParameterKind.class.getSimpleName() + " identifier: " + identifier);
    }
}
