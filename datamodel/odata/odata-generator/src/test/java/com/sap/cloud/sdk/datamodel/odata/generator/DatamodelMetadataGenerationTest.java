package com.sap.cloud.sdk.datamodel.odata.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;

import lombok.SneakyThrows;

public class DatamodelMetadataGenerationTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final Path TEST_RESOURCES_DIRECTORY = Paths.get("src/test/resources/" + getClass().getSimpleName());

    @Test
    public void testDatamodelMetadataGeneration()
    {
        final Path temporaryPath = temporaryFolder.getRoot().toPath();

        executeDatamodelGenerator(temporaryPath);

        assertOnGeneratedDatamodelMetadata(temporaryPath, getTestComparisonDirectory());
    }

    //Use this test method to regenerate the test comparison output based on the latest development
    // @Test
    public void regenerateTestComparisonOutput()
        throws IOException
    {
        final Path temporaryPath = temporaryFolder.getRoot().toPath();

        executeDatamodelGenerator(temporaryPath);

        FileUtils.cleanDirectory(getTestComparisonDirectory().toFile());

        FileUtils
            .copyDirectory(
                temporaryPath.resolve("metadata").toFile(),
                getTestComparisonDirectory().resolve("metadata").toFile());
    }

    private void assertOnGeneratedDatamodelMetadata( final Path outputDirectory, final Path comparisonDirectory )
    {
        final Path metadataInOutputDirectory = outputDirectory.resolve("metadata");
        assertThat(metadataInOutputDirectory).isDirectory().isReadable();

        final Path metadataInComparisonDirectory = comparisonDirectory.resolve("metadata");
        assertThat(metadataInComparisonDirectory).isDirectory().isReadable();

        assertOnSameJsonFiles(metadataInOutputDirectory, metadataInComparisonDirectory);
        assertOnSameJsonFiles(metadataInComparisonDirectory, metadataInOutputDirectory);
    }

    @SneakyThrows
    private void assertOnJsonFile( final File actualJsonFile, final Path metadataInTestcomparisonDirectory )
    {
        final String actualMetadataJson = getMetadataJson(actualJsonFile);
        final String expectedMetadataJson =
            getMetadataJson(metadataInTestcomparisonDirectory, actualJsonFile.getName());

        JSONAssert
            .assertEquals(
                expectedMetadataJson,
                actualMetadataJson,
                new CustomComparator(
                    JSONCompareMode.STRICT,
                    Customization.customization("pregeneratedLibrary.generatedAt", ( o1, o2 ) -> true)));
    }

    private void assertOnSameJsonFiles( final Path directoryA, final Path directoryB )
    {
        final Collection<File> jsonFiles = FileUtils.listFiles(directoryA.toFile(), new String[] { "json" }, false);

        for( final File jsonFile : jsonFiles ) {
            assertOnJsonFile(jsonFile, directoryB);
        }
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

    private void executeDatamodelGenerator( final Path outputDirectory )
    {
        new DataModelGenerator()
            .withInputDirectory(getInputDirectory().toFile())
            .withOutputDirectory(outputDirectory.toFile())
            .withServiceNameMapping(getInputDirectory().resolve("serviceNameMappings.properties").toFile())
            .withPackageName("testcomparison")
            .deleteOutputDirectory()
            .sapCopyrightHeader()
            .versionReference(false)
            .serviceMethodsPerEntitySet(true)
            .withNameSource(NameSource.NAME)
            .execute();
    }

    private Path getInputDirectory()
    {
        final Path inputDirectory = TEST_RESOURCES_DIRECTORY.resolve("input");

        assertThat(inputDirectory).isDirectory().isReadable();

        return inputDirectory;
    }

    private Path getTestComparisonDirectory()
    {
        final Path testComparisonDirectory = TEST_RESOURCES_DIRECTORY.resolve("output");

        assertThat(testComparisonDirectory).isDirectory().isReadable();

        return testComparisonDirectory;
    }
}
