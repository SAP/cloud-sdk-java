package com.sap.cloud.sdk.cloudplatform.tenant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class DefaultTenantTest
{
    @Test
    void testNullParameters()
    {
        assertThatThrownBy(() -> new DefaultTenant(null, null)).isExactlyInstanceOf(NullPointerException.class);

        DefaultTenant tenant = new DefaultTenant("foo", null);
        assertDoesNotThrow(tenant::toString);
        assertDoesNotThrow(tenant::hashCode);
        assertDoesNotThrow(() -> tenant.equals(tenant));
    }
}
