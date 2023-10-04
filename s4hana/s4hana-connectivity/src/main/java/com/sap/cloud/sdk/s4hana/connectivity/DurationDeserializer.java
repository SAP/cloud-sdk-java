/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.lang.reflect.Type;
import java.time.Duration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Helper class used for parsing durations measured by the ERP.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class DurationDeserializer implements JsonDeserializer<Duration>
{
    @Nullable
    private static Duration toDuration( final String microsecsStr )
    {
        if( microsecsStr == null ) {
            return null;
        }

        final long microsecs;
        try {
            microsecs = Long.parseLong(microsecsStr.trim());
        }
        catch( final NumberFormatException e ) {
            return null;
        }
        return Duration.ofNanos(microsecs * 1000);
    }

    /**
     * Parses and converts a string, provided in the {@link JsonElement}, from microseconds to {@link Duration}.
     *
     * @return The deserialized {@link Duration}, or {@code null} if the provided string is an invalid number.
     */
    @Override
    @Nullable
    public Duration deserialize(
        @Nonnull final JsonElement json,
        @Nonnull final Type typeOfT,
        @Nonnull final JsonDeserializationContext context )
        throws JsonParseException
    {
        return toDuration(json.getAsString());
    }
}
