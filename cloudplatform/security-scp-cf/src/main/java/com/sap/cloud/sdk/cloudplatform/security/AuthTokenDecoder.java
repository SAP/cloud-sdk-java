package com.sap.cloud.sdk.cloudplatform.security;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.net.HttpHeaders;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;

import io.vavr.control.Try;

/**
 * Decoder interface to translate an encoded JWT to new instance of {@link AuthToken}.
 */
@FunctionalInterface
interface AuthTokenDecoder
{
    /**
     * Decode a JWT.
     *
     * @param encodedJwt
     *            The encoded JWT String.
     * @return A new instance of {@link AuthToken}.
     * @throws AuthTokenAccessException
     *             If decoding was not successful.
     */
    @Nonnull
    AuthToken decode( @Nonnull final String encodedJwt )
        throws AuthTokenAccessException;

    /**
     * Decode a JWT, optionally update with help of a refresh token.
     *
     * @param encodedJwt
     *            The encoded JWT String.
     * @param refreshToken
     *            The optional refresh token.
     * @return A new instance of {@link AuthToken}.
     * @throws AuthTokenAccessException
     *             If decoding was not successful.
     *
     * @deprecated AuthToken validation should be done with the Security Library.
     */
    @Nonnull
    @Deprecated
    default AuthToken decode( @Nonnull final String encodedJwt, @Nullable final String refreshToken )
        throws AuthTokenAccessException
    {
        return decode(encodedJwt);
    }

    /**
     * Try to decode a JWT from http request headers.
     *
     * @param headers
     *            The encoded JWT String.
     * @return {@link Try.Success} of {@link AuthToken} of a {@link Try.Failure}.
     */
    @Nonnull
    default Try<AuthToken> decode( @Nonnull final RequestHeaderContainer headers )
    {
        final Collection<String> headerValues = headers.getHeaderValues(HttpHeaders.AUTHORIZATION);
        if( headerValues.isEmpty() ) {
            final String message =
                "Failed to decode JWT bearer: no '" + HttpHeaders.AUTHORIZATION + "' header present in request.";
            return Try.failure(new AuthTokenAccessException(message));
        }

        if( headerValues.size() > 1 ) {
            final String message =
                "Failed to decode JWT bearer: multiple '" + HttpHeaders.AUTHORIZATION + "' headers present in request.";
            return Try.failure(new AuthTokenAccessException(message));
        }

        final String authorizationValue = headerValues.stream().findFirst().get();

        final String bearerPrefix = "bearer ";
        if( !authorizationValue.toLowerCase(Locale.ENGLISH).startsWith(bearerPrefix) ) {
            final String message =
                "Failed to decode JWT bearer: no JWT bearer present in '"
                    + HttpHeaders.AUTHORIZATION
                    + "' header of request.";
            return Try.failure(new AuthTokenAccessException(message));
        }

        final String tokenValue = authorizationValue.substring(bearerPrefix.length());
        return Try.of(() -> decode(tokenValue));
    }
}
