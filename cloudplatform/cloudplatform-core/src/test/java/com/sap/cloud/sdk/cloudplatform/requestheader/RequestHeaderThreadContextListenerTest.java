/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.requestheader;

import static com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderThreadContextListener.PROPERTY_REQUEST_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.exception.RequestHeadersAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextAccessException;

import io.vavr.control.Try;

class RequestHeaderThreadContextListenerTest
{
    private static final RequestHeaderContainer PARENT_HEADERS =
        DefaultRequestHeaderContainer.builder().withHeader("ParentHeader", "ParentValue").build();

    private static final RequestHeaderContainer CHILD_HEADERS =
        DefaultRequestHeaderContainer.builder().withHeader("ChildHeader", "ChildValue").build();

    @Test
    void testListenerWithParentThreadContext()
    {
        // sanity check, no parent thread context
        assertThat(ThreadContextAccessor.tryGetCurrentContext()).isEmpty();

        // start with empty thread context, without listeners
        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() ->
        // invoke executor with default listeners
        ThreadContextExecutor.fromCurrentContext().execute(() -> {
            final Try<RequestHeaderContainer> headers =
                ThreadContextAccessor.getCurrentContext().getPropertyValue(PROPERTY_REQUEST_HEADERS);

            assertThat(headers.isFailure()).isTrue();
            assertThat(headers.getCause()).isInstanceOf(RequestHeadersAccessException.class);
            assertThat(headers.getCause()).hasMessageContaining("Failed to get current request headers.");
        }));
    }

    @Test
    void testListenerWithoutParentThreadContext()
    {
        // sanity check, no parent thread context
        assertThat(ThreadContextAccessor.tryGetCurrentContext()).isEmpty();

        // start with empty thread context
        ThreadContextExecutor.fromNewContext().execute(() -> {
            final Try<RequestHeaderContainer> headers =
                ThreadContextAccessor.getCurrentContext().getPropertyValue(PROPERTY_REQUEST_HEADERS);
            assertThat(headers.isFailure()).isTrue();
            assertThat(headers.getCause()).isInstanceOf(RequestHeadersAccessException.class);
            assertThat(headers.getCause()).hasMessageContaining("Failed to get current request headers.");
            assertThat(headers.getCause()).hasRootCauseInstanceOf(ThreadContextAccessException.class);
        });
    }

    @Test
    void testListenerGetHeadersViaAccessor()
    {
        // prepare accessor
        final Try<RequestHeaderContainer> TRY_HEADERS = Try.success(RequestHeaderContainer.EMPTY);
        RequestHeaderAccessor.setHeaderFacade(() -> TRY_HEADERS);

        // start with empty thread context
        ThreadContextExecutor.fromNewContext().execute(() -> {
            assertThat(ThreadContextAccessor.getCurrentContext().getPropertyValue(PROPERTY_REQUEST_HEADERS))
                .isEqualTo(TRY_HEADERS);
        });

        // reset accessor
        RequestHeaderAccessor.setHeaderFacade(null);
    }

    @Test
    void testDuplicateExistingHeaders()
    {
        final ThreadContext rootContext = new DefaultThreadContext();
        rootContext.setPropertyIfAbsent(PROPERTY_REQUEST_HEADERS, Property.of(PARENT_HEADERS));

        ThreadContextExecutor.using(rootContext).execute(() -> {
            final ThreadContext parentContext = ThreadContextAccessor.getCurrentContext();
            assertThat(parentContext).isNotSameAs(rootContext);
            assertThat(parentContext.getPropertyValue(PROPERTY_REQUEST_HEADERS).get()).isEqualTo(PARENT_HEADERS);

            ThreadContextExecutor.fromCurrentContext().execute(() -> {
                final ThreadContext childContext = ThreadContextAccessor.getCurrentContext();
                assertThat(childContext).isNotSameAs(parentContext);
                assertThat(childContext.getPropertyValue(PROPERTY_REQUEST_HEADERS).get()).isEqualTo(PARENT_HEADERS);
            });
        });
    }

    @Test
    void testBeforeInitializesSetsGivenHeaders()
    {
        final ThreadContext childContext = new DefaultThreadContext();

        final RequestHeaderThreadContextListener sut = new RequestHeaderThreadContextListener(CHILD_HEADERS);

        VavrAssertions.assertThat(childContext.getPropertyValue(PROPERTY_REQUEST_HEADERS)).isFailure();

        sut.beforeInitialize(childContext);

        VavrAssertions.assertThat(childContext.getPropertyValue(PROPERTY_REQUEST_HEADERS)).isSuccess();
        assertThat(childContext.getPropertyValue(PROPERTY_REQUEST_HEADERS).get()).isEqualTo(CHILD_HEADERS);
    }

    @Test
    void testOverrideExistingHeaders()
    {
        final ThreadContext threadContext = new DefaultThreadContext();
        threadContext.setPropertyIfAbsent(PROPERTY_REQUEST_HEADERS, Property.of(PARENT_HEADERS));

        final RequestHeaderContainer headers =
            ThreadContextExecutor
                .using(threadContext)
                .withListeners(new RequestHeaderThreadContextListener(CHILD_HEADERS))
                .execute(
                    () -> ThreadContextAccessor
                        .getCurrentContext()
                        .<RequestHeaderContainer> getPropertyValue(PROPERTY_REQUEST_HEADERS)
                        .get());

        assertThat(headers).isEqualTo(CHILD_HEADERS);
    }
}
