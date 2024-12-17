/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import lombok.RequiredArgsConstructor;

/**
 * Represents the mode of principal propagation.
 *
 * @since 5.0.0
 */
@RequiredArgsConstructor
public enum PrincipalPropagationMode
{
    /**
     * Principal propagation strategy based on exchanging the user token. <strong>This strategy is generally NOT
     * recommended</strong>. Please consider switching to {@link #TOKEN_FORWARDING} instead.
     * <p>
     * Using the {@code TOKEN_EXCHANGE} strategy produces additional round-trips and increases the load on the XSUAA
     * service. The XSUAA service is rate limited, so this strategy can lead to on-premise calls being blocked by that
     * rate limit.
     * <p>
     * Using this strategy the following headers will be populated:
     * <ul>
     * <li><strong>Header:</strong> "Proxy-Authorization" Token exchange (JWT Bearer Token Grant) on behalf of current
     * user.</li>
     * <li>token (principal) with technical credentials of connectivity service binding.</li>
     * </ul>
     */
    TOKEN_EXCHANGE,

    /**
     * Principal propagation strategy based on forwarding the user token.
     * <p>
     * Using this strategy the following headers will be populated:
     * <ul>
     * <li><strong>Header:</strong> "Proxy-Authorization" Token lookup (Client Credentials Grant) on behalf of a
     * technical user for the current tenant using the credentials of connectivity service binding.</li>
     * <li><strong>Header:</strong> "SAP-Connectivity-Authentication" Token forwarding of current user token
     * (principal).</li>
     * </ul>
     */
    TOKEN_FORWARDING,

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
        return UNKNOWN;
    }
}
