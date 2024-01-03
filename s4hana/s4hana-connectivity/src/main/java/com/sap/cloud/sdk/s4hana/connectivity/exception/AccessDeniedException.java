/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.exception;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Thrown when a certain service denies access to the requested resources.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@NoArgsConstructor
@Deprecated
public class AccessDeniedException extends RequestExecutionException
{
    private static final long serialVersionUID = 8256471661877663966L;

    /**
     * The principal reference.
     */
    @Getter
    @Nullable
    protected transient Principal principal = null;

    /**
     * The missing authorization values.
     */
    @Getter
    @Nullable
    protected transient String missingAuthorizations = null;

    /**
     * Static factory method to instantiate a new exception.
     *
     * @param principal
     *            The principal reference.
     * @param missingAuthorizations
     *            The missing authorization values.
     * @return The newly created exception.
     */
    @Nonnull
    public static
        AccessDeniedException
        raiseMissingAuthorizations( @Nullable final Principal principal, @Nullable final String missingAuthorizations )
    {
        return new AccessDeniedException(principal, missingAuthorizations);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     */
    public AccessDeniedException( @Nullable final String message )
    {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause
     *            The error cause.
     */
    public AccessDeniedException( @Nullable final Throwable cause )
    {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message
     *            The message.
     * @param cause
     *            The error cause.
     */
    public AccessDeniedException( @Nullable final String message, @Nullable final Throwable cause )
    {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param principal
     *            The principal reference.
     * @param missingAuthorizations
     *            The missing authorization values.
     */
    public AccessDeniedException( @Nullable final Principal principal, @Nullable final String missingAuthorizations )
    {
        super(buildErrorMessage(principal, missingAuthorizations));

        this.principal = principal;
        this.missingAuthorizations = missingAuthorizations;
    }

    private static
        String
        buildErrorMessage( @Nullable final Principal principal, @Nullable final String missingAuthorizations )
    {
        if( principal != null ) {
            return "Principal "
                + principal
                + " does not have the required authorizations"
                + (missingAuthorizations != null ? ": " + missingAuthorizations : "")
                + ".";
        } else {
            return "Missing authorizations" + (missingAuthorizations != null ? ": " + missingAuthorizations : "") + ".";
        }
    }
}
