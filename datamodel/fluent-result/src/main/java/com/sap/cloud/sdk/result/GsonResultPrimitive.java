/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

import com.google.gson.JsonPrimitive;

import lombok.Data;

/**
 * {@code ResultPrimitive} implementation based on a {@code JsonPrimitive}.
 */
@Data
public class GsonResultPrimitive implements ResultPrimitive
{
    /**
     * The underlying {@link JsonPrimitive} instance.
     */
    protected final JsonPrimitive jsonPrimitive;

    @Override
    public boolean isResultPrimitive()
    {
        return true;
    }

    @Override
    public boolean isResultCollection()
    {
        return false;
    }

    @Override
    public boolean isResultObject()
    {
        return false;
    }

    @Nonnull
    @Override
    public ResultPrimitive getAsPrimitive()
    {
        return this;
    }

    @Nonnull
    @Override
    public ResultCollection getAsCollection()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get a primitive as collection.");
    }

    @Nonnull
    @Override
    public ResultObject getAsObject()
        throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("Cannot get a primitive as object.");
    }

    @Override
    public boolean asBoolean()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsBoolean();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public byte asByte()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsByte();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public char asCharacter()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsString().charAt(0);
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Nonnull
    @Override
    public String asString()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsString();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public int asInteger()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsInt();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public short asShort()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsShort();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public long asLong()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsLong();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public float asFloat()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsFloat();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public double asDouble()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsDouble();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Nonnull
    @Override
    public BigInteger asBigInteger()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsBigInteger();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Nonnull
    @Override
    public BigDecimal asBigDecimal()
        throws UnsupportedOperationException
    {
        try {
            return jsonPrimitive.getAsBigDecimal();
        }
        catch( final Exception e ) {
            throw new UnsupportedOperationException(e);
        }
    }
}
