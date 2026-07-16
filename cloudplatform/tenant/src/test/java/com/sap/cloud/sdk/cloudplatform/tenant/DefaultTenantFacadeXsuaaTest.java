package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.DefaultAuthTokenFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

import io.vavr.control.Try;

class DefaultTenantFacadeXsuaaTest
{
    @AfterEach
    void resetAuthTokenAccessor()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
    }

    @Test
    void givenATokenThenXsuaaServiceIsNotCalled()
    {
        final String userTenant = "someUserTenant";
        mockCurrentTenant(userTenant);

        final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();

        VavrAssertions.assertThat(tenantTry).isSuccess();
        assertThat(tenantTry.get().getTenantId()).isEqualTo(userTenant);
    }

    @Test
    void givenNoUserTokenAndXsuaaTenantThenExceptionIsReturned()
    {
        final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();
        VavrAssertions.assertThat(tenantTry).isFailure().failBecauseOf(TenantAccessException.class);
    }

    @Test
    @DisplayName( "Subdomains don't resolve from issuers without an http prefix" )
    void givenIssuerWithoutSchemeThenExceptionIsReturned()
    {
        final String userTenant = "someUserTenant";
        final String issuerWithoutScheme = "foo.accounts.sap.com";
        final AuthToken jwtWithTenant = createJwtWithTenantAndIssuer(userTenant, issuerWithoutScheme);

        final DefaultAuthTokenFacade mockedFacade = mock(DefaultAuthTokenFacade.class);
        when(mockedFacade.tryGetCurrentToken()).thenReturn(Try.success(jwtWithTenant));
        AuthTokenAccessor.setAuthTokenFacade(mockedFacade);

        final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();

        VavrAssertions
            .assertThat(tenantTry)
            .describedAs("issuers without an https:// prefix should lead to a failure")
            .isFailure()
            .failBecauseOf(TenantAccessException.class);
    }

    private DefaultAuthTokenFacade mockCurrentTenant( final String userTenant )
    {
        final AuthToken jwtWithTenant = createJwtWithTenant(userTenant);

        final DefaultAuthTokenFacade mockedFacade = mock(DefaultAuthTokenFacade.class);
        when(mockedFacade.tryGetCurrentToken()).thenReturn(Try.success(jwtWithTenant));
        AuthTokenAccessor.setAuthTokenFacade(mockedFacade);

        return mockedFacade;
    }

    private AuthToken createJwtWithTenant( final String tenantId )
    {
        return createJwtWithTenantAndIssuer(tenantId, "https://sudomain-of-" + tenantId + ".localhost:8080");
    }

    private AuthToken createJwtWithTenantAndIssuer( final String tenantId, final String issuer )
    {
        final String encodedJwt =
            JWT.create().withClaim("zid", tenantId).withClaim("iss", issuer).sign(Algorithm.none());
        final DecodedJWT decodedJwt = JWT.decode(encodedJwt);
        return new AuthToken(decodedJwt);
    }
}
