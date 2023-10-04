/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Double}.
 */
public class DoubleExtractor implements ObjectExtractor<Double>
{
    @Nonnull
    @Override
    public Double extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asDouble();
    }
}
