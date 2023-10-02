/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;

import io.vavr.control.Try;

final class K8sWorkarounds
{
    private static final Gson GSON = new Gson();

    private K8sWorkarounds()
    {
        throw new IllegalStateException("This class should never be instantiated.");
    }

    @Nonnull
    static
        String
        getEnvironmentVariable( @Nonnull final ScpCfCloudPlatform platform, @Nonnull final String variableName )
    {
        /*
        If the Security Library requests the "VCAP_SERVICES" environment variable, we want to return **all** service
        bindings, including those from the local file system (in the K8s case).
        Therefore, instead of simply returning the result of `platform.getEnvironmentVariable(variableName).getOrNull()`
        we need to make sure to return the result of our `ServiceBindingLoader` (as configured in the ScpCfCloudPlatform).
         */
        if( "VCAP_SERVICES".equalsIgnoreCase(variableName) ) {
            return Try.of(platform::getVcapServices).map(GSON::toJson).getOrNull();
        }

        // For all other environment variables, we want to return the regular result of our environment variable reader.
        return platform.getEnvironmentVariable(variableName).getOrNull();
    }
}
