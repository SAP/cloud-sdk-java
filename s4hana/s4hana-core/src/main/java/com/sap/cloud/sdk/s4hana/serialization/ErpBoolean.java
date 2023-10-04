/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Representation of a boolean in an ERP.
 * <p>
 * This class translates the representation of booleans in an ERP ("X" is true, " " is false, and "-" is undefined).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@EqualsAndHashCode( of = "value" )
@Deprecated
public class ErpBoolean implements ErpType<ErpBoolean>
{
    private static final long serialVersionUID = -7923655156599127873L;

    /**
     * The TRUE instance.
     */
    public static final ErpBoolean TRUE = new ErpBoolean(true);
    /**
     * The FALSE instance.
     */
    public static final ErpBoolean FALSE = new ErpBoolean(false);

    @Getter
    @Nullable
    private final Boolean value;

    /**
     * A static method factory method.
     *
     * @param value
     *            The Boolean value.
     * @return The ERP type representation of the boolean value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    @Nullable
    public static ErpBoolean of( @Nullable final Boolean value )
        throws IllegalArgumentException
    {
        if( value == null ) {
            return null;
        }

        return new ErpBoolean(value);
    }

    /**
     * Constructor.
     *
     * @param value
     *            The String value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    public ErpBoolean( @Nullable final String value ) throws IllegalArgumentException
    {
        if( value == null || value.equals("-") ) {
            this.value = null;
        } else if( value.equals("X") ) {
            this.value = true;
        } else if( CharMatcher.whitespace().matchesAllOf(value) ) {
            this.value = false;
        } else {
            throw new IllegalArgumentException("Cannot construct ERP boolean from: " + value);
        }
    }

    @Nonnull
    @Override
    public ErpTypeConverter<ErpBoolean> getTypeConverter()
    {
        return ErpBooleanConverter.INSTANCE;
    }

    /**
     * Checks whether this ERP boolean is not null and true.
     *
     * @return The boolean value.
     */
    public boolean isTrue()
    {
        return value != null && value;
    }

    /**
     * Checks whether this ERP boolean is not null and false.
     *
     * @return The inverted boolean value.
     */
    public boolean isFalse()
    {
        return value != null && !value;
    }

    @Override
    @Nonnull
    public String toString()
    {
        if( value == null ) {
            return "-";
        } else if( value ) {
            return "X";
        } else {
            return " ";
        }
    }
}
