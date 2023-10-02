/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.cloudplatform.CloudPlatform;

import lombok.Getter;

/**
 * Defines from where the HTTP Security Configuration for outbound HTTP calls is determined from.
 *
 * The {@link HttpDestination} allows setting a key store and/or a trust store.
 *
 * The {@link CloudPlatform} may have platform-specific security settings managed by the infrastructure.
 */
public enum SecurityConfigurationStrategy
{
    /**
     * The HTTP Security Configuration is taken from configuration of the {@link HttpDestination}.
     */
    FROM_DESTINATION("from_destination"),
    /**
     * The HTTP Security Configuration is taken from the {@link CloudPlatform}.
     */
    FROM_PLATFORM("from_platform");

    @Nonnull
    @Getter
    private final String identifier;

    SecurityConfigurationStrategy( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    /**
     * Returns the default value for the {@link SecurityConfigurationStrategy}.
     *
     * @return The default value
     */
    public static SecurityConfigurationStrategy getDefault()
    {
        return FROM_DESTINATION;
    }

    /**
     * Returns the {@link SecurityConfigurationStrategy} which equals the given identifier, or the {@link #getDefault()}
     * in case the provided {@code identifier} is {@code null}.
     *
     * @param identifier
     *            The identifier to get a {@link SecurityConfigurationStrategy} for.
     * @return The matching {@link SecurityConfigurationStrategy} or the default, if the {@code identifier} is
     *         {@code null}.
     * @throws IllegalArgumentException
     *             if no {@link SecurityConfigurationStrategy} could be found for the given identifier.
     */
    @Beta
    @Nonnull
    public static SecurityConfigurationStrategy ofIdentifierOrDefault( @Nullable final String identifier )
    {
        if( identifier == null ) {
            return getDefault();
        }

        for( final SecurityConfigurationStrategy strategy : values() ) {
            if( strategy.getIdentifier().equals(identifier) ) {
                return strategy;
            }
        }

        throw new IllegalArgumentException("No SecurityConfigurationStrategy found for identifier: " + identifier);
    }

    @Override
    public String toString()
    {
        return identifier;
    }
}
