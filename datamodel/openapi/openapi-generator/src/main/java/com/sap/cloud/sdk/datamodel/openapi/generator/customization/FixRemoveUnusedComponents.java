package com.sap.cloud.sdk.datamodel.openapi.generator.customization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomization;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Remove unused schema components.
 */
@Slf4j
@Getter
public class FixRemoveUnusedComponents implements GeneratorCustomization.PreProcessOpenAPI
{
    private final String configKey = "removeUnusedComponents";

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public
        void
        preprocessOpenAPI( @Nonnull final ChainElementVoid<PreProcessOpenAPI> chain, @Nonnull final OpenAPI openAPI )
    {
        // process rest of the chain
        chain.doNext(next -> next.get().preprocessOpenAPI(next, openAPI));

        final var queue = new LinkedList<Schema>();
        final var done = new HashSet<Schema>();
        final var refs = new LinkedHashSet<String>();
        final var pattern = Pattern.compile("\\$ref: #/components/schemas/(\\w+)");
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

            // collect $ref attributes to mark schema used
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
}
