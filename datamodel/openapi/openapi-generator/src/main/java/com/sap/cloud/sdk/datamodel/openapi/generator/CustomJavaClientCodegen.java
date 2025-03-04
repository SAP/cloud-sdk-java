package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.FIX_REDUNDANT_IS_BOOLEAN_PREFIX;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_FLOAT_ARRAYS;
import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_ONE_OF_CREATORS;

import java.util.HashSet;
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

import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;

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
}
