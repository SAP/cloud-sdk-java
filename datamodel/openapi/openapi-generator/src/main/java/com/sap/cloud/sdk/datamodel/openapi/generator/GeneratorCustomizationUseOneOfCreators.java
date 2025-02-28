package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.languages.JavaClientCodegen;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Use JsonCreator instead of sub-type deduction for oneOf and anyOf schemas.
 */
@Slf4j
@Getter
public class GeneratorCustomizationUseOneOfCreators
    implements
    GeneratorCustomization,
    GeneratorCustomization.UpdateModelForComposedSchema
{
    private static final Set<String> PRIMITIVES = Set.of("String", "Integer", "Long", "Double", "Float", "Byte");

    private final String configKey = "useOneOfCreators";

    @Override
    @SuppressWarnings( "rawtypes" )
    public void updateModelForComposedSchema(
        @Nonnull final JavaClientCodegen ref,
        @Nonnull final CodegenModel m,
        @Nonnull final Schema schema,
        @Nonnull final Map<String, Schema> allDefinitions )
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
}
