/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;
import lombok.SneakyThrows;

class DataModelGeneratorUnitTest
{
    @TempDir
    Path outputDirectory = null;

    private final String INPUT_CLASS_PATH = DataModelGeneratorUnitTest.class.getSimpleName() + "/sodastore.yaml";

    private final String INPUT_FILE_PATH = "src/test/resources/" + INPUT_CLASS_PATH;

    @Test
    void testSuccessfulGenerationWithInputSpecAsFilePath()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("com.sap.cloud.sdk.datamodel.rest.sodastore.model")
                .apiPackage("com.sap.cloud.sdk.datamodel.rest.sodastore.api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        //assert that at least one file was generated, not asserting on the file contents
        assertThat(generationResult.get().getGeneratedFiles()).isNotEmpty();
    }

    @Test
    void testSuccessfulGenerationWithInputSpecAsClassPath()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_CLASS_PATH)
                .modelPackage("com.sap.cloud.sdk.datamodel.rest.sodastore.model")
                .apiPackage("com.sap.cloud.sdk.datamodel.rest.sodastore.api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        //assert that at least one file was generated, not asserting on the file contents
        assertThat(generationResult.get().getGeneratedFiles()).isNotEmpty();
    }

    @Test
    void testSuccessfulGenerationWithBetaApi()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.BETA)
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        //assert that at least one file was generated, not asserting on the file contents
        assertThat(generationResult.get().getGeneratedFiles()).isNotEmpty();
    }

    @Test
    void testExceptionOnMissingOrEmptyInputFile()
    {
        //assert on missing input file
        GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        Try<GenerationResult> result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));

        //assert on empty input file
        configuration =
            GenerationConfiguration
                .builder()
                .inputSpec("")
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void testExceptionOnMissingOrEmptyApiPackage()
    {
        //assert on missing API package
        GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        Try<GenerationResult> result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));

        //assert on provided but empty API package
        configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .apiPackage("")
                .modelPackage("model")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void testExceptionOnMissingOrEmptyModelPackage()
    {
        //assert on missing model package
        GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        Try<GenerationResult> result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));

        //assert on provided but empty model package
        configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .apiPackage("api")
                .modelPackage("")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void testExceptionOnMissingOrEmptyOutputDirectoryValue()
    {
        //assert on missing output directory value
        GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .apiPackage("api")
                .modelPackage("model")
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        Try<GenerationResult> result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));

        //assert on provided but empty output directory value
        configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .apiPackage("api")
                .modelPackage("model")
                .outputDirectory("")
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        result = new DataModelGenerator().generateDataModel(configuration);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void testExceptionIfTemplatesUnavailable()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .verbose(false)
                .apiMaturity(ApiMaturity.RELEASED)
                .build();

        final ClassPathResourceValidator noOpMock = Mockito.mock(ClassPathResourceValidator.class);

        Mockito
            .doThrow(new IllegalStateException())
            .when(noOpMock)
            .assertTemplatesAvailableOnClasspath(anyString(), anyString());

        final Try<GenerationResult> generationResult =
            new DataModelGenerator(noOpMock).generateDataModel(configuration);

        assertThat(generationResult.isFailure()).isTrue();
        assertThat(generationResult.getCause())
            .isInstanceOf(OpenApiGeneratorException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .satisfiesExactly(e -> assertThat(e).isInstanceOf(IllegalStateException.class));
    }

    @Test
    @SneakyThrows
    void testCleanOutputDirectory()
    {
        final File existingFile =
            Files
                .createTempFile(outputDirectory, "dummyFile", DataModelGeneratorUnitTest.class.getSimpleName())
                .toFile();
        assertThat(existingFile.exists()).isTrue();

        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .deleteOutputDirectory(true)
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        assertThat(generationResult.isSuccess()).isTrue();

        // assert that the file was deleted
        assertThat(existingFile.exists()).isFalse();
    }

    @Test
    @SneakyThrows
    void testNoExceptionIfOutputDirectoryDoesNotExist()
    {
        FileUtils.deleteDirectory(outputDirectory.toFile());

        assertThat(outputDirectory.toFile().exists()).isFalse();

        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .deleteOutputDirectory(true)
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        assertThat(generationResult.isSuccess()).isTrue();
        assertThat(generationResult.get().getGeneratedFiles()).isNotEmpty();

        // assert output directory was created implicitly
        assertThat(outputDirectory.toFile().exists()).isTrue();
    }

    @Test
    @SneakyThrows
    void testConfigOptionsArePassedToGenerator()
    {
        FileUtils.deleteDirectory(outputDirectory.toFile());

        assertThat(outputDirectory.toFile().exists()).isFalse();

        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec(INPUT_FILE_PATH)
                .modelPackage("model")
                .apiPackage("api")
                .outputDirectory(outputDirectory.toAbsolutePath().toString())
                .deleteOutputDirectory(true)
                .additionalProperties(
                    ImmutableMap.of("param1", "val1", "param2", "val2", "useAbstractionForFiles", "true"))
                .build();

        final Try<GenerationResult> generationResult = new DataModelGenerator().generateDataModel(configuration);

        assertThat(generationResult.isSuccess()).isTrue();
        assertThat(generationResult.get().getGeneratedFiles()).isNotEmpty();

        // assert output directory was created implicitly
        assertThat(outputDirectory.toFile().exists()).isTrue();
    }
}