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
import java.util.concurrent.locks.ReentrantLock;

import com.github.benmanes.caffeine.cache.Cache;
import com.sap.cloud.sdk.cloudplatform.cache.CacheKey;
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
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
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
        final DwcConfiguration dwcConfig = new DwcConfiguration(URI.create("megaclite.com"), "provider-id");
        final MegacliteDestinationFactory destinationFactory = new MegacliteDestinationFactory(dwcConfig);
        sut = spy(new MegacliteConnectivityProxyInformationResolver(destinationFactory));

        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
    }

    @AfterEach
    void reset()
    {
        TenantAccessor.setTenantFacade(null);
    }

    @SneakyThrows
    @Test
    void testProxyUrlIsCachedAcrossTenants()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        assertThat(sut.getProxyUrl()).isEqualTo(URI.create("http://some.proxy"));

        doReturn(failureResponse).when(sut).makeHttpRequest(any());

        final URI proxyUrl = TenantAccessor.executeWithTenant(new DefaultTenant("foo"), sut::getProxyUrl);
        assertThat(proxyUrl)
            .describedAs("The proxy URL should be the same across all tenants")
            .isEqualTo(sut.getProxyUrl());

        verify(sut, times(1)).getProxyInformationFromMegaclite();
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

    @SneakyThrows
    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    void testProxyUrlRetrievalIsLockedGlobally()
    {
        doAnswer((i) -> {
            Thread.sleep(5_000);
            return successResponse;
        }).when(sut).makeHttpRequest(any());

        final CountDownLatch latch = new CountDownLatch(2);

        doAnswer(invocation -> {
            latch.countDown();

            latch.await();
            return invocation.callRealMethod();
        }).when(sut).getProxyUrl();

        final CompletableFuture<URI> firstInvocation =
            CompletableFuture
                .supplyAsync(
                    () -> TenantAccessor.executeWithTenant(new DefaultTenant("Tenant1"), () -> sut.getProxyUrl()));

        final CompletableFuture<URI> secondInvocation =
            CompletableFuture
                .supplyAsync(
                    () -> TenantAccessor.executeWithTenant(new DefaultTenant("Tenant2"), () -> sut.getProxyUrl()));

        assertThat(firstInvocation.get()).isEqualTo(URI.create("http://some.proxy"));
        assertThat(secondInvocation.get()).isEqualTo(URI.create("http://some.proxy"));

        verify(sut, times(1)).makeHttpRequest(any());
        verify(sut, times(2)).getProxyUrl();
    }

    @SneakyThrows
    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    void testProxyTokenRetrievalCannotBeDoneInParallelForSameTenant()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        final CountDownLatch firstLockInvocation = new CountDownLatch(1);
        final CountDownLatch secondLockInvocation = new CountDownLatch(2);
        final CountDownLatch resumeLockInvocations = new CountDownLatch(1);

        final ReentrantLock requestLock = spy(new ReentrantLock());
        doAnswer(invocation -> {
            firstLockInvocation.countDown();
            secondLockInvocation.countDown();

            resumeLockInvocations.await(); // we are blocking all calls until the main thread decides that we may continue
            return invocation.callRealMethod();
        }).when(requestLock).lock();

        sut.getRequestLocks().put(sut.createTokenCacheKey(), requestLock);

        final Future<String> firstInvocation =
            ThreadContextExecutors
                .submit(
                    () -> PrincipalAccessor
                        .executeWithPrincipal(new DefaultPrincipal("Principal1"), () -> sut.getAuthorizationToken()));

        firstLockInvocation.await(); // wait for the first invocation to acquire the lock

        final Future<String> secondInvocation =
            ThreadContextExecutors
                .submit(
                    () -> PrincipalAccessor
                        .executeWithPrincipal(new DefaultPrincipal("Principal2"), () -> sut.getAuthorizationToken()));

        secondLockInvocation.await(); // wait for the second invocation to acquire the lock

        // make sure NONE of the tasks above is already done
        assertThat(firstInvocation.isDone()).isFalse();
        assertThat(secondInvocation.isDone()).isFalse();

        resumeLockInvocations.countDown(); // unblock the lock invocations

        assertThat(firstInvocation.get()).isEqualTo("Bearer 1234");
        assertThat(secondInvocation.get()).isEqualTo("Bearer 1234");

        verify(sut, times(1)).makeHttpRequest(any());
        verify(requestLock, times(2)).lock();
    }

    @SneakyThrows
    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    void testProxyTokenRetrievalCanBeDoneInParallelForDifferentTenants()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        final CountDownLatch firstLockInvocation = new CountDownLatch(1);
        final CountDownLatch secondLockInvocation = new CountDownLatch(1);
        final CountDownLatch resumeLockInvocation = new CountDownLatch(1);

        final ReentrantLock firstRequestLock = spy(new ReentrantLock());
        final ReentrantLock secondRequestLock = spy(new ReentrantLock());

        doAnswer(invocation -> {
            firstLockInvocation.countDown();
            resumeLockInvocation.await();
            return invocation.callRealMethod();
        }).when(firstRequestLock).unlock();

        doAnswer(invocation -> {
            secondLockInvocation.countDown();
            resumeLockInvocation.await();
            return invocation.callRealMethod();
        }).when(secondRequestLock).unlock();

        final Future<String> firstInvocation = TenantAccessor.executeWithTenant(new DefaultTenant("Tenant1"), () -> {
            sut.getRequestLocks().put(sut.createTokenCacheKey(), firstRequestLock);

            return ThreadContextExecutors.submit(sut::getAuthorizationToken);
        });

        firstLockInvocation.await(); // wait for the first invocation to return the lock (i.e. lock is STILL acquired at this point in time)

        final Future<String> secondInvocation = TenantAccessor.executeWithTenant(new DefaultTenant("Tenant2"), () -> {
            sut.getRequestLocks().put(sut.createTokenCacheKey(), secondRequestLock);

            return ThreadContextExecutors.submit(sut::getAuthorizationToken);
        });

        secondLockInvocation.await(); // wait for the second invocation to return the lock (i.e. lock is STILL acquired at this point in time)

        // make sure NONE of the tasks above is already done
        assertThat(firstInvocation).isNotNull();
        assertThat(firstInvocation.isDone()).isFalse();
        assertThat(secondInvocation).isNotNull();
        assertThat(secondInvocation.isDone()).isFalse();

        resumeLockInvocation.countDown(); // unblock the lock invocations

        assertThat(firstInvocation.get()).isEqualTo("Bearer 1234");
        assertThat(secondInvocation.get()).isEqualTo("Bearer 1234");

        verify(sut, times(2)).makeHttpRequest(any());
        verify(firstRequestLock, times(1)).unlock();
        verify(secondRequestLock, times(1)).unlock();
    }

    @SneakyThrows
    @Test
    @Timeout( value = 300_000, unit = java.util.concurrent.TimeUnit.MILLISECONDS )
    void testProxyUrlAndTokenCanBeRetrievedInParallelForSameTenant()
    {
        doReturn(successResponse).when(sut).makeHttpRequest(any());

        final CountDownLatch firstLockInvocation = new CountDownLatch(1);
        final CountDownLatch secondLockInvocation = new CountDownLatch(1);
        final CountDownLatch resumeLockInvocation = new CountDownLatch(1);

        final ReentrantLock firstRequestLock = spy(new ReentrantLock());
        final ReentrantLock secondRequestLock = spy(new ReentrantLock());

        doAnswer(invocation -> {
            firstLockInvocation.countDown();
            resumeLockInvocation.await();
            return invocation.callRealMethod();
        }).when(firstRequestLock).unlock();

        doAnswer(invocation -> {
            secondLockInvocation.countDown();
            resumeLockInvocation.await();
            return invocation.callRealMethod();
        }).when(secondRequestLock).unlock();

        final Future<String> firstInvocation = TenantAccessor.executeWithTenant(new DefaultTenant("Tenant1"), () -> {
            sut.getRequestLocks().put(sut.createTokenCacheKey(), firstRequestLock);

            return ThreadContextExecutors.submit(sut::getAuthorizationToken);
        });

        firstLockInvocation.await(); // wait for the first invocation to return the lock (i.e. lock is STILL acquired at this point in time)

        final Future<URI> secondInvocation = TenantAccessor.executeWithTenant(new DefaultTenant("Tenant1"), () -> {
            sut.getRequestLocks().put(sut.createProxyUrlCacheKey(), secondRequestLock);

            return ThreadContextExecutors.submit(sut::getProxyUrl);
        });

        secondLockInvocation.await(); // wait for the second invocation to return the lock (i.e. lock is STILL acquired at this point in time)

        // make sure NONE of the tasks above is already done
        assertThat(firstInvocation).isNotNull();
        assertThat(firstInvocation.isDone()).isFalse();
        assertThat(secondInvocation).isNotNull();
        assertThat(secondInvocation.isDone()).isFalse();

        resumeLockInvocation.countDown(); // unblock the lock invocations

        assertThat(firstInvocation.get()).isEqualTo("Bearer 1234");
        assertThat(secondInvocation.get()).hasToString("http://some.proxy");

        verify(sut, times(2)).makeHttpRequest(any());
        verify(firstRequestLock, times(1)).unlock();
        verify(secondRequestLock, times(1)).unlock();
    }
}
