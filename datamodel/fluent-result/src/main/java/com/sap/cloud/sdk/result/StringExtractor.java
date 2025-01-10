package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code String}.
 */
public class StringExtractor implements ObjectExtractor<String>
{
    @Nonnull
    @Override
    public String extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asString();
    }
}
