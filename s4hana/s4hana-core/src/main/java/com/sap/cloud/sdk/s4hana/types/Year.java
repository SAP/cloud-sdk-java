/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
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

    /**
     * Creates a new instance of {@link Year}.
     *
     * @param year
     *            The year.
     * @throws IllegalArgumentException
     *             If the given year is less than 1900.
     */
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
        return year;
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

    /**
     * Creates a new instance of {@link Year} from the given string.
     *
     * @param str
     *            The string to parse.
     * @return A new instance of {@link Year}.
     * @throws NumberFormatException
     *             If the given string cannot be parsed.
     * @throws IllegalArgumentException
     *             If the given year is less than 1900.
     */
    @Nonnull
    public static Year fromString( @Nonnull final String str )
        throws NumberFormatException
    {
        return new Year(Integer.parseInt(str));
    }
}
