/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to read {@link LocalDateTime} fields, based on a common logic reading from
 * {@link Calendar}.
 */
public class JacksonLocalDateTimeDeserializer extends AbstractJacksonCalendarDeserializer<LocalDateTime>
{
    private static final long serialVersionUID = 2970121420707666471L;

    /**
     * Default constructor needed by the framework.
     */
    public JacksonLocalDateTimeDeserializer()
    {
        super(LocalDateTime.class);
    }

    @Nonnull
    @Override
    protected AbstractTypeConverter<LocalDateTime, Calendar> getCalendarConverterInstance()
    {
        return new LocalDateTimeCalendarConverter();
    }

    @Nonnull
    @Override
    protected AbstractTypeConverter<String, Calendar> getStringCalendarConverterInstance()
    {
        return new ODataDateTimeStringCalendarConverter();
    }
}
