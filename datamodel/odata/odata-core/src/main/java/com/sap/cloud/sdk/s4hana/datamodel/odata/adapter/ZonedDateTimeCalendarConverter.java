/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Converts between the deprecated {@link Calendar} type and the new {@link ZonedDateTime}.
 * <p>
 * In combination with the {@link ODataField} annotation this can be used to expose fields of OData value which would be
 * exposed as {@code Calendar} as {@code ZonedDateTime}.
 */
public class ZonedDateTimeCalendarConverter extends AbstractTypeConverter<ZonedDateTime, Calendar>
{
    @Override
    @Nonnull
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final ZonedDateTime object )
    {
        final Calendar cal = GregorianCalendar.from(object);
        return ConvertedObject.of(cal);
    }

    @Override
    @Nonnull
    public ConvertedObject<ZonedDateTime> fromDomainNonNull( @Nonnull final Calendar domainObject )
    {
        final TimeZone timeZone = domainObject.getTimeZone();
        final ZonedDateTime zdt = ZonedDateTime.ofInstant(domainObject.toInstant(), timeZone.toZoneId());
        return ConvertedObject.of(zdt);
    }

    @Override
    @Nonnull
    public Class<ZonedDateTime> getType()
    {
        return ZonedDateTime.class;
    }

    @Override
    @Nonnull
    public Class<Calendar> getDomainType()
    {
        return Calendar.class;
    }
}
