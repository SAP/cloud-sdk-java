package com.sap.cloud.sdk.datamodel.openapi.generator;

import javax.annotation.Nonnull;

import io.swagger.v3.oas.models.OpenAPI;

final class OasVersionUtil
{
    private OasVersionUtil()
    {
    }

    static boolean isOas31( @Nonnull final OpenAPI openAPI )
    {
        final String version = openAPI.getOpenapi();
        return version != null && version.startsWith("3.1");
    }
}
