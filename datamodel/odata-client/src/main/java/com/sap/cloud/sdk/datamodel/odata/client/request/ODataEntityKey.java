/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

import lombok.extern.slf4j.Slf4j;

/**
 * Fluent API class to build and hold entity keys. A key is comprised of one or multiple individual key-value pairs.
 */
@Slf4j
public class ODataEntityKey extends AbstractODataParameters
{
    /**
     * Create a new, empty entity key for the given protocol version.
     *
     * @param protocol
     *            The {@link ODataProtocol} version this key should conform to.
     */
    public ODataEntityKey( @Nonnull final ODataProtocol protocol )
    {
        super(protocol);
    }

    /**
     * Get all field names that are part of this key.
     *
     * @return A set of entity property field names.
     */
    @Nonnull
    public Set<String> getFieldNames()
    {
        return getParameters().keySet();
    }

    /**
     * Add an entity property to this key.
     *
     * @param propertyName
     *            Name of the property (derived from the EDMX)
     * @param value
     *            Property value, assumed to be a primitive.
     * @param <PrimitiveT>
     *            Type of the primitive value.
     * @return The modified instance.
     *
     * @throws IllegalArgumentException
     *             When a parameter by that idenfitier already exists or primitive type is not supported.
     */
    @Nonnull
    public <PrimitiveT> ODataEntityKey addKeyProperty(
        @Nonnull final String propertyName,
        @Nullable final PrimitiveT value )
    {
        super.addParameterInternal(propertyName, value);
        return this;
    }

    /**
     * Add properties to the OData entity key.
     *
     * @param properties
     *            The key-value mapping.
     * @return The same instance.
     *
     * @throws IllegalArgumentException
     *             When the map contains a primitive type that is not supported.
     *
     * @see #addKeyProperty(String, Object)
     */
    @Nonnull
    public ODataEntityKey addKeyProperties( @Nonnull final Map<String, Object> properties )
    {
        super.addParameterSetInternal(properties);
        return this;
    }

    /**
     * Create an instance of {@link ODataEntityKey} from a generic key-value composition.
     *
     * @param key
     *            Key-value pairs of entity properties and their values.
     * @param protocol
     *            The {@link ODataProtocol} version this key should conform to.
     * @return A new instance of {@link ODataEntityKey}.
     *
     * @throws IllegalArgumentException
     *             When the map contains a primitive type that is not supported.
     *
     * @see #addKeyProperty(String, Object)
     */
    @Nonnull
    public static ODataEntityKey of( @Nonnull final Map<String, Object> key, @Nonnull final ODataProtocol protocol )
    {
        return new ODataEntityKey(protocol).addKeyProperties(key);
    }

    @Override
    @Nonnull
    public String toEncodedString( @Nonnull final UriEncodingStrategy strategy )
    {
        if( getParameters().isEmpty() ) {
            log
                .warn(
                    "The current entity key is empty. Using it within a request will cause the request to fail as empty entity keys are not allowed.");
        }
        return super.toEncodedString(strategy);
    }

    /**
     * Serializes the entity key into an <strong>unencoded</strong> OData URL format for entity keys.
     *
     * @return Encoded URL string representation of entity key.
     */
    @Nonnull
    @Override
    public String toString()
    {
        if( getParameters().isEmpty() ) {
            log
                .warn(
                    "The current entity key is empty. Using it within a request will cause the request to fail as empty entity keys are not allowed.");
        }
        return super.toString();
    }
}
