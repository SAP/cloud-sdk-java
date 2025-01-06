/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

/**
 * Represents the behalf upon which an outbound request can be made.
 *
 * @since 4.10.0
 */
public enum OnBehalfOf
{
    /**
     * A technical user for the provider account.
     *
     * @since 4.10.0
     */
    TECHNICAL_USER_PROVIDER,
    // TECHNICAL_USER_SUBSCRIBER,
    /**
     * A technical user based on tenant set in the current context.
     *
     * @since 4.10.0
     */
    TECHNICAL_USER_CURRENT_TENANT,
    /**
     * A named user based on the auth token set in the current context.
     *
     * @since 4.10.0
     */
    NAMED_USER_CURRENT_TENANT
}
