package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nullable;

import lombok.Value;

@Value
class InputProperties
{
    @Nullable
    String mavenGroupId;
    @Nullable
    String mavenArtifactId;
    @Nullable
    String featureDocumentationUri;
    @Nullable
    String libraryVersion;
    @Nullable
    String generatorVersion;
}
