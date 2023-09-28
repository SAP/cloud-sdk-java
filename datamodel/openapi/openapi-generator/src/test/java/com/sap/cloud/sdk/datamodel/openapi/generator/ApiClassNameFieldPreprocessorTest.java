package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class ApiClassNameFieldPreprocessorTest
{
    @Test
    public void testExtensionFieldOnRootAndPathAndOperationLevel()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/extension-field-on-various-levels.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        final String rootLevelValue =
            inputNode.get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD).asText();
        final String pathLevelValue =
            inputNode
                .path("paths")
                .path("/beers")
                .get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD)
                .asText();
        final String operationLevelValue =
            inputNode
                .path("paths")
                .path("/sodas")
                .path("post")
                .get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD)
                .asText();

        final JsonNode getSodasOperation = result.getJsonNode().path("paths").path("/sodas").path("get");
        assertOnExtensionFieldOnOperation(getSodasOperation, rootLevelValue);

        final JsonNode postSodasOperation = result.getJsonNode().path("paths").path("/sodas").path("post");
        assertOnExtensionFieldOnOperation(postSodasOperation, operationLevelValue);

        final JsonNode getDinosOperation = result.getJsonNode().path("paths").path("/beers").path("get");
        assertOnExtensionFieldOnOperation(getDinosOperation, pathLevelValue);
    }

    void assertOnExtensionFieldOnOperation( final JsonNode getSodasOperation, final String extensionFieldValue )
    {
        final JsonNode tags = getSodasOperation.get("tags");
        assertThat(tags).isInstanceOf(ArrayNode.class);
        final ArrayNode tagsArray = (ArrayNode) tags;

        assertThat(tagsArray).hasSize(1);

        assertThat(tagsArray.get(0)).isInstanceOf(TextNode.class);
        assertThat(tagsArray.get(0).asText()).isEqualTo(extensionFieldValue);
    }

    @Test
    public void testExtensionFieldOnOperationLevel()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/extension-field-on-operation-level.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        result.getJsonNode().path("paths").path("/sodas").forEach(operation -> {
            final JsonNode tags = operation.get("tags");
            assertThat(tags).isInstanceOf(ArrayNode.class);
            final ArrayNode tagsArray = (ArrayNode) tags;

            assertThat(tagsArray).hasSize(1);

            final String extensionFieldValue =
                operation.get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD).asText();

            assertThat(tagsArray.get(0)).isInstanceOf(TextNode.class);
            assertThat(tagsArray.get(0).asText()).isEqualTo(extensionFieldValue);
        });
    }

    @Test
    public void testExtensionFieldOnRootLevel()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/extension-field-on-root-level.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        final String extensionFieldValue =
            inputNode.get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD).asText();

        result.getJsonNode().path("paths").path("/sodas").forEach(operation -> {
            assertOnExtensionFieldOnOperation(operation, extensionFieldValue);
        });
    }

    @Test
    public void testExtensionFieldOnPathLevel()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/extension-field-on-path-level.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        final String extensionFieldValue =
            inputNode
                .path("paths")
                .path("/sodas")
                .get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD)
                .asText();

        result.getJsonNode().path("paths").path("/sodas").forEach(operation -> {
            if( operation.isTextual() ) {
                return;
            }

            assertOnExtensionFieldOnOperation(operation, extensionFieldValue);
        });
    }

    @Test
    public void testEmptyNameExtensionField()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/empty-extension-field.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isSameAs(inputNode);
    }

    @Test
    public void testWithoutExtensionField()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/no-extension-field.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isSameAs(inputNode);
    }

    @Test
    public void testExtensionFieldWithApiSuffix()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + ApiClassNameFieldPreprocessorTest.class.getSimpleName()
                        + "/extension-field-api-suffix.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode inputNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new ApiClassNameFieldPreprocessor().execute(inputNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        final String extensionField =
            result
                .getJsonNode()
                .path("paths")
                .path("/sodas")
                .path("get")
                .get(ApiClassNameFieldPreprocessor.API_CLASS_NAME_EXTENSION_FIELD)
                .asText();

        assertThat(extensionField).isEqualTo("AwesomeSodasApi");
    }
}
