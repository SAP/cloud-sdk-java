/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.types;

import javax.annotation.Nonnull;

import lombok.EqualsAndHashCode;

/**
 * Type Year.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = false, of = "year" )
@Deprecated
public class Year extends Number implements Comparable<Year>
{
    private static final long serialVersionUID = 1853026793702193492L;
    private final Integer year;

    public Year( final int year )
    {
        if( year < 1900 ) {
            throw new IllegalArgumentException("Invalid year: expected value >= 1900. Given: " + year + ".");
        }

        this.year = year;
    }

    @Override
    public int intValue()
    {
        return year;
    }

    @Override
    public long longValue()
    {
        return year.longValue();
    }

    @Override
    public float floatValue()
    {
        throw new UnsupportedOperationException("Cannot convert Year to float.");
    }

    @Override
    public double doubleValue()
    {
        throw new UnsupportedOperationException("Cannot convert Year to double.");
    }

    @Override
    @Nonnull
    public String toString()
    {
        return String.valueOf(year);
    }

    @Override
    public int compareTo( @Nonnull final Year other )
    {
        return year.compareTo(other.year);
    }

    @Nonnull
    public static Year fromString( @Nonnull final String str )
        throws NumberFormatException
    {
        return new Year(Integer.parseInt(str));
    }
}
