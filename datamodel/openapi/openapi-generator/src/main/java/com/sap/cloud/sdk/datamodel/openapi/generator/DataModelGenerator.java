/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.apache.commons.io.filefilter.TrueFileFilter.TRUE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.function.IOConsumer;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Delegates code generation to the public open-source OpenAPI Generator at https://openapi-generator.tech/.
 */
@Slf4j
public class DataModelGenerator
{
    private final ClassPathResourceValidator classPathResourceValidator;
    private PreprocessingStepOrchestrator preprocessingStepOrchestrator = null;

    DataModelGenerator( final ClassPathResourceValidator classPathResourceValidator )
    {
        this.classPathResourceValidator = classPathResourceValidator;
    }

    /**
     * Default constructor initializing the generator with default class members
     */
    public DataModelGenerator()
    {
        this.classPathResourceValidator = new ClassPathResourceValidator();
    }

    /**
     * Generates the data model based on the provided generation configuration.
     *
     * @param generationConfiguration
     *            The configuration for the code generation
     * @return A {@link Try} wrapping the {@link GenerationResult} in the success case. In the failure case it wraps a
     *         {@link OpenApiGeneratorException} or an {@link IllegalArgumentException}.
     */
    @Nonnull
    public Try<GenerationResult> generateDataModel( @Nonnull final GenerationConfiguration generationConfiguration )
    {
        final Try<GenerationResult> codeGenerationTry = invokeCodeGeneration(generationConfiguration);

        final Try<Void> metadataGenerationTry =
            Try
                .run(
                    () -> new DatamodelMetadataGeneratorAdapter()
                        .generateDatamodelMetadataIfApplicable(generationConfiguration, codeGenerationTry.getOrNull()));

        if( codeGenerationTry.isFailure() || metadataGenerationTry.isFailure() ) {
            final OpenApiGeneratorException finalCause =
                new OpenApiGeneratorException(
                    "Failure in the OpenAPI generator. See the suppressed exceptions for details.");

            codeGenerationTry.onFailure(finalCause::addSuppressed);
            metadataGenerationTry.onFailure(finalCause::addSuppressed);

            return Try.failure(finalCause);
        }

        return codeGenerationTry;
    }

    private Try<GenerationResult> invokeCodeGeneration( @Nonnull final GenerationConfiguration generationConfiguration )
    {
        return Try.of(() -> {
            assertRequiredFieldsAreFilled(generationConfiguration);

            final OpenApiSpec inputSpec = performPreProcessingSteps(generationConfiguration);

            final ClientOptInput clientOptInput =
                GenerationConfigurationConverter
                    .convertGenerationConfiguration(generationConfiguration, inputSpec.getFilePath());

            assertTemplatesAvailableOnClasspath(
                GenerationConfigurationConverter.TEMPLATE_DIRECTORY,
                GenerationConfigurationConverter.LIBRARY_NAME);

            cleanOutputDirectoryIfRequested(generationConfiguration);

            final List<File> generatedFiles = new DefaultGenerator().opts(clientOptInput).generate();

            performPostGenerationSteps(generatedFiles);

            return new GenerationResult(generatedFiles, getServiceName(inputSpec.getJsonNode()));
        });
    }

    private void cleanOutputDirectoryIfRequested( final GenerationConfiguration generationConfiguration )
        throws IOException
    {
        final File outputDirectory = FileUtils.getFile(generationConfiguration.getOutputDirectory());
        if( generationConfiguration.deleteOutputDirectory()
            && outputDirectory.exists()
            && outputDirectory.isDirectory() ) {
            log.info("Deleting output directory \"{}\".", outputDirectory.getAbsolutePath());

            // create set of directories that can be deleted
            final var allowedDirs = new HashSet<File>();
            allowedDirs.add(outputDirectory);
            final Consumer<String> addDirs = pckg -> {
                File d = outputDirectory;
                for( final String ns : pckg.split("\\.") ) {
                    d = new File(d, ns);
                    if( !d.exists() || !d.isDirectory() ) {
                        return;
                    }
                    allowedDirs.add(d);
                }
            };
            addDirs.accept(generationConfiguration.getModelPackage());
            addDirs.accept(generationConfiguration.getApiPackage());

            // recursively list files that are going to be deleted, safety check:
            // throw if unexpected file (non-java or non-ignore file) or unexpected folder (non-package)
            final var deleteFiles = FileUtils.listFilesAndDirs(outputDirectory, TRUE, TRUE);
            deleteFiles.removeIf(f -> f.isDirectory() && allowedDirs.contains(f));
            deleteFiles.removeIf(f -> f.isFile() && (f.getName().startsWith(".") || f.getName().endsWith(".java")));
            if( !deleteFiles.isEmpty() ) {
                throw new IOException("Unexpected files found. Will not delete: " + deleteFiles);
            }

            // get non-ignore files from outputDirectory folder (non-recursive)
            final var nonIgnoreFiles = outputDirectory.listFiles(( dir, file ) -> !file.startsWith("."));
            // delete the files (recursively)
            IOConsumer.forAll(FileUtils::forceDelete, nonIgnoreFiles);
        }
    }

    @Nonnull
    private OpenApiSpec performPreProcessingSteps( @Nonnull final GenerationConfiguration generationConfiguration )
    {
        this.preprocessingStepOrchestrator =
            new PreprocessingStepOrchestrator(Paths.get(generationConfiguration.getInputSpec()));

        return preprocessingStepOrchestrator
            .enableAnyOfOneOfGeneration(generationConfiguration.isOneOfAnyOfGenerationEnabled())
            .performPreprocessingSteps();
    }

    private void performPostGenerationSteps( @Nonnull final List<File> generatedFiles )
        throws IOException
    {
        removeSwaggerImports(generatedFiles);
        preprocessingStepOrchestrator.cleanUp();
    }

    private void assertTemplatesAvailableOnClasspath(
        @Nonnull final String templateDirectory,
        @Nonnull final String libraryName )
    {
        classPathResourceValidator.assertTemplatesAvailableOnClasspath(templateDirectory, libraryName);
    }

    private void assertRequiredFieldsAreFilled( final GenerationConfiguration configuration )
    {
        if( configuration.getInputSpec() == null || configuration.getInputSpec().isEmpty() ) {
            throw new IllegalArgumentException("Input file path is null or empty.");
        }

        if( configuration.getApiPackage() == null || configuration.getApiPackage().isEmpty() ) {
            throw new IllegalArgumentException("API package is null or empty.");
        }

        if( configuration.getModelPackage() == null || configuration.getModelPackage().isEmpty() ) {
            throw new IllegalArgumentException("Model package is null or empty.");
        }

        if( configuration.getOutputDirectory() == null || configuration.getOutputDirectory().isEmpty() ) {
            throw new IllegalArgumentException("Output directory is null or empty.");
        }
    }

    // This method introduces a workaround for a known bug [1] of the OpenAPI generator, which causes unnecessary imports
    // of the Swagger annotations (io.swagger.annotations.ApiModel and io.swagger.annotations.ApiModelProperty).
    // These imports force customers to include Swagger as a dependency in their projects and have caused lots of
    // confusion in the past already.
    // We do not expect the mentioned bug to be fixed any time soon and, thus, decided to slightly widen the scope of
    // this class, because the workaround greatly benefits customers while the implementation effort is rather low.
    // [1]: https://github.com/OpenAPITools/openapi-generator/issues/1085
    private void removeSwaggerImports( final Iterable<File> generatedFiles )
        throws IOException
    {
        final String swaggerApiModelImport = "import io.swagger.annotations.ApiModel;";
        final String swaggerApiModelPropertyImport = "import io.swagger.annotations.ApiModelProperty;";
        final String generatedAnnotation =
            "@javax.annotation.Generated(value = \"org.openapitools.codegen.languages.JavaClientCodegen\"";

        for( final File sourceFile : generatedFiles ) {
            final File tempFile = new File(sourceFile.getParent() + "/temp." + sourceFile.getName());
            try(
                BufferedReader reader = Files.newBufferedReader(sourceFile.toPath());
                BufferedWriter writer = Files.newBufferedWriter(tempFile.toPath()) ) {

                String line;
                while( (line = reader.readLine()) != null ) {
                    if( line.equals(swaggerApiModelImport)
                        || line.equals(swaggerApiModelPropertyImport)
                        || line.startsWith(generatedAnnotation) ) {
                        continue;
                    }

                    writer.write(line);
                    writer.newLine();
                }
            }

            if( !sourceFile.delete() ) {
                throw new IOException("Unable to delete original source file.");
            }

            if( !tempFile.renameTo(sourceFile) ) {
                throw new IOException("Unable to overwrite original source file with cleaned one.");
            }
        }
    }

    private String getServiceName( final JsonNode inputSpecNode )
    {
        return Option
            .of(inputSpecNode.path("info").path("title").asText())
            .getOrElseThrow(() -> new IllegalArgumentException("No API title found in the input specification."));
    }
}
