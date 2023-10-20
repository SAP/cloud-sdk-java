/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_SUBDOMAIN_HEADER;
import static com.sap.cloud.sdk.cloudplatform.DwcHeaderUtils.DWC_TENANT_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderAccessor;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ScpCfAuthTokenFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextFacade;

import io.vavr.control.Try;

@Deprecated
public class ScpCfTenantFacadeTest
{
    private static final AuthToken JWT_WITHOUT_TENANT = new AuthToken(JWT.decode(JWT.create().sign(Algorithm.none())));

    private static final ScpCfAuthTokenFacade TOKEN_FACADE_WITHOUT_TENANT = mock(ScpCfAuthTokenFacade.class);
    static {
        when(TOKEN_FACADE_WITHOUT_TENANT.tryGetCurrentToken()).thenReturn(Try.success(JWT_WITHOUT_TENANT));
    }

    @Before
    @After
    public void resetAuthTokenFacade()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
        ThreadContextAccessor.setThreadContextFacade(null);
    }

    @Test
    public void testWithoutTenantFallsBackToXsuaaTokenAndFailsDueToMissingVcapServicesAndDwcHeaders()
    {
        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            final Try<Tenant> tenantTry = new ScpCfTenantFacade().tryGetCurrentTenant();
            assertThat(tenantTry).isEmpty();
            assertThat(tenantTry.getCause()).isInstanceOf(TenantAccessException.class);
        });
    }

    @Test
    public void givenATenantThenFacadeShouldProvideThatTenant()
    {
        final Tenant tenant = mock(Tenant.class);

        final DefaultThreadContext context = new DefaultThreadContext();
        context.setProperty(TenantThreadContextListener.PROPERTY_TENANT, Property.of(tenant));

        ThreadContextExecutor.using(context).withoutDefaultListeners().execute(() -> {
            final Try<Tenant> tenantTry = new ScpCfTenantFacade().tryGetCurrentTenant();
            assertThat(tenantTry).isNotEmpty();
            assertThat(tenantTry).contains(tenant);
        });
    }

    @Test
    public void givenATokenWithoutTenantThenFacadeShouldExceptionIsReturned()
    {
        AuthTokenAccessor.setAuthTokenFacade(TOKEN_FACADE_WITHOUT_TENANT);

        final Try<Tenant> tenantTry = new ScpCfTenantFacade().tryGetCurrentTenant();
        assertThat(tenantTry).isEmpty();
    }

    @Test
    public void givenAFailurePropertyThenFacadeShouldNotComputeFromToken()
    {
        AuthTokenAccessor.setAuthTokenFacade(spy(AuthTokenAccessor.getAuthTokenFacade()));
        final ThreadContextFacade mockedThreadContextFacade = mock(ThreadContextFacade.class);
        ThreadContextAccessor.setThreadContextFacade(mockedThreadContextFacade);
        final ThreadContext defaultThreadContext = new DefaultThreadContext();
        when(mockedThreadContextFacade.getCurrentContextOrNull()).thenReturn(defaultThreadContext);

        final Try<Object> storedResult = Try.failure(new ShouldNotHappenException());
        defaultThreadContext.setProperty(TenantThreadContextListener.PROPERTY_TENANT, Property.ofTry(storedResult));

        final Try<Tenant> actualResult = new ScpCfTenantFacade().tryGetCurrentTenant();

        VavrAssertions.assertThat(actualResult).isFailure();
        assertThat(actualResult.getCause()).isEqualTo(storedResult.getCause());

        verifyNoInteractions(AuthTokenAccessor.getAuthTokenFacade());
    }

    @Test
    public void testDwcSuccessfulTenantRetrieval()
    {
        final Map<String, String> headers =
            ImmutableMap.of(DWC_TENANT_HEADER, "tenant-value", DWC_SUBDOMAIN_HEADER, "subdomain-value");

        final ScpCfTenant expectedTenant = new ScpCfTenant("tenant-value", "subdomain-value");

        RequestHeaderAccessor.executeWithHeaderContainer(headers, () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final ScpCfTenant currentTenant = (ScpCfTenant) TenantAccessor.getCurrentTenant();
            final Try<Tenant> shouldBeSuccess =
                currentContext.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT);

            assertThat(currentTenant).isEqualTo(expectedTenant);
            assertThat(shouldBeSuccess.isSuccess()).isTrue();
            assertThat(shouldBeSuccess.get()).isEqualTo(expectedTenant);
        });
    }

    @Test
    public void testDwcUnsuccessfulTenantRetrieval()
    {
        RequestHeaderAccessor.executeWithHeaderContainer(Collections.emptyMap(), () -> {
            final ThreadContext currentContext = ThreadContextAccessor.getCurrentContext();
            final Try<Tenant> tenantFailure = TenantAccessor.tryGetCurrentTenant();
            final Try<Tenant> shouldBeFailure =
                currentContext.getPropertyValue(TenantThreadContextListener.PROPERTY_TENANT);

            assertThat(tenantFailure.isFailure()).isTrue();
            assertThat(tenantFailure.getCause()).isInstanceOf(TenantAccessException.class);
            assertThat(shouldBeFailure.isFailure()).isTrue();
            assertThat(tenantFailure).isSameAs(shouldBeFailure);
        });
    }
}
