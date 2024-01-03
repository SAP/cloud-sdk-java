/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.vavr.control.Try;

class BasicAuthenticationAccessorTest
{
    @AfterEach
    void tearDown()
    {
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(null);
    }

    @Test
    void setBasicAuthenticationFacadeShouldSetGivenFacade()
    {
        final BasicAuthenticationFacade facade = () -> Try.failure(new RuntimeException());

        BasicAuthenticationAccessor.setBasicAuthenticationFacade(facade);

        assertThat(BasicAuthenticationAccessor.getBasicAuthenticationFacade()).isSameAs(facade);
    }

    @Test
    void setBasicAuthenticationFacadeShouldResetOnNullValue()
    {
        final BasicAuthenticationFacade facade = () -> Try.failure(new RuntimeException());

        BasicAuthenticationAccessor.setBasicAuthenticationFacade(facade);
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(null);

        assertThat(BasicAuthenticationAccessor.getBasicAuthenticationFacade())
            .isInstanceOf(DefaultBasicAuthenticationFacade.class)
            .isNotEqualTo(facade);
    }

    @Test
    void tryGetCurrentBasicCredentialsShouldCallGivenFacade()
    {
        final BasicAuthenticationFacade mockFacade = mock(BasicAuthenticationFacade.class);
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(mockFacade);

        BasicAuthenticationAccessor.tryGetCurrentBasicCredentials();

        verify(mockFacade).tryGetBasicCredentials();
    }
}
