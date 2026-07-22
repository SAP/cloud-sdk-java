package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

class ValidationKeywordsPreprocessor implements PreprocessingStep
{
    private final List<String> POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS = Arrays.asList("anyOf", "oneOf");
    private final String PATHS_NODE = "paths";
    private final String WEBHOOKS_NODE = "webhooks";
    private final String COMPONENTS_NODE = "components";
    private final String SCHEMAS_NODE = "schemas";

    @Nonnull
    @Override
    public PreprocessingStepResult execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
    {
        final JsonNode pathsNode = input.path(PATHS_NODE);

        // OAS 3.1 documents may omit `paths` and use only `webhooks` or `components`.
        // Webhook client generation is not yet supported; emit a clear error so the user knows why.
        if( pathsNode.isMissingNode() ) {
            final JsonNode webhooksNode = input.path(WEBHOOKS_NODE);
            if( !webhooksNode.isMissingNode() ) {
                throw new OpenApiGeneratorException(
                    "The OAS 3.1 document contains 'webhooks' but no 'paths'. "
                        + "Webhook client generation is not yet supported by this generator. "
                        + "Add at least one path to generate a client.");
            }
            // components-only or empty document — no paths to validate; let upstream handle it
            return noChanges(input);
        }

        checkForValidatorsInPaths(pathsNode);

        final JsonNode schemasNode = input.path(COMPONENTS_NODE).path(SCHEMAS_NODE);
        checkForValidatorsInSchemas(schemasNode);

        return noChanges(input);
    }

    private void checkForValidatorsInPaths( final JsonNode pathsNode )
    {
        for( final String field : POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS ) {
            // allow the OAS 3.1 canonical null-union pattern:
            //   anyOf/oneOf: [{$ref: "..."}, {type: "null"}]
            // Only block occurrences that are NOT null-union patterns.
            final boolean isUnsupported =
                pathsNode.findValues(field).stream().anyMatch(node -> !isNullUnionPattern(node));
            if( isUnsupported ) {
                throw new OpenApiGeneratorException(
                    "The OpenAPI spec contains keyword "
                        + field
                        + " inside the Paths which is supported only if you explicitly enable it's processing using <enableOneOfAnyOfGeneration> parameter in the OpenAPI generator maven plugin."
                        + " Please regenerate your client by including <enableOneOfAnyOfGeneration> parameter.");
            }
        }
    }

    private void checkForValidatorsInSchemas( final JsonNode schemasNode )
    {
        for( final String field : POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS ) {
            for( final JsonNode schema : schemasNode ) {
                for( final JsonNode schemaChild : schema ) {
                    // allow the OAS 3.1 canonical null-union pattern.
                    final boolean isUnsupported =
                        schemaChild.findValues(field).stream().anyMatch(node -> !isNullUnionPattern(node));
                    if( isUnsupported ) {
                        throw new OpenApiGeneratorException(
                            "The OpenAPI spec contains keyword "
                                + field
                                + " inside schemas which is only supported if it is a direct child."
                                + " Occurrences under additionalProperties and nesting inside a property is supported only if you explicitly enable it's processing using <enableOneOfAnyOfGeneration> parameter in the OpenAPI generator maven plugin."
                                + " Please regenerate your client by including <enableOneOfAnyOfGeneration> parameter.");
                    }
                }
            }
        }
    }

    /**
     * Returns {@code true} when the given JSON node represents the OAS 3.1 canonical nullable-ref pattern:
     *
     * <pre>
     * anyOf/oneOf:
     *   - $ref: "..."
     *   - type: "null"
     * </pre>
     *
     * This two-element array pattern is the standard way to express a nullable $ref in OAS 3.1 and must be allowed even
     * when oneOf/anyOf generation is otherwise disabled.
     */
    private boolean isNullUnionPattern( final JsonNode node )
    {
        if( !node.isArray() || node.size() != 2 ) {
            return false;
        }
        boolean hasRef = false;
        boolean hasNullType = false;
        for( final JsonNode item : node ) {
            if( item.has("$ref") && item.size() == 1 ) {
                hasRef = true;
            } else if( item.has("type") && "null".equals(item.path("type").asText()) && item.size() == 1 ) {
                hasNullType = true;
            }
        }
        return hasRef && hasNullType;
    }

    private PreprocessingStepResult noChanges( final JsonNode input )
    {
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
                return false;
            }
        };
    }
}
