/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;

import com.google.common.annotations.Beta;

import io.vavr.control.Try;

/**
 * Creates an SslContext based on properties of the platform.
 */
@Beta
@FunctionalInterface
public interface PlatformSslContextProvider
{
    /**
     * Try to create an {@link SSLContext} based on an identity provided by the platform. For example, the platform may
     * provide X509 certificates in environment variables or on the file system.
     *
     * @return A {@link Try} containing either the derived SSL context or a failure.
     */
    @Nonnull
    Try<SSLContext> tryGetContext();
}
