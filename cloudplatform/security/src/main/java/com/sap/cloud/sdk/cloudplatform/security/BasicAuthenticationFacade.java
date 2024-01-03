/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade for retrieving the current {@link BasicCredentials}.
 */
@FunctionalInterface
public interface BasicAuthenticationFacade
{
    /**
     * Retrieves the username password pair and returns them as {@link Try.Success}. If they could not be retrieved or
     * an error occurred while parsing them a {@link Try.Failure} containing the exception will be returned.
     *
     * @return A {@link Try} containing either the retrieved {@code BasicCredentials} or an {@link Exception}.
     */
    @Nonnull
    Try<BasicCredentials> tryGetBasicCredentials();
}
