/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;

class BasicAuthenticationThreadContextListenerTest
{
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final BasicCredentials BASIC_CREDENTIALS = new BasicCredentials(USERNAME, PASSWORD);

    private BasicAuthenticationFacade mockFacade;

    @BeforeEach
    void setUp()
    {
        mockFacade = mock(BasicAuthenticationFacade.class);
        when(mockFacade.tryGetBasicCredentials()).thenReturn(Try.failure(new UnsupportedOperationException()));
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(mockFacade);
    }

    @AfterEach
    void tearDown()
    {
        BasicAuthenticationAccessor.setBasicAuthenticationFacade(null);
    }

    @Test
    void afterInitializeShouldReadBasicCredentialsFromParentContext()
    {
        final ThreadContext rootContext = new DefaultThreadContext();
        rootContext
            .setPropertyIfAbsent(
                BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER,
                Property.of(BASIC_CREDENTIALS));

        // use same context on 1st level
        ThreadContextExecutor.using(rootContext).execute(() -> {
            final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
            assertThat(parentContext).isNotSameAs(rootContext);
            assertThat(
                parentContext
                    .getPropertyValue(BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER)
                    .get())
                .isEqualTo(BASIC_CREDENTIALS);

            // copy context on 2nd level
            ThreadContextExecutor.fromCurrentContext().execute(() -> {
                final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                assertThat(childContext).isNotSameAs(rootContext);
                assertThat(childContext).isNotSameAs(parentContext);
                assertThat(
                    childContext
                        .getPropertyValue(BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER)
                        .get())
                    .isEqualTo(BASIC_CREDENTIALS);
            });
        });
    }

    @Test
    void afterInitializeShouldGetBasicCredentialsFromAccessorOnNullParentContext()
    {
        when(mockFacade.tryGetBasicCredentials()).thenReturn(Try.success(BASIC_CREDENTIALS));

        final DefaultThreadContext contextToFill = new DefaultThreadContext();
        new BasicAuthenticationThreadContextListener().afterInitialize(contextToFill);

        assertThat(
            contextToFill.getPropertyValue(BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER).get())
                .isEqualTo(BASIC_CREDENTIALS);
        verify(mockFacade).tryGetBasicCredentials();
    }

    @Test
    void afterInitializeShouldGetBasicCredentialsFromAccessorOnEmptyParentContext()
    {
        when(mockFacade.tryGetBasicCredentials()).thenReturn(Try.success(BASIC_CREDENTIALS));

        final ThreadContext contextToFill = new DefaultThreadContext();
        new BasicAuthenticationThreadContextListener().afterInitialize(contextToFill);

        assertThat(
            contextToFill.getPropertyValue(BasicAuthenticationThreadContextListener.PROPERTY_BASIC_AUTH_HEADER).get())
                .isEqualTo(BASIC_CREDENTIALS);
        verify(mockFacade).tryGetBasicCredentials();
    }
}
