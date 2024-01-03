/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import com.sap.cloud.sdk.result.ElementName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class contains any Swagger 2.0 specific fields and how to retrieve them from the JSON Swagger file.
 */
@Data
@EqualsAndHashCode( callSuper = true )
class ServiceDetailsSwagger2 extends AbstractServiceDetails
{
    @ElementName( "basePath" )
    private String serviceUrl = "/";
}
