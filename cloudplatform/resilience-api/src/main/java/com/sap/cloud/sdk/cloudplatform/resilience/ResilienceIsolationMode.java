package com.sap.cloud.sdk.cloudplatform.resilience;

/**
 * Determines how to further isolate resilience constructs such as timeouts, circuit breakers, and bulkheads. This is in
 * addition to the built-in grouping by resource.
 */
public enum ResilienceIsolationMode
{
    /**
     * Global key that does not differentiate between tenants or users.
     */
    NO_ISOLATION,

    /**
     * Tenant-only key that differentiates between tenants but not users. If the current tenant cannot be determined, a
     * {@link ResilienceRuntimeException} will be thrown during runtime.
     */
    TENANT_REQUIRED,

    /**
     * Tenant-only key that differentiates between tenants but not users. Existence of tenant is optional, so if the
     * current tenant cannot be determined it will behave the same as {@link ResilienceIsolationMode#NO_ISOLATION}.
     */
    TENANT_OPTIONAL,

    /**
     * User-only key that differentiates between users but not tenants. If the current user cannot be determined, a
     * {@link ResilienceRuntimeException} will be thrown during runtime.
     */
    PRINCIPAL_REQUIRED,

    /**
     * User-only key that differentiates between users but not tenants. Existence of user is optional, so if the current
     * user cannot be determined it will behave the same as {@link ResilienceIsolationMode#NO_ISOLATION}.
     */
    PRINCIPAL_OPTIONAL,

    /**
     * Tenant and user key that differentiates between both tenants and users. If the current user or tenant cannot be
     * determined, a {@link ResilienceRuntimeException} will be thrown during runtime.
     */
    TENANT_AND_USER_REQUIRED,

    /**
     * Tenant and user key that tries to differentiate between both tenants and users. Existence of user and tenant are
     * optional, so if any of these cannot be determined it will behave like one of the less isolated modes.
     */
    TENANT_AND_USER_OPTIONAL
}
