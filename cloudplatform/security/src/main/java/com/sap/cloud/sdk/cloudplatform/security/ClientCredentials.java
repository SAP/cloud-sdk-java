package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * Simple credential implementation based on a clientId and clientSecret.
 */
@Data
public class ClientCredentials implements Credentials
{
    @Nonnull
    private final String clientId;

    @Nonnull
    private final String clientSecret;

    @Override
    @Nonnull
    public String toString()
    {
        return "ClientCredentials(clientId=" + clientId + ", clientSecret=(hidden))";
    }
}
