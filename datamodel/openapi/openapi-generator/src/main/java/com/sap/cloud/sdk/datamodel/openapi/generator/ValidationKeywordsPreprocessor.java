/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

class ValidationKeywordsPreprocessor implements PreprocessingStep
{
    private final List<String> POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS = Arrays.asList("anyOf", "oneOf");
    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";
    private static final String PATHS_NODE = "paths";
    private static final String COMPONENTS_NODE = "components";
    private static final String SCHEMAS_NODE = "schemas";

    @Nonnull
    @Override
    public PreprocessingStepResult execute(@Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper)
    {
        final JsonNode pathsNode = input.path(PATHS_NODE);
        final boolean isPathUpdated = checkAndReplaceValidatorsInPaths(input);

        final JsonNode schemasNode = input.path(COMPONENTS_NODE).path(SCHEMAS_NODE);
        final boolean isSchemaUpdated = checkForValidatorsInSchemas(schemasNode);

        return new PreprocessingStepResult()
        {
            @Nonnull
            @Override
            public JsonNode getJsonNode()
            {
                return input;
            }

            @Nonnull
            @Override
            public boolean changesApplied()
            {
                return (isPathUpdated || isSchemaUpdated);
            }
        };
    }

    private boolean checkAndReplaceValidatorsInPaths(final JsonNode pathsNode)
    {
        final AtomicBoolean isInputUpdated = new AtomicBoolean(false);
        pathsNode.findParents("schema").forEach(node -> {
                ObjectNode schemaNode = (ObjectNode) node.get("schema");
                if (schemaNode.has(ANY_OF) || schemaNode.has(ONE_OF)) {
                    schemaNode.removeAll();

                    schemaNode.put("type", "object");
                    schemaNode.put("additionalProperties", true);
                    isInputUpdated.set(true);
                }
        });
        return isInputUpdated.get();
    }

    private boolean checkForValidatorsInSchemas( final JsonNode schemasNode )
    {
        return false;
  /*      for( final String field : POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS ) {
            for( final JsonNode schema : schemasNode ) {
                for( final JsonNode schemaChild : schema ) {
                    final boolean isUnsupported = schemaChild.findValue(field) != null;
                    if( isUnsupported ) {
                        throw new OpenApiGeneratorException(
                            "The OpenAPI spec contains keyword "
                                + field
                                + " inside schemas which is only supported if it is a direct child. "
                                + "Occurances under additionalProperties and nesting inside a property is not currently supported during generation."
                                + "Please remove these fields for the generation to succeed");

                    }
                }
            }
        }
  */  }
}
