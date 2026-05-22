package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_RESPONSE_SCHEMA_TITLES;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.JavaClientCodegen;

import com.google.common.base.Strings;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
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
    static final String LIBRARY_NAME = JavaClientCodegen.RESTTEMPLATE;
    static final String SUPPORT_URL_QUERY = "supportUrlQuery";

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
        config.typeMapping().putAll(generationConfiguration.getTypeMappings());
        config.importMapping().putAll(generationConfiguration.getImportMappings());

        final var openAPI = parseOpenApiSpec(inputSpecFile, generationConfiguration);

        final var clientOptInput = new ClientOptInput();
        clientOptInput.config(config);
        clientOptInput.openAPI(openAPI);
        return clientOptInput;
    }

    private static JavaClientCodegen createCodegenConfig( @Nonnull final GenerationConfiguration config )
    {
        return new CustomJavaClientCodegen(config);
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

    @Nonnull
    private static
        OpenAPI
        parseOpenApiSpec( @Nonnull final String inputSpecFile, @Nonnull final GenerationConfiguration config )
    {
        final var authorizationValues = List.<AuthorizationValue> of();
        final var options = new ParseOptions();
        options.setResolve(true);
        final var spec = new OpenAPIParser().readLocation(inputSpecFile, authorizationValues, options);
        if( !spec.getMessages().isEmpty() ) {
            log.warn("Parsing the specification yielded the following messages: {}", spec.getMessages());
        }
        final var result = spec.getOpenAPI();
        preprocessSpecification(result, config);
        return result;
    }

    /**
     * Preprocesses the OpenAPI specification to ensure that all inline schemas in "//components/responses" have a
     * title. This does not affect regular schema definitions in "//components/schemas"! Without this fix, the OpenAPI
     * Generator will generate classes with name format "InlineObject\d*" with high chance of naming conflicts.
     *
     * @param openAPI
     *            the OpenAPI specification to preprocess
     * @param config
     *            the generation configuration to extract feature toggles from
     */
    private static
        void
        preprocessSpecification( @Nonnull final OpenAPI openAPI, @Nonnull final GenerationConfiguration config )
    {
        // Simplify oneOf/anyOf schemas that have only a single option
        //simplifyComposedSchemas(openAPI);

        if( !FIX_RESPONSE_SCHEMA_TITLES.isEnabled(config) ) {
            return;
        }
        final Components components = openAPI.getComponents();
        if( components == null ) {
            return;
        }
        final Map<String, ApiResponse> responses = components.getResponses();
        if( responses == null ) {
            return;
        }
        responses.forEach(( key, value ) -> {
            final Content mediaContent = value.getContent();
            if( mediaContent == null ) {
                return;
            }
            mediaContent.forEach(( mediaType, content ) -> {
                final Schema<?> schema = content.getSchema();
                if( schema != null && schema.getTitle() == null ) {
                    schema.setTitle(key + " " + (mediaContent.size() > 1 ? mediaType : ""));
                }
            });
        });
    }

    /**
     * Simplifies oneOf/anyOf schemas that have only a single option by removing the composition constraint and
     * flattening to the referenced schema. This prevents the creation of unnecessary wrapper classes.
     *
     * @param openAPI
     *            the OpenAPI specification to preprocess
     */
    @SuppressWarnings( "rawtypes" )
    private static void simplifyComposedSchemas( @Nonnull final OpenAPI openAPI )
    {
        final Components components = openAPI.getComponents();
        if( components == null || components.getSchemas() == null ) {
            return;
        }

        final Map<String, Schema> schemas = components.getSchemas();
        final Map<String, String> schemaReplacements = identifyWrapperSchemas(schemas);

        if( !schemaReplacements.isEmpty() ) {
            replaceSchemaReferences(openAPI, schemaReplacements);
            schemaReplacements.keySet().forEach(schemas::remove);
        }

        // Simplify remaining composed schemas
        new HashMap<>(schemas).forEach(( name, schema ) -> simplifyComposedSchema(schema));
    }

    /**
     * Identifies wrapper schemas that have only oneOf/anyOf with a single reference.
     */
    @SuppressWarnings( { "rawtypes"} )
    private static Map<String, String> identifyWrapperSchemas( @Nonnull final Map<String, Schema> schemas )
    {
        final Map<String, String> replacements = new HashMap<>();

        for( final Map.Entry<String, Schema> entry : schemas.entrySet() ) {
            final Schema schema = entry.getValue();
            final Schema referencedSchema = extractSingleComposedOption(schema);

            if( referencedSchema != null && referencedSchema.get$ref() != null ) {
                final String refName = extractRefName(referencedSchema.get$ref());
                replacements.put(entry.getKey(), refName);
                log.error("Identified wrapper schema {} that should be replaced with {}", entry.getKey(), refName);
            }
        }

        return replacements;
    }

    /**
     * Extracts the single referenced schema from oneOf or anyOf, if it's the only composition option.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    @Nullable
    private static Schema extractSingleComposedOption( @Nonnull final Schema schema )
    {
        if( schema.getEnum() != null ) {
            return null;
        }

        if( isSimpleComposition(schema.getOneOf(), schema.getAnyOf(), schema.getAllOf(), schema.getProperties()) ) {
            if( schema.getOneOf() != null && schema.getOneOf().size() == 1 ) {
                return (Schema) schema.getOneOf().get(0);
            }
            if( schema.getAnyOf() != null && schema.getAnyOf().size() == 1 ) {
                return (Schema) schema.getAnyOf().get(0);
            }
        }
        return null;
    }

    /**
     * Checks if a schema has only a single composition type with no other composition or properties.
     */
    private static boolean isSimpleComposition(
        @Nullable final java.util.List<?> oneOf,
        @Nullable final java.util.List<?> anyOf,
        @Nullable final java.util.List<?> allOf,
        @Nullable final Map<String, ?> properties )
    {
        final boolean hasOneOf = oneOf != null && oneOf.size() == 1;
        final boolean hasAnyOf = anyOf != null && anyOf.size() == 1;

        // Ensure only one composition type is present, and no properties
        return ((hasOneOf ^ hasAnyOf) && allOf == null && properties == null);
    }

    /**
     * Extracts the schema name from a $ref string like "#/components/schemas/SchemaName".
     */
    private static String extractRefName( @Nonnull final String ref )
    {
        return ref.substring(ref.lastIndexOf('/') + 1);
    }

    /**
     * Replaces all references to wrapper schemas with the actual referenced schema throughout the OpenAPI spec.
     */
    private static
        void
        replaceSchemaReferences( @Nonnull final OpenAPI openAPI, @Nonnull final Map<String, String> replacements )
    {
        final Components components = openAPI.getComponents();
        if( components != null && components.getParameters() != null ) {
            components.getParameters().values().forEach(param -> replaceSchemaRef(param.getSchema(), replacements));
        }

        if( openAPI.getPaths() != null ) {
            openAPI.getPaths().values().forEach(pathItem -> pathItem.readOperationsMap().values().forEach(operation -> {
                if( operation.getParameters() != null ) {
                    operation.getParameters().forEach(param -> replaceSchemaRef(param.getSchema(), replacements));
                }
            }));
        }
    }

    /**
     * Replaces a schema reference if it matches one of the wrapper schemas.
     */
    @SuppressWarnings( "rawtypes" )
    private static
        void
        replaceSchemaRef( @Nullable final Schema schema, @Nonnull final Map<String, String> replacements )
    {
        if( schema != null && schema.get$ref() != null ) {
            final String refName = extractRefName(schema.get$ref());
            final String newRefName = replacements.get(refName);
            if( newRefName != null ) {
                schema.set$ref("#/components/schemas/" + newRefName);
                log.error("Replaced schema reference {} with {}", refName, newRefName);
            }
        }
    }

    /**
     * Recursively simplifies a single schema by clearing oneOf/anyOf constraints and processing nested schemas.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private static void simplifyComposedSchema( @Nullable final Schema schema )
    {
        if( schema == null ) {
            return;
        }

        // Clear single-option composition constraints
        clearSingleOptionComposition(schema);

        // Recursively simplify nested schemas
        if( schema.getProperties() != null ) {
            schema.getProperties().values().forEach(s -> simplifyComposedSchema((Schema) s));
        }
        if( schema.getItems() != null ) {
            simplifyComposedSchema(schema.getItems());
        }
        if( schema.getAdditionalProperties() instanceof Schema ) {
            simplifyComposedSchema((Schema) schema.getAdditionalProperties());
        }
        if( schema.getAllOf() != null ) {
            schema.getAllOf().forEach(s -> simplifyComposedSchema((Schema) s));
        }
    }

    /**
     * Clears oneOf/anyOf constraints when they have only a single option.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private static void clearSingleOptionComposition( @Nonnull final Schema schema )
    {
        if( schema.getOneOf() != null && schema.getOneOf().size() == 1 ) {
            schema.setOneOf(null);
        }
        if( schema.getAnyOf() != null && schema.getAnyOf().size() == 1 ) {
            schema.setAnyOf(null);
        }
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

        // Always disable supportUrlQuery as it's not compatible with interface generation
        result.put(SUPPORT_URL_QUERY, "false");

        return result;
    }
}
