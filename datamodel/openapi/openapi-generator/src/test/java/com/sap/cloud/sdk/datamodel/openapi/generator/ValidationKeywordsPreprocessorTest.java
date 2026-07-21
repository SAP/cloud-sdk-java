package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

class ValidationKeywordsPreprocessorTest
{
    @Test
    void testSodastoreApiWithNoValidators()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/sodastore.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputJsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ValidationKeywordsPreprocessor().execute(inputJsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isEqualTo(inputJsonNode);
    }

    @Test
    void testApiWithValidatorsInSchemasAsDirectChild()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/AggregatorDirectSchemaChild.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isEqualTo(jsonNode);
    }

    @Test
    void testApiWithValidatorsInSchemasNested()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/AggregatorNestedSchemaChild.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        assertThatThrownBy(() -> new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper))
            .isInstanceOf(OpenApiGeneratorException.class);
    }

    @Test
    void testApiWithValidatorsInPaths()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/AggregatorInPathSchema.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        assertThatThrownBy(() -> {
            final PreprocessingStep.PreprocessingStepResult result =
                new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper);
        }).isInstanceOf(OpenApiGeneratorException.class);
    }

    @Test
    void testSodastoreApiWithAllOf()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/sodastore-with-allOf.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isEqualTo(jsonNode);
    }

    // --- OAS 3.1 tests ---

    @Test
    void testOas31NullUnionAnyOfInPaths_isAllowed()
        throws IOException
    {
        // Gap 10: anyOf: [{$ref: "..."}, {type: "null"}] in a path request body is the OAS 3.1
        // canonical nullable-$ref pattern and must be allowed even when oneOf/anyOf generation
        // is otherwise disabled.
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/sodastore-31-nullable.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isEqualTo(jsonNode);
    }

    @Test
    void testOas31NullUnionAnyOfInSchemas_isAllowed()
        throws IOException
    {
        // The sodastore-31-nullable.json fixture also contains null-union anyOf in component schemas.
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/sodastore-31-nullable.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        // Must not throw — null-union patterns in schemas are allowed
        final PreprocessingStep.PreprocessingStepResult result =
            new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
    }

    @Test
    void testNonNullUnionOneOfInPaths_isBlocked()
        throws IOException
    {
        // A oneOf with two $ref items (no null type) in a path must still be blocked
        // when oneOf/anyOf generation is disabled.
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ValidationKeywordsPreprocessorTest.class.getSimpleName()
                        + "/AggregatorInPathSchema.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        assertThatThrownBy(() -> new ValidationKeywordsPreprocessor().execute(jsonNode, objectMapper))
            .isInstanceOf(OpenApiGeneratorException.class);
    }

}
