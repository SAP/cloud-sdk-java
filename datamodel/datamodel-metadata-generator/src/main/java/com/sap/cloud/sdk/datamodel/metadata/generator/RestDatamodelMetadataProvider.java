package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RestDatamodelMetadataProvider implements ProtocolSpecificMetadataProvider
{
    private final InputProperties inputProperties;

    @Nonnull
    @Override
    public DatamodelMetadataOutput.ApiType getApiType()
    {
        return DatamodelMetadataOutput.ApiType.OPEN_API;
    }

    @Nonnull
    @Override
    public Option<MavenCoordinate> getMavenCoordinate()
    {
        if( inputProperties.getMavenArtifactId() == null || inputProperties.getMavenGroupId() == null ) {
            return Option.none();
        }
        return Option
            .of(
                MavenCoordinate
                    .builder()
                    .groupId(inputProperties.getMavenGroupId())
                    .artifactId(inputProperties.getMavenArtifactId())
                    .build());
    }

    @Nonnull
    @Override
    public Option<String> getApiSpecificUsage( @Nonnull final DatamodelMetadataInput datamodelMetadataInput )
    {
        if( datamodelMetadataInput.getProtocolSpecificMetadata().getApiUsageMetadata() == null ) {
            return Option.none();
        }

        if( !(datamodelMetadataInput
            .getProtocolSpecificMetadata()
            .getApiUsageMetadata() instanceof RestApiUsageMetadata) ) {
            throw new IllegalStateException("Instance of " + RestApiUsageMetadata.class + " expected.");
        }

        final RestApiUsageMetadata restApiUsageMetadata =
            (RestApiUsageMetadata) datamodelMetadataInput.getProtocolSpecificMetadata().getApiUsageMetadata();

        final String usageSnippet =
            MetadataApiSpecificUsage
                .builder()
                .data(restApiUsageMetadata)
                .initialValue(
                    new MetadataApiSpecificUsage.Declaration(
                        "destination",
                        Destination.class.getName(),
                        "DestinationAccessor.getDestination(\"MyDestination\")"))
                .enforceImport(DestinationAccessor.class.getName())
                .build()
                .getUsage();
        return Option.of(usageSnippet);
    }
}
