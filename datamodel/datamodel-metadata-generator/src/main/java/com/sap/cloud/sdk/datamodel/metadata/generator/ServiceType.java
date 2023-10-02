/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import com.google.common.annotations.Beta;

/**
 * The Service Type denotes the protocol of the service, like OData V2, OData V4, REST or SOAP.
 */
@Beta
public enum ServiceType
{
    /**
     * OData V2.
     */
    ODATA_V2,
    /**
     * OData V4.
     */
    ODATA_V4,
    /**
     * REST.
     */
    REST,
    /**
     * SOAP.
     */
    SOAP;
}
