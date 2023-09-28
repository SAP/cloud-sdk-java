package com.sap.cloud.sdk.testutil;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.CloudPlatform;
import com.sap.cloud.sdk.cloudplatform.CloudPlatformFacade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultCloudPlatformMocker implements CloudPlatformMocker
{
    private final Supplier<CloudPlatformFacade> resetCloudPlatformFacade;

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private CloudPlatform currentCloudPlatform;

    @Nonnull
    @Override
    public CloudPlatform mockCurrentCloudPlatform()
    {
        return mockCurrentCloudPlatform(MockUtil.MOCKED_CLOUD_APP_NAME);
    }

    @Nonnull
    @Override
    public CloudPlatform mockCurrentCloudPlatform( @Nonnull final String applicationName )
    {
        resetCloudPlatformFacade.get();

        currentCloudPlatform = Mockito.mock(CloudPlatform.class);
        Mockito.when(currentCloudPlatform.getApplicationName()).thenReturn(applicationName);

        return currentCloudPlatform;
    }
}
