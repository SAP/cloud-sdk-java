/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.exception;

import javax.annotation.Nullable;

import lombok.NoArgsConstructor;

/**
 * Throws if a certain field cannot be found for an entity.
 */
@NoArgsConstructor
public class NoSuchEntityFieldException extends RuntimeException
{
    private static final long serialVersionUID = -5897105662911702521L;

    public NoSuchEntityFieldException( @Nullable final String message )
    {
        super(message);
    }

    public NoSuchEntityFieldException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    public NoSuchEntityFieldException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
