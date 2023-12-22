/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * Enumeration which represents the strategies for performing a user token exchange, if necessary, upon retrieving a
 * destination from the Destination service on BTP Cloud Foundry.
 */
public enum DestinationServiceTokenExchangeStrategy
{
    /**
     * Default strategy of forwarding current user access token to the destination lookup.
     * <p>
     * The BTP Destination Service supports the option to automatically manage token-exchange operations when necessary.
     * This mode results in applications only invoking one outbound HTTP request per destination lookup, instead of two.
     */
    FORWARD_USER_TOKEN("ForwardUserToken"),

    /**
     * Legacy strategy of performing the user token exchange actively when necessary.
     * <p>
     * When this strategy is used, the {@link DestinationService} first performs a "look up" of the destination by
     * issuing a client credentials request to the destination service. The response then contains the destination,
     * which is needed to determine the actual authentication type. Afterwards, depending on the authentication type, a
     * user token exchange might be performed automatically.
     *
     * @deprecated since 5.0.1. Use {@link #FORWARD_USER_TOKEN} instead.
     */
    @Deprecated
    LOOKUP_THEN_EXCHANGE("LookupThenExchange"),

    /**
     * Legacy strategy to explicitly <b>not</b> perform a user token exchange.
     * <p>
     * In the past, this approach was recommended for use cases where only the destination properties were required.
     * Now, this has been superseded by {@link DestinationService#getDestinationProperties(String)}.
     * <p>
     * <b>Caution</b>: The retrieved destination might not be suitable to actually connect to the target system because
     * the token exchange request is skipped when using this strategy, even if the destination demands an authentication
     * type that is based on the user token exchange (such as {@link AuthenticationType#OAUTH2_JWT_BEARER}).
     *
     * @deprecated since 5.0.1. Use {@link #FORWARD_USER_TOKEN} instead. If you just need the destination properties,
     *             use {@link DestinationService#getDestinationProperties(String)} instead.
     */
    @Deprecated
    LOOKUP_ONLY("LookupOnly"),

    /**
     * Legacy strategy that can be used for destinations which require a user token for the authentication flow.
     * <p>
     * <b>Caution</b>: This strategy is a performance loss and is subject to stricter rate-limits compared to
     * {@link #FORWARD_USER_TOKEN}. It should only be used in case the destination service responds with an error when
     * {@link #FORWARD_USER_TOKEN} is used.
     */
    EXCHANGE_ONLY("ExchangeOnly");

    @Getter
    @Nonnull
    private final String identifier;

    DestinationServiceTokenExchangeStrategy( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    @Override
    public String toString()
    {
        return identifier;
    }

    @Nullable
    static DestinationServiceTokenExchangeStrategy ofIdentifier( @Nullable final String identifier )
    {
        return Stream
            .of(DestinationServiceTokenExchangeStrategy.values())
            .filter(s -> s.getIdentifier().equalsIgnoreCase(identifier))
            .findFirst()
            .orElse(null);
    }
}
