/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.vavr.control.Try;

class DataModelGeneratorMojoUnitTest extends AbstractMojoTestCase
{

    static final String PLUGIN_TEST_DIR = "src/test/resources/DataModelGeneratorMojoUnitTest/%s/pom.xml";

    @TempDir
    File outputDirectory;

    private DataModelGeneratorMojo sut;

    @Test
    void testInvocationWithAllParameters()
        throws Throwable
    {
        super.setUp();
        sut = loadTestProject("/testInvocationWithAllParameters");

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
        sut = loadTestProject("/testInvocationWithMandatoryParameters");

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
        sut = loadTestProject("/testEmptyRequiredParameter");

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
        sut = loadTestProject("/testSkipExecution");

        sut.execute();
        //no reasonable assertion possible
    }

    @Test
    void testInvocationWithUnexpectedApiMaturity()
        throws Throwable
    {
        sut = loadTestProject("/testInvocationWithUnexpectedApiMaturity");

        assertThatExceptionOfType(MojoExecutionException.class)
            .isThrownBy(sut::execute)
            .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testAdditionalPropertiesAndEnablingAnyOfOneOf()
        throws Throwable
    {
        sut = loadTestProject("/testAdditionalPropertiesAndEnablingAnyOfOneOf");

        assertThat(sut.retrieveGenerationConfiguration().get().getAdditionalProperties())
            .containsEntry("param1", "val1")
            .containsEntry("param2", "val2")
            .containsEntry("useAbstractionForFiles", "true");

        assertThat(sut.retrieveGenerationConfiguration().get().isOneOfAnyOfGenerationEnabled()).isTrue();
    }

    private DataModelGeneratorMojo loadTestProject( String testDir )
        throws Throwable
    {
        super.setUp();
        return (DataModelGeneratorMojo) super.lookupMojo("generate", PLUGIN_TEST_DIR.formatted(testDir));
    }
}
