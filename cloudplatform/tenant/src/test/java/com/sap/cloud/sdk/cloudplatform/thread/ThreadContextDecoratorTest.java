package com.sap.cloud.sdk.cloudplatform.thread;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.requestheader.DefaultRequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.requestheader.RequestHeaderContainer;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantFacade;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Test deriving the tenant from a property that is set via a ThreadContextDecorator * This tests the CAP integration
 * approach, where data is set on the CDS RequestContext
 */
@Slf4j
class ThreadContextDecoratorTest
{
    private static final ThreadContextDecorator customDecorator = new CustomThreadContextDecorator();
    private static final ThreadLocal<RequestHeaderContainer> headers = new ThreadLocal<>();
    private static final TenantFacade customFacade = new CustomTenantFacade();

    @BeforeAll
    static void setUp()
    {
        DefaultThreadContextDecoratorChain.registerDefaultDecorator(customDecorator);
        TenantAccessor.setTenantFacade(customFacade);
        headers.set(DefaultRequestHeaderContainer.builder().withHeader("tenant", "foo").build());
    }

    @AfterAll
    static void tearDown()
    {
        DefaultThreadContextDecoratorChain.unregisterDefaultDecorator(customDecorator.getPriority());
        TenantAccessor.setTenantFacade(null);
        headers.set(null);
    }

    @Test
    @DisplayName( "Test properties can be derived when propagated via decorators" )
    void testDerivingPropertiesFromDecorator()
        throws ExecutionException,
            InterruptedException
    {
        final Tenant result = ThreadContextExecutors.submit(TenantAccessor::getCurrentTenant).get();

        assertThat(result.getTenantId()).isEqualTo("foo");
    }

    static class CustomThreadContextDecorator implements ThreadContextDecorator
    {
        @Override
        public int getPriority()
        {
            return 1;
        }

        @Nonnull
        @Override
        public <T> Callable<T> decorateCallable( @Nonnull final Callable<T> callable )
        {
            final RequestHeaderContainer headersToPass = headers.get();
            return () -> {
                final RequestHeaderContainer initial = headers.get();
                headers.set(headersToPass);
                try {
                    return callable.call();
                }
                finally {
                    headers.set(initial);
                }
            };
        }
    }

    static class CustomTenantFacade extends DefaultTenantFacade
    {
        @Nonnull
        @Override
        public Try<Tenant> tryGetCurrentTenant()
        {
            final Try<Tenant> maybeTenant = super.tryGetCurrentTenant();
            final Try<Tenant> maybeTenantFromHeaders =
                maybeTenant
                    .recover(
                        any -> headers
                            .get()
                            .getHeaderValues("tenant")
                            .stream()
                            .findAny()
                            .map(DefaultTenant::new)
                            .get());
            return maybeTenantFromHeaders;
        }
    }
}
