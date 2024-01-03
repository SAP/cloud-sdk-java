/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.resilience;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.security.principal.Principal;
import com.sap.cloud.sdk.cloudplatform.security.principal.PrincipalAccessor;
import com.sap.cloud.sdk.cloudplatform.tenant.Tenant;
import com.sap.cloud.sdk.cloudplatform.tenant.TenantAccessor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Isolation key to manage optional tenant and principal values.
 */
@EqualsAndHashCode
@Value
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class ResilienceIsolationKey
{
    @Nullable
    Tenant tenant;

    @Nullable
    Principal principal;

    /**
     * Factory method for creating a resilience isolation key.
     *
     * @param isolationMode
     *            The isolation mode for the key to be constructed.
     *
     * @throws IllegalArgumentException
     *             If the given isolation mode is not supported.
     *
     * @return A resilience isolation key.
     */
    @Nonnull
    public static ResilienceIsolationKey of( @Nonnull final ResilienceIsolationMode isolationMode )
        throws IllegalArgumentException
    {
        switch( isolationMode ) {
            case NO_ISOLATION:
                return new ResilienceIsolationKey(null, null);
            case TENANT_REQUIRED:
                return new ResilienceIsolationKey(getTenantRequired(), null);
            case TENANT_OPTIONAL:
                return new ResilienceIsolationKey(getTenantOptional(), null);
            case PRINCIPAL_REQUIRED:
                return new ResilienceIsolationKey(null, getPrincipalRequired());
            case PRINCIPAL_OPTIONAL:
                return new ResilienceIsolationKey(null, getPrincipalOptional());
            case TENANT_AND_USER_REQUIRED:
                return new ResilienceIsolationKey(getTenantRequired(), getPrincipalRequired());
            case TENANT_AND_USER_OPTIONAL:
                return new ResilienceIsolationKey(getTenantOptional(), getPrincipalOptional());
            default:
                throw new IllegalArgumentException("Unsupported isolation mode.");
        }
    }

    private static Tenant getTenantOptional()
    {
        return TenantAccessor.tryGetCurrentTenant().getOrNull();
    }

    private static Tenant getTenantRequired()
    {
        return TenantAccessor.tryGetCurrentTenant().getOrElseThrow(failure -> new ResilienceRuntimeException(failure));
    }

    private static Principal getPrincipalOptional()
    {
        return PrincipalAccessor.tryGetCurrentPrincipal().getOrNull();
    }

    private static Principal getPrincipalRequired()
    {
        return PrincipalAccessor
            .tryGetCurrentPrincipal()
            .getOrElseThrow(failure -> new ResilienceRuntimeException(failure));
    }
}
