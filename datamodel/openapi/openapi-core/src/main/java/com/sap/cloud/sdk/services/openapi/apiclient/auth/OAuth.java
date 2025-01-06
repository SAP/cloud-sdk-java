package com.sap.cloud.sdk.services.openapi.apiclient.auth;

import javax.annotation.Nonnull;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * Authentication at the REST API via an OAuth access token
 */
public class OAuth implements Authentication
{
    private String accessToken;

    /**
     * Get the access token
     *
     * @return The access token
     */
    @Nonnull
    public String getAccessToken()
    {
        return accessToken;
    }

    /**
     * Set the access token
     *
     * @param accessToken
     *            The access token
     */
    public void setAccessToken( @Nonnull final String accessToken )
    {
        this.accessToken = accessToken;
    }

    @Override
    public void applyToParams(
        @Nonnull final MultiValueMap<String, String> queryParams,
        @Nonnull final HttpHeaders headerParams )
    {
        if( accessToken != null ) {
            headerParams.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        }
    }
}
