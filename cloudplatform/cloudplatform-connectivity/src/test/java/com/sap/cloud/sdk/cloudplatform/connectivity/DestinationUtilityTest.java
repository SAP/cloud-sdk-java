/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.BASIC_AUTHENTICATION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_JWT_BEARER;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_SAML_BEARER_ASSERTION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.OAUTH2_USER_TOKEN_EXCHANGE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.SAML_ASSERTION;
import static com.sap.cloud.sdk.cloudplatform.connectivity.AuthenticationType.SAP_ASSERTION_SSO;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.AUTH_TYPE;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.AUTH_TYPE_FALLBACK;
import static com.sap.cloud.sdk.cloudplatform.connectivity.DestinationProperty.SYSTEM_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

class DestinationUtilityTest
{
    @Test
    void testAuthenticationTypeIsExtractedFromAuthTypeProperty()
    {
        final DestinationProperties destination = mockDestination();
        mockDestinationProperty(destination, AUTH_TYPE, BASIC_AUTHENTICATION);

        DestinationUtility.requiresUserTokenExchange(destination);

        verify(destination, times(1)).get(eq(AUTH_TYPE));
    }

    @Test
    void testAuthenticationTypeIsExtractedFromAuthTypeFallbackProperty()
    {
        final DestinationProperties destination = mockDestination();
        mockDestinationProperty(destination, AUTH_TYPE_FALLBACK, BASIC_AUTHENTICATION);

        DestinationUtility.requiresUserTokenExchange(destination);

        verify(destination, times(1)).get(eq(AUTH_TYPE));
        verify(destination, times(1)).get(eq(DestinationProperty.AUTH_TYPE_FALLBACK));
    }

    @Test
    void testAuthenticationTypeIsExtractedFromAuthTypePropertyEvenIfFallbackExists()
    {
        final DestinationProperties destination = mockDestination();
        mockDestinationProperty(destination, AUTH_TYPE, BASIC_AUTHENTICATION);
        mockDestinationProperty(destination, AUTH_TYPE_FALLBACK, BASIC_AUTHENTICATION);

        DestinationUtility.requiresUserTokenExchange(destination);

        verify(destination, times(1)).get(eq(AUTH_TYPE));
        verify(destination, times(0)).get(eq(DestinationProperty.AUTH_TYPE_FALLBACK));
    }

    @Test
    void testNoUserTokenExchangeRequiredWithoutAuthenticationType()
    {
        final DestinationProperties destination = mockDestination();

        assertThat(DestinationUtility.requiresUserTokenExchange(destination)).isFalse();

        verify(destination, times(1)).get(eq(AUTH_TYPE));
        verify(destination, times(1)).get(eq(DestinationProperty.AUTH_TYPE_FALLBACK));
    }

    @Test
    void testSystemUserIsExtracted()
    {
        final DestinationProperties destination = mockDestination();
        mockDestinationProperty(destination, AUTH_TYPE, BASIC_AUTHENTICATION);
        mockDestinationProperty(destination, SYSTEM_USER, "user");

        DestinationUtility.requiresUserTokenExchange(destination);

        verify(destination, times(1)).get(eq(SYSTEM_USER));
    }

    @Test
    void testDetectPropertyExtractionRegressions()
    {
        // if this test ever fails, chances are we updated our implementation without also updating this test class

        final DestinationProperties destination = mockDestination();
        mockDestinationProperty(destination, AUTH_TYPE_FALLBACK, BASIC_AUTHENTICATION);
        mockDestinationProperty(destination, SYSTEM_USER, "user");

        DestinationUtility.requiresUserTokenExchange(destination);

        verify(destination, times(1)).get(eq(AUTH_TYPE));
        verify(destination, times(1)).get(eq(AUTH_TYPE_FALLBACK));
        verify(destination, times(1)).get(eq(SYSTEM_USER));
        verifyNoMoreInteractions(destination);
    }

    @Test
    void testRequiresUserTokenExchangeWithoutSystemUser()
    {
        final List<AuthenticationType> exchangeNeeded =
            Arrays
                .asList(
                    OAUTH2_JWT_BEARER,
                    OAUTH2_SAML_BEARER_ASSERTION,
                    SAP_ASSERTION_SSO,
                    OAUTH2_USER_TOKEN_EXCHANGE,
                    SAML_ASSERTION);

        for( final AuthenticationType authenticationType : AuthenticationType.values() ) {
            final boolean expected = exchangeNeeded.contains(authenticationType);
            final boolean actual = DestinationUtility.requiresUserTokenExchange(authenticationType, null);

            assertThat(actual)
                .withFailMessage(
                    "Expected result for '%s' to be '%s', but actually was '%s'.",
                    authenticationType,
                    expected,
                    actual)
                .isEqualTo(expected);
        }
    }

    @Test
    void testRequiresUserTokenExchangeWithSystemUser()
    {
        final List<AuthenticationType> exchangeNeeded =
            Arrays.asList(OAUTH2_JWT_BEARER, OAUTH2_USER_TOKEN_EXCHANGE, SAML_ASSERTION);

        for( final AuthenticationType authenticationType : AuthenticationType.values() ) {
            final boolean expected = exchangeNeeded.contains(authenticationType);
            final boolean actual = DestinationUtility.requiresUserTokenExchange(authenticationType, "user");

            assertThat(actual)
                .withFailMessage(
                    "Expected result for '%s' to be '%s', but actually was '%s'.",
                    authenticationType,
                    expected,
                    actual)
                .isEqualTo(expected);
        }
    }

    @Nonnull
    @SuppressWarnings( "unchecked" )
    private static DestinationProperties mockDestination()
    {
        final DestinationProperties destinationProperties = mock(DestinationProperties.class);
        when(destinationProperties.get((DestinationPropertyKey<Object>) any())).thenReturn(Option.none());

        return destinationProperties;
    }

    private static <T> void mockDestinationProperty(@Nonnull final DestinationProperties destination, @Nonnull final DestinationPropertyKey<T> propertyKey, @Nonnull final T propertyValue) {
        when(destination.get(eq(propertyKey))).thenReturn(Option.of(propertyValue));
    }
}
