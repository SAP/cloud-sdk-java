/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;

/**
 * CloudPlatform interface to provide common platform features.
 */
public interface CloudPlatform
{
    /**
     * Get the application name.
     *
     * @return The name of the application.
     * @throws CloudPlatformException
     *             If there is an error accessing the application name.
     */
    @Nonnull
    String getApplicationName()
        throws CloudPlatformException;

    /**
     * Get the application url.
     *
     * @return The URL of the current application.
     * @throws CloudPlatformException
     *             If there is an error accessing the application url.
     */
    @Nonnull
    String getApplicationUrl()
        throws CloudPlatformException;

    /**
     * Get the process ID of the current application.
     *
     * @return The process ID.
     * @throws CloudPlatformException
     *             If there is an error accessing the process ID.
     */
    @Nonnull
    String getApplicationProcessId()
        throws CloudPlatformException;

    /**
     * Set a custom function to read environment variables with.
     *
     * @param reader
     *            A generic key-value mapping.
     */
    @Beta
    default void setEnvironmentVariableReader( @Nonnull final Function<String, String> reader )
    {
        throw new UnsupportedOperationException();
    }
}
