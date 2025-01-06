/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

/**
 * Interface to represent each pre-processing step done before OpenAPI client generation
 *
 */
interface PreprocessingStep
{
    /**
     *
     * @param input
     *            The input specification file parsed and represented using
     *            {@link com.fasterxml.jackson.databind.JsonNode}
     * @param objectMapper
     *            An {@link com.fasterxml.jackson.databind.ObjectMapper} instance
     * @return an instance of {@link PreprocessingStepResult}
     * @throws OpenApiGeneratorException
     */
    @Nonnull
    PreprocessingStepResult execute( @Nonnull final JsonNode input, @Nonnull final ObjectMapper objectMapper );

    interface PreprocessingStepResult
    {
        JsonNode getJsonNode();

        boolean changesApplied();
    }
}
