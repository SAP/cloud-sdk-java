package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.config.GlobalSettings;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Converts a {@link GenerationConfiguration} instance to a {@link ClientOptInput} instance which the OpenAPI Generator
 * expects
 */
@Slf4j
class GenerationConfigurationConverter
{
    private static final String GENERATOR_NAME = "java";
    private static final String IS_RELEASED_PROPERTY_KEY = "isReleased";
    private static final String JAVA_8_PROPERTY_KEY = "java8";
    private static final String DATE_LIBRARY_PROPERTY_KEY = "dateLibrary";
    private static final String BOOLEAN_GETTER_PREFIX_PROPERTY_KEY = "booleanGetterPrefix";
    private static final String SOURCE_FOLDER_PROPERTY_KEY = "sourceFolder";
    private static final String OPEN_API_NULLABLE_PROPERTY_KEY = "openApiNullable";
    static final String COPYRIGHT_PROPERTY_KEY = "copyrightHeader";

    static final String SAP_COPYRIGHT_HEADER =
        "Copyright (c) " + Year.now() + " SAP SE or an SAP affiliate company. All rights reserved.";
    static final String TEMPLATE_DIRECTORY = Paths.get("openapi-generator").resolve("mustache-templates").toString();
    static final String LIBRARY_NAME = "resttemplate";

    @Nonnull
    static ClientOptInput convertGenerationConfiguration(
        @Nonnull final GenerationConfiguration generationConfiguration,
        @Nonnull final Path inputSpec )
    {
        final CodegenConfigurator config = new CodegenConfigurator();

        config.setVerbose(generationConfiguration.isVerbose());

        config.setInputSpec(inputSpec.toString());

        config.setGeneratorName(GENERATOR_NAME);

        config.setOutputDir(generationConfiguration.getOutputDirectory());

        config.setTemplateDir(TEMPLATE_DIRECTORY);

        setAdditionalProperties(generationConfiguration, config);

        config.setGenerateAliasAsModel(false);
        config.setRemoveOperationIdPrefix(true);

        config.setLibrary(LIBRARY_NAME);

        config.setApiPackage(generationConfiguration.getApiPackage());
        config.setModelPackage(generationConfiguration.getModelPackage());

        setGlobalSettings();

        return config.toClientOptInput();
    }

    private static void setGlobalSettings()
    {
        GlobalSettings.setProperty(CodegenConstants.APIS, "");
        GlobalSettings.setProperty(CodegenConstants.MODELS, "");
        GlobalSettings.setProperty(CodegenConstants.MODEL_TESTS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.MODEL_DOCS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_TESTS, Boolean.FALSE.toString());
        GlobalSettings.setProperty(CodegenConstants.API_DOCS, Boolean.FALSE.toString());
        GlobalSettings.clearProperty(CodegenConstants.SUPPORTING_FILES);
        GlobalSettings.setProperty(CodegenConstants.HIDE_GENERATION_TIMESTAMP, Boolean.TRUE.toString());
    }

    private static void setAdditionalProperties(
        final GenerationConfiguration generationConfiguration,
        final CodegenConfigurator config )
    {
        log.info("Using {} as {}.", ApiMaturity.class.getSimpleName(), generationConfiguration.getApiMaturity());

        final Map<String, Object> additionalProperties = new HashMap<>();

        additionalProperties.put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, Boolean.TRUE.toString());

        switch( generationConfiguration.getApiMaturity() ) {
            case RELEASED: {
                additionalProperties.put(IS_RELEASED_PROPERTY_KEY, true);
                break;
            }

            case BETA: {
                additionalProperties.remove(IS_RELEASED_PROPERTY_KEY);
                break;
            }
        }

        final String copyrightHeader =
            generationConfiguration.useSapCopyrightHeader()
                ? SAP_COPYRIGHT_HEADER
                : generationConfiguration.getCopyrightHeader();

        if( !Strings.isNullOrEmpty(copyrightHeader) ) {
            additionalProperties.put(COPYRIGHT_PROPERTY_KEY, copyrightHeader);
        }

        additionalProperties.put(CodegenConstants.SERIALIZABLE_MODEL, "true");
        additionalProperties.put(JAVA_8_PROPERTY_KEY, "true");
        additionalProperties.put(DATE_LIBRARY_PROPERTY_KEY, "java8");
        additionalProperties.put(BOOLEAN_GETTER_PREFIX_PROPERTY_KEY, "is");
        additionalProperties.put(SOURCE_FOLDER_PROPERTY_KEY, "");
        // this is set to false, to prevent issues with the JsonNullable annotation
        // long term fix part of BLI CLOUDECOSYSTEM-9843
        additionalProperties.put(OPEN_API_NULLABLE_PROPERTY_KEY, "false");

        // this allows the customer to override the default Cloud SDK settings above
        generationConfiguration.getAdditionalProperties().forEach(( k, v ) -> {
            if( additionalProperties.containsKey(k) ) {
                log
                    .info(
                        "Replacing default value \"{}\" for additional property \"{}\" with \"{}\" from user provided configuration.",
                        additionalProperties.get(k),
                        k,
                        v);
            }
            additionalProperties.put(k, v);
        });
        config.setAdditionalProperties(additionalProperties);
    }
}
