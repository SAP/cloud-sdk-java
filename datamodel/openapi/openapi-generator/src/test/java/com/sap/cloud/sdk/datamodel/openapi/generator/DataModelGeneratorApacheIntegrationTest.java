package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;

// DataModelGeneratorApacheIntegrationTest.java
class DataModelGeneratorApacheIntegrationTest extends DataModelGeneratorIntegrationTest
{

    static final String LIBRARY = "apache-httpclient";

    @Override
    @ParameterizedTest
    @EnumSource( TestCase.class )
    void integrationTests( final TestCase testCase, @TempDir final Path path )
        throws Throwable
    {
        final Path inputDirectory = getInputDirectory(testCase);
        final Path tempOutputDirectory = path.resolve("outputDirectory");
        final Path comparisonDirectory = getComparisonDirectory(testCase);

        Files.createDirectories(tempOutputDirectory);

        assertThat(inputDirectory).exists().isReadable().isDirectory();
        assertThat(tempOutputDirectory).exists().isReadable().isDirectory();
        assertThat(comparisonDirectory).exists().isReadable().isDirectory();

        final var generationConfiguration =
            GenerationConfiguration
                .builder()
                // .debugModels(true) enable this for better mustache file debugging
                .apiPackage(testCase.apiPackageName)
                .generateApis(testCase.generateApis)
                .modelPackage(testCase.modelPackageName)
                .inputSpec(inputDirectory.resolve(testCase.inputSpecFileName).toAbsolutePath().toString())
                .apiMaturity(testCase.apiMaturity)
                .outputDirectory(tempOutputDirectory.toAbsolutePath().toString())
                .withSapCopyrightHeader(true)
                .oneOfAnyOfGenerationEnabled(testCase.anyOfOneOfGenerationEnabled)
                .additionalProperty("useAbstractionForFiles", "true")
                .additionalProperty("library", LIBRARY);

        testCase.additionalProperties.forEach(generationConfiguration::additionalProperty);

        final Try<GenerationResult> maybeGenerationResult =
            new DataModelGenerator().generateDataModel(generationConfiguration.build());

        assertThat(maybeGenerationResult.get().getGeneratedFiles()).hasSize(testCase.expectedNumberOfGeneratedFiles);

        assertThatDirectoriesHaveSameContent(tempOutputDirectory, comparisonDirectory);
    }

    // Add these annotations to regenerate all sources
    // @ParameterizedTest
    // @EnumSource( TestCase.class ) // use this to regenerate all...
    // @EnumSource( value = TestCase.class, names = { "API_CLASS_VENDOR_EXTENSION_YAML" } ) // ...and this one to only generate specific ones
    @Override
    void generateDataModelForComparison( final TestCase testCase )
    {
        final Path inputDirectory = getInputDirectory(testCase);
        final Path outputDirectory = getComparisonDirectory(testCase);

        assertThat(inputDirectory).exists().isReadable().isDirectory();
        assertThat(outputDirectory).exists().isReadable().isDirectory();

        final var generationConfiguration =
            GenerationConfiguration
                .builder()
                .apiPackage(testCase.apiPackageName)
                .generateApis(testCase.generateApis)
                .modelPackage(testCase.modelPackageName)
                .inputSpec(inputDirectory.resolve(testCase.inputSpecFileName).toAbsolutePath().toString())
                .apiMaturity(testCase.apiMaturity)
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .deleteOutputDirectory(true)
                .withSapCopyrightHeader(true)
                .oneOfAnyOfGenerationEnabled(testCase.anyOfOneOfGenerationEnabled)
                .additionalProperty("useAbstractionForFiles", "true")
                .additionalProperty("library", LIBRARY);
        testCase.additionalProperties.forEach(generationConfiguration::additionalProperty);

        GenerationConfiguration build = generationConfiguration.build();
        new DataModelGenerator().generateDataModel(build).onFailure(Throwable::printStackTrace);
    }
}
