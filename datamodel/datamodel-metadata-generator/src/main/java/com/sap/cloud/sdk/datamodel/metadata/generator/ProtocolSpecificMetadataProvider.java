/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import io.vavr.control.Option;

interface ProtocolSpecificMetadataProvider
{
    @Nonnull
    Option<MavenCoordinate> getMavenCoordinate();

    @Nonnull
    Option<String> getApiSpecificUsage( @Nonnull final DatamodelMetadataInput datamodelMetadataInput );

    @Nonnull
    DatamodelMetadataOutput.ApiType getApiType();
}
