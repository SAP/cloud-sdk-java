package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
        API_CLASS_FOR_AI_SDK(
            "api-class-for-ai-sdk",
            "sodastore.json",
            "com.sap.cloud.sdk.services.builder.api",
            "com.sap.cloud.sdk.services.builder.model",
            ApiMaturity.RELEASED,
            true,
            true,
            6,
            Map.of("aiSdkConstructor", "true", "fixRedundantIsBooleanPrefix", "true", "useFloatArrays", "true")),
        API_CLASS_VENDOR_EXTENSION_YAML(
            "api-class-vendor-extension-yaml",
            "sodastore.yaml",
            "com.sap.cloud.sdk.services.apiclassvendorextension.api",
            "com.sap.cloud.sdk.services.apiclassvendorextension.model",
            ApiMaturity.RELEASED,
            false,
            true,
            4,
            Map.of()),
        API_CLASS_VENDOR_EXTENSION_JSON(
            "api-class-vendor-extension-json",
            "sodastore.json",
            "com.sap.cloud.sdk.services.apiclassvendorextension.api",
            "com.sap.cloud.sdk.services.apiclassvendorextension.model",
            ApiMaturity.RELEASED,
            false,
            true,
            6,
            Map.of()),
        INPUT_SPEC_WITH_UPPERCASE_FILE_EXTENSION(
            "input-spec-with-uppercase-file-extension",
            "sodastore.JSON",
            "com.sap.cloud.sdk.services.uppercasefileextension.api",
            "com.sap.cloud.sdk.services.uppercasefileextension.model",
            ApiMaturity.RELEASED,
            false,
            true,
            6,
            Map.of()),
        ONE_OF_INTERFACES_DISABLED(
            "oneof-interfaces-disabled",
            "sodastore.yaml",
            "test",
            "test",
            ApiMaturity.RELEASED,
            false,
            true,
            9,
            Map.of()),
        ONE_OF_INTERFACES_ENABLED(
            "oneof-interfaces-enabled",
            "sodastore.yaml",
            "test",
            "test",
            ApiMaturity.BETA,
            true,
            true,
            10,
            Map.of("useOneOfInterfaces", "true", "useOneOfCreators", "true")),
        INPUT_SPEC_WITH_BUILDER(
            "input-spec-with-builder",
            "sodastore.JSON",
            "com.sap.cloud.sdk.services.builder.api",
            "com.sap.cloud.sdk.services.builder.model",
            ApiMaturity.RELEASED,
            true,
            true,
            6,
            Map
                .of(
                    "pojoBuilderMethodName",
                    "builder",
                    "pojoBuildMethodName",
                    "build",
                    "pojoConstructorVisibility",
                    "private")),
        REMOVE_OPERATION_ID_PREFIX(
            "remove-operation-id-prefix",
            "sodastore.json",
            "com.sap.cloud.sdk.services.builder.api",
            "com.sap.cloud.sdk.services.builder.model",
            ApiMaturity.RELEASED,
            true,
            true,
            6,
            Map
                .of(
                    "removeOperationIdPrefix",
                    "true",
                    "removeOperationIdPrefixDelimiter",
                    "\\.",
                    "removeOperationIdPrefixCount",
                    "3")),
        GENERATE_APIS(
            "generate-apis",
            "sodastore.yaml",
            "test",
            "test",
            ApiMaturity.RELEASED,
            true,
            false,
            7,
            Map.of());

        final String testCaseName;
        final String inputSpecFileName;
        final String apiPackageName;
        final String modelPackageName;
        final ApiMaturity apiMaturity;
        final boolean anyOfOneOfGenerationEnabled;
        final boolean generateApis;
        final int expectedNumberOfGeneratedFiles;
        final Map<String, String> additionalProperties;
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
                .additionalProperty("useAbstractionForFiles", "true");
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
                .additionalProperty("useAbstractionForFiles", "true");
        testCase.additionalProperties.forEach(generationConfiguration::additionalProperty);

        GenerationConfiguration build = generationConfiguration.build();
        new DataModelGenerator().generateDataModel(build);
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
