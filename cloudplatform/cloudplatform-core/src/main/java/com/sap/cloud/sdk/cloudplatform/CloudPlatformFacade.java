package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade interface encapsulating the creation of a concrete {@link CloudPlatform} implementation.
 */
@FunctionalInterface
public interface CloudPlatformFacade
{
    /**
     * Returns a {@link Try} of the platform-specific implementation instance of {@link CloudPlatform}.
     *
     * @return A {@link Try} of the platform-specific implementation.
     */
    @Nonnull
    Try<CloudPlatform> tryGetCloudPlatform();
}
