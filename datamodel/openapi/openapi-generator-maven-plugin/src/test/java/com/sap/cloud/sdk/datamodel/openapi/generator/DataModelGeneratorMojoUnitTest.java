/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.vavr.control.Try;

public class DataModelGeneratorMojoUnitTest
{
    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TemporaryFolder outputDirectory = TemporaryFolder.builder().assureDeletion().build();

    @Test
    public void testInvocationWithAllParameters()
        throws Exception
    {
        final URL resource =
            getClass().getClassLoader().getResource("DataModelGeneratorMojoUnitTest/testInvocationWithAllParameters");

        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        final GenerationConfiguration configuration = mojo.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose());
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithAllParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isTrue();
        assertThat(configuration.isOneOfAnyOfGenerationEnabled()).isFalse();

        mojo.setOutputDirectory(outputDirectory.newFolder("output").toString());

        mojo.execute();
    }

    @Test
    public void testInvocationWithMandatoryParameters()
        throws Exception
    {
        final URL resource =
            getClass()
                .getClassLoader()
                .getResource("DataModelGeneratorMojoUnitTest/testInvocationWithMandatoryParameters");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        final GenerationConfiguration configuration = mojo.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isFalse();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithMandatoryParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isFalse();

        mojo.setOutputDirectory(outputDirectory.newFolder("output").toString());

        mojo.execute();
    }

    @Test
    public void testEmptyRequiredParameter()
        throws Exception
    {
        final URL resource =
            getClass().getClassLoader().getResource("DataModelGeneratorMojoUnitTest/testEmptyRequiredParameter");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        final Try<Void> mojoExecutionTry = Try.run(mojo::execute);

        assertThat(mojoExecutionTry.isFailure()).isTrue();

        assertThat(mojoExecutionTry.getCause())
            .isInstanceOf(MojoExecutionException.class)
            .hasCauseInstanceOf(OpenApiGeneratorException.class);
        assertThat(mojoExecutionTry.getCause().getCause().getSuppressed())
            .satisfiesExactly(
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class),
                e -> assertThat(e).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    public void testSkipExecution()
        throws Exception
    {
        final URL resource =
            getClass().getClassLoader().getResource("DataModelGeneratorMojoUnitTest/testSkipExecution");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        mojo.execute();

        //no reasonable assertion possible
    }

    @Test
    public void testInvocationWithUnexpectedApiMaturity()
        throws Exception
    {
        final URL resource =
            getClass()
                .getClassLoader()
                .getResource("DataModelGeneratorMojoUnitTest/testInvocationWithUnexpectedApiMaturity");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        assertThatExceptionOfType(MojoExecutionException.class)
            .isThrownBy(mojo::execute)
            .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAdditionalPropertiesAndEnablingAnyOfOneOfGeneration()
        throws Exception
    {
        final URL resource =
            getClass().getClassLoader().getResource("DataModelGeneratorMojoUnitTest/testAdditionalPropertiesAndEnablingAnyOfOneOf");
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final DataModelGeneratorMojo mojo = (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");

        assertThat(mojo.retrieveGenerationConfiguration().get().getAdditionalProperties())
            .containsEntry("param1", "val1")
            .containsEntry("param2", "val2")
            .containsEntry("useAbstractionForFiles", "true");

        assertThat(mojo.retrieveGenerationConfiguration().get().isOneOfAnyOfGenerationEnabled()).isTrue();
    }
}
