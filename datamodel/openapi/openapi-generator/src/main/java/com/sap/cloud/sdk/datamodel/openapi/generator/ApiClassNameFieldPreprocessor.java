/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

class ApiClassNameFieldPreprocessor implements PreprocessingStep
{
    static final String API_CLASS_NAME_EXTENSION_FIELD = "x-sap-cloud-sdk-api-name";

    private boolean changesApplied = false;

    @Nonnull
    @Override
    public PreprocessingStepResult execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
    {
        final String extensionFieldValue = input.path(API_CLASS_NAME_EXTENSION_FIELD).asText();

        final JsonNode paths = input.path("paths");

        paths.forEach(path -> visitPath(path, objectMapper, extensionFieldValue));

        return new PreprocessingStepResult()
        {
            @Override
            public JsonNode getJsonNode()
            {
                return input;
            }

            @Override
            public boolean changesApplied()
            {
                return changesApplied;
            }
        };
    }

    private void visitPath(
        @Nullable final JsonNode inputNode,
        @Nonnull final ObjectMapper mapper,
        @Nullable final String rootLevelValue )
    {
        if( inputNode == null || inputNode.isEmpty() ) {
            return;
        }

        String extensionFieldValue = inputNode.path(API_CLASS_NAME_EXTENSION_FIELD).asText();
        if( extensionFieldValue == null || extensionFieldValue.isEmpty() ) {
            extensionFieldValue = rootLevelValue;
        }

        for( final JsonNode jsonNode : inputNode ) {
            visitOperation(jsonNode, mapper, extensionFieldValue);
        }
    }

    private void visitOperation(
        @Nullable final JsonNode inputNode,
        @Nonnull final ObjectMapper mapper,
        @Nullable final String pathLevelValue )
    {
        if( inputNode == null || inputNode.isEmpty() ) {
            return;
        }

        String extensionFieldValue = inputNode.path(API_CLASS_NAME_EXTENSION_FIELD).asText();
        if( extensionFieldValue == null || extensionFieldValue.isEmpty() ) {
            extensionFieldValue = pathLevelValue;
        }

        if( extensionFieldValue != null && !extensionFieldValue.isEmpty() ) {
            changesApplied = true;

            final String tagName = StringUtils.removeEndIgnoreCase(extensionFieldValue, "api");

            final ObjectNode operationObjectNode = (ObjectNode) inputNode;
            operationObjectNode.set("tags", mapper.createArrayNode().add(tagName));
        }
    }
}
