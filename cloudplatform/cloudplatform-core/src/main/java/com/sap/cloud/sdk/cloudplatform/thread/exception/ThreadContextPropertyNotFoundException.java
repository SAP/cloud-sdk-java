package com.sap.cloud.sdk.cloudplatform.thread.exception;

import javax.annotation.Nonnull;

import lombok.NoArgsConstructor;

/**
 * Exception indicating a ThreadContext property not being found.
 */
@NoArgsConstructor
public class ThreadContextPropertyNotFoundException extends ThreadContextPropertyException
{
    private static final long serialVersionUID = -4099758265561452522L;

    /**
     * Default constructor.
     *
     * @param propertyName
     *            Property name not found.
     */
    public ThreadContextPropertyNotFoundException( @Nonnull final String propertyName )
    {
        super("Property '" + propertyName + "' does not exist.");
    }
}
