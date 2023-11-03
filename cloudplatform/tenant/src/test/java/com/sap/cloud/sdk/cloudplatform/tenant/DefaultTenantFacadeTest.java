/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.DefaultAuthTokenFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;

import io.vavr.control.Try;

@Deprecated
public class DefaultTenantFacadeTest
{
    private static final AuthToken JWT_WITHOUT_TENANT = new AuthToken(JWT.decode(JWT.create().sign(Algorithm.none())));

    private static final DefaultAuthTokenFacade TOKEN_FACADE_WITHOUT_TENANT = mock(DefaultAuthTokenFacade.class);
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
    public void testWithoutTenantFallsBackToXsuaaTokenAndFailsDueToMissingVcapServices()
    {
        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();
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
            final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();
            assertThat(tenantTry).isNotEmpty();
            assertThat(tenantTry).contains(tenant);
        });
    }

    @Test
    public void givenATokenWithoutTenantThenFacadeShouldExceptionIsReturned()
    {
        AuthTokenAccessor.setAuthTokenFacade(TOKEN_FACADE_WITHOUT_TENANT);

        final Try<Tenant> tenantTry = new DefaultTenantFacade().tryGetCurrentTenant();
        assertThat(tenantTry).isEmpty();
    }
}
