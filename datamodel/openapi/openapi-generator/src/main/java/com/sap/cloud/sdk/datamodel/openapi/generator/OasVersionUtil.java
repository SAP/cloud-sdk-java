package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.models.OpenAPI;

final class OasVersionUtil
{
    private OasVersionUtil()
    {
    }

    static boolean isOas31( @Nullable final String version )
    {
        return version != null && version.startsWith("3.1");
    }

    static boolean isOas31( @Nonnull final OpenAPI openAPI )
    {
        return isOas31(openAPI.getOpenapi());
    }

    static boolean isOas31( @Nonnull final JsonNode rootNode )
    {
        return isOas31(rootNode.path("openapi").asText(null));
    }
}
