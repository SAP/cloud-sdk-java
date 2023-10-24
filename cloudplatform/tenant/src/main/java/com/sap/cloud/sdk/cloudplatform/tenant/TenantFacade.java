/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import javax.annotation.Nonnull;

import io.vavr.control.Try;

/**
 * Facade for accessing the current {@link Tenant}.
 */
@FunctionalInterface
public interface TenantFacade
{
    /**
     * Returns a {@link Try} of the current {@link Tenant}.
     * <p>
     * On SAP Business Technology Platform, the availability of a tenant is defined as follows:
     * <table border="1">
     * <tr>
     * <th></th>
     * <th>Tenant available</th>
     * <th>Tenant not available</th>
     * </tr>
     * <tr>
     * <td><strong>SAP Business Technology Platform Cloud Foundry</strong></td>
     * <td>A request is present with an "Authorization" header that contains a valid JWT bearer with field "zid",
     * "app_tid", or "zone_uuid".<br>
     * As a fallback a JWT will be retrieved from a bound XSUAA instance.</td>
     * <td>A request is not available, no "Authorization" header is present in the current request, the JWT bearer does
     * not hold a field "zid", "app_tid", or "zone_uuid", or there is no XSUAA service bound to this application.</td>
     * </tr>
     * </table>
     *
     * @return A {@link Try} of the current {@link Tenant}.
     */
    @Nonnull
    Try<Tenant> tryGetCurrentTenant();
}
