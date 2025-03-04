package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Remove unused schema components.
 */
@Slf4j
@Getter
public class GeneratorCustomizationFixRedundantComponents
    implements
    GeneratorCustomization,
    GeneratorCustomization.PreProcessOpenAPI
{
    private final String configKey = "fixRedundantComponents";

    @SuppressWarnings( { "rawtypes" } )
    @Override
    public
        void
        preprocessOpenAPI( @Nonnull final ChainElementVoid<PreProcessOpenAPI> chain, @Nonnull final OpenAPI openAPI )
    {
        // process rest of the chain
        chain.doNext(next -> next.get().preprocessOpenAPI(next, openAPI));

        final var queue = new LinkedHashSet<Schema>();
        final var schemas = new LinkedHashSet<Schema>();
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
            final var qit = queue.iterator();
            final var s = qit.next();
            qit.remove();
            if( !schemas.add(s) ) {
                continue;
            }
            final var $ref = s.get$ref();
            if( $ref != null ) {
                refs.add($ref);
                final var refName = $ref.substring($ref.lastIndexOf('/') + 1);
                queue.add(openAPI.getComponents().getSchemas().get(refName));
            }
            if( s.getProperties() != null ) {
                for( final var s1 : s.getProperties().values() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getItems() != null ) {
                queue.add(s.getItems());
            }
            if( s.getAllOf() != null ) {
                for( final var s1 : s.getAllOf() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getAnyOf() != null ) {
                for( final var s1 : s.getAnyOf() ) {
                    queue.add((Schema) s1);
                }
            }
            if( s.getOneOf() != null ) {
                for( final var s1 : s.getOneOf() ) {
                    queue.add((Schema) s1);
                }
            }
        }

        openAPI.getComponents().getSchemas().keySet().removeIf(schema -> {
            if( !refs.contains("#/components/schemas/" + schema) ) {
                log.info("Removing unused schema {}", schema);
                return true;
            }
            return false;
        });
    }

}
