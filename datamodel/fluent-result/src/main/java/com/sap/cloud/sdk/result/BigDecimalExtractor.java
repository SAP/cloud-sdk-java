/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code BigDecimal}.
 */
public class BigDecimalExtractor implements ObjectExtractor<BigDecimal>
{
    @Nonnull
    @Override
    public BigDecimal extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asBigDecimal();
    }
}
