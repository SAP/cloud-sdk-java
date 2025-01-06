/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apiclient.auth;

/**
 * Supported OAuth flows to authenticate at the REST API
 */
public enum OAuthFlow
{
    /**
     * Authenticate with an access code
     */
    accessCode,

    /**
     * Authenticate implicitly
     */
    implicit,

    /**
     * Authenticate with password
     */
    password,

    /**
     * Authenticate as application
     */
    application
}
