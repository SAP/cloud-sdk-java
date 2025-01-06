/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * Simple credential implementation based on a username and password.
 */
@Data
public class BasicCredentials implements Credentials
{
    @Nonnull
    private static final String BASIC_PREFIX = "Basic ";

    @Nonnull
    private final String username;

    @Nonnull
    private final String password;

    @Nonnull
    private final String httpHeaderValue;

    /**
     * Creates a new instance based on the given username and password.
     *
     * @param username
     *            The username to use for authentication.
     * @param password
     *            The password to use for authentication.
     */
    public BasicCredentials( @Nonnull final String username, @Nonnull final String password )
    {
        this.username = username;
        this.password = password;
        httpHeaderValue = BASIC_PREFIX + BasicAuthHeaderEncoder.encodeUserPasswordBase64(username, password);
    }

    @Override
    @Nonnull
    public String toString()
    {
        return "BasicCredentials(username=" + username + ", password=(hidden))";
    }
}
