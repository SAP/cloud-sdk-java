package com.sap.cloud.sdk.cloudplatform.tenant;

import org.junit.Test;

public class ScpCfTenantTest
{
    @Test( expected = NullPointerException.class )
    public void testTenantIdNull()
    {
        new ScpCfTenant(null, null);
    }
}
