package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

class CustomOpenAPINormalizerTest
{
    private static OpenAPI oas30()
    {
        final OpenAPI openAPI = new OpenAPI();
        openAPI.setOpenapi("3.0.3");
        return openAPI;
    }

    private static OpenAPI oas31()
    {
        final OpenAPI openAPI = new OpenAPI();
        openAPI.setOpenapi("3.1.0");
        return openAPI;
    }

    private static CustomOpenAPINormalizer normalizer( final OpenAPI openAPI )
    {
        return new CustomOpenAPINormalizer(openAPI, Map.of());
    }

    // --- contentEncoding / contentMediaType → format mapping ---

    @Test
    void base64ContentEncodingMapsToByteFormat()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentEncoding("base64");

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isEqualTo("byte");
    }

    @Test
    void base64ContentEncodingCaseInsensitive()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentEncoding("BASE64");

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isEqualTo("byte");
    }

    @Test
    void binaryContentEncodingMapsToBinaryFormat()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentEncoding("binary");

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isEqualTo("binary");
    }

    @Test
    void contentMediaTypeWithoutEncodingMapsToBinaryFormat()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentMediaType("application/octet-stream");

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isEqualTo("binary");
    }

    @Test
    void existingFormatIsNotOverwritten()
    {
        final Schema<?> schema = new Schema<>();
        schema.setFormat("uuid");
        schema.setContentEncoding("base64");

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isEqualTo("uuid");
    }

    @Test
    void noContentEncodingOrMediaTypeLeaveFormatNull()
    {
        final Schema<?> schema = new Schema<>();

        normalizer(oas31()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isNull();
    }

    // --- OAS 3.0: no format mapping applied ---

    @Test
    void contentEncodingDoesNotMapFormatInOas30()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentEncoding("base64");

        normalizer(oas30()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isNull();
    }

    @Test
    void contentMediaTypeDoesNotMapFormatInOas30()
    {
        final Schema<?> schema = new Schema<>();
        schema.setContentMediaType("application/octet-stream");

        normalizer(oas30()).normalizeSchema(schema, new HashSet<>());

        assertThat(schema.getFormat()).isNull();
    }
}
