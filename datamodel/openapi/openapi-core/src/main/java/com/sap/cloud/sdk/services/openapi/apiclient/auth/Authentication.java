/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apiclient.auth;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * Representing a method to authenticate at the REST API
 */
public interface Authentication
{
    /**
     * Apply authentication settings to header and / or query parameters.
     *
     * @param queryParams
     *            The query parameters for the request
     * @param headerParams
     *            The header parameters for the request
     */
    void applyToParams( @Nonnull MultiValueMap<String, String> queryParams, @Nonnull HttpHeaders headerParams );
}
