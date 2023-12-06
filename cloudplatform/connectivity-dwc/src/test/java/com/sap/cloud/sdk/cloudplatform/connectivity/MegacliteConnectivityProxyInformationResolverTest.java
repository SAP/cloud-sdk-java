/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.google.common.base.Charsets;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;

import lombok.SneakyThrows;

class MegacliteConnectivityProxyInformationResolverTest
{

    private static final HttpResponse successResponse;
    private static final HttpResponse failureResponse;

    static {
        successResponse = mock(HttpResponse.class);
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"))
            .when(successResponse)
            .getStatusLine();
        doReturn(new StringEntity("{ \"proxy\":\"http://some.proxy\", \"proxyAuth\":\"Bearer 1234\"}", Charsets.UTF_8))
            .when(successResponse)
            .getEntity();

        failureResponse = mock(HttpResponse.class);
        doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "NOT OK"))
            .when(failureResponse)
            .getStatusLine();
    }

    private MegacliteConnectivityProxyInformationResolver sut;

    @BeforeEach
    void setup()
    {
        sut = createSut();
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
    }

    @AfterEach
    void reset()
    {
        TenantAccessor.setTenantFacade(null);
    }

    @SneakyThrows
    @Test
    void testProxyUrlIsCachedPerTenants()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        assertThat(sut.getProxyUrl()).isEqualTo(URI.create("http://some.proxy"));

        final URI proxyUrl = TenantAccessor.executeWithTenant(new DefaultTenant("foo"), sut::getProxyUrl);
        assertThat(proxyUrl)
            .describedAs("The proxy URL should be the same across all tenants")
            .isEqualTo(sut.getProxyUrl());

        verify(sut, times(2)).getProxyInformationFromMegaclite();
    }

    @SneakyThrows
    @Test
    void testAuthTokenIsCachedPerTenant()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        final String token = sut.getAuthorizationToken();
        assertThat(token).isSameAs(sut.getAuthorizationToken()).isEqualTo("Bearer 1234");

        doReturn(failureResponse).when(sut).makeHttpRequest(any());

        TenantAccessor.executeWithTenant(new DefaultTenant("foo"), () -> {
            assertThatThrownBy(sut::getAuthorizationToken)
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(ResilienceRuntimeException.class)
                .hasRootCauseInstanceOf(IllegalStateException.class);
        });

        // sanity check that the original tenant still works and is cached
        assertThat(sut.getAuthorizationToken()).isSameAs(token);

        verify(sut, times(2)).getProxyInformationFromMegaclite();
    }

    @SneakyThrows
    @Test
    void testProxyAuthHeader()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        assertThat(sut.getHeaders(mock(DestinationRequestContext.class)))
            .contains(new Header(HttpHeaders.PROXY_AUTHORIZATION, "Bearer 1234"));
    }

    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    @SneakyThrows
    void testAuthTokenCanBeRetrievedInParallelForDifferentTenants()
    {
        final CountDownLatch firstInvocationLatch = new CountDownLatch(1);
        final CountDownLatch secondInvocationLatch = new CountDownLatch(2);
        final CountDownLatch resumeInvocationsLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            firstInvocationLatch.countDown();
            secondInvocationLatch.countDown();
            resumeInvocationsLatch.await();
            return successResponse;
        }).when(sut).makeHttpRequest(any());

        final Future<String> firstTask =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("Tenant1"),
                    () -> ThreadContextExecutors.submit(sut::getAuthorizationToken));
        firstInvocationLatch.await(); // make sure the first invocation is in progress

        final Future<String> secondTask =
            TenantAccessor
                .executeWithTenant(
                    new DefaultTenant("Tenant2"),
                    () -> ThreadContextExecutors.submit(sut::getAuthorizationToken));
        secondInvocationLatch.await(); // make sure the second invocation is in progress

        // both tasks are running in parallel
        assertThat(firstTask).isNotNull();
        assertThat(secondTask).isNotNull();
        assertThat(firstTask.isDone()).isFalse();
        assertThat(secondTask.isDone()).isFalse();

        resumeInvocationsLatch.countDown();

        assertThat(firstTask.get()).isEqualTo("Bearer 1234");
        assertThat(secondTask.get()).isEqualTo("Bearer 1234");

        verify(sut, times(2)).makeHttpRequest(any());
    }

    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    @SneakyThrows
    void testInstancesUseSeparateResilienceStates()
    {
        final MegacliteConnectivityProxyInformationResolver firstSut = createSut();
        final MegacliteConnectivityProxyInformationResolver secondSut = createSut();

        final CountDownLatch firstInvocationLatch = new CountDownLatch(1);
        final CountDownLatch secondInvocationLatch = new CountDownLatch(1);
        final CountDownLatch resumeInvocationLatch = new CountDownLatch(1);

        doAnswer(invocation -> {
            firstInvocationLatch.countDown();
            resumeInvocationLatch.await();
            return successResponse;
        }).when(firstSut).makeHttpRequest(any());

        doAnswer(invocation -> {
            secondInvocationLatch.countDown();
            resumeInvocationLatch.await();
            return successResponse;
        }).when(secondSut).makeHttpRequest(any());

        final CompletableFuture<String> firstTask = CompletableFuture.supplyAsync(firstSut::getAuthorizationToken);
        firstInvocationLatch.await();

        final CompletableFuture<String> secondTask = CompletableFuture.supplyAsync(secondSut::getAuthorizationToken);
        secondInvocationLatch.await();

        // both requests are running FOR THE SAME TENANT in parallel
        assertThat(firstTask.isDone()).isFalse();
        assertThat(secondTask.isDone()).isFalse();

        resumeInvocationLatch.countDown();

        assertThat(firstTask.get()).isEqualTo("Bearer 1234");
        assertThat(secondTask.get()).isEqualTo("Bearer 1234");

        verify(firstSut, times(1)).makeHttpRequest(any());
        verify(secondSut, times(1)).makeHttpRequest(any());
    }

    private static MegacliteConnectivityProxyInformationResolver createSut()
    {
        final DwcConfiguration dwcConfig = new DwcConfiguration(URI.create("megaclite.com"), "provider-id");
        final MegacliteDestinationFactory destinationFactory = new MegacliteDestinationFactory(dwcConfig);
        return spy(new MegacliteConnectivityProxyInformationResolver(destinationFactory));
    }
}
