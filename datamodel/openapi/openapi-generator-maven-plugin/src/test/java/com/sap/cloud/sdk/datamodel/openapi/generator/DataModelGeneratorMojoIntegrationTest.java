package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.SneakyThrows;

/**
 * This integration test might look redundant to {@code DataModelGeneratorIntegrationTest} from the openapi-generator
 * module. However, it was found that the OpenAPI generator behaves strange when it comes to accessing the templates as
 * resources on the classpath. This issue was not caught in the {@code DataModelGeneratorIntegrationTest}.
 */
@MojoTest
class DataModelGeneratorMojoIntegrationTest
{
    private static final String TEST_POM =
        "src/test/resources/DataModelGeneratorMojoIntegrationTest/sodastore/input/pom.xml";
    private static final String FOLDER_WITH_EXPECTED_CONTENT =
        "src/test/resources/" + DataModelGeneratorMojoIntegrationTest.class.getSimpleName() + "/sodastore/output";

    @TempDir
    File outputDirectory;

    @Test
    @InjectMojo( goal = "generate", pom = TEST_POM )
    void generateAndCompareSodastoreLibrary( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        final String outputFolderWithActualContent =
            Paths.get(outputDirectory.getAbsolutePath()).resolve("output").toString();

        generateSodastoreLibrary(mojo, outputFolderWithActualContent);

        assertThatDirectoriesHaveSameContent(
            Paths.get(FOLDER_WITH_EXPECTED_CONTENT),
            Paths.get(outputFolderWithActualContent));
    }

    // Run this test method manually to overwrite the folder containing the expected content with the latest generator state
    // @Test
    @InjectMojo( goal = "generate", pom = TEST_POM )
    void regenerateExpectedSodastoreLibrary( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        generateSodastoreLibrary(mojo, FOLDER_WITH_EXPECTED_CONTENT);
    }

    private void generateSodastoreLibrary( DataModelGeneratorMojo mojo, final String outputDirectory )
        throws Throwable
    {
        final GenerationConfiguration configuration = mojo.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isFalse();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoIntegrationTest/sodastore/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.sodastore.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.sodastore.api");
        assertThat(configuration.useSapCopyrightHeader()).isTrue();

        mojo.setOutputDirectory(outputDirectory);

        mojo.execute();
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
