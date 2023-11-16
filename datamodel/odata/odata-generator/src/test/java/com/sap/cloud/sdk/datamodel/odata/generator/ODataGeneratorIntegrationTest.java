/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.common.collect.Sets;
import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

class ODataGeneratorIntegrationTest
{
    // extend this enum to add a new testcase
    @RequiredArgsConstructor
    private enum TestCase
    {
        TEST_SERVICE("testService", NameSource.NAME, false, true, null, null, "/", true),
        TEST_SERVICE_POJOS("testServicePojos", NameSource.NAME, true, true, null, null, "/", true),
        TEST_SERVICE_NO_API_LINKS("testServiceNoApiLinks", NameSource.NAME, false, false, null, null, "/", true),
        TEST_SERVICE_ENTITY_SETS(
            "testServiceEntitySets",
            NameSource.NAME,
            false,
            true,
            Sets.newHashSet("A_TestEntity", "A_OtherTestEntity"),
            Sets.newHashSet(),
            null,
            true),
        TEST_SERVICE_FUNCTION_IMPORTS(
            "testServiceFunctionImports",
            NameSource.NAME,
            false,
            true,
            Sets.newHashSet(),
            Sets.newHashSet("TestFunctionImportNoReturnType"),
            null,
            false),
        TEST_SERVICE_DEPRECATION(
            "testServiceDeprecation",
            NameSource.NAME,
            false,
            true,
            Sets.newHashSet(),
            Sets.newHashSet(),
            null,
            false),
        SDK_GROCERY_STORE(
            "groceryStore",
            NameSource.NAME,
            false,
            false,
            Sets.newHashSet(),
            Sets.newHashSet(),
            "/grocery",
            false),
        KEY_NAMED_FIELD("keyNamedField", NameSource.LABEL, false, true, null, null, null, false),
        FUNCTION_IMPORT_CLASHES("functionImportClash", NameSource.NAME, false, true, null, null, null, false),
        MULTIPLE_ENTITY_SETS("multipleEntitySets", NameSource.NAME, false, false, null, null, null, true),
        PROPERTY_NAME_CLASH("propertyNameClash", NameSource.NAME, false, false, null, null, null, true);

        final String testCaseName;
        final NameSource nameSource;
        final boolean generatePojosOnly;
        final boolean generateLinksToApiBusinessHub;
        final Set<String> includeEntitySets;
        final Set<String> includeFunctionImports;
        final String basePath;
        final boolean serviceMethodsPerEntitySet;
    }

    @ParameterizedTest
    @Execution(value = ExecutionMode.SAME_THREAD, reason = "Avoid overloading the CI/CD pipeline")
    @EnumSource( TestCase.class )
    void integrationTests( final TestCase testCase, @TempDir final Path path )
        throws IOException
    {
        final Path inputDirectory = getInputDirectory(testCase);
        final Path serviceNameMapping = inputDirectory.resolve("serviceNameMappings.properties");
        final Path tempOutputDirectory = path.resolve("outputDirectory");
        final Path comparisonDirectory = getComparisonDirectory(testCase);

        Files.createDirectories(tempOutputDirectory);

        assertThat(inputDirectory).exists().isReadable().isDirectory();
        assertThat(tempOutputDirectory).exists().isReadable().isDirectory();
        assertThat(comparisonDirectory).exists().isReadable().isDirectory();

        new DataModelGenerator()
            .withInputDirectory(inputDirectory.toFile())
            .withOutputDirectory(tempOutputDirectory.toFile())
            .withServiceNameMapping(serviceNameMapping.toFile())
            .withNameSource(testCase.nameSource)
            .withPackageName("testcomparison")
            .pojosOnly(testCase.generatePojosOnly)
            .linkToApiBusinessHub(testCase.generateLinksToApiBusinessHub)
            .withIncludedEntitySets(testCase.includeEntitySets)
            .withIncludedFunctionImports(testCase.includeFunctionImports)
            .sapCopyrightHeader()
            .versionReference(false)
            .withDefaultBasePath(testCase.basePath)
            .serviceMethodsPerEntitySet(testCase.serviceMethodsPerEntitySet)
            .execute();

        assertThatDirectoriesHaveSameContent(tempOutputDirectory, comparisonDirectory);
    }

    @Test
    void testFailOnWarning()
    {
        final Path inputDirectory = Paths.get("src/test/resources/oDataGeneratorIntegrationTest/failOnWarning/input");

        assertThat(inputDirectory).exists().isReadable().isDirectory();

        final DataModelGenerator generator =
            new DataModelGenerator()
                .withInputDirectory(inputDirectory.toFile())
                .withPackageName("testcomparison")
                .failOnWarning();

        generator.execute();

        assertThat(generator.failureDueToWarningsNecessary()).isTrue();
    }

    @Test
    void testSkipV4Service()
    {
        final Path inputDirectory = Paths.get("src/test/resources/oDataGeneratorIntegrationTest/invalidService");
        assertThat(inputDirectory).exists().isReadable().isDirectory();
        final File input = inputDirectory.toFile();

        DataModelGenerator generator =
            new DataModelGenerator().withInputDirectory(input).withPackageName("testcomparison").failOnWarning();

        generator.execute();
        assertThat(generator.failureDueToWarningsNecessary()).isTrue();

        generator = new DataModelGenerator().withInputDirectory(input).withPackageName("testcomparison");

        generator.execute();
        assertThat(generator.failureDueToWarningsNecessary()).isFalse();
    }

    @Test
    void testDeprecationNoticeAddition( @TempDir final Path path )
        throws IOException
    {
        final Path inputDirectory =
            Paths.get("src/test/resources/oDataGeneratorIntegrationTest/minimalTestWithExplicitDeprecation/input");
        assertThat(inputDirectory).exists().isReadable().isDirectory();
        final Path serviceNameMapping = inputDirectory.resolve("serviceNameMappings.properties");
        final Path tempOutputDirectory = path.resolve("outputDirectory");
        final Path comparisonDirectory =
            Paths.get("src/test/resources/oDataGeneratorIntegrationTest/minimalTestWithExplicitDeprecation/output");
        new DataModelGenerator()
            .withInputDirectory(inputDirectory.toFile())
            .withOutputDirectory(tempOutputDirectory.toFile())
            .withServiceNameMapping(serviceNameMapping.toFile())
            .withNameSource(NameSource.NAME)
            .withPackageName("testcomparison")
            .pojosOnly(false)
            .linkToApiBusinessHub(true)
            .withIncludedEntitySets(null)
            .withIncludedFunctionImports(null)
            .sapCopyrightHeader()
            .versionReference(false)
            .withDefaultBasePath(null)
            .serviceMethodsPerEntitySet(false)
            .withDeprecationNotice("This is a custom deprecation message.")
            .execute();

        assertThatDirectoriesHaveSameContent(tempOutputDirectory, comparisonDirectory);
    }

    // Add these annotations to regenerate all sources
    // @ParameterizedTest
    // @EnumSource( TestCase.class ) // use this to regenerate all...
    // @EnumSource( value = TestCase.class, names = {"PROPERTY_NAME_CLASH"} ) // ...and this one to only generate specific ones
    void generateComparisonJavaFiles( final TestCase testCaseToGenerateFilesFor )
    {
        final Path inputDirectory = getInputDirectory(testCaseToGenerateFilesFor);
        final Path serviceNameMapping = inputDirectory.resolve("serviceNameMappings.properties");
        final Path outputDirectory = getComparisonDirectory(testCaseToGenerateFilesFor);

        new DataModelGenerator()
            .withInputDirectory(inputDirectory.toFile())
            .withOutputDirectory(outputDirectory.toFile())
            .withServiceNameMapping(serviceNameMapping.toFile())
            .withNameSource(testCaseToGenerateFilesFor.nameSource)
            .withPackageName("testcomparison")
            .deleteOutputDirectory()
            .pojosOnly(testCaseToGenerateFilesFor.generatePojosOnly)
            .linkToApiBusinessHub(testCaseToGenerateFilesFor.generateLinksToApiBusinessHub)
            .withIncludedEntitySets(testCaseToGenerateFilesFor.includeEntitySets)
            .withIncludedFunctionImports(testCaseToGenerateFilesFor.includeFunctionImports)
            .sapCopyrightHeader()
            .versionReference(false)
            .withDefaultBasePath(testCaseToGenerateFilesFor.basePath)
            .serviceMethodsPerEntitySet(testCaseToGenerateFilesFor.serviceMethodsPerEntitySet)
            .execute();
    }

    private static Path getInputDirectory( final TestCase testCase )
    {
        final Path testCaseDirectory = getTestCaseDirectory(testCase);
        final Path inputDirectory = testCaseDirectory.resolve("input");

        assertThat(inputDirectory).exists().isDirectory().isReadable();

        return inputDirectory;
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
            Paths.get("src/test/resources/oDataGeneratorIntegrationTest", testCase.testCaseName);

        assertThat(testCaseDirectory).exists().isDirectory().isReadable();

        return testCaseDirectory;
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
