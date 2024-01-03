/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Representation of a decimal in an ERP.
 * <p>
 * This class translates the representation of decimals in an ERP, which uses the last character in its string
 * representation to indicate the decimal's sign (e.g., "12.34-" for negative and "23.45 " for positive numbers).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@AllArgsConstructor
@EqualsAndHashCode( of = "value", callSuper = true )
@Deprecated
public class ErpDecimal extends Number implements ErpType<ErpDecimal>
{
    private static final long serialVersionUID = -8669286965990460111L;

    @Getter
    @Nonnull
    private final BigDecimal value;

    /**
     * Constructor.
     *
     * @param value
     *            The String value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    public ErpDecimal( @Nonnull final String value )
    {
        this.value = new BigDecimal(toBigDecimalString(value));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The Float value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    public ErpDecimal( @Nonnull final Float value )
    {
        this.value = BigDecimal.valueOf(value);
    }

    /**
     * Constructor.
     *
     * @param value
     *            The Double value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    public ErpDecimal( @Nonnull final Double value )
    {
        this.value = BigDecimal.valueOf(value);
    }

    @Nonnull
    @Override
    public ErpTypeConverter<ErpDecimal> getTypeConverter()
    {
        return ErpDecimalConverter.INSTANCE;
    }

    @Override
    public int intValue()
    {
        return value.intValue();
    }

    @Override
    public long longValue()
    {
        return value.longValue();
    }

    @Override
    public float floatValue()
    {
        return value.floatValue();
    }

    @Override
    public double doubleValue()
    {
        return value.doubleValue();
    }

    @Override
    @Nonnull
    public String toString()
    {
        return toErpString(value.toPlainString());
    }

    private static String toBigDecimalString( final String str )
    {
        String sign = str.substring(str.length() - 1);

        if( !sign.equals("-") ) {
            if( sign.equals(" ") ) {
                sign = "";
            } else {
                return str;
            }
        }

        return sign + str.substring(0, str.length() - 1);
    }

    @Nonnull
    private static String toErpString( @Nonnull final String str )
    {
        final String sign = str.substring(0, 1);

        if( sign.equals("-") ) {
            return str.substring(1) + sign;
        } else {
            return str + " ";
        }
    }
}
