/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.testutil.DirectoryContentAssertionUtil;

/**
 * This integration test might look redundant to {@code DataModelGeneratorIntegrationTest} from the openapi-generator
 * module. However, it was found that the OpenAPI generator behaves strange when it comes to accessing the templates as
 * resources on the classpath. This issue was not caught in the {@code DataModelGeneratorIntegrationTest}.
 */
public class DataModelGeneratorMojoIntegrationTest
{
    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TemporaryFolder outputDirectory = TemporaryFolder.builder().assureDeletion().build();

    private static final String FOLDER_WITH_EXPECTED_CONTENT =
        "src/test/resources/" + DataModelGeneratorMojoIntegrationTest.class.getSimpleName() + "/sodastore/output";

    @Test
    public void generateAndCompareSodastoreLibrary()
        throws Exception
    {
        final String outputFolderWithActualContent =
            Paths.get(outputDirectory.getRoot().toString()).resolve("output").toString();

        generateSodastoreLibrary(outputFolderWithActualContent);

        DirectoryContentAssertionUtil
            .assertThatDirectoriesHaveSameContent(
                Paths.get(outputFolderWithActualContent),
                Paths.get(FOLDER_WITH_EXPECTED_CONTENT));
    }

    // Run this test method manually to overwrite the folder containing the expected content with the latest generator state
    //@Test
    public void regenerateExpectedSodastoreLibrary()
        throws Exception
    {
        generateSodastoreLibrary(FOLDER_WITH_EXPECTED_CONTENT);
    }

    private void generateSodastoreLibrary( final String outputDirectory )
        throws Exception
    {
        final URL resource =
            getClass()
                .getClassLoader()
                .getResource(DataModelGeneratorMojoIntegrationTest.class.getSimpleName() + "/sodastore/input");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

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
}
