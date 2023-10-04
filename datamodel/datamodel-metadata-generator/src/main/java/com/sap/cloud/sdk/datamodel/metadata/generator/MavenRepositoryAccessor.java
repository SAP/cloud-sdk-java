/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import io.vavr.control.Try;

@Beta
interface MavenRepositoryAccessor
{
    /**
     * Reads the latest version of a Maven module from the default Maven repository (Maven Central).
     *
     * @param mavenCoordinate
     *            The coordinate of the Maven module
     * @return A {@link Try} wrapping the latest version of the related module
     */
    @Nonnull
    Try<String> getLatestModuleVersion( @Nonnull final MavenCoordinate mavenCoordinate );
}
