package com.sap.cloud.sdk.result;

import javax.annotation.Nonnull;

/**
 * {@code ObjectExtractor} implementation transforming a given {@code ResultElement} to a {@code Character}.
 */
public class CharacterExtractor implements ObjectExtractor<Character>
{
    @Nonnull
    @Override
    public Character extract( @Nonnull final ResultElement resultElement )
    {
        return resultElement.asCharacter();
    }
}
