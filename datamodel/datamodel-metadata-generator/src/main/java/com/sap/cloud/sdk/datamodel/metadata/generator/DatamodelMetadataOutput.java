/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Value;

/**
 * Metadata about the Virtual Data Model.
 */
@Value
@Builder
class DatamodelMetadataOutput
{
    @Nonnull
    ServiceStatus serviceStatus;
    @Nonnull
    Language language = Language.JAVA;
    @Nonnull
    ApiType apiType;
    @Nullable
    PreGeneratedLibrary pregeneratedLibrary;
    @Nullable
    String apiSpecificUsage;

    enum Language
    {
        @SerializedName( "Java" )
        JAVA,
        @SerializedName( "JavaScript" )
        JAVASCRIPT
    }

    enum ApiType
    {
        @SerializedName( "OData" )
        ODATA,
        @SerializedName( "OpenAPI" )
        OPEN_API
    }

    @Value
    @Builder
    static class PreGeneratedLibrary
    {
        String groupId;
        String artifactId;
        String version;
        String compatibilityNotes;
        String description;
    }
}
