/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import lombok.Data;

/**
 * {@code ResultObject} implementation based on a {@code JsonObject}.
 */
@Data
public class GsonResultObject implements ResultObject
{
    /**
     * The underlying JSON object.
     */
    protected final JsonObject jsonObject;

    /**
     * The {@link GsonResultElementFactory} to derive GSON builder from.
     */
    protected final GsonResultElementFactory resultElementFactory;

    @Override
    public boolean isResultPrimitive()
    {
        return false;
    }

    @Override
    public boolean isResultCollection()
    {
        return false;
    }

    @Override
    public boolean isResultObject()
    {
        return true;
    }

    @Nonnull
    @Override
    public ResultPrimitive getAsPrimitive()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Nonnull
    @Override
    public ResultCollection getAsCollection()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as collection.");
    }

    @Nonnull
    @Override
    public ResultObject getAsObject()
    {
        return this;
    }

    @Override
    public boolean asBoolean()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public byte asByte()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public char asCharacter()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Nonnull
    @Override
    public String asString()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public int asInteger()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public short asShort()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public long asLong()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public float asFloat()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Override
    public double asDouble()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Nonnull
    @Override
    public BigInteger asBigInteger()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    @Nonnull
    @Override
    public BigDecimal asBigDecimal()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get an object as primitive.");
    }

    /**
     * Returns the element with the given name from this result.
     *
     * @param elementName
     *            The name of the element to be accessed.
     *
     * @return An instance of {@link GsonResultPrimitive} representing the corresponding element.
     *
     * @throws UnsupportedOperationException
     *             If the element is not an instance of {@link ResultObject}.
     */
    @Nullable
    @Override
    public ResultElement get( @Nonnull final String elementName )
        throws UnsupportedOperationException
    {
        try {
            return resultElementFactory.create(jsonObject.get(elementName));
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException("Failed to get element with name " + elementName + ".", e);
        }
    }

    /**
     * Returns a value from this element. The value is represented as an object of the given type.
     * <p>
     * <strong>Important:</strong> In order to deserialize a result element to an object of a given type, you have to
     * annotate all relevant field members of the respective class with {@link ElementName}. For example:
     *
     * <pre>
     * <code>class MyObject
     * {
     *     {@literal @}ElementName( "MANDT" )
     *     SapClient sapClient;
     * }
     * </code>
     * </pre>
     *
     * @param objectType
     *            The type into which the element value should be converted.
     *
     * @return The value as object of type {@code T}.
     *
     * @throws UnsupportedOperationException
     *             If the value could not be converted to the given type.
     */
    @Nonnull
    @Override
    public <T> T as( @Nonnull final Class<T> objectType )
        throws UnsupportedOperationException
    {
        try {
            return resultElementFactory.getGsonBuilder().create().fromJson(jsonObject, objectType);
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(
                "Failed to get element as object of type " + objectType.getName() + ".",
                e);
        }
    }

    @Nonnull
    @Override
    public <T> T as( @Nonnull final Type objectType )
        throws UnsupportedOperationException
    {
        try {
            return resultElementFactory.getGsonBuilder().create().fromJson(jsonObject, objectType);
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(
                "Failed to get element as object of type " + objectType.getTypeName() + ".",
                e);
        }
    }
}
