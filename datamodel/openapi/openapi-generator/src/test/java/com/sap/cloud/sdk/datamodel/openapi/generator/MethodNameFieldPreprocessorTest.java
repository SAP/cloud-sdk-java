/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class MethodNameFieldPreprocessorTest
{
    @Test
    void testSodastoreApiWithMethodNameExtensionField()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + MethodNameFieldPreprocessorTest.class.getSimpleName()
                        + "/sodastore-with-method-name-extension-field.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new MethodNameFieldPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isTrue();

        result.getJsonNode().path("paths").path("/sodas").path("get").get("parameters").forEach(parameter -> {
            final String fieldValue = parameter.get(MethodNameFieldPreprocessor.METHOD_NAME_EXTENSION_FIELD).asText();

            assertThat(fieldValue).isEqualTo("awesomeListSodas");
        });
    }

    @Test
    void testSodastoreApiWithEmptyMethodNameExtensionField()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + MethodNameFieldPreprocessorTest.class.getSimpleName()
                        + "/sodastore-with-empty-method-name-extension-field.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new MethodNameFieldPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();

        result.getJsonNode().path("paths").path("/sodas").path("get").get("parameters").forEach(parameter -> {
            assertThat(parameter.has(MethodNameFieldPreprocessor.METHOD_NAME_EXTENSION_FIELD)).isFalse();
        });
    }

    @Test
    void testSodastoreApiWithExtensionFieldAndOperationWithoutParameters()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + MethodNameFieldPreprocessorTest.class.getSimpleName()
                        + "/sodastore-with-method-name-extension-field-without-parameters.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new MethodNameFieldPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();
        assertThat(result.getJsonNode()).isSameAs(jsonNode);
    }

    @Test
    void testSodastoreApiWithoutMethodNameExtensionField()
        throws IOException
    {
        final Path inputFilePath =
            Paths
                .get(
                    "src/test/resources/"
                        + MethodNameFieldPreprocessorTest.class.getSimpleName()
                        + "/sodastore-without-method-name-extension-field.json");

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree(inputFilePath.toFile());

        final PreprocessingStep.PreprocessingStepResult result =
            new MethodNameFieldPreprocessor().execute(jsonNode, objectMapper);

        assertThat(result.changesApplied()).isFalse();

        result.getJsonNode().path("paths").path("/sodas").path("get").get("parameters").forEach(parameter -> {
            assertThat(parameter.has(MethodNameFieldPreprocessor.METHOD_NAME_EXTENSION_FIELD)).isFalse();
        });
    }
}