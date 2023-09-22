/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import io.vavr.control.Try;

/**
 * Represents a specific {@link CloudPlatformFacade} that is used when running on the SAP Deploy with Confidence stack
 * hosted on Cloud Foundry.
 */
@Beta
public class DwcCfCloudPlatformFacade implements CloudPlatformFacade
{
    @Nonnull
    private final Try<CloudPlatform> cloudPlatform = Try.of(DwcCfCloudPlatform::new);

    @Nonnull
    @Override
    public Try<CloudPlatform> tryGetCloudPlatform()
    {
        return cloudPlatform;
    }
}
