/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

import com.google.common.io.BaseEncoding;

/**
 * Encoder to used to encode user credentials in Base64.
 */
public class BasicAuthHeaderEncoder
{
    /**
     * Encodes the given username and password in Base64
     *
     * @param username
     *            The username to encode.
     * @param password
     *            The password to encode.
     * @return The Base64 encoded credentials.
     */
    @Nonnull
    public static String encodeUserPasswordBase64( @Nonnull final String username, @Nonnull final String password )
    {
        return BaseEncoding.base64().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Encodes the given user credentials in Base64.
     *
     * @param credentials
     *            The credentials to encode.
     * @return The Base64 encoded credentials.
     */
    @Nonnull
    public static String encodeUserPasswordBase64( @Nonnull final BasicCredentials credentials )
    {
        return encodeUserPasswordBase64(credentials.getUsername(), credentials.getPassword());
    }

    /**
     * Encodes the given client credentials in Base64.
     *
     * @param credentials
     *            The credentials to encode.
     * @return The Base64 encoded credentials.
     */
    @Nonnull
    public static String encodeClientCredentialsBase64( @Nonnull final ClientCredentials credentials )
    {
        return encodeUserPasswordBase64(credentials.getClientId(), credentials.getClientSecret());
    }
}
