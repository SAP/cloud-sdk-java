/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;

/**
 * Representation of a distribution channel (VTWEG).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@Deprecated
public class DistributionChannel extends StringBasedErpType<DistributionChannel>
{
    private static final long serialVersionUID = -1631961052410528581L;

    /**
     * The empty instance.
     */
    public static final DistributionChannel EMPTY = new DistributionChannel("");

    /**
     * Default constructor.
     *
     * @param value
     *            The String representation.
     * @throws IllegalArgumentException
     *             when the String value cannot be parsed accordingly.
     */
    public DistributionChannel( @Nullable final String value ) throws IllegalArgumentException
    {
        super(value);
    }

    /**
     * Static method factory for the value type.
     *
     * @param value
     *            The String representation.
     * @return A newly constructed value instance.
     * @throws IllegalArgumentException
     *             when the String value cannot be parsed accordingly.
     */
    @Nullable
    public static DistributionChannel of( @Nullable final String value )
        throws IllegalArgumentException
    {
        if( value == null ) {
            return null;
        }

        return new DistributionChannel(value);
    }

    @Nonnull
    @Override
    public ErpTypeConverter<DistributionChannel> getTypeConverter()
    {
        return DistributionChannelConverter.INSTANCE;
    }

    @Nonnull
    @Override
    public Class<DistributionChannel> getType()
    {
        return DistributionChannel.class;
    }

    @Override
    public int getMaxLength()
    {
        return 2;
    }

    @Nonnull
    @Override
    public FillCharStrategy getFillCharStrategy()
    {
        return FillCharStrategy.DO_NOTHING;
    }

    /**
     * Helper method to transform a collection of Strings to a Set of objects.
     *
     * @param values
     *            The items of type String
     * @return The Set of converted Objects.
     */
    @Nonnull
    public static Set<DistributionChannel> toDistributionChannels( @Nonnull final Collection<String> values )
    {
        return values
            .stream()
            .map(StringBasedErpType.transformToType(new DistributionChannelConverter()))
            .collect(Collectors.toSet());
    }

    /**
     * Helper method to transform a collection of objects to a Set of Strings.
     *
     * @param values
     *            The items of type DistributionChannel
     * @return The Set of String representations.
     */
    @Nonnull
    public static Set<String> toStrings( @Nonnull final Collection<DistributionChannel> values )
    {
        return values
            .stream()
            .map(StringBasedErpType.transformToString(new DistributionChannelConverter()))
            .collect(Collectors.toSet());
    }
}
