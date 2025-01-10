package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Representation of an integer in an ERP.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ErpInteger extends IntegerBasedErpType<ErpInteger>
{
    private static final long serialVersionUID = 2001728174402033425L;

    @Getter( AccessLevel.PROTECTED )
    private final boolean signed;

    /**
     * Constructor.
     *
     * @param value
     *            The Long value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    @SuppressWarnings( "this-escape" )
    public ErpInteger( @Nonnull final Long value )
    {
        super(value);
        signed = getValue() != null && getValue().signum() < 0;
    }

    /**
     * Constructor.
     *
     * @param str
     *            The String value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    @SuppressWarnings( "this-escape" )
    public ErpInteger( @Nullable final String str )
    {
        super(str);
        signed = getValue() != null && getValue().signum() < 0;
    }

    /**
     * Constructor.
     *
     * @param value
     *            The Integer value.
     * @throws IllegalArgumentException
     *             when the input value could not be converted.
     */
    @SuppressWarnings( "this-escape" )
    public ErpInteger( @Nonnull final Integer value )
    {
        super(value);
        signed = getValue() != null && getValue().signum() < 0;
    }

    @Override
    @Nonnull
    protected Class<ErpInteger> getType()
    {
        return ErpInteger.class;
    }

    @Override
    protected int getMaxLength()
    {
        return Integer.MAX_VALUE;
    }
}
