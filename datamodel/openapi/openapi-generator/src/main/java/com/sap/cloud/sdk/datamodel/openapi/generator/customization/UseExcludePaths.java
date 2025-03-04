package com.sap.cloud.sdk.datamodel.openapi.generator.customization;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.openapi.generator.GeneratorCustomization;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Remove selected paths from the OpenAPI specification.
 */
@Getter
@Slf4j
public class UseExcludePaths implements GeneratorCustomization, GeneratorCustomization.PreProcessOpenAPI
{
    private final String configKey = "excludePaths";

    @Override
    public
        void
        preprocessOpenAPI( @Nonnull final ChainElementVoid<PreProcessOpenAPI> chain, @Nonnull final OpenAPI openAPI )
    {
        // remove selected properties
        final String excludePathsRaw = Objects.requireNonNull(getConfigValue(chain.config()));
        final String[] excludePaths = excludePathsRaw.split("[,\\s]+");

        for( final var removePath : excludePaths ) {
            if( !openAPI.getPaths().keySet().remove(removePath) ) {
                log.error("Could not remove path {}", removePath);
            }
        }

        // process rest of chain
        chain.doNext(next -> next.get().preprocessOpenAPI(next, openAPI));
    }
}
