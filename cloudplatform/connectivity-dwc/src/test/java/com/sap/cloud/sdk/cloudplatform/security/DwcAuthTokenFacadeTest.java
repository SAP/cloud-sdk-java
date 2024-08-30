package com.sap.cloud.sdk.cloudplatform.security;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_IAS_JWT_HEADER;
import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_JWT_HEADER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.security.exception.AuthTokenAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;

import io.vavr.control.Try;

class DwcAuthTokenFacadeTest
{
    @Test
    void testFacadeIsPickedUpAutomatically()
    {
        assertThat(AuthTokenAccessor.getAuthTokenFacade()).isInstanceOf(DwcAuthTokenFacade.class);
    }

    @Test
    void testSuccessfulAuthTokenRetrieval()
    {
        this.doTestSuccessfulAuthTokenRetrieval(DWC_JWT_HEADER);
    }

    @Test
    void testSuccessfulIasAuthTokenRetrieval()
    {
        this.doTestSuccessfulAuthTokenRetrieval(DWC_IAS_JWT_HEADER);
    }

    void doTestSuccessfulAuthTokenRetrieval(String dwcHeaderKey)
    {
        final String token = JWT.create().sign(Algorithm.none());

        final AuthToken expectedToken = new AuthToken(JWT.decode(token));
        final Map<String, String> headers = ImmutableMap.of(dwcHeaderKey, token);

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final AuthToken currentToken = AuthTokenAccessor.getCurrentToken();
            final Try<AuthToken> maybeTokenFromContext =
                currentContext.getPropertyValue(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN);

            assertThat(currentToken).isEqualTo(expectedToken);
            assertThat(maybeTokenFromContext).contains(expectedToken);
        });
    }

    @Test
    void testIasAuthTokenTakePrecedenceInRetrieval()
    {
        final String iasToken = JWT.create().sign(Algorithm.none());
        final String xsuaaToken = JWT.create().sign(Algorithm.none());

        final AuthToken expectedToken = new AuthToken(JWT.decode(iasToken));

        final Map<String, String> headers = ImmutableMap.of(DWC_IAS_JWT_HEADER, iasToken, DWC_JWT_HEADER, xsuaaToken);

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final AuthToken currentToken = AuthTokenAccessor.getCurrentToken();
            final Try<AuthToken> maybeTokenFromContext =
                    currentContext.getPropertyValue(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN);

            assertThat(currentToken).isEqualTo(expectedToken);
            assertThat(maybeTokenFromContext).contains(expectedToken);
        });
    }

    @Test
    void testUnsuccessfulAuthTokenRetrieval()
    {
        RequestHeaderAccessor.executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final Try<AuthToken> authTokenFailure = AuthTokenAccessor.tryGetCurrentToken();
            final Try<AuthToken> shouldBeFailure =
                currentContext.getPropertyValue(AuthTokenThreadContextListener.PROPERTY_AUTH_TOKEN);

            assertThat(authTokenFailure.isFailure()).isTrue();
            assertThat(authTokenFailure.getCause()).isInstanceOf(AuthTokenAccessException.class);
            assertThat(shouldBeFailure.isFailure()).isTrue();
            assertThat(authTokenFailure).isSameAs(shouldBeFailure);
        });
    }
}
