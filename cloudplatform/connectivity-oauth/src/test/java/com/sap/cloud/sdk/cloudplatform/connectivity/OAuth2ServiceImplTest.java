/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationOAuthTokenException;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.security.config.ClientCredentials;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import lombok.SneakyThrows;

public class OAuth2ServiceImplTest
{
    private static final ResilienceConfiguration NO_RESILIENCE =
        ResilienceConfiguration.empty(OAuth2ServiceImplTest.class.getName() + "_empty");

    @Test
    @SneakyThrows
    public void testRetrieveAccessTokenHandlesNullResponse()
    {
        final XsuaaTokenFlows tokenFlows = mock(XsuaaTokenFlows.class);
        final ClientCredentialsTokenFlow clientCredentialsTokenFlows = mock(ClientCredentialsTokenFlow.class);

        when(tokenFlows.clientCredentialsTokenFlow()).thenReturn(clientCredentialsTokenFlows);
        doReturn(clientCredentialsTokenFlows).when(clientCredentialsTokenFlows).zoneId(anyString());

        // this is the crucial part:
        // the token flow returns null instead of a token BUT does not throw an exception.
        // as per API contract, that seems to be a valid outcome.
        doReturn(null).when(clientCredentialsTokenFlows).execute();

        final ClientCredentials identity = new ClientCredentials("clientid", "clientsecret");
        final OAuth2ServiceImpl sut =
            spy(new OAuth2ServiceImpl("some.uri", identity, OnBehalfOf.TECHNICAL_USER_PROVIDER));
        doReturn(tokenFlows).when(sut).getFlows();

        assertThatThrownBy(() -> sut.retrieveAccessToken(NO_RESILIENCE))
            .isExactlyInstanceOf(DestinationOAuthTokenException.class)
            .hasMessageContaining("OAuth2 token request failed");
    }
}
