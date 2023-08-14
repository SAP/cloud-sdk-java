package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

class MethodNameFieldPreprocessor implements PreprocessingStep
{
    static final String METHOD_NAME_EXTENSION_FIELD = "x-sap-cloud-sdk-operation-name";

    private boolean changesApplied = false;

    @Nonnull
    @Override
    public PreprocessingStepResult execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
    {
        input.path("paths").forEach(path -> {
            path.forEach(this::visitOperationNode);
        });

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

    private void visitOperationNode( final JsonNode operationNode )
    {
        if( operationNode == null
            || operationNode.isEmpty()
            || operationNode.path(METHOD_NAME_EXTENSION_FIELD).asText().isEmpty()
            || !operationNode.has("parameters") ) {
            return;
        }

        changesApplied = true;

        final String methodName = operationNode.get(METHOD_NAME_EXTENSION_FIELD).asText();

        final JsonNode genericParametersNode = operationNode.get("parameters");

        if( !genericParametersNode.isArray() ) {
            throw new OpenApiGeneratorException("Parameters node is not an array.");
        }

        genericParametersNode.forEach(parameter -> {
            ((ObjectNode) parameter).put(METHOD_NAME_EXTENSION_FIELD, methodName);
        });
    }
}
