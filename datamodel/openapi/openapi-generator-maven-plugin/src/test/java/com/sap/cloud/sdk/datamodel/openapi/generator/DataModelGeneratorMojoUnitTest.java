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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.vavr.control.Try;

class DataModelGeneratorMojoUnitTest
{
    @TempDir
    File outputDirectory;

    private DataModelGeneratorMojo sut;

    @Test
    void testInvocationWithAllParameters()
        throws Throwable
    {
        sut = loadTestProject("testInvocationWithAllParameters");

        final GenerationConfiguration configuration = sut.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isTrue();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithAllParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isTrue();
        assertThat(configuration.isOneOfAnyOfGenerationEnabled()).isFalse();

        sut.setOutputDirectory(outputDirectory.getAbsolutePath());

        sut.execute();
    }

    @Test
    void testInvocationWithMandatoryParameters()
        throws Throwable
    {
        sut = loadTestProject("testInvocationWithMandatoryParameters");

        final GenerationConfiguration configuration = sut.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isFalse();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithMandatoryParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isFalse();

        sut.setOutputDirectory(outputDirectory.getAbsolutePath());

        sut.execute();
    }

    @Test
    void testEmptyRequiredParameter()
        throws Throwable
    {
        sut = loadTestProject("testEmptyRequiredParameter");

        final Try<Void> mojoExecutionTry = Try.run(sut::execute);

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
    void testSkipExecution()
        throws Throwable
    {
        sut = loadTestProject("testSkipExecution");

        sut.execute();
        //no reasonable assertion possible
    }

    @Test
    void testInvocationWithUnexpectedApiMaturity()
        throws Throwable
    {
        sut = loadTestProject("testInvocationWithUnexpectedApiMaturity");

        assertThatExceptionOfType(MojoExecutionException.class)
            .isThrownBy(sut::execute)
            .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAdditionalPropertiesAndEnablingAnyOfOneOf()
        throws Throwable
    {
        sut = loadTestProject("testAdditionalPropertiesAndEnablingAnyOfOneOf");

        assertThat(sut.retrieveGenerationConfiguration().get().getAdditionalProperties())
            .containsEntry("param1", "val1")
            .containsEntry("param2", "val2")
            .containsEntry("useAbstractionForFiles", "true");

        assertThat(sut.retrieveGenerationConfiguration().get().isOneOfAnyOfGenerationEnabled()).isTrue();
    }

    private DataModelGeneratorMojo loadTestProject( String testDir )
        throws Throwable
    {
        final URL resource =
            DataModelGeneratorMojoUnitTest.class
                .getClassLoader()
                .getResource("DataModelGeneratorMojoUnitTest/" + testDir);
        assertThat(resource).isNotNull();

        final File pomFile = new File(resource.getFile());

        final MojoRule rule = new MojoRule();
        // hacky workaround to invoke the internal call to "testCase.setUp()" inside MojoRule
        // exploiting the fact that the setup is not teared down after "evaluate" returns
        // this workaround is applied because "lookupConfiguredMojo" is not available on AbstractMojoTestCase
        // and this way we can skip the effort to re-implement what is already available in MojoRule
        rule.apply(new Statement()
        {
            @Override
            public void evaluate()
            {

            }
        }, Description.createSuiteDescription("dummy")).evaluate();
        return (DataModelGeneratorMojo) rule.lookupConfiguredMojo(pomFile, "generate");
    }
}
