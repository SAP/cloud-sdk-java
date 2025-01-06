package com.sap.cloud.sdk.datamodel.metadata.generator;

/**
 * The Service Type denotes the protocol of the service, like OData V2, OData V4, REST or SOAP.
 */
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
