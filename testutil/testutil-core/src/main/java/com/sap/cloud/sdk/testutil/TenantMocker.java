/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.testutil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;

interface TenantMocker
{
    /**
     * Mocks a {@link Tenant} with the given tenant/zone identifier.
     */
    @Nonnull
    Tenant mockTenant( @Nonnull final String tenantId );

    /**
     * Mocks the current {@link Tenant} with tenant/zone identifier {@link MockUtil#MOCKED_TENANT}.
     */
    @Nonnull
    Tenant mockCurrentTenant();

    /**
     * Mocks a {@link Tenant} with the given tenant/zone identifier.
     */
    @Nonnull
    Tenant mockCurrentTenant( @Nonnull final String tenantId );

    /**
     * Sets the current {@link Tenant}. Clears the current {@link Tenant} if given {@code null}.
     */
    void setCurrentTenant( @Nullable final String tenantId );

    /**
     * Sets or mocks the current {@link Tenant}. Clears the current {@link Tenant} if given {@code null}.
     */
    void setOrMockCurrentTenant( @Nullable final String tenantId );

    /**
     * Clears all previously mocked {@link Tenant}s.
     */
    void clearTenants();
}
