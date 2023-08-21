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

public class ValidationKeywordsPreprocessorTest
{
    @Test
    public void testSodastoreApiWithNoValidators()
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
    public void testApiWithValidatorsInSchemasAsDirectChild()
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
    public void testApiWithValidatorsInSchemasNested()
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
    public void testApiWithValidatorsInPaths()
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
    public void testSodastoreApiWithAllOf()
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

}
