/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import javax.annotation.Nonnull;

/**
 * NameFormattingStrategy that can be used in the {@link NamingContext} in the OData VDM generator. Implement this
 * interface to create a custom NameFormattingStrategy and set in the NamingContext's constructor.
 */
public interface NameFormattingStrategy
{
    /**
     * Returns the input string formatted according to the rules of the respective implementation of this interface.
     *
     * @param input
     *            The string that should be formatted.
     * @return A formatted string.
     */
    @Nonnull
    String applyFormat( @Nonnull final String input );
}
