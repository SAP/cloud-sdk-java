/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.Map;

import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionExceptionFactory;

interface RemoteFunctionErrorMapper
{
    @Deprecated
    Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionFactory<?>> getMapping();

    @Deprecated
    Map<com.sap.cloud.sdk.s4hana.serialization.RemoteFunctionError, RemoteFunctionExceptionPriority> getPriorities();
}
