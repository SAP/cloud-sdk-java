/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import io.vavr.control.Try;

/**
 * This class provides access to the {@link RequestHeaderContainer} for the current context.
 */
@FunctionalInterface
@Beta
public interface RequestHeaderFacade
{
    /**
     * Returns a {@link Try} that might contain the {@link RequestHeaderContainer} of the current context.
     *
     * @return A {@link Try} that might contain the {@link RequestHeaderContainer} of the current context.
     */
    @Nonnull
    Try<RequestHeaderContainer> tryGetRequestHeaders();
}
