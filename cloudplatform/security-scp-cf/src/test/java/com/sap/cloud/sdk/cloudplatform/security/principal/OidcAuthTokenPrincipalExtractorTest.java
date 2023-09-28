package com.sap.cloud.sdk.cloudplatform.security.principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Collections;

import org.junit.After;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;

import io.vavr.control.Try;

@SuppressWarnings( "deprecation" )
public class OidcAuthTokenPrincipalExtractorTest
{
    @After
    public void cleanUpMockedAuthTokenFacade()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    public void testExceptionIsThrownIfAuthTokenIsNotAvailable()
    {
        mockAuthTokenFacadeWithMissingAuthToken();

        final Try<Principal> principalTry = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal();

        assertThat(principalTry.isFailure()).isTrue();
        assertThat(principalTry.getCause()).isExactlyInstanceOf(AuthTokenAccessException.class);
    }

    @Test
    public void testExceptionIsThrownIfUserUuidIsNotAvailable()
    {
        mockAuthTokenFacade(JWT.create().withAudience("audience a", "audience b"));

        final Try<Principal> principalTry = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal();

        assertThat(principalTry.isFailure()).isTrue();
        assertThat(principalTry.getCause()).isExactlyInstanceOf(PrincipalAccessException.class);
    }

    @Test
    public void testReadPrincipalWithoutAudiences()
    {
        mockAuthTokenFacade(JWT.create().withClaim("user_uuid", "principal id"));

        final Principal principal = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal().get();

        assertThat(principal.getPrincipalId()).isEqualTo("principal id");
        assertThat(principal.getAuthorizations()).isEmpty();
        assertThat(principal.getAuthorizationsByAudience()).isEmpty();
    }

    @Test
    public void testReadPrincipalWithAudiences()
    {
        mockAuthTokenFacade(
            JWT.create().withClaim("user_uuid", "principal id").withAudience("audience a", "audience b"));

        final Principal principal = new OidcAuthTokenPrincipalExtractor().tryGetCurrentPrincipal().get();

        assertThat(principal.getPrincipalId()).isEqualTo("principal id");
        assertThat(principal.getAuthorizations()).isEmpty();
        assertThat(principal.getAuthorizationsByAudience())
            .containsExactly(
                entry(new com.sap.cloud.sdk.cloudplatform.security.Audience("audience a"), Collections.emptySet()),
                entry(new com.sap.cloud.sdk.cloudplatform.security.Audience("audience b"), Collections.emptySet()));
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
