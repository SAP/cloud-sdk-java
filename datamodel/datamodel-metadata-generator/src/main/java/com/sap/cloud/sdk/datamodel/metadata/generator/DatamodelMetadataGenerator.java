/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Generates metadata about the Virtual Data Model.
 */
@Slf4j
public class DatamodelMetadataGenerator
{
    private static final String METADATA_GENERATOR_INPUT_FILENAME = "metadata-generator-properties.json";
    private static final String METADATA_FILE_SUFFIX = "_CLIENT_JAVA";
    private InputProperties inputProperties;
    private final Path metadataGeneratorPropertiesFile;
    private final MavenRepositoryAccessor mavenRepositoryAccessor;

    /**
     * Constructor for DatamodelMetadataGenerator.
     *
     * @param inputDirectory
     *            The input directory where input for metadata generation "metadata-generator-properties.json" resides
     */
    public DatamodelMetadataGenerator( @Nonnull final Path inputDirectory )
    {
        this(inputDirectory, new DefaultMavenRepositoryAccessor());
    }

    DatamodelMetadataGenerator(
        @Nonnull final Path inputDirectory,
        @Nonnull final MavenRepositoryAccessor mavenRepositoryAccessor )
    {
        metadataGeneratorPropertiesFile = inputDirectory.resolve(METADATA_GENERATOR_INPUT_FILENAME);
        this.mavenRepositoryAccessor = mavenRepositoryAccessor;
    }

    /**
     * Checks if metadata generation is enabled along with the datamodel generation.
     *
     * @return A boolean value, which indicates if datamodel metadata generation is enabled.
     */
    public boolean isMetadataGenerationEnabled()
    {
        return isMetadataGeneratorPropertiesFileExisting();
    }

    /**
     * Generates metadata about the Virtual Data Model.
     *
     * Throws {@link MetadataGenerationException} in case of an error.
     *
     * By calling {@link #isMetadataGenerationEnabled()} you can check beforehand if the required configuration file is
     * existing within the input directory. If that is not the case, you can omit calling this method.
     *
     * @param datamodelMetadataInput
     *            The list of {@link DatamodelMetadataInput} used for generation.
     * @param outputDirectory
     *            The directory where the generated files should be written.
     *
     */
    public void generate(
        @Nonnull final List<DatamodelMetadataInput> datamodelMetadataInput,
        @Nonnull final Path outputDirectory )
    {
        inputProperties = getInputProperties();

        for( final DatamodelMetadataInput datamodelMetadataInputEntry : datamodelMetadataInput ) {
            final DatamodelMetadataOutput datamodelMetadataOutput =
                getDataModelMetadataOutput(datamodelMetadataInputEntry);

            try {
                //Create a new "metadata" directory so that all metadata files lie in one central folder
                final Path modifiedOutputDir = Files.createDirectories(outputDirectory.resolve("metadata"));

                log
                    .info(
                        "Metadata files will be generated into output directory: "
                            + modifiedOutputDir.toAbsolutePath());

                saveMetadataToFile(datamodelMetadataOutput, datamodelMetadataInputEntry, modifiedOutputDir);
            }
            catch( final IOException e ) {
                throw new MetadataGenerationException("Failed to save the datamodel metadata.", e);
            }
        }
    }

    private DatamodelMetadataOutput getDataModelMetadataOutput( final DatamodelMetadataInput datamodelMetadataInput )
    {
        return new ProtocolAgnosticMetadataProvider(
            getProtocolSpecificMetadataProvider(datamodelMetadataInput.getProtocolSpecificMetadata().getServiceType()),
            mavenRepositoryAccessor,
            inputProperties)
            .tryGetDatamodelMetadata(datamodelMetadataInput)
            .getOrElseThrow(e -> new MetadataGenerationException("Failure generating datamodel metadata.", e));
    }

    private ProtocolSpecificMetadataProvider getProtocolSpecificMetadataProvider( final ServiceType serviceType )
    {
        switch( serviceType ) {
            case ODATA_V2:
            case ODATA_V4:
                return new ODataDatamodelMetadataProvider(inputProperties);
            case REST:
                return new RestDatamodelMetadataProvider(inputProperties);
            default:
                throw new IllegalStateException("Service type " + serviceType + " not supported.");
        }
    }

    private boolean isMetadataGeneratorPropertiesFileExisting()
    {
        if( Files.exists(metadataGeneratorPropertiesFile) ) {
            log
                .info(
                    "Input file required for metadata generation is available and exists at "
                        + metadataGeneratorPropertiesFile.toAbsolutePath());
            return true;
        }
        return false;
    }

    @Nonnull
    private InputProperties getInputProperties()
    {
        if( !isMetadataGeneratorPropertiesFileExisting() ) {
            throw new MetadataGenerationException(
                "Failed to start metadata generation as " + METADATA_GENERATOR_INPUT_FILENAME + " is not available");
        }

        try {
            return new Gson().fromJson(Files.newBufferedReader(metadataGeneratorPropertiesFile), InputProperties.class);
        }
        catch( final IOException e ) {
            throw new MetadataGenerationException(
                "Failed to read from input file: " + metadataGeneratorPropertiesFile + " during metadata generation",
                e);
        }

    }

    private void saveMetadataToFile(
        @Nonnull final DatamodelMetadataOutput datamodelMetadataOutput,
        @Nonnull final DatamodelMetadataInput datamodelMetadataInput,
        @Nonnull final Path outputDirectory )
        throws IOException
    {
        final String metadataFileNamePrefix =
            FilenameUtils.removeExtension(datamodelMetadataInput.getApiSpecFilePath().getFileName().toString());

        final Path filePath = outputDirectory.resolve(metadataFileNamePrefix + METADATA_FILE_SUFFIX + ".json");

        final String serializedMetadata =
            new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create()
                .toJson(datamodelMetadataOutput);

        log
            .info(
                "Saving metadata for {} API specification {} to file {}.",
                datamodelMetadataInput.getProtocolSpecificMetadata().getServiceType(),
                datamodelMetadataInput.getApiSpecFilePath().getFileName().toString(),
                filePath);

        Files.write(filePath, serializedMetadata.getBytes(StandardCharsets.UTF_8));
    }
}
