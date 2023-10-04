/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantFacade;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
class DefaultTenantMocker implements TenantMocker
{
    private final Supplier<TenantFacade> resetTenantFacade;

    @Getter( AccessLevel.PACKAGE )
    private final Map<String, Tenant> tenants = new HashMap<>();

    @Getter( AccessLevel.PACKAGE )
    @Nullable
    private Tenant currentTenant;

    @Nonnull
    @Override
    public Tenant mockTenant( @Nonnull final String tenantId )
    {
        resetTenantFacade.get();

        final Tenant tenant = Mockito.mock(Tenant.class);

        Mockito.when(tenant.getTenantId()).thenReturn(tenantId);

        tenants.put(tenantId, tenant);
        return tenant;
    }

    @Nonnull
    @Override
    public Tenant mockCurrentTenant()
    {
        return mockCurrentTenant(MockUtil.MOCKED_TENANT);
    }

    @Nonnull
    @Override
    public Tenant mockCurrentTenant( @Nonnull final String tenantId )
    {
        final Tenant tenant = mockTenant(tenantId);
        currentTenant = tenant;
        return tenant;
    }

    @Override
    public void setCurrentTenant( @Nullable final String tenantId )
    {
        if( tenantId == null ) {
            currentTenant = null;
        } else {
            final Tenant tenant = tenants.get(tenantId);

            if( tenant == null ) {
                throw new TestConfigurationError(
                    "No tenant/zone mocked with id "
                        + tenantId
                        + ". Make sure to mock the respective tenant/zone before calling this method.");
            }

            currentTenant = tenant;
        }
    }

    @Override
    public void setOrMockCurrentTenant( @Nullable final String tenantId )
    {
        if( tenantId == null ) {
            currentTenant = null;
            return;
        }

        Tenant tenant = tenants.get(tenantId);
        if( tenant == null ) {
            tenant = mockTenant(tenantId);
        }

        currentTenant = tenant;
    }

    @Override
    public void clearTenants()
    {
        currentTenant = null;
        tenants.clear();
    }
}
