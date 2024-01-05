/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.OnBehalfOf.TECHNICAL_USER_PROVIDER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import lombok.SneakyThrows;

class OAuth2ServiceTest
{
    @Test
    @SneakyThrows
    void testRetrieveAccessTokenHandlesNullResponse()
    {
        final XsuaaTokenFlows tokenFlows = mock(XsuaaTokenFlows.class);
        final ClientCredentialsTokenFlow clientCredentialsTokenFlows = mock(ClientCredentialsTokenFlow.class);
        final ClientCredentials identity = new ClientCredentials("clientid", "clientsecret");

        when(tokenFlows.clientCredentialsTokenFlow()).thenReturn(clientCredentialsTokenFlows);
        doReturn(clientCredentialsTokenFlows).when(clientCredentialsTokenFlows).zoneId(anyString());

        // this is the crucial part:
        // the token flow returns null instead of a token BUT does not throw an exception.
        // as per API contract, that seems to be a valid outcome.
        doReturn(null).when(clientCredentialsTokenFlows).execute();

        final OAuth2Service sut = spy(new OAuth2Service("some.uri", identity, TECHNICAL_USER_PROVIDER));
        doReturn(tokenFlows).when(sut).getTokenFlowFactory(isNull());

        assertThatThrownBy(sut::retrieveAccessToken)
            .isExactlyInstanceOf(DestinationOAuthTokenException.class)
            .hasMessageContaining("OAuth2 token request failed");
    }
}
