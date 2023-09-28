package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * OpenID Connect protocol to extract principal from current auth token.
 */
@SuppressWarnings( "deprecation" )
class OidcAuthTokenPrincipalExtractor implements PrincipalExtractor
{
    private static final String JWT_USER_UUID_CLAIM = "user_uuid";
    private static final String JWT_AUDIENCES_CLAIM = "aud";

    private static final Set<com.sap.cloud.sdk.cloudplatform.security.Authorization> EMPTY_AUTHORIZATIONS =
        Collections.emptySet();
    private static final Map<String, PrincipalAttribute> EMPTY_ATTRIBUTES = Collections.emptyMap();

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

        final Set<com.sap.cloud.sdk.cloudplatform.security.Audience> audiences =
            tryGetAudiences(jwt).getOrElse(Collections::emptySet);

        return Try
            .of(
                () -> new ScpCfPrincipal(
                    principalId,
                    EMPTY_AUTHORIZATIONS,
                    EMPTY_AUTHORIZATIONS,
                    audiences,
                    EMPTY_ATTRIBUTES));
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

    private Try<Set<com.sap.cloud.sdk.cloudplatform.security.Audience>> tryGetAudiences( @Nonnull final DecodedJWT jwt )
    {
        return Try
            .of(
                () -> Option
                    .of(jwt.getClaim(JWT_AUDIENCES_CLAIM).asList(String.class))
                    .get()
                    .stream()
                    .map(com.sap.cloud.sdk.cloudplatform.security.Audience::new)
                    .collect(Collectors.toSet()));
    }
}
