/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.services.openapi.apiclient.auth;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;

/**
 * Authentication at the OpenAPI API by providing username and password
 */
public class HttpBasicAuth implements Authentication
{
    private String username;
    private String password;

    /**
     * Get the username
     *
     * @return The username
     */
    @Nonnull
    public String getUsername()
    {
        return username;
    }

    /**
     * Set the username
     *
     * @param username
     *            The username
     */
    public void setUsername( @Nonnull final String username )
    {
        this.username = username;
    }

    /**
     * Get the password
     *
     * @return The password
     */
    @Nonnull
    public String getPassword()
    {
        return password;
    }

    /**
     * Set the password
     *
     * @param password
     *            The password
     */
    public void setPassword( @Nonnull final String password )
    {
        this.password = password;
    }

    @Override
    public void applyToParams(
        @Nonnull final MultiValueMap<String, String> queryParams,
        @Nonnull final HttpHeaders headerParams )
    {
        if( username == null && password == null ) {
            return;
        }
        final String str = (username == null ? "" : username) + ":" + (password == null ? "" : password);
        headerParams
            .add(
                HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString(str.getBytes(StandardCharsets.UTF_8)));
    }
}
