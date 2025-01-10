package com.sap.cloud.sdk.datamodel.odata.generator;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * The NameFormattingStrategy used when processing an entity's properties. Used in {@link NamingContext}.
 */
public class LowercaseNameFormattingStrategy implements NameFormattingStrategy
{
    @Nonnull
    @Override
    public String applyFormat( @Nonnull final String input )
    {
        return input.toLowerCase(Locale.ENGLISH);
    }
}
