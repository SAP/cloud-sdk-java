package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ODataDateTimeStringCalendarConverter extends AbstractTypeConverter<String, Calendar>
{
    private static final Pattern JSON_PATTERN = Pattern.compile("/Date\\((-?\\p{Digit}+)\\)/");

    @Nonnull
    @Override
    public ConvertedObject<Calendar> toDomainNonNull( @Nonnull final String value )
    {
        final Matcher matcher = JSON_PATTERN.matcher(value);
        if( !matcher.matches() ) {
            return ConvertedObject.ofNotConvertible();
        }

        long millis;
        try {
            millis = Long.parseLong(matcher.group(1));
        }
        catch( final NumberFormatException e ) {
            log.debug("The given date string cannot be converted to milliseconds: " + e.getMessage());
            return ConvertedObject.ofNotConvertible();
        }
        final Calendar dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        dateTimeValue.clear();
        dateTimeValue.setTimeInMillis(millis);
        return ConvertedObject.of(dateTimeValue);

    }

    @Nonnull
    @Override
    public ConvertedObject<String> fromDomainNonNull( @Nonnull final Calendar value )
    {
        final long timeInMillis = value.getTimeInMillis();

        return ConvertedObject.of("/Date(" + timeInMillis + ")/");
    }

    @Nonnull
    @Override
    public Class<String> getType()
    {
        return String.class;
    }

    @Nonnull
    @Override
    public Class<Calendar> getDomainType()
    {
        return Calendar.class;
    }
}
