/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

class DatamodelMetadataGenerationTest
{
    @TempDir
    Path temporaryFolder = null;

    @RequiredArgsConstructor
    private enum TestCase
    {
        SIMPLE_SODASTORE("simple-sodastore", "sodastore.yaml", false),
        FAILING_GENERATION("failing-generation", "failing-generation.yaml", true),
        FAILING_GENERATION_WITH_VALIDATION_FIELDS(
            "failing-generation-validation-fields",
            "failing-sodastore.yaml",
            true);

        final String testCaseName;
        final String inputSpecFileName;
        final boolean shallFail;
    }

    @ParameterizedTest
    @Execution(value = ExecutionMode.SAME_THREAD, reason = "Avoid overloading the CI/CD pipeline")
    @EnumSource( TestCase.class )
    void testDatamodelMetadataGeneration( final TestCase testCase )
    {
        final Try<GenerationResult> generationResult = readInputSpecAndInvokeGenerator(testCase);

        assertOnGenerationResult(testCase, generationResult);

        assertOnGeneratedDatamodelMetadata(temporaryFolder, getComparisonDirectory(testCase));
    }

    private void assertOnGenerationResult( final TestCase testCase, final Try<GenerationResult> generationResult )
    {
        if( testCase.shallFail != generationResult.isFailure() ) {
            fail(
                "Metadata generation result unexpected: Expected "
                    + (testCase.shallFail ? "failure" : "success")
                    + ", but was "
                    + (generationResult.isSuccess() ? "success" : "failure")
                    + ".");
        }
    }

    //Use this test method to regenerate the test comparison output based on the latest development
    //@ParameterizedTest
    //@EnumSource( TestCase.class ) // use this to regenerate all...
    // @EnumSource( value = TestCase.class, names = { "SIMPLE_SODASTORE" } ) // ...and this one to only generate specific ones
    public void regenerateTestComparisonOutput( final TestCase testCase )
        throws IOException
    {
        final Try<GenerationResult> generationResult = readInputSpecAndInvokeGenerator(testCase);

        assertOnGenerationResult(testCase, generationResult);

        FileUtils.cleanDirectory(getComparisonDirectory(testCase).toFile());

        FileUtils
            .copyDirectory(
                temporaryFolder.resolve("metadata").toFile(),
                getComparisonDirectory(testCase).resolve("metadata").toFile());
    }

    private Try<GenerationResult> readInputSpecAndInvokeGenerator( final TestCase testCase )
    {
        final Path inputSpecPath = getInputDirectory(testCase).resolve(testCase.inputSpecFileName);

        assertThat(inputSpecPath).exists().isRegularFile().isReadable();

        return executeDatamodelGenerator(temporaryFolder, inputSpecPath);
    }

    private void assertOnGeneratedDatamodelMetadata( final Path outputDirectory, final Path testcomparisonDirectory )
    {
        final Path metadataInOutputDirectory = outputDirectory.resolve("metadata");
        assertThat(metadataInOutputDirectory).isDirectory().isReadable();

        final Path metadataInTestcomparisonDirectory = testcomparisonDirectory.resolve("metadata");
        assertThat(metadataInTestcomparisonDirectory).isDirectory().isReadable();

        final Collection<File> jsonFiles =
            FileUtils.listFiles(metadataInOutputDirectory.toFile(), new String[] { "json" }, false);

        for( final File jsonFile : jsonFiles ) {
            assertOnJsonFile(jsonFile, metadataInTestcomparisonDirectory);
        }
    }

    @SneakyThrows
    private void assertOnJsonFile( final File expectedJsonFile, final Path metadataInTestcomparisonDirectory )
    {
        final String expectedMetadataJson = getMetadataJson(expectedJsonFile);
        final String actualMetadataJson =
            getMetadataJson(metadataInTestcomparisonDirectory, expectedJsonFile.getName());

        JSONAssert
            .assertEquals(
                expectedMetadataJson,
                actualMetadataJson,
                new CustomComparator(
                    JSONCompareMode.STRICT,
                    Customization.customization("pregeneratedLibrary.generatedAt", ( o1, o2 ) -> true)));
    }

    private String getMetadataJson( final File jsonFile )
        throws IOException
    {
        assertThat(jsonFile).hasExtension("json");

        return FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8);
    }

    private String getMetadataJson( final Path directory, final String fileName )
        throws IOException
    {
        final Path jsonFilePath = directory.resolve(fileName);

        assertThat(jsonFilePath).isReadable().isRegularFile();
        assertThat(jsonFilePath.toFile()).hasExtension("json");

        return FileUtils.readFileToString(jsonFilePath.toFile(), StandardCharsets.UTF_8);
    }

    private Try<GenerationResult> executeDatamodelGenerator( final Path outputDirectory, final Path inputSpec )
    {
        final GenerationConfiguration generationConfiguration =
            GenerationConfiguration
                .builder()
                .deleteOutputDirectory(true)
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .apiPackage("openapi.test.api")
                .modelPackage("openapi.test.model")
                .inputSpec(inputSpec.toAbsolutePath().toString())
                .build();

        return new DataModelGenerator().generateDataModel(generationConfiguration);
    }

    private static Path getComparisonDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory(testCase);
        final Path comparisonDirectory = testCaseDirectory.resolve("output");

        assertThat(comparisonDirectory).exists().isDirectory().isReadable();

        return comparisonDirectory;
    }

    private static Path getTestCaseDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory =
            Paths
                .get(
                    "src/test/resources/" + DatamodelMetadataGenerationTest.class.getSimpleName(),
                    testCase.testCaseName);

        assertThat(testCaseDirectory).exists().isDirectory().isReadable();

        return testCaseDirectory;
    }

    private static Path getInputDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory(testCase);
        final Path inputDirectory = testCaseDirectory.resolve("input");

        assertThat(inputDirectory).exists().isDirectory().isReadable();

        return inputDirectory;
    }
}