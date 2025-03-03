package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class GeneratorCustomizationUseExcludeProperties
    implements
    GeneratorCustomization,
    GeneratorCustomization.PreProcessOpenAPI
{
    private final String configKey = "excludeProperties";

    // TODO chatCompletionResponseMessage.context, createChatCompletionRequest.data_sources

    @SuppressWarnings( { "rawtypes", "unchecked", "ReplaceInefficientStreamCount" } )
    @Override
    public void preprocessOpenAPI( @Nonnull final ContextVoid<PreProcessOpenAPI> chain, @Nonnull final OpenAPI openAPI )
    {
        // remove selected properties
        final String excludePropertiesRaw = Objects.requireNonNull(getConfigValue(chain.config()));
        final String[] excludeProperties = excludePropertiesRaw.split("[,\\s]+");

        for( final var removeProperty : excludeProperties ) {
            final var split = removeProperty.split("\\.", 2);
            final var schema = openAPI.getComponents().getSchemas().get(split[0]);

            boolean removed = false;

            final Predicate<Schema> remove =
                s -> s != null && s.getProperties() != null && s.getProperties().remove(split[1]) != null;
            final var schemasToCheck = new LinkedHashSet<Schema>();
            schemasToCheck.add(schema);
            while( !schemasToCheck.isEmpty() ) {
                final var sit = schemasToCheck.iterator();
                final var s = sit.next();
                sit.remove();
                removed |= remove.test(s);
                if( s.getAllOf() != null ) {
                    removed |= s.getAllOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getAllOf());
                }
                if( s.getAnyOf() != null ) {
                    removed |= s.getAnyOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getAnyOf());
                }
                if( s.getOneOf() != null ) {
                    removed |= s.getOneOf().stream().filter(remove).count() > 0;
                    schemasToCheck.addAll(s.getOneOf());
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
