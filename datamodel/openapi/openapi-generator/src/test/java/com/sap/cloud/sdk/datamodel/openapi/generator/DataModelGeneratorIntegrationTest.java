/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

class DataModelGeneratorIntegrationTest
{
    @RequiredArgsConstructor
    private enum TestCase
    {
        API_CLASS_VENDOR_EXTENSION_YAML(
            "api-class-vendor-extension-yaml",
            "sodastore.yaml",
            "com.sap.cloud.sdk.services.apiclassvendorextension.api",
            "com.sap.cloud.sdk.services.apiclassvendorextension.model",
            ApiMaturity.RELEASED,
            4),
        API_CLASS_VENDOR_EXTENSION_JSON(
            "api-class-vendor-extension-json",
            "sodastore.json",
            "com.sap.cloud.sdk.services.apiclassvendorextension.api",
            "com.sap.cloud.sdk.services.apiclassvendorextension.model",
            ApiMaturity.RELEASED,
            6),
        INPUT_SPEC_WITH_UPPERCASE_FILE_EXTENSION(
            "input-spec-with-uppercase-file-extension",
            "sodastore.JSON",
            "com.sap.cloud.sdk.services.uppercasefileextension.api",
            "com.sap.cloud.sdk.services.uppercasefileextension.model",
            ApiMaturity.RELEASED,
            6);

        final String testCaseName;
        final String inputSpecFileName;
        final String apiPackageName;
        final String modelPackageName;
        final ApiMaturity apiMaturity;
        final int expectedNumberOfGeneratedFiles;
    }

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

        final GenerationConfiguration generationConfiguration =
            GenerationConfiguration
                .builder()
                .apiPackage(testCase.apiPackageName)
                .modelPackage(testCase.modelPackageName)
                .inputSpec(inputDirectory.resolve(testCase.inputSpecFileName).toAbsolutePath().toString())
                .apiMaturity(testCase.apiMaturity)
                .outputDirectory(tempOutputDirectory.toAbsolutePath().toString())
                .withSapCopyrightHeader(true)
                .additionalProperty("useAbstractionForFiles", "true")
                .build();

        final Try<GenerationResult> maybeGenerationResult =
            new DataModelGenerator().generateDataModel(generationConfiguration);

        assertThat(maybeGenerationResult.get().getGeneratedFiles()).hasSize(testCase.expectedNumberOfGeneratedFiles);

        assertThatDirectoriesHaveSameContent(tempOutputDirectory, comparisonDirectory);
    }

    // Add these annotations to regenerate all sources
    @ParameterizedTest
    @EnumSource( TestCase.class ) // use this to regenerate all...
    // @EnumSource( value = TestCase.class, names = { "API_CLASS_VENDOR_EXTENSION_YAML" } ) // ...and this one to only generate specific ones
    void generateDataModelForComparison( final TestCase testCase )
    {
        final Path inputDirectory = getInputDirectory(testCase);
        final Path outputDirectory = getComparisonDirectory(testCase);

        assertThat(inputDirectory).exists().isReadable().isDirectory();
        assertThat(inputDirectory).exists().isReadable().isDirectory();

        final GenerationConfiguration generationConfiguration =
            GenerationConfiguration
                .builder()
                .apiPackage(testCase.apiPackageName)
                .modelPackage(testCase.modelPackageName)
                .inputSpec(inputDirectory.resolve(testCase.inputSpecFileName).toAbsolutePath().toString())
                .apiMaturity(testCase.apiMaturity)
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .deleteOutputDirectory(true)
                .withSapCopyrightHeader(true)
                .additionalProperty("useAbstractionForFiles", "true")
                .build();

        new DataModelGenerator().generateDataModel(generationConfiguration);
    }

    private static Path getInputDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory(testCase);
        final Path inputDirectory = testCaseDirectory.resolve("input");

        assertThat(inputDirectory).exists().isDirectory().isReadable();

        return inputDirectory;
    }

    private static Path getTestCaseDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory =
            Paths
                .get(
                    "src/test/resources/" + DataModelGeneratorIntegrationTest.class.getSimpleName(),
                    testCase.testCaseName);

        assertThat(testCaseDirectory).exists().isDirectory().isReadable();

        return testCaseDirectory;
    }

    private static Path getComparisonDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory(testCase);
        final Path comparisonDirectory = testCaseDirectory.resolve("output");

        assertThat(comparisonDirectory).exists().isDirectory().isReadable();

        return comparisonDirectory;
    }

    @SuppressWarnings( "resource" )
    @SneakyThrows
    private static void assertThatDirectoriesHaveSameContent( final Path a, final Path b )
    {
        final Predicate<Path> isFile = p -> p.toFile().isFile();
        Files.walk(a).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(b.resolve(a.relativize(p))));
        Files.walk(b).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(a.resolve(b.relativize(p))));
    }
}
