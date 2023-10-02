/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * Class representing an unstructured result primitive resulting from a call to an external service (e.g. after invoking
 * a BAPI or a remote-enabled function module).
 * <p>
 * Access the content of this result element using the respective method, such as {@link #asString()} to obtain a String
 * object.
 */
public interface ResultPrimitive extends ResultElement
{
    @Override
    boolean asBoolean()
        throws UnsupportedOperationException;

    @Override
    byte asByte()
        throws UnsupportedOperationException;

    @Override
    char asCharacter()
        throws UnsupportedOperationException;

    @Nonnull
    @Override
    String asString()
        throws UnsupportedOperationException;

    @Override
    int asInteger()
        throws UnsupportedOperationException;

    @Override
    short asShort()
        throws UnsupportedOperationException;

    @Override
    long asLong()
        throws UnsupportedOperationException;

    @Override
    float asFloat()
        throws UnsupportedOperationException;

    @Override
    double asDouble()
        throws UnsupportedOperationException;

    @Nonnull
    @Override
    BigInteger asBigInteger()
        throws UnsupportedOperationException;

    @Nonnull
    @Override
    BigDecimal asBigDecimal()
        throws UnsupportedOperationException;
}
