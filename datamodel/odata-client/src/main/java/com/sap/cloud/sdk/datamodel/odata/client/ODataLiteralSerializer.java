/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * Descriptor for protocol specific information on serializing type literals for filter expressions and entity keys.
 */
public interface ODataLiteralSerializer
{
    /**
     * Returns a function to convert a given {@link Number} into a {@link String} that conforms with the protocol
     * specification.
     *
     * @return A serializer for {@link Number}s.
     */
    @Nonnull
    Function<Number, String> getNumberSerializer();

    /**
     * Returns a function to convert a given {@link UUID} into a {@link String} that conforms with the protocol
     * specification.
     *
     * @return A serializer for {@link UUID}s.
     */
    @Nonnull
    Function<UUID, String> getUUIDSerializer();

    /**
     * Returns a function to convert a given {@link OffsetDateTime} into a {@link String} that conforms with the
     * protocol specification.
     *
     * @return A serializer for {@link OffsetDateTime}s.
     */
    @Nonnull
    Function<OffsetDateTime, String> getDateTimeOffsetSerializer();

    /**
     * Returns a function to convert a given {@link LocalTime} into a {@link String} that conforms with the protocol
     * specification.
     *
     * @return A serializer for {@link LocalTime}s.
     */
    @Nonnull
    Function<LocalTime, String> getTimeOfDaySerializer();

    /**
     * Returns a function to convert a given {@link LocalDateTime} into a {@link String} that conforms with the protocol
     * specification.
     *
     * @return A serializer for {@link LocalDateTime}s.
     */
    @Nonnull
    Function<LocalDateTime, String> getDateTimeSerializer();
}
