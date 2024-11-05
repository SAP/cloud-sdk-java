/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.utility;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.NoArgsConstructor;

/**
 * Represents the default {@link NamingStrategy} implementation, which generates syntactically correct Java names
 * without doing any additional modifications.
 */
@NoArgsConstructor
public class SimpleNamingStrategy extends AbstractNamingStrategy
{
    /**
     * Initializes a new {@link SimpleNamingStrategy} with the given {@code nameSource}.
     *
     * @param nameSource
     *            The {@link NameSource} to use.
     */
    public SimpleNamingStrategy( @Nonnull final NameSource nameSource )
    {
        super(nameSource);
    }
}
