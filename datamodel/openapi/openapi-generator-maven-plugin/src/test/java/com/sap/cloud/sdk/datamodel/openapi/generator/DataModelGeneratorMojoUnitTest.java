package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.vavr.control.Try;

@MojoTest
class DataModelGeneratorMojoUnitTest
{
    private static final String RESOURCE_PATH = "src/test/resources/DataModelGeneratorMojoUnitTest";
    private static final String ALL_PARAMETERS_POM = RESOURCE_PATH + "/testInvocationWithAllParameters/pom.xml";
    private static final String MANDATORY_PARAMETERS_POM =
        RESOURCE_PATH + "/testInvocationWithMandatoryParameters/pom.xml";
    private static final String EMPTY_REQUIRED_PARAMETER_POM = RESOURCE_PATH + "/testEmptyRequiredParameter/pom.xml";
    private static final String SKIP_EXECUTION_POM = RESOURCE_PATH + "/testSkipExecution/pom.xml";
    private static final String UNEXPECTED_API_MATURITY_POM =
        RESOURCE_PATH + "/testInvocationWithUnexpectedApiMaturity/pom.xml";
    private static final String ADDITIONAL_PROPERTIES_POM =
        RESOURCE_PATH + "/testAdditionalPropertiesAndEnablingAnyOfOneOf/pom.xml";

    @TempDir
    File outputDirectory;

    @Test
    @InjectMojo( goal = "generate", pom = ALL_PARAMETERS_POM )
    void testInvocationWithAllParameters( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        final GenerationConfiguration configuration = mojo.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isTrue();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithAllParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isTrue();
        assertThat(configuration.isOneOfAnyOfGenerationEnabled()).isFalse();

        mojo.setOutputDirectory(outputDirectory.getAbsolutePath());

        mojo.execute();
    }

    @Test
    @InjectMojo( goal = "generate", pom = MANDATORY_PARAMETERS_POM )
    void testInvocationWithMandatoryParameters( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        final GenerationConfiguration configuration = mojo.retrieveGenerationConfiguration().get();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isFalse();
        assertThat(configuration.getOutputDirectory()).isEqualTo("output-directory");
        assertThat(configuration.getInputSpec())
            .isEqualTo("DataModelGeneratorMojoUnitTest/testInvocationWithMandatoryParameters/input/sodastore.yaml");
        assertThat(configuration.getModelPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.model");
        assertThat(configuration.getApiPackage()).isEqualTo("com.sap.cloud.sdk.datamodel.rest.test.api");
        assertThat(configuration.deleteOutputDirectory()).isFalse();

        mojo.setOutputDirectory(outputDirectory.getAbsolutePath());

        mojo.execute();
    }

    @Test
    @InjectMojo( goal = "generate", pom = EMPTY_REQUIRED_PARAMETER_POM )
    void testEmptyRequiredParameter( DataModelGeneratorMojo mojo )
        throws Throwable
    {
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
    @InjectMojo( goal = "generate", pom = SKIP_EXECUTION_POM )
    void testSkipExecution( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        mojo.execute();
        //no reasonable assertion possible
    }

    @Test
    @InjectMojo( goal = "generate", pom = UNEXPECTED_API_MATURITY_POM )
    void testInvocationWithUnexpectedApiMaturity( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        assertThatExceptionOfType(MojoExecutionException.class)
            .isThrownBy(mojo::execute)
            .withCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @InjectMojo( goal = "generate", pom = ADDITIONAL_PROPERTIES_POM )
    void testAdditionalPropertiesAndEnablingAnyOfOneOf( DataModelGeneratorMojo mojo )
        throws Throwable
    {
        assertThat(mojo.retrieveGenerationConfiguration().get().getAdditionalProperties())
            .containsEntry("param1", "val1")
            .containsEntry("param2", "val2")
            .containsEntry("useAbstractionForFiles", "true");

        assertThat(mojo.retrieveGenerationConfiguration().get().isOneOfAnyOfGenerationEnabled()).isTrue();
    }
}
