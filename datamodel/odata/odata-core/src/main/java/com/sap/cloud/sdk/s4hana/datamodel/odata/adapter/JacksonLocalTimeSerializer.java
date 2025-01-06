/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to write {@link LocalTime} fields, based on a common logic writing a
 * {@link Calendar}.
 */
public class JacksonLocalTimeSerializer extends AbstractJacksonCalendarSerializer<LocalTime>
{
    private static final long serialVersionUID = 5786928813508456930L;

    /**
     * Default constructor needed by the framework.
     */
    protected JacksonLocalTimeSerializer()
    {
        super(LocalTime.class);
    }

    @Override
    @Nonnull
    protected AbstractTypeConverter<LocalTime, Calendar> getConverterInstance()
    {
        return new LocalTimeCalendarConverter();
    }

    @Nonnull
    @Override
    protected AbstractTypeConverter<String, Calendar> getStringCalendarConverterInstance()
    {
        return new ODataTimeStringCalendarConverter();
    }
}
