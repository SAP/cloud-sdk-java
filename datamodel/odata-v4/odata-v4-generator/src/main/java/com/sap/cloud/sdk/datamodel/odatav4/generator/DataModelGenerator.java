/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.AnnotationStrategy;

import lombok.Getter;

/**
 * Builder for the {@code ODataToVdmGenerator}, gathering all relevant parameter or providing default values for
 * unspecified ones.
 */
@Beta
@Getter
public class DataModelGenerator implements DataModelGeneratorConfig
{
    private static final Logger logger = MessageCollector.getLogger(DataModelGenerator.class);

    /**
     * The default directory name to search and save configuration data in.
     */
    public static final String DEFAULT_INPUT_DIRECTORY_NAME = "input";

    /**
     * The default directory name to store the generated sources in.
     */
    public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "target";

    /**
     * The default flag indicating whether the output directory should be deleted prior to the generation.
     */
    public static final Boolean DEFAULT_DELETE_OUTPUT_DIRECTORY = false;

    /**
     * The default flag indicating whether already existing files should be overwritten. If {@code false}, an exception
     * is thrown if a file already exists.
     */
    public static final Boolean DEFAULT_OVERWRITE_FILES = false;

    /**
     * The default flag indicating that existing signatures from already generated classes will be considered.
     */
    public static final boolean DEFAULT_KEEP_EXISTING_SIGNATURES = true;

    /**
     * The default package prefix of the generated sources.
     */
    public static final String DEFAULT_PACKAGE_NAME = "com.sap.cloud.sdk.s4hana.datamodel.odatav4";

    /**
     * The default base path, used in absence of a swagger file to determine a service path.
     */
    public static final String DEFAULT_BASE_PATH = null;

    /**
     * The default file name to the name mapping properties file.
     */
    public static final String DEFAULT_SERVICE_NAME_MAPPING_FILE_NAME = "serviceNameMappings.properties";

    /**
     * The default fully-qualified name of the class to use for converting names from the OData world to the Java world.
     */
    public static final String DEFAULT_NAMING_STRATEGY =
        "com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy";

    /**
     * The default fully-qualified name of the class to use for providing generated Java classes with necessary
     * annotations.
     */
    public static final String DEFAULT_ANNOTATION_STRATEGY =
        "com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.DefaultAnnotationStrategy";

    /**
     * The default naming source used in the {@link S4HanaNamingStrategy}.
     */
    public static final NameSource DEFAULT_NAMING_SOURCE = NameSource.LABEL;

    /**
     * The default flag indicating whether to generate just the POJO classes (entities and complex types).
     */
    public static final Boolean DEFAULT_POJOS_ONLY = false;

    /**
     * The default ant style pattern of filenames for which VDM should not be generated - empty string.
     */
    public static final String DEFAULT_EXCLUDES_PATTERN = "";

    /**
     * The default flag indicating whether to generate API reference URLs linking to the SAP Business Accelerator Hub.
     */
    public static final Boolean DEFAULT_LINK_TO_API_BUSINESS_HUB = false;

    /**
     * The default flag indicating whether to exit with a failure in case a warning occurs.
     */
    public static final Boolean DEFAULT_FAIL_ON_WARNING = false;

    /**
     * The default flag indicating whether to skip generating comments indicating the version reference of the used
     * OData generator plugin.
     */
    public static final Boolean DEFAULT_VERSION_REFERENCE = true;

    /**
     * The default copyright header that is added to generated files. By default, no copyright headers are added.
     */
    public static final String DEFAULT_COPYRIGHT_HEADER = "";

    /**
     * By default the generator generates service methods for the first entity set per entity type only. All additional
     * entity sets of for the entity type are disregarded.
     */
    public static final Boolean DEFAULT_SERVICE_METHODS_PER_ENTITY_SET = false;

    /**
     * An SAP copyright header that can be added to generated files.
     */
    public static final String SAP_COPYRIGHT_HEADER =
        "/*\n * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.\n */\n";

    private File inputDirectory = new File(DEFAULT_INPUT_DIRECTORY_NAME);
    private File outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY_NAME);
    private boolean deleteTargetDirectory = DEFAULT_DELETE_OUTPUT_DIRECTORY;
    private boolean forceFileOverride = DEFAULT_OVERWRITE_FILES;
    private boolean keepExistingSignatures = DEFAULT_KEEP_EXISTING_SIGNATURES;
    private String packageName = DEFAULT_PACKAGE_NAME;
    private String defaultBasePath = DEFAULT_BASE_PATH;
    private File serviceNameMappings = new File(DEFAULT_SERVICE_NAME_MAPPING_FILE_NAME);
    private NamingStrategy namingStrategy = getStrategyInstanceFromName(DEFAULT_NAMING_STRATEGY, NamingStrategy.class);
    private NameSource nameSource = DEFAULT_NAMING_SOURCE;
    private AnnotationStrategy annotationStrategy =
        getStrategyInstanceFromName(DEFAULT_ANNOTATION_STRATEGY, AnnotationStrategy.class);
    private boolean generatePojosOnly = DEFAULT_POJOS_ONLY;
    private String excludeFilePattern = DEFAULT_EXCLUDES_PATTERN;
    private boolean generateLinksToApiBusinessHub = DEFAULT_LINK_TO_API_BUSINESS_HUB;
    private boolean generateVersionReference = DEFAULT_VERSION_REFERENCE;
    private String copyrightHeader = DEFAULT_COPYRIGHT_HEADER;
    private Set<String> includedEntitySets = null;
    private Set<String> includedFunctionImports = null;
    private Set<String> includedActionImports = null;
    private boolean serviceMethodsPerEntitySet = DEFAULT_SERVICE_METHODS_PER_ENTITY_SET;
    private String deprecationNotice = null;

    /**
     * The flag indicating whether the generator fails in case a warning occurs. True, if the generator fails on a
     * warning; false otherwise.
     */
    private boolean failOnWarning = DEFAULT_FAIL_ON_WARNING;

    /**
     * Path to the input directory that contains the service definition files.
     *
     * @param inputDirectory
     *            The directory to read the service data from.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withInputDirectory( @Nonnull final String inputDirectory )
    {
        return withInputDirectory(new File(inputDirectory));
    }

    /**
     * Input directory that contains the service definition files.
     *
     * @param inputDirectory
     *            The directory to read the service data from.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withInputDirectory( @Nonnull final File inputDirectory )
    {
        this.inputDirectory = inputDirectory;
        return this;
    }

    /**
     * Path to the output directory to store the generated sources in.
     * <p>
     * The generated sources will be stored under this directory according to the packages specified.
     *
     * @param outputDirectory
     *            The path to the root directory to store the generated sources in.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withOutputDirectory( @Nonnull final String outputDirectory )
    {
        return withOutputDirectory(new File(outputDirectory));
    }

    /**
     * Output directory to store the generated sources in.
     * <p>
     * The generated sources will be stored under this directory according to the packages specified.
     *
     * @param outputDirectory
     *            The root directory to store the generated sources in.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withOutputDirectory( @Nonnull final File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
        return this;
    }

    /**
     * Deletes the output directory specified by {@link #withPackageName(String)} prior to generating the sources.
     * <p>
     * This option should be used to determine the whole change set, including deleted files.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator deleteOutputDirectory()
    {
        return deleteOutputDirectory(true);
    }

    /**
     * Defines whether to delete the output directory specified by {@link #withPackageName(String)} prior to generating
     * the sources.
     * <p>
     * This option should be used to determine the whole change set, including deleted files.
     *
     * @param deleteOutputDirectory
     *            Flag indicating whether the output directory should be cleaned.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator deleteOutputDirectory( final boolean deleteOutputDirectory )
    {
        this.deleteTargetDirectory = deleteOutputDirectory;
        return this;
    }

    /**
     * Defines that already existing files will get overwritten. Otherwise, an exception is thrown if a file already
     * exists.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator overwriteFiles()
    {
        return overwriteFiles(true);
    }

    /**
     * Defines whether already existing files will get overwritten. If {@code false}, an exception is thrown if a file
     * already exists.
     *
     * @param overwriteFiles
     *            Flag indicating whether already existing files can be overwritten.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator overwriteFiles( final boolean overwriteFiles )
    {
        this.forceFileOverride = overwriteFiles;
        return this;
    }

    /**
     * Defines whether existing signatures from already generated classes will be considered when generating again. If
     * {@code true}, breaking changes in method signature will be avoided, when the argument order in OData
     * specification was altered.
     *
     * @param keepExistingSignatures
     *            Flag indicating whether already generated classes will be considered.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator keepExistingSignatures( final boolean keepExistingSignatures )
    {
        this.keepExistingSignatures = keepExistingSignatures;
        return this;
    }

    /**
     * Specifies the package prefix to be used in the generated sources.
     *
     * @param packageName
     *            The package prefix for the generated sources.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withPackageName( @Nonnull final String packageName )
    {
        this.packageName = packageName;
        return this;
    }

    /**
     * Specifies the default base path.
     * <p>
     * If no swagger file is given, this base path, together with the metadata file name, constitute the service path
     * with the following pattern: &lt;defaultBasePath&gt; + &lt;metadataFileNameWithoutExtension&gt;.
     *
     * @param defaultBasePath
     *            The default base path.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withDefaultBasePath( @Nullable final String defaultBasePath )
    {
        this.defaultBasePath = defaultBasePath;
        return this;
    }

    /**
     * The properties file containing the service name mapping.
     * <p>
     * Any service not mapped in this file will get created/updated.
     *
     * @param serviceNameMappingFile
     *            The file to read/set the service name mappings in.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withServiceNameMapping( @Nonnull final File serviceNameMappingFile )
    {
        serviceNameMappings = serviceNameMappingFile;
        return this;
    }

    /**
     * The path to the properties file containing the service name mapping.
     * <p>
     * Any service not mapped in this file will get created/updated.
     *
     * @param serviceNameMappingFile
     *            The file to read/set the service name mappings in.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withServiceNameMapping( @Nonnull final String serviceNameMappingFile )
    {
        return withServiceNameMapping(new File(serviceNameMappingFile));
    }

    /**
     * Sets the class to use to convert OData names into suitable Java names.
     *
     * @param namingStrategy
     *            Instance of a class, which must either implement the {@link NamingStrategy CodeNamingStrategy}
     *            interface, or extend a class which implements this interface.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withNamingStrategy( @Nonnull final NamingStrategy namingStrategy )
    {
        this.namingStrategy = namingStrategy;
        return this;
    }

    /**
     * Sets the class to use to convert OData names into suitable Java names.
     *
     * @param namingStrategyClassName
     *            Fully-qualified name of a class, which must either implement the {@link NamingStrategy NamingStrategy}
     *            interface, or extend a class which implements this interface.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withNamingStrategy( @Nonnull final String namingStrategyClassName )
    {
        final NamingStrategy namingStrategy =
            getStrategyInstanceFromName(namingStrategyClassName, NamingStrategy.class);
        return withNamingStrategy(namingStrategy);
    }

    /**
     * Getter for the given {@link NameSource}.
     * <p>
     * If the {@link #namingStrategy} is {@link S4HanaNamingStrategy}, this enum will be used to determine the actual
     * source of the java name.
     *
     * @param nameSource
     *            The {@code NameSource} to be used by the {@code DefaulNamingStrategy}.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withNameSource( @Nonnull final NameSource nameSource )
    {
        this.nameSource = nameSource;
        return this;
    }

    /**
     * Sets the class to use to provide generated Java classes with necessary annotations.
     *
     * @param annotationStrategy
     *            Instance of a class, which must either implement the {@link AnnotationStrategy AnnotationStrategy}
     *            interface, or extend a class which implements this interface.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withAnnotationStrategy( @Nonnull final AnnotationStrategy annotationStrategy )
    {
        this.annotationStrategy = annotationStrategy;
        return this;
    }

    /**
     * Sets the class to use to provide generated Java classes with necessary annotations.
     *
     * @param annotationStrategyClassName
     *            Fully-qualified name of a class, which must either implement the {@link AnnotationStrategy
     *            AnnotationStrategy} interface, or extend a class which implements this interface.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withAnnotationStrategy( @Nonnull final String annotationStrategyClassName )
    {
        final AnnotationStrategy annotationStrategy =
            getStrategyInstanceFromName(annotationStrategyClassName, AnnotationStrategy.class);
        return withAnnotationStrategy(annotationStrategy);
    }

    /**
     * Defines whether to exit with failure in case a warning occurs during processing.
     *
     * @param failOnWarning
     *            Flag indicating whether to generate just the POJO classes.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator failOnWarning( final boolean failOnWarning )
    {
        this.failOnWarning = failOnWarning;
        return this;
    }

    /**
     * The generator exists with failure in case a warning occurs during processing.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator failOnWarning()
    {
        return failOnWarning(true);
    }

    /**
     * Defines whether to generate just the POJO classes (entities and complex types).
     *
     * @param pojosOnly
     *            Flag indicating whether to generate just the POJO classes.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator pojosOnly( final boolean pojosOnly )
    {
        this.generatePojosOnly = pojosOnly;
        return this;
    }

    /**
     * Activates POJO only generation, so just entity and complex type classes are generated.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator pojosOnly()
    {
        return pojosOnly(true);
    }

    /**
     * The generator generates service methods for each entity set of one entity type. If this flag is not used, the
     * generator generates service methods only for the first entity set of one entity type. All additional entity sets
     * for the entity type are disregarded.
     *
     * @param serviceMethodsPerEntitySet
     *            Flag indicating whether to generate service methods per entity set
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator serviceMethodsPerEntitySet( final boolean serviceMethodsPerEntitySet )
    {
        this.serviceMethodsPerEntitySet = serviceMethodsPerEntitySet;
        return this;
    }

    /**
     * The generator generates service methods for each entity set of one entity type.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator serviceMethodsPerEntitySet()
    {
        return serviceMethodsPerEntitySet(true);
    }

    /**
     * The ant style filename pattern for which VDM should not be generated.
     *
     * @param excludeFilePattern
     *            Provide the ant style pattern for filenames for which VDM should not be generated.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withExcludeFilePattern( @Nonnull final String excludeFilePattern )
    {
        this.excludeFilePattern = excludeFilePattern;
        return this;
    }

    /**
     * Defines whether to generate API reference URLs linking to the SAP Business Accelerator Hub.
     *
     * @param linkToApiBusinessHub
     *            Flag indicating whether to generate URLs.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator linkToApiBusinessHub( final boolean linkToApiBusinessHub )
    {
        this.generateLinksToApiBusinessHub = linkToApiBusinessHub;
        return this;
    }

    /**
     * Activates generation of API reference URLs linking to the SAP Business Accelerator Hub.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator linkToApiBusinessHub()
    {
        return linkToApiBusinessHub(true);
    }

    /**
     * Defines whether to generate the version reference of the used OData generator plugin.
     *
     * @param versionReference
     *            Flag indicating whether to generate the comments.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator versionReference( final boolean versionReference )
    {
        this.generateVersionReference = versionReference;
        return this;
    }

    /**
     * Generate comments, which indicate the version of the used OData VDM generator plugin.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator versionReference()
    {
        return versionReference(true);
    }

    /**
     * Defines a copyright header to be placed at the beginning of generated files. Use an empty string if no header
     * needs to be set. For setting SAP copyright headers {@see #defaultSAPCopyrightHeaders}.
     *
     * @param copyrightHeader
     *            The copyright header to add to generated files.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator copyrightHeader( @Nonnull final String copyrightHeader )
    {
        this.copyrightHeader = copyrightHeader;
        return this;
    }

    /**
     * Generate SAP copyright headers at the top of generated files.
     *
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator sapCopyrightHeader()
    {
        return copyrightHeader(SAP_COPYRIGHT_HEADER);
    }

    /**
     * Restricts the generated entity classes to the provided values, plus any dependent complex types. For navigation
     * properties, the associated entity set names must be included in order to be generated.
     *
     * @param includedEntitySets
     *            List of entity set names to generate classes for. These names must exist in the provided EDMX files in
     *            order to be recognized. If a null value is provided, then all entity sets will be generated.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withIncludedEntitySets( @Nullable final Set<String> includedEntitySets )
    {
        this.includedEntitySets = includedEntitySets;
        return this;
    }

    /**
     * Restricts the generated function imports to the provided values, plus any dependent complex types. If the return
     * type is an entity, then make sure to include its entity set name using {@link #withIncludedEntitySets(Set)} (or
     * generate all entity sets). Otherwise the function import code cannot be generated.
     *
     * @param includedFunctionImports
     *            List of function import names to generate code for. These names must exist in the provided EDMX files
     *            in order to be recognized. If a null value is provided, then all function imports will be generated.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withIncludedFunctionImports( @Nullable final Set<String> includedFunctionImports )
    {
        this.includedFunctionImports = includedFunctionImports;
        return this;
    }

    /**
     * Restricts the generated action imports to the provided values, plus any dependent complex types. If the return
     * type is an entity, then make sure to include its entity set name using {@link #withIncludedEntitySets(Set)} (or
     * generate all entity sets). Otherwise the action import code cannot be generated.
     *
     * @param includedActionImports
     *            List of action import names to generate code for. These names must exist in the provided EDMX files in
     *            order to be recognized. If a null value is provided, then all action imports will be generated.
     * @return This {@code DataModelGenerator} for chained method calls.
     */
    @Nonnull
    public DataModelGenerator withIncludedActionImports( @Nullable final Set<String> includedActionImports )
    {
        this.includedActionImports = includedActionImports;
        return this;
    }

    /**
     * Generates deprecation notices for generated service,and it's implementation class
     *
     * @param deprecationNotice
     *            The custom deprecation notice to be added
     * @return This {@code DataModelGenerator} for chained method calls
     */
    @Nonnull
    public DataModelGenerator withDeprecationNotice( @Nonnull final String deprecationNotice )
    {
        this.deprecationNotice = deprecationNotice;
        return this;
    }

    @Nonnull
    @Override
    public NamingStrategy getNamingStrategy()
    {
        namingStrategy.setNameSource(getNameSource());
        return namingStrategy;
    }

    /**
     * Executes the actual generator based on the given parameter or, in case of absence, their default values.
     */
    public void execute()
    {
        printExecuteInformation();
        new ODataToVdmGenerator().generate(this);
    }

    /**
     * Prints all information gathered in this builder.
     */
    protected void printExecuteInformation()
    {
        if( logger.isInfoEnabled() ) {
            logger
                .info(
                    String
                        .format("Generating %s with parameters:", isGeneratePojosOnly() ? "POJOS ONLY" : "EVERYTHING"));

            final String defaultBasePath = getDefaultBasePath();

            logger.info("  Input directory:                " + getInputDirectory().getAbsolutePath());
            logger.info("  Output directory:               " + getOutputDirectory().getAbsolutePath());
            logger.info("  Delete output directory:        " + isDeleteTargetDirectory());
            logger.info("  Overwrite files:                " + isForceFileOverride());
            logger.info("  Package name:                   " + getPackageName());
            logger.info("  Default base path:              " + (defaultBasePath != null ? defaultBasePath : "<none>"));
            logger.info("  Service name mapping file:      " + getServiceNameMappings().getAbsolutePath());
            logger.info("  Naming strategy class:          " + getNamingStrategy().getClass().getName());
            logger.info("  Name source (default strategy): " + getNameSource());
            logger.info("  Annotation strategy class:      " + getAnnotationStrategy().getClass().getName());
            logger.info("  Pattern for excluded files:     " + getExcludeFilePattern());
            logger.info("  SAP Business Accelerator Hub:   " + isGenerateLinksToApiBusinessHub());
            logger.info("  Fail on Warning:                " + isFailOnWarning());
            logger
                .info(
                    "  Entity sets to process:         "
                        + (includedEntitySets != null && !includedEntitySets.isEmpty()
                            ? String.join(", ", includedEntitySets)
                            : "<all>"));
            logger
                .info(
                    "  Function imports to process:    "
                        + (includedFunctionImports != null && !includedFunctionImports.isEmpty()
                            ? String.join(", ", includedFunctionImports)
                            : "<all>"));
        }
    }

    @SuppressWarnings( "unchecked" )
    private <T> T getStrategyInstanceFromName( final String fullyQualifiedClassName, final Class<T> expectedInterface )
    {
        final T strategyInstance;
        try {
            strategyInstance = (T) Class.forName(fullyQualifiedClassName).getDeclaredConstructor().newInstance();
        }
        catch( final
            InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e ) {
            throw new ODataGeneratorException(
                "Unable to load specified strategy class. "
                    + "Check the class for errors in its constructor(s) "
                    + "and ensure that security permissions are configured to allow access to the class",
                e);
        }
        catch( final ClassNotFoundException e ) {
            throw new ODataGeneratorException(
                "Unable to find specified strategy class. "
                    + "Check that the class is included (directly or via a JAR file) "
                    + "in your classpath list when running Java. "
                    + "Also make sure to enter the fully-qualified class name "
                    + "(e.g. com.myorg.mypath.MyStrategy)",
                e);
        }
        catch( final ClassCastException e ) {
            throw new ODataGeneratorException(
                String
                    .format(
                        "Unable to load specified strategy class. "
                            + "Check that the class either implements the %s interface, "
                            + "or extends a class which implements this interface.",
                        expectedInterface.getName()),
                e);
        }
        return strategyInstance;
    }

    /**
     * Get the failure indicator due to warning messages.
     *
     * @return true if there where warning messages and the flag to fail was set.
     */
    public boolean failureDueToWarningsNecessary()
    {
        return !MessageCollector.getWarningMessages().isEmpty() && failOnWarning;
    }
}
