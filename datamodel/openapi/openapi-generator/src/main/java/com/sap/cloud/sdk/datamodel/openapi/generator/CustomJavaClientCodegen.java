package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_REDUNDANT_IS_BOOLEAN_PREFIX;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_REMOVE_UNUSED_COMPONENTS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_EXCLUDE_PATHS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_EXCLUDE_PROPERTIES;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_FLOAT_ARRAYS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_ONE_OF_CREATORS;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.ModelsMap;
import org.openapitools.codegen.model.OperationsMap;
import org.openapitools.codegen.utils.ModelUtils;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings( "PMD.TooManyStaticImports" )
@Slf4j
class CustomJavaClientCodegen extends JavaClientCodegen
{
    private final GenerationConfiguration config;
    private static final Predicate<String> DOUBLE_IS_PATTERN = Pattern.compile("^isIs[A-Z]").asPredicate();
    private static final String GENERATE_ALIAS_AS_MODEL = "x-generate-alias-as-model";
    private final Set<String> originalSchemaNames = new HashSet<>();
    // schemaName -> (propertyName -> sibling description) captured before normalization strips $ref context
    private final Map<String, Map<String, String>> siblingDescriptions = new HashMap<>();

    public CustomJavaClientCodegen( @Nonnull final GenerationConfiguration config )
    {
        this.config = config;
    }

    @Override
    public void setOpenAPI( @Nonnull final OpenAPI openAPI )
    {
        if( this.openAPI != openAPI ) {
            originalSchemaNames.clear();
            originalSchemaNames.addAll(ModelUtils.getSchemas(openAPI).keySet());
        }
        super.setOpenAPI(openAPI);
    }

    @Override
    public void preprocessOpenAPI( @Nonnull final OpenAPI openAPI )
    {
        // Capture sibling descriptions on $ref property schemas before normalization resolves them away.
        captureSiblingDescriptions(openAPI);

        if( USE_EXCLUDE_PROPERTIES.isEnabled(config) ) {
            final String[] exclusions = USE_EXCLUDE_PROPERTIES.getValue(config).trim().split("[,\\s]+");
            for( final String exclusion : exclusions ) {
                final String[] split = exclusion.split("\\.", 2);
                preprocessRemoveProperty(openAPI, split[0], split[1]);
            }
        }

        // OAS 3.1 documents may have no paths (webhooks-only or components-only).
        if( USE_EXCLUDE_PATHS.isEnabled(config) ) {
            final String[] exclusions = USE_EXCLUDE_PATHS.getValue(config).trim().split("[,\\s]+");
            if( openAPI.getPaths() != null ) {
                for( final String exclusion : exclusions ) {
                    openAPI.getPaths().remove(exclusion);
                }
            }
        }

        if( useOneOfInterfaces ) {
            // A raw map alias cannot implement the generated union interface.
            preserveFreeFormUnionModels(openAPI);
        }

        resolveInlineModelNameCollisions(openAPI);
        super.preprocessOpenAPI(openAPI);

        if( FIX_REMOVE_UNUSED_COMPONENTS.isEnabled(config) ) {
            preprocessRemoveRedundancies(openAPI);
        }
    }

    private void resolveInlineModelNameCollisions( final OpenAPI openAPI )
    {
        final var schemas = ModelUtils.getSchemas(openAPI);
        final Set<String> reservedNames =
            schemas
                .keySet()
                .stream()
                .map(this::toModelName)
                .map(n -> n.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        final Set<String> assignedNames =
            schemas
                .keySet()
                .stream()
                .filter(n -> originalSchemaNames.contains(n) || modelNameMapping.containsKey(n))
                .map(this::toModelName)
                .map(n -> n.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        for( final String name : schemas.keySet().stream().sorted().toList() ) {
            if( originalSchemaNames.contains(name) || modelNameMapping.containsKey(name) ) {
                continue;
            }
            final String modelName = toModelName(name);
            if( assignedNames.add(modelName.toLowerCase(Locale.ROOT)) ) {
                continue;
            }
            int suffix = 1;
            String uniqueName;
            do {
                uniqueName = toModelName(name + "_" + suffix++);
            } while( !reservedNames.add(uniqueName.toLowerCase(Locale.ROOT)) );
            assignedNames.add(uniqueName.toLowerCase(Locale.ROOT));
            modelNameMapping.put(name, uniqueName);
            log.warn("Renaming inline model '{}' to '{}' to avoid a Java model-name collision.", name, uniqueName);
        }
    }

    @Override
    @Nonnull
    public String modelFilename( @Nonnull final String templateName, @Nonnull final String modelName )
    {
        return modelFilename(templateName, modelName, modelFileFolder());
    }

    @Override
    @Nonnull
    public String modelFilename(
        @Nonnull final String templateName,
        @Nonnull final String modelName,
        @Nonnull final String outputDir )
    {
        if( modelNameMapping.containsKey(modelName) ) {
            // Upstream uniquifies schema keys, which would discard the collision-free Java name mapping.
            return outputDir + File.separator + toModelFilename(modelName) + modelTemplateFiles().get(templateName);
        }
        return super.modelFilename(templateName, modelName, outputDir);
    }

    @SuppressWarnings( "rawtypes" )
    private void preserveFreeFormUnionModels( final OpenAPI openAPI )
    {
        for( final Schema<?> schema : ModelUtils.getAllSchemas(openAPI) ) {
            for( final List<Schema> alternatives : Arrays.asList(schema.getOneOf(), schema.getAnyOf()) ) {
                if( alternatives == null ) {
                    continue;
                }
                for( final Schema<?> alternative : alternatives ) {
                    if( alternative.get$ref() == null || !alternative.get$ref().startsWith("#/components/schemas/") ) {
                        continue;
                    }
                    final String name = ModelUtils.getSimpleRef(alternative.get$ref());
                    if( schemaMapping.containsKey(name) || typeMapping.containsKey(name) ) {
                        continue;
                    }
                    final Schema<?> target = ModelUtils.getReferencedSchema(openAPI, alternative);
                    if( !ModelUtils.isFreeFormObject(target, openAPI) ) {
                        continue;
                    }
                    if( target.getExtensions() != null
                        && target.getExtensions().containsKey(GENERATE_ALIAS_AS_MODEL)
                        && !Boolean.TRUE.equals(target.getExtensions().get(GENERATE_ALIAS_AS_MODEL)) ) {
                        throw new OpenApiGeneratorException(
                            "Free-form union member '" + name + "' requires " + GENERATE_ALIAS_AS_MODEL + ": true.");
                    }
                    target.addExtension(GENERATE_ALIAS_AS_MODEL, true);
                }
            }
        }
    }

    @Override
    protected
        void
        updatePropertyForArray( @Nonnull final CodegenProperty property, @Nonnull final CodegenProperty innerProperty )
    {
        super.updatePropertyForArray(property, innerProperty);

        if( USE_FLOAT_ARRAYS.isEnabled(config) && innerProperty.isNumber && property.isArray ) {
            property.datatypeWithEnum = "float[]";
            property.vendorExtensions.put("isPrimitiveArray", true);
        }
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    @Nullable
    public String toDefaultValue( @Nonnull final CodegenProperty cp, @Nonnull final Schema schema )
    {
        if( USE_FLOAT_ARRAYS.isEnabled(config) && "float[]".equals(cp.datatypeWithEnum) ) {
            return null;
        }
        return super.toDefaultValue(cp, schema);
    }

    @Override
    @Nullable
    public String toBooleanGetter( @Nullable final String name )
    {
        final String result = super.toBooleanGetter(name);
        if( FIX_REDUNDANT_IS_BOOLEAN_PREFIX.isEnabled(config) && result != null && DOUBLE_IS_PATTERN.test(result) ) {
            return "is" + result.substring(4);
        }
        return result;
    }

    // Custom processor to inject "x-return-nullable" extension
    @Override
    @Nonnull
    public
        OperationsMap
        postProcessOperationsWithModels( @Nonnull final OperationsMap ops, @Nonnull final List<ModelMap> allModels )
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
            new CreatorForInterfaceSubtypesFeature(m, config).apply();
        }
    }

    @Override
    @Nonnull
    public Map<String, ModelsMap> postProcessAllModels( @Nonnull final Map<String, ModelsMap> objs )
    {
        final Map<String, ModelsMap> result = super.postProcessAllModels(objs);

        // Restore sibling descriptions lost during $ref resolution for primitive-typed properties.
        for( final var schemaEntry : siblingDescriptions.entrySet() ) {
            final ModelsMap modelsMap = result.get(schemaEntry.getKey());
            if( modelsMap == null ) {
                continue;
            }
            for( final ModelMap modelMap : modelsMap.getModels() ) {
                for( final CodegenProperty prop : modelMap.getModel().vars ) {
                    final String siblingDesc = schemaEntry.getValue().get(prop.baseName);
                    if( siblingDesc != null ) {
                        prop.description = escapeText(siblingDesc);
                        prop.unescapedDescription = siblingDesc;
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void captureSiblingDescriptions( @Nonnull final OpenAPI openAPI )
    {
        if( openAPI.getComponents() == null || openAPI.getComponents().getSchemas() == null ) {
            return;
        }
        for( final var schemaEntry : openAPI.getComponents().getSchemas().entrySet() ) {
            final Schema modelSchema = schemaEntry.getValue();
            if( modelSchema.getProperties() == null ) {
                continue;
            }
            for( final var propEntry : ((Map<String, Schema>) modelSchema.getProperties()).entrySet() ) {
                final Schema propSchema = propEntry.getValue();
                if( propSchema.get$ref() != null && propSchema.getDescription() != null ) {
                    siblingDescriptions
                        .computeIfAbsent(schemaEntry.getKey(), k -> new HashMap<>())
                        .put(propEntry.getKey(), propSchema.getDescription());
                }
            }
        }
    }

    /**
     * Remove property from specification.
     *
     * @param openAPI
     *            The OpenAPI specification to update.
     * @param schemaName
     *            The name of the schema to update.
     * @param propertyName
     *            The name of the property to remove.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void preprocessRemoveProperty(
        @Nonnull final OpenAPI openAPI,
        @Nonnull final String schemaName,
        @Nonnull final String propertyName )
    {
        final var schema = openAPI.getComponents().getSchemas().get(schemaName);
        if( schema == null ) {
            log.error("Could not find schema {} to remove property {} from.", schemaName, propertyName);
            return;
        }
        boolean removed = false;

        final Predicate<Schema> remove =
            s -> s.getProperties() != null && s.getProperties().remove(propertyName) != null;
        final var schemasQueued = new LinkedList<Schema>();
        final var schemasDone = new HashSet<Schema>();
        schemasQueued.add(schema);

        while( !schemasQueued.isEmpty() ) {
            final var s = schemasQueued.remove();
            if( s == null || !schemasDone.add(s) ) {
                continue;
            }
            // check removal of direct schema property
            removed |= remove.test(s);

            // check for allOf, anyOf, oneOf
            for( final List<Schema> list : Arrays.asList(s.getAllOf(), s.getAnyOf(), s.getOneOf()) ) {
                if( list != null ) {
                    schemasQueued.addAll(list);
                }
            }
        }
        if( !removed ) {
            log.error("Could not remove property {} from schema {}.", propertyName, schemaName);
        }
    }

    /**
     * Remove unused schema components.
     *
     * @param openAPI
     *            The OpenAPI specification to update.
     */
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void preprocessRemoveRedundancies( @Nonnull final OpenAPI openAPI )
    {
        final var queue = new LinkedList<Schema>();
        final var done = new HashSet<Schema>();
        final var refs = new LinkedHashSet<String>();
        final var pattern = Pattern.compile("\\$ref: #/components/schemas/(\\w+)");

        // OAS 3.1 documents may have no paths (webhooks-only or components-only).
        if( openAPI.getPaths() == null || openAPI.getPaths().isEmpty() ) {
            return;
        }

        // find and queue schemas nested in paths
        for( final var path : openAPI.getPaths().values() ) {
            final var m = pattern.matcher(path.toString());
            while( m.find() ) {
                final var name = m.group(1);
                final var schema = openAPI.getComponents().getSchemas().get(name);
                queue.add(schema);
                refs.add(m.group(0).split(" ")[1]);
            }
        }

        // OAS 3.1 adds components/pathItems — traverse them for schema references too
        final var pathItems = openAPI.getComponents() != null ? openAPI.getComponents().getPathItems() : null;
        if( pathItems != null ) {
            for( final var pathItem : pathItems.values() ) {
                final var m = pattern.matcher(pathItem.toString());
                while( m.find() ) {
                    final var name = m.group(1);
                    final var schema = openAPI.getComponents().getSchemas().get(name);
                    queue.add(schema);
                    refs.add(m.group(0).split(" ")[1]);
                }
            }
        }

        while( !queue.isEmpty() ) {
            final var s = queue.remove();
            if( s == null || !done.add(s) ) {
                continue;
            }

            // check for $ref attribute
            final var ref = s.get$ref();
            if( ref != null ) {
                refs.add(ref);
                final var refName = ref.substring(ref.lastIndexOf('/') + 1);
                queue.add(openAPI.getComponents().getSchemas().get(refName));
            }

            // check for direct properties
            if( s.getProperties() != null ) {
                for( final var s1 : s.getProperties().values() ) {
                    queue.add((Schema) s1);
                }
            }

            // check for array items
            if( s.getItems() != null ) {
                queue.add(s.getItems());
            }

            // check for allOf, anyOf, oneOf
            for( final List<Schema> list : Arrays.asList(s.getAllOf(), s.getAnyOf(), s.getOneOf()) ) {
                if( list != null ) {
                    queue.addAll(list);
                }
            }
        }

        // remove all schemas that have not been marked "used"
        openAPI.getComponents().getSchemas().keySet().removeIf(schema -> {
            if( !refs.contains("#/components/schemas/" + schema) ) {
                log.info("Removing unused schema {}", schema);
                return true;
            }
            return false;
        });
    }

    @SuppressWarnings( { "rawtypes", "RedundantSuppression" } )
    @Override
    protected void updateModelForObject( @Nonnull final CodegenModel m, @Nonnull final Schema schema )
    {
        // Suppress map inheritance locally; custom-field APIs handle additional attributes.
        final Schema<?> modelSchema = ModelUtils.cloneSchema(schema, OasVersionUtil.isOas31(openAPI));
        // Preserve property identity for upstream processing and caching.
        modelSchema.setProperties(((Schema<?>) schema).getProperties());
        modelSchema.setAdditionalProperties(Boolean.FALSE);
        super.updateModelForObject(m, modelSchema);
    }
}
