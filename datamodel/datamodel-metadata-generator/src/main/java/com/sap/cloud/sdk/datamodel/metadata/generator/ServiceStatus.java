/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import com.google.gson.annotations.SerializedName;

enum ServiceStatus
{
    @SerializedName( "certified" )
    CERTIFIED,
    @SerializedName( "verified" )
    VERIFIED,
    @SerializedName( "failed" )
    FAILED,
    @SerializedName( "unknown" )
    UNKNOWN
}
