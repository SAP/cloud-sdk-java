package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

/**
 * The NameFormattingStrategy used when processing an entity's properties. Used in {@link NamingContext}.
 */
@Beta
public class LowercaseNameFormattingStrategy implements NameFormattingStrategy
{
    @Nonnull
    @Override
    public String applyFormat( @Nonnull final String input )
    {
        return input.toLowerCase(Locale.ENGLISH);
    }
}
