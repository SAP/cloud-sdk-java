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
    private final String COMPONENTS_NODE = "components";
    private final String SCHEMAS_NODE = "schemas";

    @Nonnull
    @Override
    public PreprocessingStepResult execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper )
    {
        final JsonNode pathsNode = input.path(PATHS_NODE);
        checkForValidatorsInPaths(pathsNode);

        final JsonNode schemasNode = input.path(COMPONENTS_NODE).path(SCHEMAS_NODE);
        checkForValidatorsInSchemas(schemasNode);

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

    private void checkForValidatorsInPaths( final JsonNode pathsNode )
    {
        for( final String field : POTENTIALLY_UNSUPPORTED_VALIDATION_KEYWORDS ) {
            final boolean isUnsupported = pathsNode.findValue(field) != null;
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
                    final boolean isUnsupported = schemaChild.findValue(field) != null;
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
}
