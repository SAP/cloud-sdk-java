package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Boolean}.
 */
public class BooleanExtractor implements ObjectExtractor<Boolean>
{
    @Nonnull
    @Override
    public Boolean extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asBoolean();
    }
}
