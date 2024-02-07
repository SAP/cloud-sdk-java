/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.tenant;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.tenant.exception.TenantAccessException;
import com.sap.cloud.sdk.cloudplatform.thread.Executable;
import com.sap.cloud.sdk.cloudplatform.thread.exception.ThreadContextExecutionException;
import com.sap.cloud.sdk.cloudplatform.util.FacadeLocator;

import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Accessor for retrieving the current {@link Tenant}.
 */
@NoArgsConstructor( access = AccessLevel.PRIVATE )
@Slf4j
public final class TenantAccessor
{
    @Nonnull
    private static TenantFacade tenantFacade = loadFacadeOrDefault();

    /**
     * Global fallback {@link Tenant}. By default, no fallback is used, i.e., the fallback is {@code null}. A global
     * fallback can be useful to ensure a safe fallback or to ease testing with a mocked tenant.
     */
    @Getter
    @Setter
    @Nullable
    private static Supplier<Tenant> fallbackTenant = null;

    /**
     * Returns the {@link TenantFacade} instance.
     *
     * @return The {@link TenantFacade} instance, or {@code null}.
     */
    @Nonnull
    public static TenantFacade getTenantFacade()
    {
        return tenantFacade;
    }

    /**
     * Returns a {@link Try} of the {@link TenantFacade} instance.
     *
     * @return A {@link Try} of the {@link TenantFacade} instance.
     */
    @Nonnull
    public static Try<TenantFacade> tryGetTenantFacade()
    {
        return Try.success(tenantFacade);
    }

    /**
     * Replaces the default {@link TenantFacade} instance.
     *
     * @param tenantFacade
     *            An instance of {@link TenantFacade}. Use {@code null} to reset the facade.
     */
    public static void setTenantFacade( @Nullable final TenantFacade tenantFacade )
    {
        if( tenantFacade == null ) {
            TenantAccessor.tenantFacade = loadFacadeOrDefault();
        } else {
            TenantAccessor.tenantFacade = tenantFacade;
        }
    }

    private static TenantFacade loadFacadeOrDefault()
    {
        return FacadeLocator.getFacade(TenantFacade.class).getOrElseGet(e -> {
            log.debug("No TenantFacade found via FacadeLocator. Falling back to DefaultTenantFacade.");
            return new DefaultTenantFacade();
        });
    }

    /**
     * Returns the current {@link Tenant}.
     *
     * @return The current {@link Tenant}.
     *
     * @throws TenantAccessException
     *             If there is an issue while accessing the {@link Tenant}.
     */
    @Nonnull
    public static Tenant getCurrentTenant()
        throws TenantAccessException
    {
        return tryGetCurrentTenant().getOrElseThrow(failure -> {
            if( failure instanceof TenantAccessException ) {
                throw (TenantAccessException) failure;
            } else {
                throw new TenantAccessException("Failed to get current tenant.", failure);
            }
        });
    }

    /**
     * Returns a {@link Try} of the current {@link Tenant}, or, if the {@link Try} is a failure, the global fallback.
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
     * <td>A request is present with an "Authorization" header that contains a valid JWT bearer with field "app_tid",
     * "zid" (legacy), or "zone_uuid" (legacy).<br>
     * As a fallback a JWT will be retrieved from a bound XSUAA instance.</td>
     * <td>A request is not available, no "Authorization" header is present in the current request, the JWT bearer does
     * not hold a field "app_tid", "zid" (legacy), or "zone_uuid" (legacy), or there is no XSUAA service bound to this
     * application.</td>
     * </tr>
     * </table>
     *
     * @return A {@link Try} of the current {@link Tenant}.
     */
    @Nonnull
    public static Try<Tenant> tryGetCurrentTenant()
    {
        final Try<Tenant> tenantTry = tenantFacade.tryGetCurrentTenant();
        if( tenantTry.isSuccess() || fallbackTenant == null ) {
            return tenantTry;
        }

        @Nullable
        final Tenant fallback = fallbackTenant.get();
        if( fallback == null ) {
            return Try.failure(new TenantAccessException());
        }

        return tenantTry.recover(failure -> {
            log.warn("Recovering with fallback tenant: {}.", fallback, failure);
            return fallback;
        });
    }

    /**
     * Execute the given {@link Callable} on behalf of a given tenant.
     *
     * @param tenant
     *            The tenant to execute on behalf of.
     * @param callable
     *            The callable to execute.
     *
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the tenant.
     */
    @Nullable
    public static <T> T executeWithTenant( @Nonnull final Tenant tenant, @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final TenantFacade maybeTenantFacade = getTenantFacade();
        if( maybeTenantFacade == null || !DefaultTenantFacade.class.isAssignableFrom(maybeTenantFacade.getClass()) ) {
            throw new ThreadContextExecutionException(String.format("""
                The 'executeWith...' API is currently supported only when using the Cloud SDK's 'DefaultFacade'\
                 implementations (e.g. '%s'). This is an issue especially when using the CAP integration\
                 ('cds-integration-cloud-sdk'). To workaround this shortcoming, please refer to the CAP\
                 documentation about how to manipulate the request context:\
                 https://cap.cloud.sap/docs/java/request-contexts#defining-requestcontext.\
                """, DefaultTenantFacade.class.getName()));
        }

        return ((DefaultTenantFacade) maybeTenantFacade).executeWithTenant(tenant, callable);
    }

    /**
     * Execute the given {@link Executable} on behalf of a given tenant.
     *
     * @param tenant
     *            The tenant to execute on behalf of.
     * @param executable
     *            The operation to execute.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the tenant.
     */
    public static void executeWithTenant( @Nonnull final Tenant tenant, @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithTenant(tenant, () -> {
            executable.execute();
            return null;
        });
    }

    /**
     * Execute the given {@link Callable}, using the given tenant as fallback if there is no other tenant available.
     *
     * @param fallbackTenant
     *            The tenant to fall back to.
     * @param callable
     *            The callable to execute.
     * @param <T>
     *            The type of the callable.
     *
     * @return The value computed by the callable.
     *
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the tenant.
     */
    @Nullable
    public static <T> T executeWithFallbackTenant(
        @Nonnull final Supplier<Tenant> fallbackTenant,
        @Nonnull final Callable<T> callable )
        throws ThreadContextExecutionException
    {
        final Try<Tenant> tenantTry = tryGetCurrentTenant();

        if( tenantTry.isSuccess() ) {
            try {
                return callable.call();
            }
            catch( final ThreadContextExecutionException e ) {
                throw e;
            }
            catch( final Exception e ) {
                throw new ThreadContextExecutionException(e);
            }
        }

        return executeWithTenant(fallbackTenant.get(), callable);
    }

    /**
     * Execute the given {@link Executable}, using the given tenant as fallback if there is no other tenant available.
     *
     * @param fallbackTenant
     *            The tenant to fall back to.
     * @param executable
     *            The operation to execute.
     * @throws ThreadContextExecutionException
     *             If there is an issue while running the code on behalf of the tenant.
     */
    public static void executeWithFallbackTenant(
        @Nonnull final Supplier<Tenant> fallbackTenant,
        @Nonnull final Executable executable )
        throws ThreadContextExecutionException
    {
        executeWithFallbackTenant(fallbackTenant, () -> {
            executable.execute();
            return null;
        });
    }
}
