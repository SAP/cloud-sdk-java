package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Integer}.
 */
public class IntegerExtractor implements ObjectExtractor<Integer>
{
    @Nonnull
    @Override
    public Integer extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asInteger();
    }
}
