package com.sap.cloud.sdk.datamodel.openapi.generator;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.core.models.AuthorizationValue;
import io.swagger.v3.parser.core.models.ParseOptions;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_FLOAT_ARRAYS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_ONE_OF_CREATORS;

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
    static final String LIBRARY_NAME = JavaClientCodegen.RESTTEMPLATE;

    @Nonnull
    static ClientOptInput convertGenerationConfiguration(
        @Nonnull final GenerationConfiguration generationConfiguration,
        @Nonnull final Path inputSpec )
    {
        setGlobalSettings(generationConfiguration);
        final var inputSpecFile = inputSpec.toString();

        final var config = createCodegenConfig(generationConfiguration);
        config.setOutputDir(generationConfiguration.getOutputDirectory());
        config.setLibrary(LIBRARY_NAME);
        config.setApiPackage(generationConfiguration.getApiPackage());
        config.setModelPackage(generationConfiguration.getModelPackage());
        config.setTemplateDir(TEMPLATE_DIRECTORY);
        config.additionalProperties().putAll(getAdditionalProperties(generationConfiguration));

        final var clientOptInput = new ClientOptInput();
        clientOptInput.config(config);
        clientOptInput.openAPI(parseOpenApiSpec(inputSpecFile));
        return clientOptInput;
    }

    private static JavaClientCodegen createCodegenConfig( @Nonnull final GenerationConfiguration config )
    {
        final var primitives = Set.of("String", "Integer", "Long", "Double", "Float", "Byte");
        return new JavaClientCodegen()
        {
            @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
            @Override
            protected void updatePropertyForArray(
                @Nonnull final CodegenProperty property,
                @Nonnull final CodegenProperty innerProperty )
            {
                super.updatePropertyForArray(property, innerProperty);

                if( USE_FLOAT_ARRAYS.isEnabled(config) && innerProperty.isNumber && property.isArray ) {
                    property.dataType = "float[]";
                    property.datatypeWithEnum = "float[]";
                    property.isArray = false; // set false to omit `add{{nameInPascalCase}}Item(...)` convenience method
                    //property.isByteArray = true;
                    property.vendorExtensions.put("isPrimitiveArray", true);
                }
            }

            @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
            @Override
            @Nullable
            public String toDefaultValue( @Nonnull final CodegenProperty cp, @Nonnull final Schema schema )
            {
                if( USE_FLOAT_ARRAYS.isEnabled(config) && "float[]".equals(cp.dataType) ) {
                    return null;
                }
                return super.toDefaultValue(cp, schema);
            }

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

            @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
            @Override
            protected void updateModelForComposedSchema(
                @Nonnull final CodegenModel m,
                @Nonnull final Schema schema,
                @Nonnull final Map<String, Schema> allDefinitions )
            {
                super.updateModelForComposedSchema(m, schema, allDefinitions);

                if( USE_ONE_OF_CREATORS.isEnabled(config) ) {
                    useCreatorsForInterfaceSubtypes(m);
                }
            }

            /**
             * Use JsonCreator for interface sub-types in case there are any primitives.
             *
             * @param m
             *            The model to update.
             */
            private void useCreatorsForInterfaceSubtypes( @Nonnull final CodegenModel m )
            {
                if( m.discriminator != null ) {
                    return;
                }
                boolean useCreators = false;
                for( final Set<String> candidates : List.of(m.anyOf, m.oneOf) ) {
                    int nonPrimitives = 0;
                    final var candidatesSingle = new HashSet<String>();
                    final var candidatesMultiple = new HashSet<String>();

                    for( final String candidate : candidates ) {
                        if( candidate.startsWith("List<") ) {
                            final var c1 = candidate.substring(5, candidate.length() - 1);
                            candidatesMultiple.add(c1);
                            useCreators = true;
                        } else {
                            candidatesSingle.add(candidate);
                            useCreators |= primitives.contains(candidate);
                            if( !primitives.contains(candidate) ) {
                                nonPrimitives++;
                            }
                        }
                    }
                    if( useCreators ) {
                        if( nonPrimitives > 1 ) {
                            final var msg =
                                "Generating interface with mixed multiple non-primitive and primitive sub-types: {}. Deserialization may not work.";
                            log.warn(msg, m.name);
                        }
                        candidates.clear();
                        final var monads = Map.of("single", candidatesSingle, "multiple", candidatesMultiple);
                        m.vendorExtensions.put("x-monads", monads);
                        m.vendorExtensions.put("x-is-one-of-interface", true); // enforce template usage
                    }
                }
            }

            @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
            @Override
            protected void updateModelForObject( @Nonnull final CodegenModel m, @Nonnull final Schema schema )
            {
                // Disable additional attributes to prevent model classes from extending "HashMap"
                // SAP Cloud SDK offers custom field APIs to handle additional attributes already
                schema.setAdditionalProperties(Boolean.FALSE);
                super.updateModelForObject(m, schema);
            }
        };
    }

    private static void setGlobalSettings( @Nonnull final GenerationConfiguration configuration )
    {
        if( configuration.isGenerateApis() ) {
            GlobalSettings.setProperty(CodegenConstants.APIS, "");
        }
        if( configuration.isGenerateModels() ) {
            GlobalSettings.setProperty(CodegenConstants.MODELS, "");
        }
        if( configuration.isDebugModels() ) {
            GlobalSettings.setProperty("debugModels", "true");
        }
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
        result.put(CodegenConstants.SERIALIZABLE_MODEL, "false");
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
