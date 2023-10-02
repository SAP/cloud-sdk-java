/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade implementation for {@link ScpCfCloudPlatform}.
 */
public class ScpCfCloudPlatformFacade implements CloudPlatformFacade
{
    private Try<CloudPlatform> cloudPlatform;

    @Nonnull
    @Override
    public Try<CloudPlatform> tryGetCloudPlatform()
    {
        if( cloudPlatform == null ) {
            cloudPlatform = Try.of(ScpCfCloudPlatform::new);
        }
        return cloudPlatform;
    }
}
