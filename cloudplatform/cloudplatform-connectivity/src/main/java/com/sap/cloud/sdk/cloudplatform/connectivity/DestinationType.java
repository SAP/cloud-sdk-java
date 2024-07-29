/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing the types a destination can be of.
 */
@Slf4j
public enum DestinationType
{
    /**
     * HTTP
     */
    HTTP("HTTP"),

    /**
     * RFC
     */
    RFC("RFC"),

    /**
     * MAIL
     */
    MAIL("MAIL"),

    /**
     * LDAP
     */
    LDAP("LDAP");

    @Getter
    @Nonnull
    private final String identifier;

    DestinationType( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public String toString()
    {
        return identifier;
    }

    /**
     * Returns the {@code DestinationType} which equals the given identifier.
     *
     * @param identifier
     *            The identifier to get a {@code DestinationType} for.
     *
     * @return The matching {@code DestinationType}.
     *
     * @throws IllegalArgumentException
     *             If the given identifier does not map to a {@code DestinationType}.
     */
    @Nonnull
    public static DestinationType ofIdentifier( @Nonnull final String identifier )
        throws IllegalArgumentException
    {
        for( final DestinationType destinationType : values() ) {
            if( destinationType.getIdentifier().equals(identifier) ) {
                return destinationType;
            }
        }

        throw new IllegalArgumentException(
            "Unknown " + DestinationType.class.getSimpleName() + ": " + identifier + ".");
    }

    /**
     * Returns the {@code DestinationType} which equals the given identifier, or the defaultDestinationType in case
     * there is none.
     *
     * @param identifier
     *            The identifier to get a {@code DestinationType} for.
     * @param defaultDestinationType
     *            The {@code DestinationType} to return if no matching {@code DestinationType} could be found.
     *
     * @return The matching {@code DestinationType} or the default, if none is matching.
     */
    @Nonnull
    public static DestinationType ofIdentifierOrDefault(
        @Nullable final String identifier,
        @Nonnull final DestinationType defaultDestinationType )
    {
        if( identifier == null ) {
            return defaultDestinationType;
        }

        try {
            return ofIdentifier(identifier);
        }
        catch( final IllegalArgumentException e ) {
            log.warn("Identifier '{}' is not supported. Falling back to {}.", identifier, defaultDestinationType);
            return defaultDestinationType;
        }
    }
}
