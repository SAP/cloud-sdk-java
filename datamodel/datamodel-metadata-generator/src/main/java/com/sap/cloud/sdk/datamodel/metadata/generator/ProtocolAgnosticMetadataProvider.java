/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ProtocolAgnosticMetadataProvider
{
    private final ProtocolSpecificMetadataProvider metadataProvider;
    private final MavenRepositoryAccessor mavenRepositoryAccessor;
    private final InputProperties inputProperties;

    @Nonnull
    Try<DatamodelMetadataOutput> tryGetDatamodelMetadata( @Nonnull final DatamodelMetadataInput datamodelMetadataInput )
    {
        return Try.of(() -> {
            final Option<MavenCoordinate> maybeLibraryInformation = metadataProvider.getMavenCoordinate();

            final ServiceStatus serviceStatus;
            @Nullable
            final DatamodelMetadataOutput.PreGeneratedLibrary preGeneratedLibrary;
            if( maybeLibraryInformation.isDefined() && datamodelMetadataInput.isCodeGenerationSuccessful() ) {
                serviceStatus = ServiceStatus.CERTIFIED;
                preGeneratedLibrary = getPreGeneratedLibrary(datamodelMetadataInput, maybeLibraryInformation.get());
            } else {
                serviceStatus =
                    datamodelMetadataInput.isCodeGenerationSuccessful() ? ServiceStatus.VERIFIED : ServiceStatus.FAILED;
                preGeneratedLibrary = null;
            }

            @Nullable
            final String apiSpecificUsageInstructions =
                datamodelMetadataInput.isCodeGenerationSuccessful()
                    ? metadataProvider.getApiSpecificUsage(datamodelMetadataInput).getOrNull()
                    : null;

            return DatamodelMetadataOutput
                .builder()
                .serviceStatus(serviceStatus)
                .apiType(metadataProvider.getApiType())
                .pregeneratedLibrary(preGeneratedLibrary)
                .apiSpecificUsage(apiSpecificUsageInstructions)
                .build();
        });
    }

    private DatamodelMetadataOutput.PreGeneratedLibrary getPreGeneratedLibrary(
        final DatamodelMetadataInput datamodelMetadataInput,
        final MavenCoordinate mavenCoordinate )
    {
        final String libraryGroupId = mavenCoordinate.getGroupId();
        final String libraryArtifactId = mavenCoordinate.getArtifactId();
        final String libraryVersion =
            getLibraryVersion(mavenCoordinate)
                .getOrElseThrow(
                    cause -> new MetadataGenerationException("Failed to determine library version.", cause));

        return DatamodelMetadataOutput.PreGeneratedLibrary
            .builder()
            .groupId(libraryGroupId)
            .artifactId(libraryArtifactId)
            .version(libraryVersion)
            .compatibilityNotes("")
            .description(datamodelMetadataInput.getDescription())
            .build();
    }

    private Try<String> getLibraryVersion( final MavenCoordinate libraryMavenCoordinate )
    {
        return inputProperties.getLibraryVersion() == null
            ? mavenRepositoryAccessor.getLatestModuleVersion(libraryMavenCoordinate)
            : Try.success(inputProperties.getLibraryVersion());
    }
}
