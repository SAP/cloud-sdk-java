package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.openapitools.codegen.OpenAPINormalizer;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

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

    // --- nullable: true warning on $ref schema ---

    @Test
    void oas31RefSchemaWithNullableEmitsWarning()
        throws Throwable
    {
        final Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/Foo");
        schema.setNullable(true);

        final String output = captureWarn(() -> normalizer(oas31()).normalizeReferenceSchema(schema));

        assertThat(output).contains("nullable: true").contains("#/components/schemas/Foo");
    }

    @Test
    void oas30RefSchemaWithNullableDoesNotWarn()
        throws Throwable
    {
        final Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/Foo");
        schema.setNullable(true);

        final String output = captureWarn(() -> normalizer(oas30()).normalizeReferenceSchema(schema));

        assertThat(output).doesNotContain("nullable: true");
    }

    private static String captureWarn( final Executable action )
        throws Throwable
    {
        // slf4j-simple writes to System.err; temporarily lower its level to WARN so the message is emitted.
        final org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(OpenAPINormalizer.class);
        final Field levelField = slf4jLogger.getClass().getDeclaredField("currentLogLevel");
        levelField.setAccessible(true);
        final int original = levelField.getInt(slf4jLogger);
        levelField.setInt(slf4jLogger, LocationAwareLogger.WARN_INT);

        final PrintStream originalErr = System.err;
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setErr(new PrintStream(buf));
        try {
            action.execute();
        }
        finally {
            System.setErr(originalErr);
            levelField.setInt(slf4jLogger, original);
        }
        return buf.toString();
    }
}
