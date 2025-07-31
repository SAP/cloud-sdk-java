package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.util.StringUtils;

import lombok.Data;

/**
 * Simple credential implementation based on a single token.
 */
@Data
public class BearerCredentials implements Credentials
{
    @Nonnull
    private static final String BEARER_PREFIX = "Bearer "; // casing will be ignored

    @Nonnull
    private final String token;

    @Nonnull
    private final String httpHeaderValue;

    /**
     * Creates a new instance based on the given token.
     * <p>
     * If the given {@code token} starts with the prefix {@code "Bearer "} (ignoring casing), the prefix will be removed
     * from the token. The {@link #getHttpHeaderValue()}, on the other hand, will always contain the prefix.
     * </p>
     *
     * @param token
     *            The token to use for authentication.
     */
    public BearerCredentials( @Nonnull final String token )
    {
        final String trimmedToken = token.trim();
        if( StringUtils.startsWithIgnoreCase(trimmedToken, BEARER_PREFIX) ) {
            this.token = trimmedToken.substring(BEARER_PREFIX.length()).trim();
        } else {
            this.token = trimmedToken;
        }
        httpHeaderValue = BEARER_PREFIX + this.token;
    }

    @Override
    @Nonnull
    public String toString()
    {
        return "BearerCredentials(token=(hidden))";
    }
}
