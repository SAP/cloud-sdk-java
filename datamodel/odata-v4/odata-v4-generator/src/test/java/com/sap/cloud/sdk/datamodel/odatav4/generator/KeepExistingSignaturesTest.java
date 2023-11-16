package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lombok.SneakyThrows;

class KeepExistingSignaturesTest
{
    @TempDir
    Path path;

    private Path pathOutput;
    private Path pathOld;
    private File FIRST_NAME_LAST_NAME;
    private File LAST_NAME_FIRST_NAME;
    private File FIRST_NAME_LAST_NAME_FOO;
    private File serviceNameMappingFile;

    @SneakyThrows
    @BeforeEach
    void setupPaths()
    {
        pathOutput = path.resolve("output");
        pathOld = path.resolve("old");
        Files.createDirectories(pathOutput);
        Files.createDirectories(pathOld);

        final String testName = KeepExistingSignaturesTest.class.getSimpleName();
        final Path inputDirectory = Paths.get("src/test/resources/" + testName);
        FIRST_NAME_LAST_NAME = inputDirectory.resolve("FirstNameLastName").toFile();
        LAST_NAME_FIRST_NAME = inputDirectory.resolve("LastNameFirstName").toFile();
        FIRST_NAME_LAST_NAME_FOO = inputDirectory.resolve("FirstNameLastNameFoo").toFile();
        serviceNameMappingFile = inputDirectory.resolve("serviceNameMappings.properties").toFile();
    }

    @SneakyThrows
    @Test
    void testDefaultKeepExistingSignatures()
    {
        new DataModelGenerator()
            .withInputDirectory(FIRST_NAME_LAST_NAME)
            .withOutputDirectory(pathOutput.toFile())
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath("/")
            .keepExistingSignatures(true) // default: true
            .execute();

        FileUtils.copyDirectory(pathOutput.toFile(), pathOld.toFile());

        new DataModelGenerator()
            .withInputDirectory(LAST_NAME_FIRST_NAME)
            .withOutputDirectory(pathOutput.toFile())
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath("/")
            .overwriteFiles()
            .keepExistingSignatures(true) // default: true
            .execute();

        assertThatDirectoriesHaveSameContent(pathOutput, pathOld);
    }

    @SuppressWarnings( "resource" )
    @SneakyThrows
    private static void assertThatDirectoriesHaveSameContent( final Path a, final Path b )
    {
        final Predicate<Path> isFile = p -> p.toFile().isFile();
        Files.walk(a).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(b.resolve(a.relativize(p))));
        Files.walk(b).filter(isFile).forEach(p -> assertThat(p).hasSameTextualContentAs(a.resolve(b.relativize(p))));
    }

    @SneakyThrows
    @Test
    void testNotKeepingExistingSignatures()
    {
        new DataModelGenerator()
            .withInputDirectory(FIRST_NAME_LAST_NAME)
            .withOutputDirectory(pathOutput.toFile())
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath("/")
            .keepExistingSignatures(true) // default: true
            .execute();

        FileUtils.copyDirectory(pathOutput.toFile(), pathOld.toFile());

        new DataModelGenerator()
            .withInputDirectory(LAST_NAME_FIRST_NAME)
            .withOutputDirectory(pathOutput.toFile())
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath("/")
            .overwriteFiles()
            .keepExistingSignatures(false) // not default, provoke generation of difference code for different order
            .execute();

        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> assertThatDirectoriesHaveSameContent(pathOutput, pathOld));
    }

    @Test
    void testFailingToKeepSignatures()
    {
        new DataModelGenerator()
            .withInputDirectory(FIRST_NAME_LAST_NAME)
            .withOutputDirectory(pathOutput.toFile())
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath("/")
            .keepExistingSignatures(true) // default: true
            .execute();

        assertThatExceptionOfType(ODataGeneratorWriteException.class)
            .isThrownBy(
                () -> new DataModelGenerator()
                    .withInputDirectory(FIRST_NAME_LAST_NAME_FOO)
                    .withOutputDirectory(pathOutput.toFile())
                    .withServiceNameMapping(serviceNameMappingFile)
                    .withDefaultBasePath("/")
                    .overwriteFiles()
                    .keepExistingSignatures(true) // default: true, provoke exception due to breaking change in entity key
                    .execute());
    }
}
