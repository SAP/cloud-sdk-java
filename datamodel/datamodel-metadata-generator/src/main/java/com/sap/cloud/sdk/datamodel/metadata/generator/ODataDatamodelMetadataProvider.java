package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ODataDatamodelMetadataProvider implements ProtocolSpecificMetadataProvider
{
    private final InputProperties inputProperties;

    @Nonnull
    @Override
    public DatamodelMetadataOutput.ApiType getApiType()
    {
        return DatamodelMetadataOutput.ApiType.ODATA;
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
            .getApiUsageMetadata() instanceof ODataApiUsageMetadata) ) {
            throw new IllegalStateException("Instance of " + ODataApiUsageMetadata.class + " expected.");
        }

        final ODataApiUsageMetadata oDataApiUsageMetadata =
            (ODataApiUsageMetadata) datamodelMetadataInput.getProtocolSpecificMetadata().getApiUsageMetadata();

        final String usageSnippet =
            MetadataApiSpecificUsage
                .builder()
                .initialValue(
                    new MetadataApiSpecificUsage.Declaration(
                        "destination",
                        Destination.class.getName(),
                        "DestinationAccessor.getDestination(\"MyDestination\")"))
                .enforceImport(DestinationAccessor.class.getName())
                .data(oDataApiUsageMetadata)
                .build()
                .getUsage();

        return Option.of(usageSnippet);
    }
}
