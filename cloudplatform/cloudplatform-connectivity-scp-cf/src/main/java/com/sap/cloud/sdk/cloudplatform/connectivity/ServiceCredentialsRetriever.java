/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.net.URI;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.cloud.sdk.cloudplatform.ScpCfCloudPlatform;
import com.sap.cloud.sdk.cloudplatform.security.ClientCertificate;
import com.sap.cloud.sdk.cloudplatform.security.ClientCredentials;
import com.sap.cloud.sdk.cloudplatform.security.Credentials;
import com.sap.cloud.sdk.cloudplatform.security.exception.TokenRequestFailedException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class ServiceCredentialsRetriever
{
    private static final String SERVICE_CLIENT_ID = "clientid";
    private static final String SERVICE_CLIENT_SECRET = "clientsecret";

    private static final String SERVICE_CERTIFICATE = "certificate";
    private static final String SERVICE_KEY = "key";

    private static final String XSUAA_URI = "url";
    private static final String XSUAA_TYPE = "credential-type";

    OAuth2Credentials getCredentials( final String serviceName )
        throws TokenRequestFailedException
    {
        try {
            final ScpCfCloudPlatform platform = ScpCfCloudPlatform.getInstanceOrThrow();
            final JsonObject serviceCredentials = platform.getServiceCredentials(serviceName);

            return getCredentials(serviceCredentials);
        }
        catch( final Exception e ) {
            throw new TokenRequestFailedException(
                String
                    .format(
                        "Failed to get %s service client identifier and secret. Please make sure to correctly bind your application to a %s service instance.",
                        serviceName,
                        serviceName),
                e);
        }
    }

    OAuth2Credentials getCredentials( final JsonObject serviceCredentials )
    {
        final JsonElement type = serviceCredentials.get(XSUAA_TYPE);
        final boolean isX509 = type != null && type.isJsonPrimitive() && "x509".equals(type.getAsString());
        final Credentials credentials =
            isX509 ? getClientCertificate(serviceCredentials) : getClientCredentials(serviceCredentials);

        final String oauthUri =
            isX509 ? serviceCredentials.get("certurl").getAsString() : serviceCredentials.get(XSUAA_URI).getAsString();
        return new OAuth2Credentials(credentials, URI.create(oauthUri));
    }

    private static ClientCertificate getClientCertificate( final JsonObject serviceCredentials )
    {
        final String clientId = serviceCredentials.get(SERVICE_CLIENT_ID).getAsString();
        final String certificate = serviceCredentials.get(SERVICE_CERTIFICATE).getAsString();
        final String key = serviceCredentials.get(SERVICE_KEY).getAsString();
        return new ClientCertificate(clientId, certificate, key);
    }

    private static ClientCredentials getClientCredentials( final JsonObject serviceCredentials )
    {
        final String clientId = serviceCredentials.get(SERVICE_CLIENT_ID).getAsString();
        final String clientSecret = serviceCredentials.get(SERVICE_CLIENT_SECRET).getAsString();
        return new ClientCredentials(clientId, clientSecret);
    }

    @RequiredArgsConstructor
    @Getter
    static class OAuth2Credentials
    {
        private final Credentials credentials;
        private final URI uri;
    }
}
