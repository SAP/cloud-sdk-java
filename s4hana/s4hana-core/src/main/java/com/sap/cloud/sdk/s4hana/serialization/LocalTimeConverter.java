/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link LocalTime}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class LocalTimeConverter extends AbstractErpTypeConverter<LocalTime>
{
    /**
     * Statically created instance of this converter.
     */
    public static final LocalTimeConverter INSTANCE = new LocalTimeConverter();

    /**
     * DateTimeFormat pattern suited for the time format 17:34:56
     */
    public static final String PATTERN_WITH_COLONS = "HH:mm:ss";

    /**
     * DateTimeFormat pattern suited for the time format 173456
     */
    public static final String PATTERN_WITHOUT_DELIMITER = "HHmmss";

    @Nullable
    private final String pattern;

    /**
     * Creates an instance that formats {@link LocalTime} instances with the pattern {@link #PATTERN_WITHOUT_DELIMITER}.
     * For parsing, the format depends on the input String. If the input String contains ":",
     * {@link #PATTERN_WITH_COLONS} is used; otherwise, {@link #PATTERN_WITHOUT_DELIMITER} is used.
     */
    public LocalTimeConverter()
    {
        this(null);
    }

    /**
     * Creates an instance with the given pattern.
     *
     * @param pattern
     *            The pattern for {@link LocalTime}. if {@code null}, this constructor creates an instance that formats
     *            {@link LocalTime} instances with the pattern {@link #PATTERN_WITHOUT_DELIMITER}. For parsing, the
     *            format depends on the input String. If the input String contains ":", {@link #PATTERN_WITH_COLONS} is
     *            used; otherwise, {@link #PATTERN_WITHOUT_DELIMITER} is used.
     */
    public LocalTimeConverter( @Nullable final String pattern )
    {
        this.pattern = pattern;
    }

    @Nonnull
    @Override
    public Class<LocalTime> getType()
    {
        return LocalTime.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final LocalTime object )
    {
        return ConvertedObject
            .of(object.format(DateTimeFormatter.ofPattern(pattern != null ? pattern : PATTERN_WITHOUT_DELIMITER)));
    }

    @Nonnull
    @Override
    public ConvertedObject<LocalTime> fromDomainNonNull( @Nonnull final String domainObject )
    {
        final String pattern;

        if( this.pattern != null ) {
            pattern = this.pattern;
        } else {
            if( domainObject.contains(":") ) {
                pattern = PATTERN_WITH_COLONS;
            } else {
                pattern = PATTERN_WITHOUT_DELIMITER;
            }
        }

        return ConvertedObject.of(LocalTime.parse(domainObject, DateTimeFormatter.ofPattern(pattern)));
    }
}
