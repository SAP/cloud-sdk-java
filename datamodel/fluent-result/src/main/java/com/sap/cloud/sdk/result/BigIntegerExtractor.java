/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigInteger;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code BigInteger}.
 */
public class BigIntegerExtractor implements ObjectExtractor<BigInteger>
{
    @Nonnull
    @Override
    public BigInteger extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asBigInteger();
    }
}
