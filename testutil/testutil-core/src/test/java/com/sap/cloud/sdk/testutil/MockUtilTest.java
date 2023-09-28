package com.sap.cloud.sdk.testutil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;

import org.junit.Test;

import com.sap.cloud.sdk.cloudplatform.security.Scope;
import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.security.principal.exception.PrincipalAccessException;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;

public class MockUtilTest
{
    @Test
    public void testRepeatedInstantiation()
    {
        new MockUtil();
        new MockUtil();
        new MockUtil();
    }

    @Test
    public void testMockTenant()
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

    @SuppressWarnings( "deprecation" )
    @Test
    public void testMockPrincipal()
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

        final Principal user =
            mockUtil
                .mockPrincipal(
                    "dummy",
                    Collections.singleton(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope")),
                    Collections
                        .singletonMap(
                            "key",
                            new com.sap.cloud.sdk.cloudplatform.security.principal.SimplePrincipalAttribute<>(
                                "key",
                                "value")));
        assertThat(user.getPrincipalId()).isEqualTo("dummy");
        assertThat(user.getAuthorizations()).containsOnly(new com.sap.cloud.sdk.cloudplatform.security.Scope("scope"));
        assertThat(user.getAttribute("key"))
            .contains(
                new com.sap.cloud.sdk.cloudplatform.security.principal.SimplePrincipalAttribute<>("key", "value"));
    }
}
