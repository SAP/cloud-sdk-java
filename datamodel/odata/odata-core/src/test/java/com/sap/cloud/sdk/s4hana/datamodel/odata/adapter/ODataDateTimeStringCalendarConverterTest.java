/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

class ODataDateTimeStringCalendarConverterTest
{
    @Test
    void testDateZeroToCalendar()
    {
        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final Calendar calendar = sut.toDomainNonNull("/Date(0)/").orNull();
        assertThat(calendar).isNotNull();
        assertThat(calendar.getTimeInMillis()).isEqualTo(0);
        assertThat(calendar.getTimeZone()).isEqualTo(TimeZone.getTimeZone("GMT"));
        assertThat(calendar.toInstant()).isEqualTo(Instant.parse("1970-01-01T00:00:00Z"));
    }

    @Test
    void testDatePositiveToCalendar()
    {
        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final Calendar calendar = sut.toDomainNonNull("/Date(1643775600000)/").orNull();
        assertThat(calendar).isNotNull();
        assertThat(calendar.getTimeInMillis()).isEqualTo(1643775600000L);
        assertThat(calendar.getTimeZone()).isEqualTo(TimeZone.getTimeZone("GMT"));
        assertThat(calendar.toInstant()).isEqualTo(Instant.parse("2022-02-02T04:20:00Z"));
    }

    @Test
    void testDateNegativeToCalendar()
    {
        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final Calendar calendar = sut.toDomainNonNull("/Date(-5480538000)/").orNull();
        assertThat(calendar).isNotNull();
        assertThat(calendar.getTimeInMillis()).isEqualTo(-5480538000L);
        assertThat(calendar.getTimeZone()).isEqualTo(TimeZone.getTimeZone("GMT"));
        assertThat(calendar.toInstant()).isEqualTo(Instant.parse("1969-10-29T13:37:42Z"));
    }

    @Test
    void testDateLimitsToCalendar()
    {
        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        // max
        assertThat(sut.toDomainNonNull("/Date(" + Long.MAX_VALUE + ")/").isConvertible()).isTrue();
        // > max
        assertThat(sut.toDomainNonNull("/Date(" + Long.MAX_VALUE + "9)/").isConvertible()).isFalse();
        // min
        assertThat(sut.toDomainNonNull("/Date(" + Long.MIN_VALUE + ")/").isConvertible()).isTrue();
        // < min
        assertThat(sut.toDomainNonNull("/Date(" + Long.MIN_VALUE + "9)/").isConvertible()).isFalse();
    }

    @Test
    void testInvalidStringCannotBeConverted()
    {
        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        assertThat(sut.toDomainNonNull("").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("Not a number").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("0").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("-1").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("1").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("Date()").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("/Date()/").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("/date(0)/").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("/Date(-)/").isNotConvertible()).isTrue();
        assertThat(sut.toDomainNonNull("/Date(+0)/").isNotConvertible()).isTrue();
    }

    @Test
    void testCalendarZeroToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(Date.from(Instant.parse("1970-01-01T00:00:00Z")));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(0)/");
    }

    @Test
    void testCalendarZeroWithOffsetToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
        calendar.setTime(Date.from(OffsetDateTime.parse("1970-01-01T00:00:00+01:00").toInstant()));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(-3600000)/");
    }

    @Test
    void testCalendarPositiveToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(Date.from(Instant.parse("2022-02-02T04:20:00Z")));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(1643775600000)/");
    }

    @Test
    void testCalendarPositiveWithOffsetToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-00:30"));
        calendar.setTime(Date.from(OffsetDateTime.parse("2022-02-02T04:20:00-00:30").toInstant()));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(1643777400000)/");
    }

    @Test
    void testCalendarNegativeToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(Date.from(Instant.parse("1969-10-29T13:37:42Z")));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(-5480538000)/");
    }

    @Test
    void testCalendarNegativeWithOffsetToString()
    {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:45"));
        calendar.setTime(Date.from(OffsetDateTime.parse("1969-10-29T13:37:42+01:45").toInstant()));

        final ODataDateTimeStringCalendarConverter sut = new ODataDateTimeStringCalendarConverter();

        final String dateString = sut.fromDomainNonNull(calendar).orNull();
        assertThat(dateString).isNotNull();
        assertThat(dateString).isEqualTo("/Date(-5486838000)/");
    }

}
