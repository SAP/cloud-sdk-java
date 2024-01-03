/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

/**
 * The strategy to use when updating existing entities.
 */
public enum UpdateStrategy
{
    /**
     * Request to update the entity is sent with the HTTP method PUT and its payload contains all fields of the entity,
     * regardless which of them have been changed
     */
    REPLACE_WITH_PUT,

    /**
     * Request to update the entity is sent with the HTTP method PATCH and its payload contains the changed fields only.
     */
    MODIFY_WITH_PATCH;
}
