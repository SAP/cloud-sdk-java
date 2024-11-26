/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Arrays;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;

/**
 * Mojo used in the REST Generator Maven Plugin.
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = false )
public class DataModelGeneratorMojo extends AbstractMojo
{
    /**
     * Project reference to add generated classed to the set of compiled classes.
     */
    @Parameter( defaultValue = "${project}" )
    private MavenProject project;

    /**
     * This required parameter contains the file path to the OpenAPI specification which the REST Generator should use.
     */
    @Parameter( property = "openapi.generate.inputSpec", required = true )
    private String inputSpec;

    /**
     * This required parameter contains the directory path where the generated code should be stored in.
     */
    @Parameter( property = "openapi.generate.outputDirectory", required = true )
    private String outputDirectory;

    /**
     * This required parameter defines the Java package in which the generated API classes should be located in.
     */
    @Parameter( property = "openapi.generate.apiPackage", required = true )
    private String apiPackage;

    /**
     * This required parameter defines the Java package in which the generated model classes should be located in.
     */
    @Parameter( property = "openapi.generate.modelPackage", required = true )
    private String modelPackage;

    /**
     * This optional parameter defines the maturity of the REST API for which the client library gets generatd.
     *
     * Possible values = {released, beta} Default value = released
     *
     * If "released" is chosen, the generated code does not contain the {@link com.google.common.annotations.Beta}
     * annotation. If "beta" is chosen, the generated code does contain the {@link com.google.common.annotations.Beta}
     * annotation.
     */
    @Parameter( property = "openapi.generate.apiMaturity" )
    private String apiMaturity;

    /**
     * This optional parameter that defines whether the wrapped OpenAPI Generator uses verbose output.
     *
     * Default value = false
     */
    @Parameter( property = "openapi.generate.verbose", defaultValue = "false" )
    private Boolean verbose;

    /**
     * Defines to which compile scope the generated source code should be added (if any).
     * <p>
     * Valid options are:
     * <ul>
     * <li>{@link CompileScope#COMPILE COMPILE}: Adding the generated source code the default compile phase.</li>
     * <li>{@link CompileScope#TEST_COMPILE TEST_COMPILE}: Adding the generated source code the test compile phase.</li>
     * <li>{@link CompileScope#NONE NONE}: Does not add the generated source code to any compile phase. (default)</li>
     * </ul>
     */
    @Parameter( property = "openapi.generate.compileScope" )
    private CompileScope compileScope;

    @Parameter( property = "openapi.generate.skip", defaultValue = "false" )
    private Boolean skip;

    /**
     * Defines a copyright header to be set for generated classes. If an empty string is passed in, no header will be
     * set.
     */
    @Parameter( property = "openapi.generate.copyrightHeader", defaultValue = "" )
    private String copyrightHeader;

    /**
     * Defines whether to add a SAP copyright header to generated classes. This option cannot be used with
     * {@link #copyrightHeader} and will override what is set there.
     */
    @Parameter( property = "openapi.generate.sapCopyrightHeader", defaultValue = "false" )
    private Boolean sapCopyrightHeader;

    /**
     * Defines whether to delete the generated files from output directory prior to the generation.
     */
    @Parameter( property = "openapi.generate.deleteOutputDirectory", defaultValue = "false" )
    private boolean deleteOutputDirectory;

    /**
     * Defines whether to enable processing of anyOf/oneOf keywords during client generation
     */
    @Parameter( property = "openapi.generate.enableOneOfAnyOfGeneration", defaultValue = "false" )
    private boolean enableOneOfAnyOfGeneration;

    /**
     * Generate model classes
     */
    @Parameter( property = "openapi.generate.generateModels", defaultValue = "true" )
    private boolean generateModels;

    /**
     * Generate API classes (client classes)
     */
    @Parameter( property = "openapi.generate.generateApis", defaultValue = "true" )
    private boolean generateApis;

    /**
     * Defines a list of additional properties that will be passed to the Java generator.
     */
    @Parameter( property = "openapi.generate.additionalProperties" )
    private Map<String, String> additionalProperties;

    @Override
    public void execute()
        throws MojoExecutionException
    {
        if( skip ) {
            getLog().info("Skipping the REST VDM Generator Maven Plugin as instructed.");
            return;
        }

        initializeEmptyParameters();

        final GenerationConfiguration generationConfiguration =
            retrieveGenerationConfiguration()
                .getOrElseThrow(cause -> new MojoExecutionException("Failed to generate data model.", cause));

        final Try<GenerationResult> generationResult =
            new DataModelGenerator().generateDataModel(generationConfiguration);

        if( generationResult.isFailure() ) {
            Arrays
                .stream(generationResult.getCause().getSuppressed())
                .forEach(cause -> getLog().error(cause.getMessage(), cause));
            throw new MojoExecutionException("Failed to generate data model.", generationResult.getCause());
        }

        getLog().info("Successfully generated " + generationResult.get().getGeneratedFiles().size() + " files.");

        compileScope.addSourceRoot(project, outputDirectory);
        getLog().info("Added generated sources according to compile scope '" + compileScope + "'.");
    }

    private void initializeEmptyParameters()
    {
        if( compileScope == null ) {
            compileScope = CompileScope.NONE;
        }
    }

    Try<GenerationConfiguration> retrieveGenerationConfiguration()
    {
        return Try
            .of(
                () -> GenerationConfiguration
                    .builder()
                    .outputDirectory(outputDirectory)
                    .inputSpec(inputSpec)
                    .apiPackage(apiPackage)
                    .modelPackage(modelPackage)
                    .apiMaturity(ApiMaturity.getValueOrDefault(apiMaturity))
                    .verbose(verbose)
                    .withSapCopyrightHeader(sapCopyrightHeader)
                    .copyrightHeader(copyrightHeader)
                    .deleteOutputDirectory(deleteOutputDirectory)
                    .additionalProperties(additionalProperties)
                    .oneOfAnyOfGenerationEnabled(enableOneOfAnyOfGeneration)
                    .generateModels(generateModels)
                    .generateApis(generateApis)
                    .build());
    }

    //we need to set a temporary folder from JUnit during test time from outside
    void setOutputDirectory( final String outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }

}
