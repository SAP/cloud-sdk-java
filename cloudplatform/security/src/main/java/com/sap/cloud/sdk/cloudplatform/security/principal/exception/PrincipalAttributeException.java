package com.sap.cloud.sdk.cloudplatform.security.principal.exception;

import javax.annotation.Nonnull;

import lombok.NoArgsConstructor;

/**
 * An exception that is thrown in case of errors related to principal attributes.
 */
@NoArgsConstructor
public class PrincipalAttributeException extends RuntimeException
{
    private static final long serialVersionUID = 7236297206042127942L;

    /**
     * Creates a new {@code PrincipalAttributeException} with the given message.
     *
     * @param message
     *            The message of this {@code PrincipalAttributeException}.
     */
    public PrincipalAttributeException( @Nonnull final String message )
    {
        super(message);
    }

    /**
     * Creates a new {@code PrincipalAttributeException} with the given cause.
     *
     * @param cause
     *            The reason this exception is thrown.
     */
    public PrincipalAttributeException( @Nonnull final Throwable cause )
    {
        super(cause);
    }

    /**
     * Creates a new {@code PrincipalAttributeException} with the given message and the given cause.
     *
     * @param message
     *            The message of this {@code PrincipalAttributeException}.
     * @param cause
     *            The reason this exception is thrown.
     */
    public PrincipalAttributeException( @Nonnull final String message, @Nonnull final Throwable cause )
    {
        super(message, cause);
    }
}
