/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.Data;

/**
 * Factory implementation that creates a {@code ResultElement}, based on a given {@code JsonElement}.
 */
@Data
public class GsonResultElementFactory implements ResultElementFactory<JsonElement>
{
    /**
     * The {@link GsonBuilder} instance to be used for deserialization.
     */
    @Nonnull
    protected final GsonBuilder gsonBuilder;

    /**
     * Returns a {@link ResultPrimitive} from the given {@code resultElement}.
     *
     * @param resultElement
     *            The {@code JsonElement} to be converted.
     * @return The {@code ResultPrimitive} instance.
     */
    @Nonnull
    protected ResultPrimitive newPrimitive( @Nonnull final JsonElement resultElement )
    {
        return new GsonResultPrimitive(resultElement.getAsJsonPrimitive());
    }

    /**
     * Returns a {@link ResultObject} from the given {@code resultElement}.
     *
     * @param resultElement
     *            The {@code JsonElement} to be converted.
     * @return The {@code ResultObject} instance.
     */
    @Nonnull
    protected ResultObject newObject( @Nonnull final JsonElement resultElement )
    {
        return new GsonResultObject(resultElement.getAsJsonObject(), this);
    }

    /**
     * Returns a {@link ResultCollection} from the given {@code resultElement}.
     *
     * @param resultElement
     *            The {@code JsonElement} to be converted.
     * @return The {@code ResultCollection} instance.
     */
    @Nonnull
    protected ResultCollection newCollection( @Nonnull final JsonElement resultElement )
    {
        final List<ResultElement> resultElements = new ArrayList<>();

        for( final JsonElement jsonElement : resultElement.getAsJsonArray() ) {
            resultElements.add(create(jsonElement));
        }

        return new DefaultResultCollection(resultElements);
    }

    @Nullable
    @Override
    public ResultElement create( @Nullable final JsonElement resultElement )
        throws IllegalArgumentException
    {
        if( resultElement == null ) {
            return null;
        }

        if( resultElement.isJsonPrimitive() ) {
            return newPrimitive(resultElement);
        }

        if( resultElement.isJsonObject() ) {
            return newObject(resultElement);
        }

        if( resultElement.isJsonArray() ) {
            return newCollection(resultElement);
        }

        throw new IllegalArgumentException(
            "Failed to convert "
                + JsonElement.class.getSimpleName()
                + " "
                + resultElement
                + " to instance of "
                + ResultElement.class.getSimpleName()
                + ".");
    }
}
