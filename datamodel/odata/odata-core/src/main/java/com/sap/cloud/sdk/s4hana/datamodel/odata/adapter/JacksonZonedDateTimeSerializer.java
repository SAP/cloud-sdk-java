/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to write {@link ZonedDateTime} fields, based on a common logic writing a
 * {@link Calendar}.
 */
public class JacksonZonedDateTimeSerializer extends AbstractJacksonCalendarSerializer<ZonedDateTime>
{
    private static final long serialVersionUID = -3595139411304341322L;

    /**
     * Default constructor needed by the framework.
     */
    protected JacksonZonedDateTimeSerializer()
    {
        super(ZonedDateTime.class);
    }

    @Override
    @Nonnull
    protected AbstractTypeConverter<ZonedDateTime, Calendar> getConverterInstance()
    {
        return new ZonedDateTimeCalendarConverter();
    }

    @Nonnull
    @Override
    protected AbstractTypeConverter<String, Calendar> getStringCalendarConverterInstance()
    {
        return new ODataDateTimeOffsetStringCalendarConverter();
    }
}
