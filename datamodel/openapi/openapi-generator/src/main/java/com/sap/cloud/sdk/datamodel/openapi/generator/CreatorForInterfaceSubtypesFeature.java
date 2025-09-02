package com.sap.cloud.sdk.datamodel.openapi.generator;

import static com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomProperties.USE_FLOAT_ARRAYS;

import java.util.HashSet;
import java.util.Set;

import org.openapitools.codegen.CodegenModel;

import com.google.common.collect.Sets;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class CreatorForInterfaceSubtypesFeature
{
    private static final Set<String> PRIMITIVES =
        Set.of("String", "Boolean", "Integer", "Long", "Float", "Double", "BigDecimal");
    private final CodegenModel m;
    private final GenerationConfiguration config;
    private static final String VENDOR_EXT_MONADS = "x-monads";
    private static final String VENDOR_EXT_ONE_OF_INTERFACE = "x-is-one-of-interface";

    void apply()
    {
        if( m.discriminator != null ) {
            return;
        }

        final var creators = new HashSet<CreatorDetails>();
        for( final String candidate : Sets.union(m.anyOf, m.oneOf) ) {
            final var creator = processCandidate(candidate);
            creators.add(creator);

        }

        boolean hasArray = creators.stream().anyMatch(CreatorDetails::isArray);
        boolean hasPrimitive = creators.stream().anyMatch(CreatorDetails::isPrimitive);
        boolean hasObject = creators.stream().anyMatch(CreatorDetails::isObject);

        if( !hasArray && !hasPrimitive )
            return;

        if( hasPrimitive && hasObject ) {
            final var msg =
                "Generating interface with mixed multiple non-primitive and primitive sub-types: {}. Deserialization may not work.";
            log.warn(msg, m.name);
        }
        if( hasArray ) {
            final var msg = "Field can be oneOf %d array types. Deserialization may not work as expected.";
            log.warn(msg, m.name);
        }

        m.anyOf.clear(); // clear candidates to avoid default generator behavior
        m.oneOf.clear();
        m.vendorExtensions.put(VENDOR_EXT_MONADS, creators); // update set of creator for oneOf/anyOf
        m.vendorExtensions.put(VENDOR_EXT_ONE_OF_INTERFACE, true); // enforce template usage
    }

    private CreatorDetails processCandidate( String candidate )
    {
        if( candidate.startsWith("List<") ) {
            var targetType = candidate;
            var wrapperType = candidate.replace("<", "Of").replaceFirst(">", "s").replace(">", "");

            if( USE_FLOAT_ARRAYS.isEnabled(config) && candidate.contains("BigDecimal") ) {
                targetType = candidate.replace("List<", "").replace("BigDecimal", "float").replace(">", "[]");
                wrapperType = wrapperType.replace("List", "Array").replace("BigDecimal", "Float");
            }

            return new CreatorDetails(wrapperType, targetType, true, false, false);
        }
        var isPrimitive = PRIMITIVES.contains(candidate);
        var wrapperType = "Inner" + candidate;
        return new CreatorDetails(wrapperType, candidate, false, isPrimitive, !isPrimitive);
    }

    private
        record
        CreatorDetails( String wrapperType, String targetType, boolean isArray, boolean isPrimitive, boolean isObject )
    {
    }
}
