/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import javax.annotation.Nonnull;

import com.auth0.jwt.JWT;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class AuthTokenDecoderDefault implements AuthTokenDecoder
{
    @Nonnull
    @Override
    public AuthToken decode( @Nonnull final String encodedJwt )
        throws AuthTokenAccessException
    {
        return Try
            .of(() -> new AuthToken(JWT.decode(encodedJwt)))
            .getOrElseThrow(e -> new AuthTokenAccessException("Failed to decode the access token.", e));
    }
}
