/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Float}.
 */
public class FloatExtractor implements ObjectExtractor<Float>
{
    @Nonnull
    @Override
    public Float extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asFloat();
    }
}
