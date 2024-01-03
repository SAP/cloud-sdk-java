/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * Date that is represented using the nines' complement.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Data
@Deprecated
public class InvertedLocalDate implements ErpType<InvertedLocalDate>
{
    private static final long serialVersionUID = 3144015896159592550L;
    private static final String PATTERN = "yyyyMMdd";

    @Nonnull
    private final transient LocalDate date;

    /**
     * Constructor.
     */
    public InvertedLocalDate()
    {
        date = LocalDate.now();
    }

    /**
     * Constructor.
     *
     * @param str
     *            The String representation of the ERP value.
     */
    public InvertedLocalDate( @Nonnull final String str )
    {
        date = LocalDate.parse(getNthComplement(str, 9), DateTimeFormatter.ofPattern(PATTERN));
    }

    /**
     * Constructor.
     *
     * @param date
     *            The LocalDate representation of the ERP value.
     */
    public InvertedLocalDate( @Nonnull final LocalDate date )
    {
        this.date = date;
    }

    /**
     * Constructor.
     *
     * @param year
     *            The year.
     * @param monthOfYear
     *            The month.
     * @param dayOfMonth
     *            The day.
     */
    public InvertedLocalDate( final int year, final int monthOfYear, final int dayOfMonth )
    {
        date = LocalDate.of(year, monthOfYear, dayOfMonth);
    }

    @Nonnull
    @Override
    public ErpTypeConverter<InvertedLocalDate> getTypeConverter()
    {
        return InvertedLocalDateConverter.INSTANCE;
    }

    @Override
    @Nonnull
    public String toString()
    {
        return getNthComplement(date.format(DateTimeFormatter.ofPattern(PATTERN)), 9);
    }

    private String getNthComplement( @Nonnull final String str, final int n )
    {
        final StringBuilder builder = new StringBuilder(str.length());

        for( final char c : str.toCharArray() ) {
            final int complement = n - Character.getNumericValue(c);
            builder.append(complement);
        }

        return builder.toString();
    }

    /**
     * Static factory method to construct current value for this ERP type.
     *
     * @return The newly created instance.
     */
    @Nonnull
    public static InvertedLocalDate now()
    {
        return new InvertedLocalDate(LocalDate.now());
    }
}
