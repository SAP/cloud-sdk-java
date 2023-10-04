/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Metadata about OData API usage.
 */
@Value
@Beta
@Builder
public class ODataApiUsageMetadata implements ApiUsageMetadata
{
    @Nonnull
    String qualifiedServiceInterfaceName; // e.g. "com.sap.cloud.BusinessPartnerService"

    @Nonnull
    String qualifiedServiceClassName; // e.g. "com.sap.cloud.DefaultBusinessPartnerService"

    @Nonnull
    @Singular
    List<MethodArgument> serviceConstructorArguments; // for future, empty by default

    @Nonnull
    @Singular
    List<Invocation> serviceMethodInvocations; // e.g. top(5).executeRequest(destination)

    @Nonnull
    String qualifiedServiceMethodResult; // e.g. java.util.List<com.sap.cloud.Businesspartner>
}
