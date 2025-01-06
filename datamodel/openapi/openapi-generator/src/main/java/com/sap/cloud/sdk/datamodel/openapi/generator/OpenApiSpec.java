package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Value;

@Value
class OpenApiSpec
{
    Path filePath;
    JsonNode jsonNode;
}
