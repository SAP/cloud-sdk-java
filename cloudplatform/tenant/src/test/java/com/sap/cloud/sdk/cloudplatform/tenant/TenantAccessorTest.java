/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Property;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContext;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutor;
import com.sap.cloud.sdk.cloudplatform.thread.ThreadContextExecutors;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextPropertyNotFoundException;

import io.vavr.control.Try;

public class TenantAccessorTest
{
    @Before
    @After
    public void resetAccessor()
    {
        // reset the facade
        TenantAccessor.setTenantFacade(null);

        // make sure that there is no global fallback between tests
        TenantAccessor.setFallbackTenant(null);
    }

    @Test
    public void testGetCurrentTenant()
    {
        final String tenantOrZoneId = "tenantOrZoneId";

        TenantAccessor.setTenantFacade(() -> Try.success(new DefaultTenant(tenantOrZoneId)));

        final Tenant currentTenant = TenantAccessor.getCurrentTenant();

        assertThat(currentTenant.getTenantId()).isEqualTo(tenantOrZoneId);
    }

    @Test
    public void testExecute()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        assertThat(TenantAccessor.tryGetCurrentTenant().getCause()).isInstanceOf(TenantAccessException.class);

        TenantAccessor.executeWithTenant(() -> "custom1", () -> {
            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("custom1");

            TenantAccessor.executeWithTenant(() -> "custom2", () -> {
                assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("custom2");

                TenantAccessor.executeWithTenant(() -> "custom3", () -> {
                    assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("custom3");
                });

                assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("custom2");
            });

            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("custom1");
        });

        assertThat(TenantAccessor.tryGetCurrentTenant().getCause()).isInstanceOf(TenantAccessException.class);
    }

    @Test
    public void testExecuteAsync()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        TenantAccessor.executeWithTenant(() -> "async", () -> {
            // no ThreadContext managed
            final Try<Tenant> tenantTry = CompletableFuture.supplyAsync(TenantAccessor::tryGetCurrentTenant).get();

            assertThat(tenantTry.getCause()).isInstanceOf(TenantAccessException.class);
        });

        final Tenant tenant =
            TenantAccessor
                .executeWithTenant(
                    () -> "async",
                    () -> ThreadContextExecutors.submit(TenantAccessor::getCurrentTenant).get());

        assertThat(tenant.getTenantId()).isEqualTo("async");
    }

    private static class FailingTenantFacade extends DefaultTenantFacade
    {
        @Nonnull
        @Override
        public Try<Tenant> tryGetCurrentTenant()
        {
            return super.tryGetCurrentTenant().orElse(Try.failure(new TenantAccessException()));
        }
    }

    @Test
    public void testExecuteWithFallback()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();

        // check if fallback is not used if there is already a tenant
        TenantAccessor.executeWithTenant(() -> "success", () -> {
            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("success");

            TenantAccessor.executeWithFallbackTenant(() -> () -> "fallback", () -> {
                assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("success");
            });
        });

        // check if fallback is used
        TenantAccessor.setTenantFacade(new FailingTenantFacade());
        assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();

        TenantAccessor.executeWithFallbackTenant(() -> () -> "fallback", () -> {
            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("fallback");
        });
    }

    @Test
    public void testExecuteWithException()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();

        assertThatThrownBy(() -> TenantAccessor.executeWithTenant(() -> "tenant", () -> {
            throw new IllegalArgumentException();
        }))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testExecuteWithFallbackWithException()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();

        final String tenantOrZoneId = "tenantOrZoneId";
        TenantAccessor.executeWithTenant(() -> tenantOrZoneId, () -> {

            // check if IllegalArgumentException is wrapped
            assertThatThrownBy(() -> TenantAccessor.executeWithFallbackTenant(() -> () -> "fallback", () -> {
                assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo(tenantOrZoneId);
                throw new IllegalArgumentException();

            }))
                .isExactlyInstanceOf(ThreadContextExecutionException.class)
                .hasCauseExactlyInstanceOf(IllegalArgumentException.class);

            // check if ThreadContextExecutionException is not wrapped
            assertThatThrownBy(() -> TenantAccessor.executeWithFallbackTenant(() -> () -> "fallback", () -> {
                assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo(tenantOrZoneId);
                throw new ThreadContextExecutionException();
            })).isExactlyInstanceOf(ThreadContextExecutionException.class).hasNoCause();

        });

        assertThat(TenantAccessor.tryGetCurrentTenant()).isEmpty();
    }

    @Test
    public void testGlobalFallback()
    {
        TenantAccessor.setFallbackTenant(() -> () -> "globalFallback");
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("globalFallback");

        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("globalFallback");

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("globalFallback");
        });

        final String tenantOrZoneId = "tenantOrZoneId";
        TenantAccessor.executeWithTenant(() -> tenantOrZoneId, () -> {
            assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo(tenantOrZoneId);
        });
    }

    private static class BrokenTenantThreadContextListener extends TenantThreadContextListener
    {
        @Override
        public void beforeInitialize( @Nonnull final ThreadContext threadContext )
        {
            threadContext
                .setPropertyIfAbsent(
                    TenantThreadContextListener.PROPERTY_TENANT,
                    Property.of("this-is-not-a-tenant-object"));
        }
    }

    @Test
    public void testWrongPropertyType()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new BrokenTenantThreadContextListener())
            .execute(() -> {
                assertThatThrownBy(TenantAccessor::getCurrentTenant).isExactlyInstanceOf(ClassCastException.class);
            });
    }

    @Test
    public void testMissingThreadContext()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        ThreadContextExecutor
            .fromNewContext()
            .withoutDefaultListeners()
            .withListeners(new TenantThreadContextListener())
            .execute(() -> {
                assertThat(TenantAccessor.tryGetCurrentTenant().getCause())
                    .isInstanceOf(TenantAccessException.class)
                    .hasMessage("Failed to get current tenant."); // as thrown implicitly via TenantAccessor
            });
    }

    @Test
    public void testMissingThreadContextProperty()
    {
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());

        ThreadContextExecutor.fromNewContext().withoutDefaultListeners().execute(() -> {
            assertThat(TenantAccessor.tryGetCurrentTenant().getCause().getSuppressed())
                .anyMatch(e -> e instanceof ThreadContextPropertyNotFoundException);
            assertThat(TenantAccessor.tryGetCurrentTenant().getCause().getSuppressed())
                .anyMatch(
                    e -> e
                        .getMessage()
                        .equals("Property '" + TenantThreadContextListener.PROPERTY_TENANT + "' does not exist."));
        });
    }

    @Test
    public void testExecuteWithThrowsExceptionIfCustomFacadeIsUsed()
    {
        final TenantFacade customFacade = () -> Try.failure(new IllegalStateException());
        assertThat(customFacade).isNotInstanceOf(DefaultTenantFacade.class);

        TenantAccessor.setTenantFacade(customFacade);

        assertThatThrownBy(() -> TenantAccessor.executeWithTenant(mock(Tenant.class), () -> "foo"))
            .isExactlyInstanceOf(ThreadContextExecutionException.class)
            .hasMessageContaining("https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext");
    }

    @Test
    public void testExecuteWithSucceedsIfSubTypeOfDefaultFacadeIsUsed()
    {
        final TenantFacade customFacade = spy(DefaultTenantFacade.class);
        assertThat(customFacade).isInstanceOf(DefaultTenantFacade.class);

        TenantAccessor.setTenantFacade(customFacade);

        assertThat(TenantAccessor.executeWithTenant(mock(Tenant.class), () -> "foo")).isEqualTo("foo");
    }
}
