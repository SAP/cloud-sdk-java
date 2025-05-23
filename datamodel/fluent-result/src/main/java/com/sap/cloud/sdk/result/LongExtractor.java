package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Long}.
 */
public class LongExtractor implements ObjectExtractor<Long>
{
    @Nonnull
    @Override
    public Long extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asLong();
    }
}
