package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.assertj.vavr.api.VavrAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sap.cloud.sdk.cloudplatform.exception.CloudPlatformException;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.cloudplatform.security.AuthToken;
import com.sap.cloud.sdk.cloudplatform.security.AuthTokenAccessor;
import com.sap.cloud.sdk.cloudplatform.security.ScpCfAuthTokenFacade;
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
    public void testWithoutTenantFallsBackToXsuaaTokenAndFailsDueToMissingVcapServices()
    {
        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            final Try<Tenant> tenantTry = new ScpCfTenantFacade().tryGetCurrentTenant();
            assertThat(tenantTry).isEmpty();
            assertThat(tenantTry.getCause()).isInstanceOf(CloudPlatformException.class);
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
}
