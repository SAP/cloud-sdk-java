/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.cloudplatform.connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum representing the type of a remote installation.
 */
@Slf4j
public enum ProxyType
{
    /**
     * Represents a common Internet connection.
     */
    INTERNET("Internet"),

    /**
     * Represents a connection to an on-premise systems.
     */
    ON_PREMISE("OnPremise"),

    /**
     * Represents a connection within a private Hyperscaler network.
     */
    PRIVATE_LINK("PrivateLink");

    @Getter
    private final String identifier;

    ProxyType( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    /**
     * Returns the {@code ProxyType} which equals the given identifier, or the {@code defaultProxyType} in case there is
     * none.
     *
     * @param identifier
     *            The identifier to get a {@code ProxyType} for.
     * @param defaultProxyType
     *            The {@code ProxyType} to return if no matching {@code ProxyType} could be found.
     * @return The matching {@code ProxyType} or the default, if none is matching.
     */
    @Nonnull
    public static
        ProxyType
        ofIdentifierOrDefault( @Nullable final String identifier, @Nonnull final ProxyType defaultProxyType )
    {
        if( identifier == null ) {
            return defaultProxyType;
        }

        try {
            return ofIdentifier(identifier);
        }
        catch( final IllegalArgumentException e ) {
            log.warn("Identifier '{}' is not supported. Falling back to {}.", identifier, defaultProxyType);
            return defaultProxyType;
        }
    }

    /**
     * Returns the {@code ProxyType} which equals the given identifier.
     *
     * @param identifier
     *            The identifier to get a {@code ProxyType} for.
     * @return The matching {@code ProxyType}.
     * @throws IllegalArgumentException
     *             If the given identifier does not map to a {@code ProxyType}.
     */
    @Nonnull
    public static ProxyType ofIdentifier( @Nonnull final String identifier )
        throws IllegalArgumentException
    {
        final ProxyType proxyType = getProxyTypeByIdentifier(identifier);

        if( proxyType == null ) {
            throw new IllegalArgumentException("Unknown " + ProxyType.class.getSimpleName() + ": " + identifier + ".");
        }

        return proxyType;
    }

    @Nullable
    private static ProxyType getProxyTypeByIdentifier( @Nullable final String identifier )
    {
        for( final ProxyType proxyType : values() ) {
            if( proxyType.getIdentifier().equals(identifier) ) {
                return proxyType;
            }
        }

        return null;
    }

    /**
     * Returns the {@code ProxyType} which equals the given identifier.
     * <p>
     * The matching considers different spellings of {@code ProxyType.ON_PREMISE}.
     *
     * @param identifier
     *            The identifier to get a {@code ProxyType} for.
     * @return The matching {@code ProxyType}.
     * @throws IllegalArgumentException
     *             If the given identifier does not map to a {@code ProxyType}.
     */
    @Nonnull
    public static ProxyType ofIdentifierSensitive( @Nonnull final String identifier )
        throws IllegalArgumentException
    {
        final ProxyType proxyType = getProxyTypeByIdentifier(identifier);

        if( proxyType != null ) {
            return proxyType;
        }

        if( identifier.equalsIgnoreCase("onpremise")
            || identifier.equalsIgnoreCase("on-premise")
            || identifier.equalsIgnoreCase("on_premise") ) {
            return ON_PREMISE;
        }

        throw new IllegalArgumentException("Unknown " + ProxyType.class.getSimpleName() + ": " + identifier + ".");
    }

    @Nonnull
    @Override
    public String toString()
    {
        return identifier;
    }

}
