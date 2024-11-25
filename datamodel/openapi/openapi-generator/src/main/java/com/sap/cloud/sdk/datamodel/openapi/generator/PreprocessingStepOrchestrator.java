/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Pre-processes{@link GenerationConfiguration#getInputSpec()} to create a temporary input file for client generation
 * that contains processed vendor extensions.
 */
@Slf4j
class PreprocessingStepOrchestrator
{
    private final Path originalInputSpec;
    private Path adjustedInputSpec;
    private boolean originalInputSpecChanged;
    private final OpenApiSpecUtil.FileFormat fileFormat;

    @Setter( AccessLevel.PACKAGE )
    private List<Supplier<PreprocessingStep>> steps =
        new ArrayList<>(Arrays.asList(ApiClassNameFieldPreprocessor::new, MethodNameFieldPreprocessor::new));

    PreprocessingStepOrchestrator( @Nonnull final Path originalInputSpec )
    {
        this.originalInputSpec = originalInputSpec;
        fileFormat = OpenApiSpecUtil.getFileFormat(originalInputSpec);
    }

    /**
     * Enable/Disable generating clients for input specification with oneOf, anyOf keywords based on
     * {@code anyOfOneOfGenerationEnabled}
     *
     * @param anyOfOneOfGenerationEnabled
     *            true if clients should be generated for input specification with oneOf, anyOf
     *
     * @return this
     */
    PreprocessingStepOrchestrator enableAnyOfOneOfGeneration( final boolean anyOfOneOfGenerationEnabled )
    {
        if( !anyOfOneOfGenerationEnabled ) {
            steps.add(ValidationKeywordsPreprocessor::new);
        }
        return this;
    }

    @Nonnull
    OpenApiSpec performPreprocessingSteps()
    {
        final ObjectMapper objectMapper = fileFormat.getObjectMapperSupplier().get();

        final JsonNode rootNode;
        try {
            rootNode = OpenApiSpecUtil.getJsonNodeFromInputSpec(objectMapper, originalInputSpec);
        }
        catch( final IOException e ) {
            throw new OpenApiGeneratorException("Error during pre-processing.", e);
        }

        if( rootNode == null ) {
            throw new OpenApiGeneratorException(
                "Could not parse the input specification " + originalInputSpec.toAbsolutePath());
        }

        JsonNode currentNode = rootNode;
        boolean changesApplied = false;

        for( final Supplier<PreprocessingStep> step : steps ) {
            final PreprocessingStep.PreprocessingStepResult stepResult = step.get().execute(currentNode, objectMapper);

            if( stepResult.changesApplied() ) {
                currentNode = stepResult.getJsonNode();
                changesApplied = true;
            }
        }

        if( changesApplied ) {
            originalInputSpecChanged = true;
            adjustedInputSpec = writeProcessedSpecToTempFile(currentNode, objectMapper);
            return new OpenApiSpec(adjustedInputSpec, currentNode);
        }

        return new OpenApiSpec(originalInputSpec, currentNode);
    }

    private Path writeProcessedSpecToTempFile( @Nonnull final JsonNode rootNode, @Nonnull final ObjectMapper mapper )
    {
        try {
            final Path path =
                Files
                    .createTempFile(
                        originalInputSpec.getFileName().toString(),
                        "." + fileFormat.getFileExtensions().get(0));

            final String content = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            Files.writeString(path, content);

            return path.normalize().toAbsolutePath();
        }
        catch( final IOException e ) {
            throw new OpenApiGeneratorException("Error during pre-processing.", e);
        }
    }

    void cleanUp()
    {
        if( originalInputSpecChanged ) {
            try {
                Files.deleteIfExists(adjustedInputSpec);
            }
            catch( final IOException e ) {
                throw new OpenApiGeneratorException(
                    "Could not remove temporary input spec " + adjustedInputSpec + " .",
                    e);
            }
        }
    }

}
