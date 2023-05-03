package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.CloudPlatform;

interface CloudPlatformMocker
{
    /**
     * Mocks the current {@link CloudPlatform} using application name {@link MockUtil#MOCKED_CLOUD_APP_NAME}.
     */
    @Nonnull
    CloudPlatform mockCurrentCloudPlatform();

    /**
     * Mocks the current {@link CloudPlatform} using the given application name.
     */
    @Nonnull
    CloudPlatform mockCurrentCloudPlatform( @Nonnull final String applicationName );
}
