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
    private static final String JWT_SAP_ID_TYPE_CLAIM = "sap_id_type";
    private static final String JWT_SAP_ID_TYPE_USER_VALUE = "user";
    private static final String JWT_SUB_CLAIM = "sub";
    private static final String JWT_USER_UUID_CLAIM = "user_uuid";
    private static final String JWT_EMAIL_CLAIM = "email";

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
            // First, try to use the new sap_id_type and sub claims (preferred approach)
            final String sapIdType = getClaimAsString(jwt, JWT_SAP_ID_TYPE_CLAIM);
            if( JWT_SAP_ID_TYPE_USER_VALUE.equals(sapIdType) ) {
                final String sub = getClaimAsString(jwt, JWT_SUB_CLAIM);
                if( sub != null ) {
                    return sub;
                }
            }

            // Fallback to legacy user_uuid claim
            final String userUuid = getClaimAsString(jwt, JWT_USER_UUID_CLAIM);
            if( userUuid != null ) {
                return userUuid;
            }

            // Fallback to email claim
            final String email = getClaimAsString(jwt, JWT_EMAIL_CLAIM);
            if( email != null ) {
                return email;
            }

            throw new PrincipalAccessException(
                "The current JWT does not contain a valid principal identifier. "
                    + "Expected one of: sap_id_type='user' with sub claim, user_uuid, or email.");
        });
    }

    private String getClaimAsString( @Nonnull final DecodedJWT jwt, @Nonnull final String claimName )
    {
        final Claim claim = jwt.getClaim(claimName);
        if( claim != null && !claim.isMissing() && !claim.isNull() ) {
            return claim.asString();
        }
        return null;
    }
}
