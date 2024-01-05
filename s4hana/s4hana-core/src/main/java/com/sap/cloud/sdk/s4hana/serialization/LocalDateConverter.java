/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link LocalDate}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class LocalDateConverter extends AbstractErpTypeConverter<LocalDate>
{
    /**
     * Statically created instance of this converter.
     */
    public static final LocalDateConverter INSTANCE = new LocalDateConverter();

    /**
     * DateTimeFormat pattern suited for the date format 20181231
     */
    public static final String PATTERN_WITHOUT_DELIMITER = "yyyyMMdd";

    /**
     * DateTimeFormat pattern suited for the date format 2018-12-31
     */
    public static final String PATTERN_WITH_DASHES = "yyyy-MM-dd";

    @Nullable
    private final String pattern;

    /**
     * Creates an instance that formats {@link LocalDate} instances with the pattern {@link #PATTERN_WITHOUT_DELIMITER}.
     * For parsing, the format depends on the input String. If the input String contains "-",
     * {@link #PATTERN_WITH_DASHES} is used; otherwise, {@link #PATTERN_WITHOUT_DELIMITER} is used.
     */
    public LocalDateConverter()
    {
        this(null);
    }

    /**
     * Creates an instance with the given pattern.
     *
     * @param pattern
     *            The pattern for {@link LocalDate}. if {@code null}, this constructor creates an instance that formats
     *            {@link LocalDate} instances with the pattern {@link #PATTERN_WITHOUT_DELIMITER}. For parsing, the
     *            format depends on the input String. If the input String contains "-", {@link #PATTERN_WITH_DASHES} is
     *            used; otherwise, {@link #PATTERN_WITHOUT_DELIMITER} is used.
     */
    public LocalDateConverter( @Nullable final String pattern )
    {
        this.pattern = pattern;
    }

    @Nonnull
    @Override
    public Class<LocalDate> getType()
    {
        return LocalDate.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final LocalDate object )
    {
        return ConvertedObject
            .of(object.format(DateTimeFormatter.ofPattern(pattern != null ? pattern : PATTERN_WITHOUT_DELIMITER)));
    }

    @Nonnull
    @Override
    public ConvertedObject<LocalDate> fromDomainNonNull( @Nonnull final String domainObject )
    {
        final String pattern;

        if( this.pattern != null ) {
            pattern = this.pattern;
        } else {
            if( domainObject.contains("-") ) {
                pattern = PATTERN_WITH_DASHES;
            } else {
                pattern = PATTERN_WITHOUT_DELIMITER;
            }
        }

        return ConvertedObject.of(LocalDate.parse(domainObject, DateTimeFormatter.ofPattern(pattern)));
    }
}
