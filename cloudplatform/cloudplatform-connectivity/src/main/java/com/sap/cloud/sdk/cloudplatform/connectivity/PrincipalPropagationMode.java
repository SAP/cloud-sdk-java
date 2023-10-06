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
     * Recommended principal propagation strategy.
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
    RECOMMENDED,

    /**
     * Compatibility mode for principal propagation.
     * <p>
     * <strong>Header:</strong> "Proxy-Authorization" Token lookup (Client Credentials Grant) on behalf of technical
     * credentials of connectivity service binding.
     * <p>
     * <strong>Header:</strong> "SAP-Connectivity-Authentication" Token forwarding of current user token (principal).
     * <p>
     * <strong>Note:</strong> The OAuth2 service of target tenant will be used.
     */
    COMPATIBILITY,

    /**
     * Unknown principal propagation mode.
     * <p>
     * This will likely lead to an error.
     */
    UNKNOWN;

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
