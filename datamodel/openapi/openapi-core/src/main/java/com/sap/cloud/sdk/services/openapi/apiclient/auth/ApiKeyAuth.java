/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apiclient.auth;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * Authentication at the REST API by providing an API key
 */
public class ApiKeyAuth implements Authentication
{
    private final String location;
    private final String paramName;

    private String apiKey;
    private String apiKeyPrefix;

    /**
     * Create an instance of this authentication using an API key
     *
     * @param location
     *            The location of the API key
     * @param paramName
     *            The parameter name holding the API key
     */
    public ApiKeyAuth( @Nonnull final String location, @Nonnull final String paramName )
    {
        this.location = location;
        this.paramName = paramName;
    }

    /**
     * Get the location of the API Key
     *
     * @return The location of the API key
     */
    @Nonnull
    public String getLocation()
    {
        return location;
    }

    /**
     * Get the parameter name of the API Key
     *
     * @return The parameter name of the API key
     */
    @Nonnull
    public String getParamName()
    {
        return paramName;
    }

    /**
     * Get the API Key
     *
     * @return The API key
     */
    @Nonnull
    public String getApiKey()
    {
        return apiKey;
    }

    /**
     * Set the API key
     *
     * @param apiKey
     *            The API key
     */
    public void setApiKey( @Nonnull final String apiKey )
    {
        this.apiKey = apiKey;
    }

    /**
     * Get the API key prefix
     *
     * @return The API key prefix
     */
    @Nonnull
    public String getApiKeyPrefix()
    {
        return apiKeyPrefix;
    }

    /**
     * Set the API key prefix
     *
     * @param apiKeyPrefix
     *            The API key prefix
     */
    public void setApiKeyPrefix( @Nonnull final String apiKeyPrefix )
    {
        this.apiKeyPrefix = apiKeyPrefix;
    }

    @Override
    public void applyToParams(
        @Nonnull final MultiValueMap<String, String> queryParams,
        @Nonnull final HttpHeaders headerParams )
    {
        if( apiKey == null ) {
            return;
        }
        final String value;
        if( apiKeyPrefix != null ) {
            value = apiKeyPrefix + " " + apiKey;
        } else {
            value = apiKey;
        }
        if( location.equals("query") ) {
            queryParams.add(paramName, value);
        } else if( location.equals("header") ) {
            headerParams.add(paramName, value);
        }
    }
}
