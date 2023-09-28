package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.time.Instant;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Access Token with expiry date.
 */
@RequiredArgsConstructor
@Getter( AccessLevel.PACKAGE )
@EqualsAndHashCode
public class AccessToken
{
    /**
     * The String representation of the access token.
     */
    @Nonnull
    private final String value;

    /**
     * The date at which the token expires.
     */
    @Nonnull
    private final Instant expiry;

    @Nonnull
    static AccessToken of( @Nonnull final OAuth2TokenResponse response )
    {
        final String accessToken = Objects.requireNonNull(response.getAccessToken(), "Access Token");
        return new AccessToken(accessToken, response.getExpiredAt());
    }

    /**
     * Check whether the access token is still valid.
     *
     * @return true if the token is valid, false if it is not.
     */
    boolean isValid()
    {
        return expiry.isAfter(Instant.now());
    }

    @Override
    @Nonnull
    public String toString()
    {
        return "AccessToken(value=(hidden), expiry=" + expiry + ")";
    }
}
