/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.utility.NameSource;
import com.sap.cloud.sdk.datamodel.odata.utility.NamingStrategy;
import com.sap.cloud.sdk.datamodel.odata.utility.S4HanaNamingStrategy;
import com.sap.cloud.sdk.datamodel.odatav4.generator.annotation.AnnotationStrategy;

interface DataModelGeneratorConfig
{
    /**
     * Getter for the flag indicating that the {@code #getOutputDirectory()} should be cleared before generating new
     * files.
     *
     * @return true, if the {@code #getOutputDirectory()} should be cleared; false otherwise.
     */
    boolean isDeleteTargetDirectory();

    /**
     * Getter for the flag indicating that already existing files in the {@code #getOutputDirectory()} can be
     * overridden.
     *
     * @return true, if existing files in {@code #getOutputDirectory()} can be overridden; false otherwise.
     */
    boolean isForceFileOverride();

    /**
     * Getter for the flag indicating whether to generate just the POJO classes.
     *
     * @return true, if only the POJO classes should be generated; false otherwise.
     */
    boolean isGeneratePojosOnly();

    /**
     * Getter for the flag indicating whether service methods are generated per entity set.
     *
     * @return true, if service methods are generated per entity set; false if service methods are generated for the
     *         first entity set of one entity type only
     */
    boolean isServiceMethodsPerEntitySet();

    /**
     * Getter for the flag indicating whether to generate API reference URLs linking to the SAP Business Accelerator
     * Hub.
     *
     * @return true, if URLs should be generated; false otherwise.
     */
    boolean isGenerateLinksToApiBusinessHub();

    /**
     * Getter for the flag indicating whether to generate comments referencing the used OData code generator version.
     *
     * @return true, if the comments should be generated; false otherwise.
     */
    boolean isGenerateVersionReference();

    /**
     * Getter for the flag indicating that existing signatures from already generated classes in the
     * {@code #getOutputDirectory()} will be considered when generating again. When active, breaking changes in method
     * signature can be avoided, when the argument order in OData specification was altered.
     *
     * @return true, if the existing signatures will be considered; false otherwise.
     */
    boolean isKeepExistingSignatures();

    /**
     * Getter for the directory containing the metadata and swagger files.
     *
     * @return The input directory of the generator.
     */
    @Nonnull
    File getInputDirectory();

    /**
     * Getter for the directory the generated files should be written into.
     * <p>
     * This is also the directory which gets cleared if the {@link #isDeleteTargetDirectory()} is set.
     *
     * @return The output directory of the generator.
     */
    @Nonnull
    File getOutputDirectory();

    /**
     * Getter for the file containing the custom service names.
     * <p>
     * If this file does not exist it will be created.
     *
     * @return The service name mapping file.
     */
    @Nonnull
    File getServiceNameMappings();

    /**
     * Getter for the prefix of the java package the generated files should be located in.
     * <p>
     * This also builds a directory structure inside the {@link #getOutputDirectory()}.
     *
     * @return The package prefix of the generated files.
     */
    @Nonnull
    String getPackageName();

    /**
     * Getter for the default base path.
     * <p>
     * If no swagger file is given, this base path, together with the metadata file name, constitute the service path
     * with the following pattern: &lt;defaultBasePath&gt; + &lt;metadataFileNameWithoutExtension&gt;.
     *
     * @return The default base path.
     */
    @Nullable
    String getDefaultBasePath();

    /**
     * Getter for the given {@link NameSource}.
     *
     * @return The specified {@code NameSource} enum to be used by the {@link NamingStrategy}.
     */
    @Nonnull
    NameSource getNameSource();

    /**
     * Getter for the class which provides generated Java classes with necessary annotations.
     * <p>
     * Defaults to the DefaultAnnotationStrategy.
     *
     * @return Class instance which provides generated Java classes with necessary annotations.
     */
    @Nonnull
    AnnotationStrategy getAnnotationStrategy();

    /**
     * The ant style filename pattern for which VDM should not be generated.
     *
     * @return The ant style exclusion pattern.
     */
    @Nonnull
    String getExcludeFilePattern();

    /**
     * Getter for the copyright header to be added to generated files.
     *
     * @return a string representing the header or an empty string, if no header is to be set.
     */
    @Nonnull
    String getCopyrightHeader();

    /**
     * Getter for the class which converts OData names to Java names.
     * <p>
     * Defaults to the {@link S4HanaNamingStrategy DefaultNamingStrategy} class.
     *
     * @return Class instance which converts OData names to Java names.
     */
    @Nonnull
    NamingStrategy getNamingStrategy();

    /**
     * Getter for the list of entity sets to process.
     *
     * @return List of entity set names defined in the EDMX.
     */
    @Nullable
    Set<String> getIncludedEntitySets();

    /**
     * Getter for the list of function imports to process.
     *
     * @return List of function import names defined in the EDMX.
     */
    @Nullable
    Set<String> getIncludedFunctionImports();

    /**
     * Getter for the list of action imports to process.
     *
     * @return List of action import names defined in the EDMX.
     */
    Set<String> getIncludedActionImports();

    /**
     * Getter for the deprecation notice to be added to generated service,and it's implementation class
     *
     * @return a string representing the deprecation notice to be added
     */
    @Nullable
    String getDeprecationNotice();
}
