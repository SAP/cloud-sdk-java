/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.soap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.apache.axis2.databinding.utils.ConverterUtil;

/**
 * Custom converter class being registered in the Axis2 framework by default through the
 * {@link Axis2CustomConverterListener}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class SoapCustomConverter extends ConverterUtil
{
    /**
     * Converts a given {@link Calendar} value into a String according to the expected format by the ERP system.
     *
     * @param value
     *            Instance of a {@link Calendar}
     * @return The value of the {@link Calendar} instance as a String.
     */
    @Nonnull
    public static String convertToString( @Nonnull final Calendar value )
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(value.getTime());
    }

    /**
     * Converts a given {@link Date} value into a String according to the expected format by the ERP system.
     *
     * @param value
     *            Instance of a {@link Date}
     * @return The value of the {@link Date} instance as a String.
     */
    @Nonnull
    public static String convertToString( @Nonnull final Date value )
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat.format(value);
    }
}
