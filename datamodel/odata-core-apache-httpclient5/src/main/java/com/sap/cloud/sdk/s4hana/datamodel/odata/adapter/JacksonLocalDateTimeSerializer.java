package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to write {@link LocalDateTime} fields, based on a common logic writing a
 * {@link Calendar}.
 */
public class JacksonLocalDateTimeSerializer extends AbstractJacksonCalendarSerializer<LocalDateTime>
{
    private static final long serialVersionUID = 17342648935256631L;

    /**
     * Default constructor needed by the framework.
     */
    protected JacksonLocalDateTimeSerializer()
    {
        super(LocalDateTime.class);
    }

    @Override
    @Nonnull
    protected AbstractTypeConverter<LocalDateTime, Calendar> getConverterInstance()
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
