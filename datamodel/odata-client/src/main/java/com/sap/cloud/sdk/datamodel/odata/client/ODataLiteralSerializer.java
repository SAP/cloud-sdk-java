/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
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
    @Nonnull
    Function<Number, String> getNumberSerializer();

    @Nonnull
    Function<UUID, String> getUUIDSerializer();

    @Nonnull
    Function<OffsetDateTime, String> getDateTimeOffsetSerializer();

    @Nonnull
    Function<LocalTime, String> getTimeOfDaySerializer();

    @Nonnull
    Function<LocalDateTime, String> getDateTimeSerializer();
}
