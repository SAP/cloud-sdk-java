/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBinding;
import com.sap.cloud.environment.servicebinding.api.DefaultServiceBindingAccessor;
import com.sap.cloud.environment.servicebinding.api.ServiceBinding;
import com.sap.cloud.environment.servicebinding.api.ServiceBindingAccessor;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.DefaultAuthTokenFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.DefaultThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextAccessor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextFacade;

import io.vavr.control.Try;

public class DefaultTenantFacadeTest
{
    private static final AuthToken JWT_WITHOUT_TENANT = new AuthToken(JWT.decode(JWT.create().sign(Algorithm.none())));

    private static final DefaultAuthTokenFacade TOKEN_FACADE_WITHOUT_TENANT = mock(DefaultAuthTokenFacade.class);
    static {
        when(TOKEN_FACADE_WITHOUT_TENANT.tryGetCurrentToken()).thenReturn(Try.success(JWT_WITHOUT_TENANT));
    }

    @Before
    @After
    public void resetAccessors()
    {
        AuthTokenAccessor.setAuthTokenFacade(null);
        ThreadContextAccessor.setThreadContextFacade(null);
        DefaultServiceBindingAccessor.setInstance(null);
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

    @Test
    public void givenAFailurePropertyThenFacadeShouldNotComputeFromToken()
    {
        AuthTokenAccessor.setAuthTokenFacade(spy(AuthTokenAccessor.getAuthTokenFacade()));
        final ThreadContextFacade mockedThreadContextFacade = mock(ThreadContextFacade.class);
        ThreadContextAccessor.setThreadContextFacade(mockedThreadContextFacade);
        final ThreadContext defaultThreadContext = new DefaultThreadContext();
        when(mockedThreadContextFacade.tryGetCurrentContext()).thenReturn(Try.success(defaultThreadContext));

        final Try<Object> storedResult = Try.failure(new ShouldNotHappenException());
        defaultThreadContext.setProperty(TenantThreadContextListener.PROPERTY_TENANT, Property.ofTry(storedResult));

        final Try<Tenant> actualResult = new DefaultTenantFacade().tryGetCurrentTenant();

        VavrAssertions.assertThat(actualResult).isFailure();
        assertThat(actualResult.getCause().getSuppressed()).contains(storedResult.getCause());
    }

    @Test
    public void givenAnUnsupportedServiceBindingThenFacadeShouldReturnNoTenant()
    {
        final ServiceBinding serviceBinding =
            spy(
                DefaultServiceBinding
                    .builder()
                    .copy(Collections.singletonMap("credentials", Collections.emptyMap()))
                    .withServiceName(ServiceBindingTenantExtractor.XSUAA.getService())
                    .withCredentialsKey("credentials")
                    .build());

        final ServiceBindingAccessor serviceBindingAccessor = mock(ServiceBindingAccessor.class);
        when(serviceBindingAccessor.getServiceBindings()).thenReturn(Collections.singletonList(serviceBinding));
        DefaultServiceBindingAccessor.setInstance(serviceBindingAccessor);

        final TenantFacade sut = new DefaultTenantFacade();

        // make sure an "unexpected" service binding (i.e. no credentials) doesn't lead to an unexpected exception
        assertThatNoException().isThrownBy(sut::tryGetCurrentTenant);
        final Try<Tenant> maybeTenant = sut.tryGetCurrentTenant();

        assertThat(maybeTenant.isFailure()).isTrue();
        assertThat(maybeTenant.getCause())
            .isExactlyInstanceOf(TenantAccessException.class)
            .extracting(Throwable::getSuppressed, as(InstanceOfAssertFactories.array(Throwable[].class)))
            .anySatisfy(e -> assertThat(e).hasMessageContaining("Failed to extract tenant from service bindings."));

        verify(serviceBindingAccessor, times(2)).getServiceBindings();
        verify(serviceBinding, times(2)).getCredentials();
    }
}
