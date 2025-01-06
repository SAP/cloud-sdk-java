/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import lombok.Data;
import lombok.Getter;

/**
 * This implementation of {@link Principal} represents the information available for an authenticated entity.
 */
@Data
public class DefaultPrincipal implements Principal
{
    @Getter
    @Nonnull
    private final String principalId;

    /**
     * Creates a new {@link DefaultPrincipal}.
     *
     * @param principalId
     *            The ID of the principal
     */
    public DefaultPrincipal( @Nonnull final String principalId )
    {
        this.principalId = principalId;
    }
}
