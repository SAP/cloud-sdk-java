/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

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
