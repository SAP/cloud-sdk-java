/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.control.Try;

class OidcAuthTokenPrincipalExtractorTest
{
    @AfterEach
    void cleanUpMockedAuthTokenFacade()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    void testExceptionIsThrownIfAuthTokenIsNotAvailable()
    {
        mockAuthTokenFacadeWithMissingAuthToken();

        final Try<Principal> principalTry = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal();

        assertThat(principalTry.isFailure()).isTrue();
        assertThat(principalTry.getCause()).isExactlyInstanceOf(AuthTokenAccessException.class);
    }

    @Test
    void testExceptionIsThrownIfUserUuidIsNotAvailable()
    {
        mockAuthTokenFacade(JWT.create().withAudience("audience a", "audience b"));

        final Try<Principal> principalTry = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal();

        assertThat(principalTry.isFailure()).isTrue();
        assertThat(principalTry.getCause()).isExactlyInstanceOf(PrincipalAccessException.class);
    }

    @Test
    void testReadPrincipal()
    {
        mockAuthTokenFacade(JWT.create().withClaim("user_uuid", "principal id"));

        final Principal principal = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal().get();

        assertThat(principal.getPrincipalId()).isEqualTo("principal id");
    }

    private void mockAuthTokenFacadeWithMissingAuthToken()
    {
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.failure(new AuthTokenAccessException("Auth token not mocked.")));
    }

    private void mockAuthTokenFacade( final JWTCreator.Builder jwtBuilder )
    {
        final String encodedJwt = jwtBuilder.sign(Algorithm.none());
        final DecodedJWT decodedJwt = JWT.decode(encodedJwt);
        AuthTokenAccessor.setAuthTokenFacade(() -> Try.success(new AuthToken(decodedJwt)));
    }
}
