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

    /**
     * Initializes a new {@link NoSuchEntityFieldException} instance.
     *
     * @param message
     *            The exception message.
     */
    public NoSuchEntityFieldException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Initializes a new {@link NoSuchEntityFieldException} instance.
     *
     * @param cause
     *            The exception cause.
     */
    public NoSuchEntityFieldException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Initializes a new {@link NoSuchEntityFieldException} instance.
     *
     * @param message
     *            The exception message.
     * @param cause
     *            The exception cause.
     */
    public NoSuchEntityFieldException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }
}
