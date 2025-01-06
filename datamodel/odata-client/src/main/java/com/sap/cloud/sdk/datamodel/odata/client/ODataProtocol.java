/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * The {@code ODataProtocol} defines all necessary information that is needed in order to differentiate between
 * different OData protocol versions.
 */
public interface ODataProtocol extends ODataResponseDescriptor, ODataLiteralSerializer
{
    /**
     * Version 2.0 of the OData protocol.
     */
    ODataProtocol V2 = new ODataProtocolV2();

    /**
     * Version 4.0 of the OData protocol.
     */
    ODataProtocol V4 = new ODataProtocolV4();

    /**
     * The version number of this protocol.
     *
     * @return A string representing the OData version, e.g. "4.0"
     */
    @Nonnull
    String getProtocolVersion();

    /**
     * Build the (inline) count query option for this protocol version.
     *
     * @param optionEnabled
     *            Determines the value of the query option.
     * @return An entry to add to the URL query.
     */
    @Nonnull
    Map.Entry<String, String> getQueryOptionInlineCount( boolean optionEnabled );

    /**
     * OData protocol v2.
     */
    final class ODataProtocolV2 implements ODataProtocol
    {
        private static final DateTimeFormatter EDM_DATE_TIME_FORMATTER =
            new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .append(
                    new DateTimeFormatterBuilder()
                        .appendValue(ChronoField.HOUR_OF_DAY, 2)
                        .appendLiteral(':')
                        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                        .optionalStart()
                        .appendLiteral(':')
                        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                        .optionalStart()
                        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 7, true) // <-- the OData V2 protocol defines that the nanoseconds must only contain at maximum 7 digits
                        .toFormatter())
                .toFormatter();

        @Getter
        private final String protocolVersion = "2.0";
        @Getter
        private final JsonLookup pathToResultSet = JsonLookup.of(JsonPath.of("d", "results"));
        @Getter
        private final JsonLookup pathToResultSingle = JsonLookup.of(JsonPath.of("d"));
        // The * is the function import name, but not passed through since it does not really matter.
        @Getter
        private final JsonLookup pathToResultPrimitive = JsonLookup.of(JsonPath.of("d", JsonPath.WILDCARD));
        @Getter
        private final JsonLookup pathToInlineCount = JsonLookup.of(JsonPath.of("__count"));
        @Getter
        private final JsonLookup pathToNextLink = JsonLookup.of(JsonPath.of("__next"));
        @Getter
        private final JsonLookup pathToDeltaLink = JsonLookup.empty();

        @Getter
        private final Function<Number, String> numberSerializer = ODataProtocolV2::numberToString;
        @Getter
        private final Function<UUID, String> UUIDSerializer = v -> String.format("guid'%s'", v);
        @Getter
        private final Function<OffsetDateTime, String> dateTimeOffsetSerializer =
            v -> String.format("datetimeoffset'%s'", v.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        @Getter
        private final Function<LocalTime, String> timeOfDaySerializer =
            v -> String.format("time'%s'", Duration.ofNanos(v.toNanoOfDay()));
        @Getter
        private final Function<LocalDateTime, String> dateTimeSerializer =
            v -> String.format("datetime'%s'", v.format(EDM_DATE_TIME_FORMATTER));

        private static String numberToString( final Number n )
        {
            if( n instanceof Integer ) {
                return Integer.toString(n.intValue());
            } else if( n instanceof Short ) {
                return Short.toString(n.shortValue());
            } else if( n instanceof Byte ) {
                return Byte.toString(n.byteValue());
            } else if( n instanceof Long ) {
                return n.longValue() + "L";
            } else if( n instanceof Float ) {
                return n.floatValue() + "f";
            } else if( n instanceof Double ) {
                return n.doubleValue() + "d";
            } else if( n instanceof BigDecimal ) {
                return ((BigDecimal) n).toPlainString() + "M";
            }
            final String message =
                "Unrecognized number type: %s. Should be one of: Integer, Long, Float, Double, BigDecimal.";
            throw new IllegalStateException(String.format(message, n.getClass()));
        }

        @Override
        @Nonnull
        public Map.Entry<String, String> getQueryOptionInlineCount( final boolean optionEnabled )
        {
            return new AbstractMap.SimpleEntry<>("$inlinecount", optionEnabled ? "allpages" : "none");
        }

        @Override
        @Nonnull
        public String toString()
        {
            return "OData " + protocolVersion;
        }
    }

    /**
     * OData protocol v4.
     */
    final class ODataProtocolV4 implements ODataProtocol
    {
        @Getter
        private final String protocolVersion = "4.0";
        @Getter
        private final JsonLookup pathToResultSet = JsonLookup.of(JsonPath.of("value"));
        @Getter
        private final JsonLookup pathToResultSingle = JsonLookup.of(JsonPath.ofRoot());
        @Getter
        private final JsonLookup pathToResultPrimitive = JsonLookup.of(JsonPath.of("value"));
        @Getter
        private final JsonLookup pathToInlineCount = JsonLookup.of(JsonPath.of("@odata.count"), JsonPath.of("@count"));
        @Getter
        private final JsonLookup pathToNextLink =
            JsonLookup.of(JsonPath.of("@odata.nextLink"), JsonPath.of("@nextLink"));
        @Getter
        private final JsonLookup pathToDeltaLink =
            JsonLookup.of(JsonPath.of("@odata.deltaLink"), JsonPath.of("@deltaLink"));

        @Getter
        private final Function<Number, String> numberSerializer = Number::toString;
        @Getter
        private final Function<UUID, String> UUIDSerializer = UUID::toString;
        @Getter
        private final Function<OffsetDateTime, String> dateTimeOffsetSerializer =
            v -> v.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        @Getter
        private final Function<LocalTime, String> timeOfDaySerializer = v -> v.format(DateTimeFormatter.ISO_LOCAL_TIME);
        @Getter
        private final Function<LocalDateTime, String> dateTimeSerializer =
            v -> dateTimeOffsetSerializer.apply(v.atOffset(ZoneOffset.UTC));

        @Override
        @Nonnull
        public Map.Entry<String, String> getQueryOptionInlineCount( final boolean optionEnabled )
        {
            return new AbstractMap.SimpleEntry<>("$count", optionEnabled ? "true" : "false");
        }

        @Override
        @Nonnull
        public String toString()
        {
            return "OData " + protocolVersion;
        }
    }

    /**
     * Compares this protocol with the given protocol based on their version identifiers.
     *
     * @param otherProtocol
     *            The protocol to compare to.
     * @return True, if the protocols resemble the same OData version.
     */
    default boolean isEqualTo( @Nonnull final ODataProtocol otherProtocol )
    {
        return otherProtocol.getProtocolVersion().equals(getProtocolVersion());
    }
}
