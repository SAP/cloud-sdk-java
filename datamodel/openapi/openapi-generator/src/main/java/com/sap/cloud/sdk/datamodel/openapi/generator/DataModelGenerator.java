package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

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

    private void cleanOutputDirectoryIfRequested( final GenerationConfiguration configuration )
        throws IOException
    {
        final File outputDirectory = FileUtils.getFile(configuration.getOutputDirectory());
        if( configuration.deleteOutputDirectory() && outputDirectory.exists() && outputDirectory.isDirectory() ) {
            log.info("Cleaning generated folders in output directory \"{}\".", outputDirectory.getAbsolutePath());

            for( final var pckg : List.of(configuration.getModelPackage(), configuration.getApiPackage()) ) {
                final var file = outputDirectory.toPath().resolve(pckg.replace(".", File.separator)).toFile();
                if( file.exists() && file.isDirectory() ) {
                    log.info("Deleting files from directory \"{}\".", file);
                    IOConsumer.forAll(FileUtils::forceDelete, file);
                }
            }
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
        if( configuration.getOutputDirectory() == null || configuration.getOutputDirectory().isEmpty() ) {
            throw new IllegalArgumentException("Output directory is null or empty.");
        }

        final Predicate<String> goodPackage = p -> !p.isEmpty() && !p.startsWith(".") && !p.contains(File.separator);
        if( configuration.getApiPackage() == null || !goodPackage.test(configuration.getApiPackage()) ) {
            throw new IllegalArgumentException("API package is null or empty or invalid.");
        }
        if( configuration.getModelPackage() == null || !goodPackage.test(configuration.getModelPackage()) ) {
            throw new IllegalArgumentException("Model package is null or empty or invalid.");
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
