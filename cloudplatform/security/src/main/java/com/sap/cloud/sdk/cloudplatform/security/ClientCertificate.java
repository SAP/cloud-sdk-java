package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import lombok.Value;

/**
 * Simple credential implementation based on a clientId and certificate.
 */
@Value
public class ClientCertificate implements Credentials
{
    @Nonnull
    String clientId;

    @Nonnull
    String certificate;

    @Nonnull
    String key;

    @Override
    @Nonnull
    public String toString()
    {
        return "ClientCertificate(clientId=" + clientId + ", certificate=(hidden), key=(hidden))";
    }
}
