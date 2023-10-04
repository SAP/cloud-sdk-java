/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

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
