/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.datamodel.odata.client.request;

import com.google.common.annotations.Beta;

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
    MODIFY_WITH_PATCH,

    /**
     * Request to update the entity is sent with the HTTP method PATCH and its payload contains the changed fields
     * including the changes in nested non-entity type fields.
     *
     * The request payload contains only the changed fields. Navigation properties are not supported.
     *
     * @since 5.16.0
     */
    @Beta
    MODIFY_WITH_PATCH_RECURSIVE_DELTA,

    /**
     * Request to update the entity is sent with the HTTP method PATCH and its payload contains the changed fields
     * including the changes in nested non-entity type fields.
     *
     * The request payload contains the full value of complex fields for changes in any nested field. Navigation
     * properties are not supported.
     *
     * @since 5.16.0
     */
    @Beta
    MODIFY_WITH_PATCH_RECURSIVE_FULL;
}
