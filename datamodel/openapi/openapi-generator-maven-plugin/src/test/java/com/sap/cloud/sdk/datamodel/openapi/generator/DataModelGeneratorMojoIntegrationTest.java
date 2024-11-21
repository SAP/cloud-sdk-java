/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
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
class DataModelGeneratorMojoIntegrationTest extends AbstractMojoTestCase
{
    @TempDir
    File outputDirectory;

    private static final String FOLDER_WITH_EXPECTED_CONTENT =
        "src/test/resources/" + DataModelGeneratorMojoIntegrationTest.class.getSimpleName() + "/sodastore/output";

    @Test
    void generateAndCompareSodastoreLibrary()
        throws Throwable
    {
        super.setUp();
        final String outputFolderWithActualContent =
            Paths.get(outputDirectory.getAbsolutePath()).resolve("output").toString();

        generateSodastoreLibrary(outputFolderWithActualContent);

        assertThatDirectoriesHaveSameContent(
            Paths.get(FOLDER_WITH_EXPECTED_CONTENT),
            Paths.get(outputFolderWithActualContent));
    }

    // Run this test method manually to overwrite the folder containing the expected content with the latest generator state
     @Test
    void regenerateExpectedSodastoreLibrary()
        throws Throwable
    {
        super.setUp();
        generateSodastoreLibrary(FOLDER_WITH_EXPECTED_CONTENT);
    }

    private void generateSodastoreLibrary( final String outputDirectory )
        throws Throwable
    {
        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) super.lookupMojo("generate", "src/test/resources/DataModelGeneratorMojoIntegrationTest/sodastore/input/pom.xml");

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