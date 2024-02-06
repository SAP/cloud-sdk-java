/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;

import io.vavr.control.Try;

@Isolated
class RequestHeaderAccessorTest
{
    @BeforeEach
    @AfterEach
    void resetAccessor()
    {
        RequestHeaderAccessor.setHeaderFacade(null);
        RequestHeaderAccessor.setFallbackHeaderContainer(null);
    }

    @Test
    void testFacadeIsCalled()
    {
        final RequestHeaderFacade mockedFacade = mock(RequestHeaderFacade.class);
        when(mockedFacade.tryGetRequestHeaders()).thenReturn(Try.success(DefaultRequestHeaderContainer.EMPTY));
        RequestHeaderAccessor.setHeaderFacade(mockedFacade);

        final RequestHeaderContainer headers = RequestHeaderAccessor.getHeaderContainer();

        assertThat(headers).isNotNull();
        assertThat(headers.getHeaderNames()).isEmpty();

        verify(mockedFacade, times(1)).tryGetRequestHeaders();
    }

    @Test
    void testExecuteWithCustomHeaders()
    {
        final RequestHeaderContainer expectedHeaders =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader("header#1", "Value")
                .withHeader("header#2", "Value")
                .build();

        final RequestHeaderContainer actualHeaders =
            RequestHeaderAccessor
                .executeWithHeaderContainer(expectedHeaders, RequestHeaderAccessor::getHeaderContainer);

        assertThat(actualHeaders).isNotNull();
        assertThat(actualHeaders).isEqualTo(expectedHeaders);
    }

    @Test
    void testHeadersAreInheritedByChildContext()
    {
        final RequestHeaderContainer expectedHeaders =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader("header#1", "Value1-1", "Value1-2")
                .withHeader("header#2", "Value")
                .build();

        final RequestHeaderContainer actualHeaders =
            RequestHeaderAccessor
                .executeWithHeaderContainer(
                    expectedHeaders,
                    () -> ThreadContextExecutors.submit(RequestHeaderAccessor::getHeaderContainer).get());

        assertThat(actualHeaders).isNotNull();
        assertThat(actualHeaders).isEqualTo(expectedHeaders);
    }

    @Test
    void testFallbackIsUsed()
    {
        final RequestHeaderFacade failingFacade = mock(RequestHeaderFacade.class);
        when(failingFacade.tryGetRequestHeaders()).thenReturn(Try.failure(new ThreadContextAccessException()));

        final RequestHeaderContainer fallbackHeaders =
            DefaultRequestHeaderContainer
                .builder()
                .withHeader("header#1", "Value")
                .withHeader("header#2", "Value")
                .build();

        RequestHeaderAccessor.setHeaderFacade(failingFacade);
        RequestHeaderAccessor.setFallbackHeaderContainer(() -> fallbackHeaders);

        final Try<RequestHeaderContainer> actualHeaders = RequestHeaderAccessor.tryGetHeaderContainer();

        VavrAssertions.assertThat(actualHeaders).isSuccess();
        assertThat(actualHeaders.get()).isEqualTo(fallbackHeaders);

        verify(failingFacade, times(1)).tryGetRequestHeaders();
    }

    @Test
    void testExecuteWithThrowsExceptionIfCustomFacadeIsUsed()
    {
        final RequestHeaderFacade customFacade = () -> Try.success(RequestHeaderContainer.EMPTY);
        assertThat(customFacade).isNotInstanceOf(DefaultRequestHeaderFacade.class);

        RequestHeaderAccessor.setHeaderFacade(customFacade);

        assertThatThrownBy(
            () -> RequestHeaderAccessor.executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> "foo"))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasMessageContaining("https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext");
    }

    @Test
    void testExecuteWithSucceedsIfSubTypeOfDefaultFacadeIsUsed()
    {
        final RequestHeaderFacade customFacade = spy(DefaultRequestHeaderFacade.class);
        assertThat(customFacade).isInstanceOf(DefaultRequestHeaderFacade.class);

        RequestHeaderAccessor.setHeaderFacade(customFacade);

        assertThat(RequestHeaderAccessor.executeWithHeaderContainer(RequestHeaderContainer.EMPTY, () -> "foo"))
            .isEqualTo("foo");
    }
}
