package com.sap.cloud.sdk.datamodel.openapi.generator.customization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomization;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Remove selected properties from the OpenAPI specification.
 */
@Getter
@Slf4j
public class UseExcludeProperties implements GeneratorCustomization.PreProcessOpenAPI
{
    private final String configKey = "excludeProperties";

    @SuppressWarnings( { "rawtypes", "unchecked", "ReplaceInefficientStreamCount" } )
    @Override
    public
        void
        preprocessOpenAPI( @Nonnull final ChainElementVoid<PreProcessOpenAPI> chain, @Nonnull final OpenAPI openAPI )
    {
        // remove selected properties
        final String excludePropertiesRaw = Objects.requireNonNull(getConfigValue(chain.config()));
        final String[] excludeProperties = excludePropertiesRaw.trim().split("[,\\s]+");

        for( final var removeProperty : excludeProperties ) {
            final var split = removeProperty.split("\\.", 2);
            final var schema = openAPI.getComponents().getSchemas().get(split[0]);
            if( schema == null ) {
                log.error("Could not find schema {} to remove property {} from.", split[0], split[1]);
                continue;
            }
            boolean removed = false;

            final var schemasQueued = new LinkedList<Schema>();
            final var schemasDone = new HashSet<Schema>();
            schemasQueued.add(schema);
            while( !schemasQueued.isEmpty() ) {
                final var s = schemasQueued.remove();
                if( s == null || !schemasDone.add(s) ) {
                    continue;
                }

                // check removal of direct schema property
                if(s.getProperties() != null && s.getProperties().remove(split[1]) != null) {
                    removed = true;
                }

                // check for allOf, anyOf, oneOf
                for( final List<Schema> list : Arrays.asList(s.getAllOf(), s.getAnyOf(), s.getOneOf()) ) {
                    if( list != null ) {
                        schemasQueued.addAll(list);
                    }
                }
            }
            if( !removed ) {
                log.error("Could not remove property {}", removeProperty);
            }
        }

        // process rest of chain
        chain.doNext(next -> next.get().preprocessOpenAPI(next, openAPI));
    }
}
