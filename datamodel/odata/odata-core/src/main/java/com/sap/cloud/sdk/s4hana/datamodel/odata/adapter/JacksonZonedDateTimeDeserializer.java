/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to read {@link ZonedDateTime} fields, based on a common logic reading from
 * {@link Calendar}.
 */
public class JacksonZonedDateTimeDeserializer extends AbstractJacksonCalendarDeserializer<ZonedDateTime>
{
    private static final long serialVersionUID = -7432353030570821745L;

    /**
     * Default constructor needed by the framework.
     */
    protected JacksonZonedDateTimeDeserializer()
    {
        super(ZonedDateTime.class);
    }

    @Override
    @Nonnull
    protected AbstractTypeConverter<ZonedDateTime, Calendar> getCalendarConverterInstance()
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
