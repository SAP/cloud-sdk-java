package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

class CustomJavaClientCodegenTest
{
    private static final String ALIAS_AS_MODEL = "x-generate-alias-as-model";

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    void preservesNamedFreeFormAlternatives( final boolean anyOf )
    {
        final Schema<Object> empty = freeFormObject();
        final Schema<Object> union = new Schema<>();
        for( final Schema<?> alternative : List.of(reference("Empty"), new Schema<>().type("string")) ) {
            if( anyOf ) {
                union.addAnyOfItem(alternative);
            } else {
                union.addOneOfItem(alternative);
            }
        }
        final OpenAPI openAPI = specification(empty, union);

        codegen(true).preprocessOpenAPI(openAPI);

        assertThat(empty.getExtensions()).containsEntry(ALIAS_AS_MODEL, true);
    }

    @Test
    void preservesAlternativesInNestedPropertiesAndHandlesRecursiveReferences()
    {
        final Schema<Object> empty = freeFormObject();
        final Schema<Object> union = new Schema<>();
        union.setOneOf(List.of(reference("Empty"), reference("Container")));
        final Schema<Object> container = new Schema<>();
        container.addProperty("strategyConfig", union);

        codegen(true).preprocessOpenAPI(specification(empty, container));

        assertThat(empty.getExtensions()).containsEntry(ALIAS_AS_MODEL, true);
    }

    @Test
    void leavesUnrelatedMapsAndInlineAlternativesUnchanged()
    {
        final Schema<Object> empty = freeFormObject();
        final Schema<Object> inline = freeFormObject();
        final Schema<Object> container = new Schema<>();
        container.addProperty("ordinaryMap", reference("Empty"));
        container.setOneOf(List.of(inline, new Schema<>().type("string")));

        codegen(true).preprocessOpenAPI(specification(empty, container));

        assertThat(empty.getExtensions()).isNull();
        assertThat(inline.getExtensions()).isNull();
    }

    @Test
    void leavesAliasesUnchangedWhenInterfacesAreDisabled()
    {
        final Schema<Object> empty = freeFormObject();
        final Schema<Object> union = new Schema<>();
        union.setOneOf(List.of(reference("Empty"), new Schema<>().type("string")));

        codegen(false).preprocessOpenAPI(specification(empty, union));

        assertThat(empty.getExtensions()).isNull();
    }

    @ParameterizedTest
    @ValueSource( booleans = { false, true } )
    void respectsExplicitMappings( final boolean schemaMapping )
    {
        final Schema<Object> empty = freeFormObject();
        final Schema<Object> union = new Schema<>();
        union.setOneOf(List.of(reference("Empty"), new Schema<>().type("string")));
        final CustomJavaClientCodegen codegen = codegen(true);
        final Map<String, String> mappings = schemaMapping ? codegen.schemaMapping() : codegen.typeMapping();
        mappings.put("Empty", "ExternalModel");

        codegen.preprocessOpenAPI(specification(empty, union));

        assertThat(empty.getExtensions()).isNull();
    }

    @Test
    void reportsConflictingAliasOptOut()
    {
        final Schema<Object> empty = freeFormObject();
        empty.addExtension(ALIAS_AS_MODEL, false);
        final Schema<Object> union = new Schema<>();
        union.setOneOf(List.of(reference("Empty"), new Schema<>().type("string")));

        assertThatThrownBy(() -> codegen(true).preprocessOpenAPI(specification(empty, union)))
            .isInstanceOf(OpenApiGeneratorException.class)
            .hasMessageContaining("Empty", ALIAS_AS_MODEL);
        assertThat(empty.getExtensions()).containsEntry(ALIAS_AS_MODEL, false);
    }

    private static CustomJavaClientCodegen codegen( final boolean useInterfaces )
    {
        final CustomJavaClientCodegen codegen = new CustomJavaClientCodegen(GenerationConfiguration.builder().build());
        codegen.setUseOneOfInterfaces(useInterfaces);
        return codegen;
    }

    private static OpenAPI specification( final Schema<?> empty, final Schema<?> container )
    {
        return new OpenAPI()
            .openapi("3.0.3")
            .components(new Components().addSchemas("Empty", empty).addSchemas("Container", container));
    }

    private static Schema<Object> freeFormObject()
    {
        final Schema<Object> schema = new Schema<>();
        schema.setType("object");
        schema.setAdditionalProperties(true);
        return schema;
    }

    private static Schema<Object> reference( final String name )
    {
        final Schema<Object> schema = new Schema<>();
        schema.set$ref("#/components/schemas/" + name);
        return schema;
    }
}
