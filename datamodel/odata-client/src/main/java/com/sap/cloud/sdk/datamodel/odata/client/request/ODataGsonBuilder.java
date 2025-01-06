/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.gson.GsonBuilder;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.BinaryTypeAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.DurationTypeAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.LocalDateTypeAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.LocalTimeTypeAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.OffsetDateTimeTypeAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.UuidTypeAdapter;
import com.sap.cloud.sdk.result.AnnotatedFieldGsonExclusionStrategy;
import com.sap.cloud.sdk.result.ElementName;
import com.sap.cloud.sdk.result.ElementNameGsonFieldNamingStrategy;

/**
 * Factory class to manage GSON references.
 */
public final class ODataGsonBuilder
{
    /**
     * Construct a new GsonBuilder for serialization and deserialization of OData values.
     *
     * @return The GsonBuilder reference.
     */
    @Nonnull
    public static GsonBuilder newGsonBuilder()
    {
        return newGsonBuilder(NumberDeserializationStrategy.DOUBLE);
    }

    /**
     * Construct a new GsonBuilder for serialization and deserialization of OData values.
     *
     * @param numberStrategy
     *            The default number deserialization strategy to be used for untyped numbers.
     * @return The GsonBuilder reference.
     */
    @Nonnull
    static GsonBuilder newGsonBuilder( @Nonnull final NumberDeserializationStrategy numberStrategy )
    {
        final GsonBuilder gsonBuilder =
            new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(new ElementNameGsonFieldNamingStrategy())
                .setExclusionStrategies(new AnnotatedFieldGsonExclusionStrategy<>(ElementName.class))
                .registerTypeAdapter(UUID.class, new UuidTypeAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(byte[].class, new BinaryTypeAdapter());

        numberStrategy.decorate(gsonBuilder);

        return gsonBuilder;
    }
}
