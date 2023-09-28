package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;

/**
 * Represents a class that can set specialized configuration parameters.
 */
@FunctionalInterface
public interface DestinationOptionsAugmenter
{
    /**
     * Use this class to set configuration parameters that are specific to a platform/environment/etc.
     *
     * Once called, any parameter setting methods of this class will affect the provided
     * {@link com.sap.cloud.sdk.cloudplatform.connectivity.DestinationOptions.Builder} and the resulting
     * {@link DestinationOptions} object that the builder creates.
     *
     * @param builder
     *            The builder to attach this augmenter to
     */
    void augmentBuilder( @Nonnull final DestinationOptions.Builder builder );
}
