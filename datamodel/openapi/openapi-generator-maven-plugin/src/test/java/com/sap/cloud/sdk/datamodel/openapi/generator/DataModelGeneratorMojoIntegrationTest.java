/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

//import org.junit.runner.Description;
//import org.junit.runners.model.Statement;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.SneakyThrows;

/**
 * This integration test might look redundant to {@code DataModelGeneratorIntegrationTest} from the openapi-generator
 * module. However, it was found that the OpenAPI generator behaves strange when it comes to accessing the templates as
 * resources on the classpath. This issue was not caught in the {@code DataModelGeneratorIntegrationTest}.
 */
class DataModelGeneratorMojoIntegrationTest
{
    @TempDir
    File outputDirectory;

    private static final String FOLDER_WITH_EXPECTED_CONTENT =
        "src/test/resources/" + DataModelGeneratorMojoIntegrationTest.class.getSimpleName() + "/sodastore/output";

    @Test
    @Disabled( "find a solution without using deprecated MojoRule" )
    void generateAndCompareSodastoreLibrary()
        throws Throwable
    {
        final String outputFolderWithActualContent =
            Paths.get(outputDirectory.getAbsolutePath()).resolve("output").toString();

        generateSodastoreLibrary(outputFolderWithActualContent);

        assertThatDirectoriesHaveSameContent(
            Paths.get(FOLDER_WITH_EXPECTED_CONTENT),
            Paths.get(outputFolderWithActualContent));
    }

    // Run this test method manually to overwrite the folder containing the expected content with the latest generator state
    // @Test
    void regenerateExpectedSodastoreLibrary()
        throws Throwable
    {
        generateSodastoreLibrary(FOLDER_WITH_EXPECTED_CONTENT);
    }

    private void generateSodastoreLibrary( final String outputDirectory )
        throws Throwable
    {
        final DataModelGeneratorMojo mojo = loadTestProject();

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

    private DataModelGeneratorMojo loadTestProject()
        throws Throwable
    {
        final URL resource = getClass().getClassLoader().getResource(getClass().getSimpleName() + "/sodastore/input");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final MojoRule rule = new MojoRule();
        // hacky workaround to invoke the internal call to "testCase.setUp()" inside MojoRule
        // exploiting the fact that the setup is not teared down after "evaluate" returns
        // this workaround is applied because "lookupConfiguredMojo" is not available on AbstractMojoTestCase
        // and this way we can skip the effort to re-implement what is already available in MojoRule
        /*
        rule.apply(new Statement()
        {
            @Override
            public void evaluate()
            {

            }
        }, Description.createSuiteDescription("dummy")).evaluate();
        return (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");
         */
        return null;
    }
}

/**
 * Since version 3.2 you don't need to use @MojoRule, 7 years ago. Just follow the three steps below:
 *
 * Your test class should extend AbstractMojoTestCase
 *
 * Before your tests, call super.setUp()
 *
 * Perform a lookup for your mojo:
 *
 * MyMojo myMojo = (MyMojo) super.lookupMojo("myGoal", "src/test/resources/its/my-test-mojo.pom.xml");
 *
 * With that, you can work with Junit 5, Mockito, etc, with no overhead.
 */
