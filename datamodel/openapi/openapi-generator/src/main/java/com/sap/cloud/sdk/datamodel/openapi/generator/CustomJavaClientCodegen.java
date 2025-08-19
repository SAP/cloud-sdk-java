package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_REDUNDANT_IS_BOOLEAN_PREFIX;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_REMOVE_UNUSED_COMPONENTS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_EXCLUDE_PATHS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_EXCLUDE_PROPERTIES;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_FLOAT_ARRAYS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_ONE_OF_CREATORS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.openapitools.codegen.model.ModelMap;
import org.openapitools.codegen.model.OperationsMap;

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
    private static final Set<String> PRIMITIVES = Set.of("String", "Integer", "Long", "Double", "Float", "Byte");

    public CustomJavaClientCodegen( @Nonnull final GenerationConfiguration config )
    {
        this.config = config;
    }

    @Override
    public void preprocessOpenAPI( @Nonnull final OpenAPI openAPI )
    {
        if( USE_EXCLUDE_PROPERTIES.isEnabled(config) ) {
            final String[] exclusions = USE_EXCLUDE_PROPERTIES.getValue(config).trim().split("[,\\s]+");
            for( final String exclusion : exclusions ) {
                final String[] split = exclusion.split("\\.", 2);
                preprocessRemoveProperty(openAPI, split[0], split[1]);
            }
        }

        if( USE_EXCLUDE_PATHS.isEnabled(config) ) {
            final String[] exclusions = USE_EXCLUDE_PATHS.getValue(config).trim().split("[,\\s]+");
            for( final String exclusion : exclusions ) {
                if( !openAPI.getPaths().keySet().remove(exclusion) ) {
                    log.error("Could not remove path {}", exclusion);
                }
            }
        }

        super.preprocessOpenAPI(openAPI);

        if( FIX_REMOVE_UNUSED_COMPONENTS.isEnabled(config) ) {
            preprocessRemoveRedundancies(openAPI);
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
            useCreatorsForInterfaceSubtypes(m);
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
    @SuppressWarnings( { "rawtypes", "unchecked", "ReplaceInefficientStreamCount" } )
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
            s -> s != null && s.getProperties() != null && s.getProperties().remove(propertyName) != null;
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
            final var candidatesMultiple1D = new HashSet<String>();
            final var candidatesMultipleND = new HashSet<Map<String, String>>();

            for( final String candidate : candidates ) {
                if( candidate.startsWith("List<") ) {
                    int depth = 0;
                    String sub = candidate;
                    while( sub.startsWith("List<") ) {
                        sub = sub.substring(5, sub.length() - 1);
                        depth++;
                    }

                    final String innerType = sub;
                    if( depth == 1 ) {
                        candidatesMultiple1D.add(innerType);
                    } else {
                        candidatesMultipleND
                            .add(Map.of("innerType", innerType, "depth", String.valueOf(depth), "fullType", candidate));
                    }

                    useCreators = true;
                } else {
                    candidatesSingle.add(candidate);
                    useCreators |= PRIMITIVES.contains(candidate);
                    if( !PRIMITIVES.contains(candidate) ) {
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
                final var numArrayTypes = candidatesSingle.size() + candidatesMultipleND.size();
                if( numArrayTypes > 1 ) {
                    final var msg =
                        "Field can be oneOf %d array types. Deserialization may not work as expected."
                            .formatted(numArrayTypes);
                    log.warn(msg, m.name);
                }

                candidates.clear();
                final var monads =
                    Map
                        .of(
                            "single",
                            candidatesSingle,
                            "multiple1D",
                            candidatesMultiple1D,
                            "multipleND",
                            candidatesMultipleND);
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
}
