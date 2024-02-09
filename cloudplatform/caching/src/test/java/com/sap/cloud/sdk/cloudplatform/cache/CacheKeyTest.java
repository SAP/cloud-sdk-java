/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.sap.cloud.sdk.cloudplatform.security.principal.DefaultPrincipal;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.testutil.TestContext;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

class CacheKeyTest
{
    @RegisterExtension
    static TestContext context = TestContext.withThreadContext();

    @BeforeEach
    void setup()
    {
        context.setTenant();
        context.setPrincipal();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class ComplexComponent
    {
        private final String val1;
        private final double val2;
        private final float val3;
        private final boolean val4;
        private final int val5;
        private final Object val6;
        private final long val7;
    }

    @Test
    void testEquals()
    {
        final String a = "dummy";
        final int b = 42;
        final ComplexComponent c = new ComplexComponent("something", 1.23d, 4.56f, true, 12, null, 42L);

        final CacheKey cacheKey = CacheKey.ofTenantAndPrincipalIsolation().append(a, b, c);

        assertThat(cacheKey.getTenantId()).isNotEmpty();
        assertThat(cacheKey.getPrincipalId()).isNotEmpty();
        assertThat(cacheKey.getComponents()).containsExactly(a, b, c);

        assertThat(cacheKey).isEqualTo(CacheKey.ofTenantAndPrincipalIsolation().append(a, b, c));
        assertThat(cacheKey)
            .isEqualTo(
                CacheKey
                    .ofTenantAndPrincipalIsolation()
                    .append("dummy", 42, new ComplexComponent("something", 1.23d, 4.56f, true, 12, null, 42L)));

        // make sure CacheKeys are not equal when switching the order of components
        assertThat(cacheKey).isNotEqualTo(CacheKey.ofTenantAndPrincipalIsolation().append(c, b, a));

        // make sure CacheKeys are not equal when using a different isolation
        assertThat(cacheKey).isNotEqualTo(CacheKey.ofTenantIsolation().append(a, b, c));
        assertThat(cacheKey).isNotEqualTo(CacheKey.ofTenantIsolation().append(a, b, c));
        assertThat(cacheKey).isNotEqualTo(CacheKey.ofTenantIsolation().append(a, b, c));
        assertThat(cacheKey).isNotEqualTo(CacheKey.ofTenantIsolation().append(a, b, c));
    }

    @Test
    void testAppend()
    {
        assertThatThrownBy(() -> CacheKey.ofTenantAndPrincipalIsolation().append("string", null))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testOf()
    {
        final String tenantOrZoneId = "tenantOrZoneId";
        final Tenant tenant = new DefaultTenant(tenantOrZoneId);
        final Principal principal = new DefaultPrincipal("user");

        assertThat(CacheKey.of(null, null).getTenantId()).isEmpty();
        assertThat(CacheKey.of(null, null).getPrincipalId()).isEmpty();

        assertThat(CacheKey.of(tenant, null).getTenantId()).contains(tenantOrZoneId);
        assertThat(CacheKey.of(tenant, null).getPrincipalId()).isEmpty();

        assertThat(CacheKey.of(null, principal).getTenantId()).isEmpty();
        assertThat(CacheKey.of(null, principal).getPrincipalId()).contains("user");

        assertThat(CacheKey.of(tenant, principal).getTenantId()).contains(tenantOrZoneId);
        assertThat(CacheKey.of(tenant, principal).getPrincipalId()).contains("user");
    }

    @Test
    void testOfNoIsolation()
    {
        final CacheKey cacheKey = CacheKey.of(null, null);

        assertThat(cacheKey.getTenantId()).isEmpty();
        assertThat(cacheKey.getPrincipalId()).isEmpty();
    }

    @Test
    void testOfTenantOnlyIsolation()
    {
        final CacheKey cacheKey = CacheKey.ofTenantIsolation();

        assertThat(cacheKey.getTenantId()).isNotEmpty();
        assertThat(cacheKey.getPrincipalId()).isEmpty();
    }

    @Test
    void testofTenantAndPrincipalIsolation()
    {
        final CacheKey cacheKey = CacheKey.ofTenantAndPrincipalIsolation();

        assertThat(cacheKey.getTenantId()).isNotEmpty();
        assertThat(cacheKey.getPrincipalId()).isNotEmpty();
    }
}
