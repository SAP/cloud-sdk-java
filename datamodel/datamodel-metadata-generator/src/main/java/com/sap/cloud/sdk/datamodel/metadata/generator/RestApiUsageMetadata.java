/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.util.List;

import javax.annotation.Nonnull;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Metadata about REST API Usage.
 */
@Value
@Builder
public class RestApiUsageMetadata implements ApiUsageMetadata
{
    @Nonnull
    String qualifiedServiceClassName; // e.g. "com.sap.cloud.DefaultBusinessPartnerService"

    @Nonnull
    @Singular
    List<MethodArgument> serviceConstructorArguments; // e.g. new Api(destination)

    @Nonnull
    @Singular
    List<Invocation> serviceMethodInvocations; // e.g. top(5).executeRequest(destination)

    @Nonnull
    String qualifiedServiceMethodResult; // e.g. java.util.List<com.sap.cloud.Businesspartner>

    @Nonnull
    @Override
    public String getQualifiedServiceInterfaceName()
    {
        return getQualifiedServiceClassName();
    }
}
