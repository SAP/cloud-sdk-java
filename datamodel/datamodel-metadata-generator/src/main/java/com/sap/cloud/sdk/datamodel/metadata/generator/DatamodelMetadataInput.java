/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.net.URI;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Builder;
import lombok.Value;

/**
 * Metadata about the Virtual Data Model.
 */
@Value
@Builder
@Beta
public class DatamodelMetadataInput
{
    boolean codeGenerationSuccessful;
    @Nonnull
    Path apiSpecFilePath;
    @Nonnull
    ZonedDateTime generationTime;
    @Nonnull
    String description;
    @Nonnull
    MavenCoordinate generatorMavenCoordinate;
    @Nonnull
    URI generatorRepositoryLink;
    @Nonnull
    ProtocolSpecificMetadata protocolSpecificMetadata;
}
