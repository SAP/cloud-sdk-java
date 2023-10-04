/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

import javax.annotation.Nonnull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceConfiguration;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceDecorator;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipalFacade;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenantFacade;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

public class AccessorTest
{
    @Before
    @After
    public void resetAccessors()
    {
        // reset the facades
        TenantAccessor.setTenantFacade(new DefaultTenantFacade());
        PrincipalAccessor.setPrincipalFacade(new DefaultPrincipalFacade());

        // make sure that there are no global fallbacks between tests
        TenantAccessor.setFallbackTenant(null);
        PrincipalAccessor.setFallbackPrincipal(null);
    }

    private void assertNoTenantAvailable()
    {
        assertThat(TenantAccessor.tryGetCurrentTenant()).isFailure();
    }

    private void assertTenantId( @Nonnull final String tenantId )
    {
        assertThat(TenantAccessor.tryGetCurrentTenant().map(Tenant::getTenantId)).contains(tenantId);
    }

    private void assertNoPrincipalAvailable()
    {
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal()).isFailure();
    }

    private void assertPrincipalId( @Nonnull final String principalId )
    {
        assertThat(PrincipalAccessor.tryGetCurrentPrincipal().map(Principal::getPrincipalId)).contains(principalId);
    }

    @Test
    public void testNesting()
    {
        assertNoTenantAvailable();
        assertNoPrincipalAvailable();

        TenantAccessor.executeWithTenant(() -> "tenant", () -> {
            assertTenantId("tenant");
            assertNoPrincipalAvailable();

            PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
                assertTenantId("tenant");
                assertPrincipalId("principal");
            });

            assertTenantId("tenant");
            assertNoPrincipalAvailable();
        });

        assertNoTenantAvailable();
        assertNoPrincipalAvailable();
    }

    @Test
    public void testNestingWithOverriding()
    {
        assertNoTenantAvailable();
        assertNoPrincipalAvailable();

        TenantAccessor.executeWithTenant(() -> "tenant", () -> {
            assertTenantId("tenant");
            assertNoPrincipalAvailable();

            PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
                assertTenantId("tenant");
                assertPrincipalId("principal");

                TenantAccessor.executeWithTenant(() -> "overridden-tenant", () -> {
                    assertTenantId("overridden-tenant");
                    assertPrincipalId("principal");

                    PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("overridden-principal"), () -> {
                        assertTenantId("overridden-tenant");
                        assertPrincipalId("overridden-principal");
                    });

                    assertTenantId("overridden-tenant");
                    assertPrincipalId("principal");
                });

                assertTenantId("tenant");
                assertPrincipalId("principal");
            });

            assertTenantId("tenant");
            assertNoPrincipalAvailable();
        });

        assertNoTenantAvailable();
        assertNoPrincipalAvailable();
    }

    @Test
    public void testNestingWithFallback()
    {
        assertNoTenantAvailable();
        assertNoPrincipalAvailable();

        TenantAccessor.executeWithFallbackTenant(() -> () -> "tenant", () -> {
            assertTenantId("tenant");
            assertNoPrincipalAvailable();

            PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("principal"), () -> {
                assertTenantId("tenant");
                assertPrincipalId("principal");
            });

            assertTenantId("tenant");
            assertNoPrincipalAvailable();
        });

        assertNoTenantAvailable();
        assertNoPrincipalAvailable();
    }

    @Test
    public void testNestingWithGlobalFallback()
    {
        assertNoTenantAvailable();
        assertNoPrincipalAvailable();

        TenantAccessor.setFallbackTenant(() -> () -> "global-tenant");

        assertTenantId("global-tenant");
        assertNoPrincipalAvailable();

        TenantAccessor.executeWithTenant(() -> "overridden-tenant", () -> {
            assertTenantId("overridden-tenant");
            assertNoPrincipalAvailable();
        });

        PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback-principal"), () -> {
            assertTenantId("global-tenant");
            assertPrincipalId("fallback-principal");
        });

        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
            assertTenantId("global-tenant");
            assertPrincipalId("principal");

            TenantAccessor.executeWithTenant(() -> "overridden-tenant", () -> {
                assertTenantId("overridden-tenant");
                assertPrincipalId("principal");
            });
        });

        PrincipalAccessor.executeWithPrincipal(new DefaultPrincipal("principal"), () -> {
            assertTenantId("global-tenant");
            assertPrincipalId("principal");

            PrincipalAccessor.executeWithFallbackPrincipal(() -> new DefaultPrincipal("fallback-principal"), () -> {
                assertTenantId("global-tenant");
                assertPrincipalId("principal");
            });
        });

        assertTenantId("global-tenant");
        assertNoPrincipalAvailable();
    }

    @Test
    public void testTenantFallbackReturnsNull()
    {
        TenantAccessor.setFallbackTenant(() -> null);
        assertNoTenantAvailable();
    }

    @Test
    public void testPrincipalFallbackReturnsNull()
    {
        PrincipalAccessor.setFallbackPrincipal(() -> null);
        assertNoPrincipalAvailable();
    }

    @Test
    public void testExecuteWithFallbackReturnsNull()
    {
        TenantAccessor.executeWithFallbackTenant(() -> null, this::assertNoTenantAvailable);
        PrincipalAccessor.executeWithFallbackPrincipal(() -> null, this::assertNoPrincipalAvailable);
    }

    @Test
    public void testNestingWithResilience()
    {
        ResilienceDecorator.executeSupplier(() -> {
            testNesting();
            testNestingWithOverriding();
            return null;
        }, ResilienceConfiguration.of(AccessorTest.class));
    }
}
