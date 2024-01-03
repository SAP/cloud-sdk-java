/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 protocol to extract principal from current auth token.
 */
@Slf4j
class OAuth2AuthTokenPrincipalExtractor implements PrincipalExtractor
{
    private static final String JWT_CLIENT_ID_CLAIM = "client_id";
    private static final String JWT_USER_NAME_CLAIM = "user_name";

    private static final String JWT_GRANT_TYPE_CLAIM = "grant_type";

    private final Map<String, CheckedFunction1<DecodedJWT, String>> grantTypeToPrincipalIdExtractor = new HashMap<>();

    OAuth2AuthTokenPrincipalExtractor()
    {
        grantTypeToPrincipalIdExtractor.put("password", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("client_credentials", jwt -> jwt.getClaim(JWT_CLIENT_ID_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("authorization_code", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor.put("user_token", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor
            .put("urn:ietf:params:oauth:grant-type:saml2-bearer", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
        grantTypeToPrincipalIdExtractor
            .put("urn:ietf:params:oauth:grant-type:jwt-bearer", jwt -> jwt.getClaim(JWT_USER_NAME_CLAIM).asString());
    }

    void setIdExtractorFunction(
        @Nonnull final String grantType,
        @Nonnull final CheckedFunction1<DecodedJWT, String> principalIdExtractor )
    {
        final CheckedFunction1<DecodedJWT, String> previousExtractorFunction =
            grantTypeToPrincipalIdExtractor.put(grantType, principalIdExtractor);

        if( log.isDebugEnabled() ) {
            if( previousExtractorFunction != null ) {
                log.debug("Replaced the logic for grant type '" + grantType + "' with a new one.");
            } else {
                log.debug("Added initial logic for grant type '" + grantType + "'.");
            }
        }
    }

    @Nonnull
    private Try<String> getPrincipalId( @Nonnull final DecodedJWT jwt )
    {
        return Try.of(() -> {
            final Claim grantTypeClaim = jwt.getClaim(JWT_GRANT_TYPE_CLAIM);
            if( grantTypeClaim.isMissing() || grantTypeClaim.isNull() ) {
                throw new PrincipalAccessException("The current JWT does not contain any grant type.");
            }

            if( log.isDebugEnabled() ) {
                if( grantTypeToPrincipalIdExtractor.isEmpty() ) {
                    log
                        .debug(
                            "There is no logic registered for any grant type, so no principal will get extracted from the JWT.");
                } else {
                    log
                        .debug(
                            "To extract a principal from JWT the following grant types will get handled: {}",
                            grantTypeToPrincipalIdExtractor.keySet());
                }
            }

            final String grantType = grantTypeClaim.asString();
            final CheckedFunction1<DecodedJWT, String> idSupplier = grantTypeToPrincipalIdExtractor.get(grantType);

            if( idSupplier == null ) {
                throw new PrincipalAccessException("There is no reader registered for grant type '" + grantType + "'.");
            }

            final String principalId;
            try {
                principalId = idSupplier.apply(jwt);
            }
            // The vavr CheckedFunction throws a throwable, so we need to catch/handle it
            catch( final Throwable t ) { // ALLOW CATCH THROWABLE
                throw new PrincipalAccessException("Could not read id for grant type " + grantType + " from JWT.", t);
            }

            if( principalId == null ) {
                throw new PrincipalAccessException(
                    "The principalId for grant type " + grantType + " must not be null.");
            }

            log.debug("Extracted principal '{}' from the current JWT.", principalId);
            return principalId;
        });
    }

    @Override
    @Nonnull
    public Try<Principal> tryGetCurrentPrincipal()
    {
        final Try<DecodedJWT> jwtTry = AuthTokenAccessor.tryGetCurrentToken().map(AuthToken::getJwt);

        if( jwtTry.isFailure() ) {
            return Try.failure(jwtTry.getCause());
        }

        final DecodedJWT jwt = jwtTry.get();

        final Try<String> principalId = getPrincipalId(jwt);

        if( principalId.isFailure() ) {
            return Try.failure(principalId.getCause());
        }

        return Try.of(() -> new DefaultPrincipal(principalId.get()));
    }
}
