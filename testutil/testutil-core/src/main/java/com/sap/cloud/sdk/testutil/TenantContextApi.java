package com.sap.cloud.sdk.testutil;

import com.sap.cloud.sdk.cloudplatform.tenant.DefaultTenant;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantThreadContextListener;

public interface TenantContextApi extends TestContextApi
{
    default Tenant setTenant()
    {
        return setTenant("tenant");
    }

    default Tenant setTenant( String tenant )
    {
        return setTenant(new DefaultTenant(tenant));
    }

    default Tenant setTenant( Tenant tenant )
    {
        setProperty(TenantThreadContextListener.PROPERTY_TENANT, tenant);
        return tenant;
    }
}
