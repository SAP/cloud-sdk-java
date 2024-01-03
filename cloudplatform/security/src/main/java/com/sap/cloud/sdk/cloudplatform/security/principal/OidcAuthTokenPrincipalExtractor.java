/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import javax.annotation.Nonnull;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.control.Try;

/**
 * OpenID Connect protocol to extract principal from current auth token.
 */
class OidcAuthTokenPrincipalExtractor implements PrincipalExtractor
{
    private static final String JWT_USER_UUID_CLAIM = "user_uuid";

    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        final Try<DecodedJWT> jwtTry = AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt);

        if( jwtTry.isFailure() ) {
            return Try.failure(jwtTry.getCause());
        }

        final DecodedJWT jwt = jwtTry.get();

        final Try<String> principalIdTry = tryGetPrincipalId(jwt);
        if( principalIdTry.isFailure() ) {
            return Try.failure(principalIdTry.getCause());
        }
        final String principalId = principalIdTry.get();

        return Try.of(() -> new DefaultPrincipal(principalId));
    }

    private Try<String> tryGetPrincipalId( @Nonnull final DecodedJWT jwt )
    {
        return Try.of(() -> {
            final Claim userUuidClaim = jwt.getClaim(JWT_USER_UUID_CLAIM);

            if( userUuidClaim.isMissing() || userUuidClaim.isNull() ) {
                throw new PrincipalAccessException("The current JWT does not contain the IAS user uuid.");
            }

            return userUuidClaim.asString();
        });
    }
}
