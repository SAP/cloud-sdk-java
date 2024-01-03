/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import javax.annotation.Nonnull;

/**
 * NameFormattingStrategy used by default in the {@link NamingContext}. This strategy does nothing, i.e. simply returns
 * the input.
 */
public class DefaultNameFormattingStrategy implements NameFormattingStrategy
{
    @Nonnull
    @Override
    public String applyFormat( @Nonnull final String input )
    {
        return input;
    }
}
