package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Accessor class for information related to the current cloud platform.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
public final class CloudPlatformAccessor
{
    @Nonnull
    private static Try<CloudPlatformFacade> cloudPlatformFacade = FacadeLocator.getFacade(CloudPlatformFacade.class);

    /**
     * Returns the {@link CloudPlatformFacade} instance.
     *
     * @return The {@link CloudPlatformFacade} instance, or {@code null}.
     */
    @Nullable
    public static CloudPlatformFacade getCloudPlatformFacade()
    {
        return cloudPlatformFacade.getOrNull();
    }

    /**
     * Returns a {@link Try} of the {@link CloudPlatformFacade} instance.
     *
     * @return A {@link Try} of the {@link CloudPlatformFacade} instance.
     */
    @Nonnull
    public static Try<CloudPlatformFacade> tryGetCloudPlatformFacade()
    {
        return cloudPlatformFacade;
    }

    /**
     * Replaces the default {@link CloudPlatformFacade} instance.
     *
     * @param cloudPlatformFacade
     *            An instance of {@link CloudPlatformFacade}. Use {@code null} to reset the facade.
     */
    public static void setCloudPlatformFacade( @Nullable final CloudPlatformFacade cloudPlatformFacade )
    {
        if( cloudPlatformFacade == null ) {
            CloudPlatformAccessor.cloudPlatformFacade = FacadeLocator.getFacade(CloudPlatformFacade.class);
        } else {
            CloudPlatformAccessor.cloudPlatformFacade = Try.success(cloudPlatformFacade);
        }
    }

    /**
     * Returns the current {@link CloudPlatform}.
     *
     * @return The current {@link CloudPlatform}.
     *
     * @throws CloudPlatformException
     *             If there is an issue while accessing the {@link CloudPlatform}.
     */
    @Nonnull
    public static CloudPlatform getCloudPlatform()
        throws CloudPlatformException
    {
        return tryGetCloudPlatform().getOrElseThrow(failure -> {
            if( failure instanceof CloudPlatformException ) {
                throw (CloudPlatformException) failure;
            } else {
                throw new CloudPlatformException("Failed to get current cloud platform.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of the current {@link CloudPlatform}.
     *
     * @return A {@link Try} of the current {@link CloudPlatform}.
     */
    @Nonnull
    public static Try<CloudPlatform> tryGetCloudPlatform()
    {
        return cloudPlatformFacade.flatMap(CloudPlatformFacade::tryGetCloudPlatform);
    }
}
