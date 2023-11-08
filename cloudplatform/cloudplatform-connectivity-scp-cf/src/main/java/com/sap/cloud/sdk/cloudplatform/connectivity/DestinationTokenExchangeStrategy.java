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
public enum DestinationTokenExchangeStrategy
{
    /**
     * Default strategy of forwarding current user access token to the destination lookup.
     * <p>
     * The BTP Destination Service supports the option to automatically manage token-exchange operations when necessary.
     * This mode results in applications only invoking one outbound HTTP request per destination lookup, instead of two.
     */
    FORWARD_USER_TOKEN("ForwardUserToken"),

    /**
     * Classic strategy of performing the user token exchange actively when necessary.
     * <p>
     * When this strategy is used, the {@link DestinationService} first performs a "look up" of the destination by
     * issuing a client credentials request to the destination service. The response then contains the destination,
     * which is needed to determine the actual authentication type. Afterwards, depending that authentication type, a
     * user token exchange might be performed automatically.
     */
    LOOKUP_THEN_EXCHANGE("LookupThenExchange"),

    /**
     * Use this strategy if you are interested in reading only the destination's properties.
     * <p>
     * <b>Caution</b>: The retrieved destination might not be suitable to actually connect to the target system because
     * the token exchange request is skipped when using this strategy, even if the destination demands an authentication
     * type that is based on the user token exchange (such as {@link AuthenticationType#OAUTH2_JWT_BEARER}).
     */
    LOOKUP_ONLY("LookupOnly"),

    /**
     * Use this strategy if you <b>know for sure</b> that the requested destination requires a user token exchange for
     * the authentication.
     * <p>
     * <b>Caution</b>: The {@link DestinationService} skips the initial "look up" request against the destination
     * service, which is usually needed to determine whether a user token exchange is needed. Instead, the user token
     * exchange is performed immediately, which might cause errors if the destination is not suited for that flow.
     */
    EXCHANGE_ONLY("ExchangeOnly");

    @Getter
    @Nonnull
    private final String identifier;

    DestinationTokenExchangeStrategy( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    @Override
    public String toString()
    {
        return identifier;
    }

    @Nullable
    static DestinationTokenExchangeStrategy ofIdentifier( @Nullable final String identifier )
    {
        return Stream
            .of(DestinationTokenExchangeStrategy.values())
            .filter(s -> s.getIdentifier().equalsIgnoreCase(identifier))
            .findFirst()
            .orElse(null);
    }
}
