package com.sap.cloud.sdk.typeconverter;

import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@code JsonDeserializer} deserializing a {@code JsonPrimitive} to an object of type {@code T} using
 * the given {@code TypeConverter}.
 *
 * @param <T>
 *            The type of the object created by deserialization.
 */
@RequiredArgsConstructor
public class TypeConverterGsonDeserializer<T> implements JsonDeserializer<T>
{
    private final TypeConverter<T, String> typeConverter;

    @Override
    @Nullable
    public T deserialize(
        @Nonnull final JsonElement json,
        @Nonnull final Type typeOfT,
        @Nonnull final JsonDeserializationContext context )
        throws JsonParseException
    {
        return typeConverter.fromDomain(json.getAsJsonPrimitive().getAsString()).orNull();
    }
}
