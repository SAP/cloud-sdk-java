/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.cloud.sdk.cloudplatform.resilience4j;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

@Isolated
class Resilience4jThreadContextTest
{
    private static final Tenant TENANT_GLOBAL = () -> "global-tenant";
    private static final Tenant TENANT_ALTERED = () -> "altered-tenant";
    private static final ResilienceConfiguration CONFIG =
        ResilienceConfiguration.of(Resilience4jThreadContextTest.class);

    @BeforeEach
    @AfterEach
    void resetAccessors()
    {
        // reset the facades and make sure that there are no global fallbacks between tests
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        TenantAccessor.setFallbackTenant(null);
    }

    @Test
    void testSyncExecute()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // declare lazy evaluated lambda decoration with thread-dependent tenant
        final Supplier<String> supplier =
            () -> ResilienceDecorator.executeSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // actual resilience decoration happens in this thread, where THE ALTERED TENANT IS PRESENT
            // the tenant being evaluated is the altered one from changed thread context
            assertThat(supplier.get()).isEqualTo(TENANT_ALTERED.getTenantId());
        });
    }

    @Test
    void testSyncDecorate()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // decorate lambda with original tenant
        final Supplier<String> supplier =
            ResilienceDecorator.decorateSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // the tenant being evaluated is the original one from global thread context
            assertThat(supplier.get()).isEqualTo(TENANT_GLOBAL.getTenantId());
        });
    }

    @Test
    void testSyncDecorateInAccessor()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // decorate lambda with altered tenant
            final Supplier<String> supplier =
                ResilienceDecorator.decorateSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

            // the tenant being evaluated is the altered one from changed thread context
            assertThat(supplier.get()).isEqualTo(TENANT_ALTERED.getTenantId());
        });
    }

    @Test
    void testAsyncExecute()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // declare lazy evaluated lambda decoration with thread-dependent tenant
        final Supplier<String> supplier =
            () -> ResilienceDecorator.executeSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // schedule lazy evaluated lambda from changed thread context
            // actual resilience decoration happens in the new thread, where THE ALTERED TENANT IS NOT PRESENT
            final CompletableFuture<String> future = CompletableFuture.supplyAsync(supplier);

            // the tenant being evaluated is the original one from global thread context
            assertThat(future.get()).isEqualTo(TENANT_GLOBAL.getTenantId());
        });
    }

    @Test
    void testAsyncDecorate()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // decorate lambda with original tenant
        final Supplier<String> supplier =
            ResilienceDecorator.decorateSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // schedule lambda from changed thread context
            final CompletableFuture<String> future = CompletableFuture.supplyAsync(supplier);

            // the tenant being evaluated is the original one from global thread context
            assertThat(future.get()).isEqualTo(TENANT_GLOBAL.getTenantId());
        });
    }

    @Test
    void testAsyncDecorateInAccessor()
    {
        // set tenant for global thread context
        TenantAccessor.setFallbackTenant(() -> TENANT_GLOBAL);

        // change thread context with altered tenant
        TenantAccessor.executeWithTenant(TENANT_ALTERED, () -> {

            // decorate lambda with alternate tenant
            final Supplier<String> supplier =
                ResilienceDecorator.decorateSupplier(() -> TenantAccessor.getCurrentTenant().getTenantId(), CONFIG);

            // schedule lambda from changed thread context
            final CompletableFuture<String> future = CompletableFuture.supplyAsync(supplier);

            // the tenant being evaluated is the altered one, from changed thread context
            assertThat(future.get()).isEqualTo(TENANT_ALTERED.getTenantId());
        });
    }
}
