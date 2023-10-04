/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;

/**
 * Mojo used in the OData V4 Generator Maven Plugin.
 */
@Beta
@Mojo( name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = false )
public class DataModelGeneratorMojo extends AbstractMojo
{
    /**
     * Project reference to add generated classed to the set of compiled classes.
     */
    @Parameter( defaultValue = "${project}" )
    private MavenProject project;

    /**
     * Path to the input directory that contains the service definition files.
     */
    @Parameter(
        property = "odatav4.generate.inputDirectory",
        defaultValue = DataModelGenerator.DEFAULT_INPUT_DIRECTORY_NAME )
    private String inputDirectory;

    /**
     * Path to the output directory to store the generated sources in.
     */
    @Parameter(
        property = "odatav4.generate.outputDirectory",
        defaultValue = DataModelGenerator.DEFAULT_OUTPUT_DIRECTORY_NAME )
    private String outputDirectory;

    /**
     * Defines whether to delete the output directory prior to generating sources.
     */
    @Parameter( property = "odatav4.generate.deleteOutputDirectory" )
    private Boolean deleteOutputDirectory;

    /**
     * Defines whether already existing files will get overwritten. If {@code false}, an exception is thrown if a file
     * already exists.
     */
    @Parameter( property = "odatav4.generate.overwriteFiles" )
    private Boolean overwriteFiles;

    /**
     * Defines whether existing signatures from already generated classes will be considered when generating again. If
     * {@code true}, breaking changes in method signature will be avoided, when the argument order in OData
     * specification was altered.
     */
    @Parameter( property = "odatav4.generate.keepExistingSignatures" )
    private Boolean keepExistingSignatures;

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
    @Parameter( property = "odatav4.generate.compileScope" )
    private CompileScope compileScope;

    /**
     * Specifies the package prefix to be used in the generated sources.
     */
    @Parameter( property = "odatav4.generate.packageName", defaultValue = DataModelGenerator.DEFAULT_PACKAGE_NAME )
    private String packageName;

    /**
     * The path to the properties file containing the service name mapping.
     * <p>
     * Any service not mapped in this file will get created/updated.
     */
    @Parameter(
        property = "odatav4.generate.serviceNameMappingFile",
        defaultValue = DataModelGenerator.DEFAULT_SERVICE_NAME_MAPPING_FILE_NAME )
    private String serviceNameMappingFile;

    /**
     * Specifies the default base path.
     * <p>
     * If no swagger file is given, this base path, together with the edmx file name, constitute the service path with
     * the following pattern: &lt;defaultBasePath&gt; + &lt;edmxFileNameWithoutExtension&gt;.
     * <p>
     * Default value is {@code null}.
     */
    @Parameter( property = "odatav4.generate.defaultBasePath" )
    private String defaultBasePath;

    /**
     * The fully specified path to the class implementing the {@link NamingStrategy} interface.
     * <p>
     * This class is then used to map the edm names to java names.
     */
    @Parameter(
        property = "odatav4.generate.namingStrategy",
        defaultValue = DataModelGenerator.DEFAULT_NAMING_STRATEGY )
    private String namingStrategy;

    /**
     * Getter for the given {@link NameSource}.
     * <p>
     * If the {@link #namingStrategy} is {@link S4HanaNamingStrategy}, this enum will be used to determine the actual
     * source of the java name.
     */
    @Parameter( property = "odatav4.generate.nameSource" )
    private NameSource nameSource;

    /**
     * The fully specified path to the class implementing the AnnotationStrategy interface.
     * <p>
     * This class is then used to provide generated Java classes with necessary annotations.
     */
    @Parameter(
        property = "odatav4.generate.annotationStrategy",
        defaultValue = DataModelGenerator.DEFAULT_ANNOTATION_STRATEGY )
    private String annotationStrategy;

    /**
     * Defines whether to generate just the POJO classes.
     */
    @Parameter( property = "odatav4.generate.pojosOnly" )
    private Boolean pojosOnly;

    /**
     * Defines whether to exit with failure in case a warning occurs during processing.
     */
    @Parameter( property = "odatav4.generate.failOnWarning" )
    private Boolean failOnWarning;

    /**
     * The ant style pattern of filenames for which VDM should not be generated. Multiple patterns are separated by
     * commas (without SPACES). E. g.: "*us_en.xml,*DE_de.edmx".
     * <p>
     * For files matching this ant style pattern, VDM is not generated.
     */
    @Parameter( property = "odatav4.generate.excludes" )
    private String excludes;

    /**
     * Defines whether to generate API reference URLs linking to the SAP Business Accelerator Hub.
     */
    @Parameter( property = "odatav4.generate.linkToApiBusinessHub" )
    private Boolean linkToApiBusinessHub;

    /**
     * Defines whether to generate comments indicating the OData VDM code generator version.
     */
    @Parameter( property = "odatav4.generate.versionReference" )
    private Boolean versionReference;

    /**
     * Defines a copyright header to be set for generated classes. If an empty string is passed in, no header will be
     * set.
     */
    @Parameter( property = "odatav4.generate.copyrightHeader" )
    private String copyrightHeader;

    /**
     * Defines whether to add a SAP copyright header to generated classes. This option cannot be used with
     * {@link #copyrightHeader} and will override what is set there.
     */
    @Parameter( property = "odatav4.generate.sapCopyrightHeader" )
    private Boolean sapCopyrightHeader;

    /**
     * Restricts the generated entity classes to the provided values, plus any dependent complex types. For navigation
     * properties, the associated entity set names must be included in order to be generated. If no value is provided,
     * then all entity sets will be generated.
     */
    @Parameter( property = "odatav4.generate.includeEntitySets" )
    private Set<String> includeEntitySets;

    /**
     * Restricts the generated function imports to the provided values, plus any dependent complex types. If the return
     * type is an entity, then make sure to include its entity set name in the {@code includeEntitySets} parameter (or
     * generate all entity sets). Otherwise the function import code cannot be generated. If no value is provided, then
     * all function imports will be generated.
     */
    @Parameter( property = "odatav4.generate.includeFunctionImports" )
    private Set<String> includeFunctionImports;

    /**
     * The generator generates service methods for each entity set of one entity type. If this flag is not used, the
     * generator generates service methods only for the first entity set of one entity type. All additional entity sets
     * for the entity type are disregarded.
     */
    @Parameter( property = "odatav4.generate.serviceMethodsPerEntitySet" )
    private Boolean serviceMethodsPerEntitySet;

    private void initializeParameters()
    {
        if( deleteOutputDirectory == null ) {
            deleteOutputDirectory = DataModelGenerator.DEFAULT_DELETE_OUTPUT_DIRECTORY;
        }

        if( overwriteFiles == null ) {
            overwriteFiles = DataModelGenerator.DEFAULT_OVERWRITE_FILES;
        }

        if( keepExistingSignatures == null ) {
            keepExistingSignatures = DataModelGenerator.DEFAULT_KEEP_EXISTING_SIGNATURES;
        }

        if( compileScope == null ) {
            compileScope = CompileScope.NONE;
        }

        if( nameSource == null ) {
            nameSource = DataModelGenerator.DEFAULT_NAMING_SOURCE;
        }

        if( pojosOnly == null ) {
            pojosOnly = DataModelGenerator.DEFAULT_POJOS_ONLY;
        }

        if( linkToApiBusinessHub == null ) {
            linkToApiBusinessHub = DataModelGenerator.DEFAULT_LINK_TO_API_BUSINESS_HUB;
        }

        if( failOnWarning == null ) {
            failOnWarning = DataModelGenerator.DEFAULT_FAIL_ON_WARNING;
        }

        if( versionReference == null ) {
            versionReference = DataModelGenerator.DEFAULT_VERSION_REFERENCE;
        }

        if( sapCopyrightHeader == null ) {
            sapCopyrightHeader = false;
        }

        if( copyrightHeader == null ) {
            copyrightHeader = DataModelGenerator.DEFAULT_COPYRIGHT_HEADER;
        }

        // needed here, as the default value of the Parameter annotation (empty String) is silently converted to null
        if( excludes == null ) {
            excludes = DataModelGenerator.DEFAULT_EXCLUDES_PATTERN;
        }

        if( serviceMethodsPerEntitySet == null ) {
            serviceMethodsPerEntitySet = false;
        }
    }

    DataModelGenerator getDataModelGenerator()
    {
        final DataModelGenerator dataModelGenerator = new DataModelGenerator();
        initializeParameters();

        if( sapCopyrightHeader ) {
            dataModelGenerator.sapCopyrightHeader();
        } else {
            dataModelGenerator.copyrightHeader(copyrightHeader);
        }

        return dataModelGenerator
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .deleteOutputDirectory(deleteOutputDirectory)
            .overwriteFiles(overwriteFiles)
            .keepExistingSignatures(keepExistingSignatures)
            .withPackageName(packageName)
            .withServiceNameMapping(serviceNameMappingFile)
            .withDefaultBasePath(defaultBasePath)
            .withNamingStrategy(namingStrategy)
            .withNameSource(nameSource)
            .withAnnotationStrategy(annotationStrategy)
            .pojosOnly(pojosOnly)
            .withExcludeFilePattern(excludes)
            .linkToApiBusinessHub(linkToApiBusinessHub)
            .versionReference(versionReference)
            .withIncludedEntitySets(includeEntitySets)
            .withIncludedFunctionImports(includeFunctionImports)
            .failOnWarning(failOnWarning)
            .serviceMethodsPerEntitySet(serviceMethodsPerEntitySet);
    }

    @Override
    public void execute()
        throws MojoExecutionException
    {
        try {
            final DataModelGenerator generator = getDataModelGenerator();

            generator.execute();

            if( generator.failureDueToWarningsNecessary() ) {
                throw new MojoExecutionException(
                    "Failed to generate data model because warning occurred. See the log above.");
            }

            compileScope.addSourceRoot(project, outputDirectory);
            getLog().info("Added generated sources according to compile scope '" + compileScope + "'.");
        }
        catch( final ODataGeneratorException e ) {
            throw new MojoExecutionException("Failed to generate data model.", e);
        }
    }

}
