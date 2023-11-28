/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.services.openapi.apiclient;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.util.StdDateFormat;

/**
 * Date format according to the <a href="https://www.rfc-editor.org/rfc/rfc3339">RFC3339</a>.
 */
public class RFC3339DateFormat extends DateFormat
{
    private static final long serialVersionUID = 1L;
    private static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("UTC");

    private final StdDateFormat fmt = new StdDateFormat().withTimeZone(TIMEZONE_Z).withColonInTimeZone(true);

    /**
     * Default constructor
     */
    public RFC3339DateFormat()
    {
        calendar = new GregorianCalendar();
        numberFormat = new DecimalFormat();
    }

    @Override
    @Nonnull
    public Date parse( @Nonnull final String source )
    {
        return parse(source, new ParsePosition(0));
    }

    @Override
    @Nonnull
    public Date parse( @Nonnull final String source, @Nonnull final ParsePosition pos )
    {
        return fmt.parse(source, pos);
    }

    @Override
    @Nonnull
    public StringBuffer format(
        @Nonnull final Date date,
        @Nonnull final StringBuffer toAppendTo,
        @Nullable final FieldPosition fieldPosition )
    {
        return fmt.format(date, toAppendTo, fieldPosition);
    }
}
