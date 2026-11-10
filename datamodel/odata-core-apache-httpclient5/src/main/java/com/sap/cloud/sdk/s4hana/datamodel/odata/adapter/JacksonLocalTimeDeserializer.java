package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.LocalTime;
import java.util.Calendar;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;

/**
 * Jackson deserializer that is able to read {@link LocalTime} fields, based on a common logic reading from
 * {@link Calendar}.
 */
public class JacksonLocalTimeDeserializer extends AbstractJacksonCalendarDeserializer<LocalTime>
{
    private static final long serialVersionUID = -6461230022477505904L;

    /**
     * Default constructor needed by the framework.
     */
    protected JacksonLocalTimeDeserializer()
    {
        super(LocalTime.class);
    }

    @Override
    @Nonnull
    protected AbstractTypeConverter<LocalTime, Calendar> getCalendarConverterInstance()
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
