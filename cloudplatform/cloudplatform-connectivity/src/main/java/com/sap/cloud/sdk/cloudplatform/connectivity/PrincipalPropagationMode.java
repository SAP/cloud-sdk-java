/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import com.google.common.annotations.Beta;

import lombok.RequiredArgsConstructor;

/**
 * Represents the mode of principal propagation.
 *
 * @since 5.0.0
 */
@RequiredArgsConstructor
@Beta
public enum PrincipalPropagationMode
{
    /**
     * Same as {@link #TOKEN_EXCHANGE}. <strong>This is NOT the recommended strategy</strong>. Please consider switching
     * to {@link #TOKEN_FORWARDING} instead.
     *
     * @deprecated Please use {@link #TOKEN_EXCHANGE} instead.
     */
    @Deprecated
    RECOMMENDED,

    /**
     * Principal propagation strategy that will exchange the user token.
     * <p>
     * <strong>Header:</strong> "Proxy-Authorization" Token exchange (JWT Bearer Token Grant) on behalf of current user
     * token (principal) with technical credentials of connectivity service binding. The OAuth2 service of target tenant
     * will be used.
     * <p>
     * <strong>Note:</strong> The OAuth2 service of target tenant will be used.
     * <p>
     * <strong>Note:</strong> Despite the name, we're not recommending this mode by default. Due to token exchanges and
     * additional round-trips, this leads to an increased load on the XSUAA service instance. The platform rate limiter
     * could quickly block further OnPremise calls.
     */
    TOKEN_EXCHANGE,

    /**
     * Same as {@link #TOKEN_FORWARDING}.
     *
     * @deprecated Please use {@link #TOKEN_FORWARDING} instead.
     */
    @Deprecated
    COMPATIBILITY,

    /**
     * Token forwarding strategy for principal propagation.
     * <p>
     * <strong>Header:</strong> "Proxy-Authorization" Token lookup (Client Credentials Grant) on behalf of a technical
     * user for the current tenant using the credentials of connectivity service binding.
     * <p>
     * <strong>Header:</strong> "SAP-Connectivity-Authentication" Token forwarding of current user token (principal).
     * <p>
     * <strong>Note:</strong> The OAuth2 service of target tenant will be used.
     */
    TOKEN_FORWARDING,

    /**
     * Unknown principal propagation mode.
     * <p>
     * This will likely lead to an error.
     */
    UNKNOWN;

    /**
     * The default strategy for principal propagation.
     *
     * @return {@link #TOKEN_FORWARDING}
     */
    public static PrincipalPropagationMode getDefault()
    {
        return TOKEN_FORWARDING;
    }

    static PrincipalPropagationMode ofIdentifier( final String identifier )
    {
        for( final PrincipalPropagationMode mode : values() ) {
            if( mode.name().equalsIgnoreCase(identifier.trim()) ) {
                return mode;
            }
        }
        return PrincipalPropagationMode.UNKNOWN;
    }
}
