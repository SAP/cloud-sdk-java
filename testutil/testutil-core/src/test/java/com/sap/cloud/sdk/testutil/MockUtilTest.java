/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

class MockUtilTest
{
    @Test
    void testRepeatedInstantiation()
    {
        new MockUtil();
        new MockUtil();
        new MockUtil();
    }

    @Test
    void testMockTenant()
    {
        final MockUtil mockUtil = new MockUtil();

        mockUtil.mockDefaults();
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo(MockUtil.MOCKED_TENANT);

        mockUtil.mockTenant("tenant1");
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo(MockUtil.MOCKED_TENANT);

        mockUtil.mockCurrentTenant("tenant2");
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("tenant2");

        mockUtil.setCurrentTenant("tenant1");
        assertThat(TenantAccessor.getCurrentTenant().getTenantId()).isEqualTo("tenant1");

        mockUtil.setCurrentTenant(null);
        assertThatThrownBy(TenantAccessor::getCurrentTenant).isExactlyInstanceOf(TenantAccessException.class);
    }

    @Test
    void testMockPrincipal()
    {
        final MockUtil mockUtil = new MockUtil();

        mockUtil.mockDefaults();
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo(MockUtil.MOCKED_PRINCIPAL);

        mockUtil.mockPrincipal("user1");
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo(MockUtil.MOCKED_PRINCIPAL);

        mockUtil.mockCurrentPrincipal("user2");
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("user2");

        mockUtil.setCurrentPrincipal("user1");
        assertThat(PrincipalAccessor.getCurrentPrincipal().getPrincipalId()).isEqualTo("user1");

        mockUtil.setCurrentPrincipal(null);
        assertThatThrownBy(PrincipalAccessor::getCurrentPrincipal).isExactlyInstanceOf(PrincipalAccessException.class);

        final Principal user = mockUtil.mockPrincipal("dummy");
        assertThat(user.getPrincipalId()).isEqualTo("dummy");
    }
}
