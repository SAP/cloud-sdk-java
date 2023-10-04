/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Short}.
 */
public class ShortExtractor implements ObjectExtractor<Short>
{
    @Nonnull
    @Override
    public Short extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asShort();
    }
}
