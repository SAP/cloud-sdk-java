package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openapitools.codegen.languages.JavaClientCodegen;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

class FreeFormObjectUnionTest
{
    @ParameterizedTest
    @ValueSource( strings = { "oneOf", "anyOf" } )
    void broadObjectAlternativeProducesMap( final String keyword )
    {
        for( final boolean reference : List.of(false, true) ) {
            final Schema<?> broad = new Schema<>().type("object").additionalProperties(true);
            final OpenAPI api = api(broad);
            final Schema<?> alternative = reference ? new Schema<>().$ref("#/components/schemas/FreeForm") : broad;
            final Schema<?> union = union(keyword, alternative, typedObject());
            union.setDescription("Configuration options");
            union.setNullable(true);
            final Schema<?> normalized =
                new CustomOpenAPINormalizer(api, Map.of()).normalizeSchema(union, new HashSet<>());
            final JavaClientCodegen codegen = new JavaClientCodegen();
            codegen.setOpenAPI(api);
            assertThat(codegen.fromProperty("config", normalized).dataType).isEqualTo("Map<String, Object>");
            assertThat(normalized.getDescription()).isEqualTo("Configuration options");
            assertThat(normalized.getNullable()).isTrue();
        }
    }

    @ParameterizedTest
    @ValueSource( strings = { "oneOf", "anyOf" } )
    void constrainedObjectsDoNotAbsorbAlternatives( final String keyword )
    {
        final Schema<?> closed = new Schema<>().type("object").additionalProperties(false);
        final Schema<?> typedValues = new Schema<>().type("object").additionalProperties(new Schema<>().type("string"));
        final Schema<?> bounded = new Schema<>().type("object").additionalProperties(true).maxProperties(0);
        final Schema<?> required = new Schema<>().type("object").additionalProperties(true);
        required.setRequired(new java.util.ArrayList<>(List.of("id")));
        for( final Schema<?> constrained : List.of(closed, typedValues, bounded, required, typedObject()) ) {
            final Schema<?> result =
                new CustomOpenAPINormalizer(api(constrained), Map.of())
                    .normalizeSchema(
                        union(
                            keyword,
                            constrained,
                            new Schema<>().type("object").addProperty("other", new Schema<>().type("integer"))),
                        new HashSet<>());
            assertThat(keyword.equals("oneOf") ? result.getOneOf() : result.getAnyOf()).hasSize(2);
        }
    }

    @ParameterizedTest
    @ValueSource( strings = { "oneOf", "anyOf" } )
    void nonObjectAlternativesArePreserved( final String keyword )
    {
        final Schema<?> broad = new Schema<>().type("object").additionalProperties(true);
        final Schema<?> result =
            new CustomOpenAPINormalizer(api(broad), Map.of())
                .normalizeSchema(union(keyword, broad, new Schema<>().type("string")), new HashSet<>());
        assertThat(keyword.equals("oneOf") ? result.getOneOf() : result.getAnyOf()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource( strings = { "oneOf", "anyOf" } )
    void constrainedReferencesAreNotBroadened( final String keyword )
    {
        final Schema<?> broad = new Schema<>().type("object").additionalProperties(true);
        final Schema<?> constrainedRef = new Schema<>().$ref("#/components/schemas/FreeForm").maxProperties(0);
        final Schema<?> result =
            new CustomOpenAPINormalizer(api(broad), Map.of())
                .normalizeSchema(union(keyword, constrainedRef, typedObject()), new HashSet<>());
        assertThat(keyword.equals("oneOf") ? result.getOneOf() : result.getAnyOf()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource( strings = { "oneOf", "anyOf" } )
    void multipleTypesAreNotRestrictedToObjects( final String keyword )
    {
        final Schema<?> broad = new Schema<>().type("object").additionalProperties(true);
        final Schema<?> mixed = new Schema<>().type("object");
        mixed.setTypes(new java.util.LinkedHashSet<>(List.of("object", "string")));
        final Schema<?> result =
            new CustomOpenAPINormalizer(api(broad), Map.of())
                .normalizeSchema(union(keyword, broad, mixed), new HashSet<>());
        assertThat(keyword.equals("oneOf") ? result.getOneOf() : result.getAnyOf()).hasSize(2);
    }

    private static OpenAPI api( final Schema<?> broad )
    {
        return new OpenAPI().openapi("3.0.3").components(new Components().addSchemas("FreeForm", broad));
    }

    private static Schema<?> typedObject()
    {
        return new Schema<>().type("object").addProperty("enabled", new Schema<>().type("boolean"));
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private static Schema<?> union( final String keyword, final Schema<?> first, final Schema<?> second )
    {
        final Schema result = new Schema<>();
        if( keyword.equals("oneOf") ) {
            result.setOneOf(new java.util.ArrayList<>(List.of(first, second)));
        } else {
            result.setAnyOf(new java.util.ArrayList<>(List.of(first, second)));
        }
        return result;
    }
}
