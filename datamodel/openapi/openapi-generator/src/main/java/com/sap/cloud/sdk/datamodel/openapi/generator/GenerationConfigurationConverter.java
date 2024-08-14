/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Converts a {@link GenerationConfiguration} instance to a {@link ClientOptInput} instance which the OpenAPI Generator
 * expects
 */
@Slf4j
class GenerationConfigurationConverter
{
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
        setGlobalSettings();
        final var inputSpecFile = inputSpec.toString();

        final var config = createCodegenConfig();
        config.setOutputDir(generationConfiguration.getOutputDirectory());
        config.setLibrary(LIBRARY_NAME);
        config.setApiPackage(generationConfiguration.getApiPackage());
        config.setModelPackage(generationConfiguration.getModelPackage());
        config.setTemplateDir(TEMPLATE_DIRECTORY);
        config.additionalProperties().putAll(getAdditionalProperties(generationConfiguration));

        final var operationIdNameMapping = getOperationIdNameMapping(generationConfiguration);
        config.operationIdNameMapping().putAll(operationIdNameMapping);

        final var clientOptInput = new ClientOptInput();
        clientOptInput.config(config);
        clientOptInput.openAPI(parseOpenApiSpec(inputSpecFile));
        return clientOptInput;
    }

    private static JavaClientCodegen createCodegenConfig()
    {
        return new JavaClientCodegen()
        {
            // Custom processor to inject "x-return-nullable" extension
            @Override
            @Nonnull
            public OperationsMap postProcessOperationsWithModels(
                @Nonnull final OperationsMap ops,
                @Nonnull final List<ModelMap> allModels )
            {
                for( final CodegenOperation op : ops.getOperations().getOperation() ) {
                    final var noContent =
                        op.isResponseOptional
                            || op.responses == null
                            || op.responses.stream().anyMatch(r -> "204".equals(r.code));
                    op.vendorExtensions.put("x-return-nullable", op.returnType != null && noContent);
                }
                return super.postProcessOperationsWithModels(ops, allModels);
            }
        };
    }

    private static Map<String, String> getOperationIdNameMapping( @Nonnull final GenerationConfiguration config )
    {
        var allowIds = config.getAdditionalProperties().get("operationIdNames");
        if( allowIds == null || allowIds.isBlank() ) {
            return Collections.emptyMap();
        }

        // sanitized
        allowIds = String.join(",", allowIds.trim().replaceAll(",?\\s+,?", ","));

        // split to map, throw exception for duplicate keys or invalid strings
        return Splitter.on(",").withKeyValueSeparator("=").split(allowIds);
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

    private static OpenAPI parseOpenApiSpec( @Nonnull final String inputSpecFile )
    {
        final List<AuthorizationValue> authorizationValues = List.of();
        final var options = new ParseOptions();
        options.setResolve(true);
        final var spec = new OpenAPIParser().readLocation(inputSpecFile, authorizationValues, options);
        if( !spec.getMessages().isEmpty() ) {
            log.warn("Parsing the specification yielded the following messages: {}", spec.getMessages());
        }
        return spec.getOpenAPI();
    }

    private static Map<String, Object> getAdditionalProperties( @Nonnull final GenerationConfiguration config )
    {
        log.info("Using {} as {}.", ApiMaturity.class.getSimpleName(), config.getApiMaturity());

        final Map<String, Object> result = new HashMap<>();
        result.put(CodegenConstants.HIDE_GENERATION_TIMESTAMP, Boolean.TRUE.toString());

        switch( config.getApiMaturity() ) {
            case RELEASED -> result.put(IS_RELEASED_PROPERTY_KEY, true);
            case BETA -> result.remove(IS_RELEASED_PROPERTY_KEY);
        }

        final var copyrightHeader = config.useSapCopyrightHeader() ? SAP_COPYRIGHT_HEADER : config.getCopyrightHeader();
        if( !Strings.isNullOrEmpty(copyrightHeader) ) {
            result.put(COPYRIGHT_PROPERTY_KEY, copyrightHeader);
        }
        result.put(CodegenConstants.SERIALIZABLE_MODEL, "true");
        result.put(JAVA_8_PROPERTY_KEY, "true");
        result.put(DATE_LIBRARY_PROPERTY_KEY, "java8");
        result.put(BOOLEAN_GETTER_PREFIX_PROPERTY_KEY, "is");
        result.put(SOURCE_FOLDER_PROPERTY_KEY, "");
        // this is set to false, to prevent issues with the JsonNullable annotation
        // long term fix part of BLI CLOUDECOSYSTEM-9843
        result.put(OPEN_API_NULLABLE_PROPERTY_KEY, "false");

        // this allows the customer to override the default Cloud SDK settings above
        config.getAdditionalProperties().forEach(( k, v ) -> {
            if( result.containsKey(k) ) {
                final var msg =
                    "Replacing default value \"{}\" for additional property \"{}\" with \"{}\" from user provided configuration.";
                log.info(msg, result.get(k), k, v);
            }
            result.put(k, v);
        });
        return result;
    }
}
