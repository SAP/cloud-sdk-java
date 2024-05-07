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
 * Representation of a SAP client (MANDT).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@Deprecated
public class SapClient extends StringBasedErpType<SapClient>
{
    private static final long serialVersionUID = 6256161724339462422L;

    /**
     * Placeholder to indicate that the default {@link SapClient} should be used when connecting to the ERP.
     * <p>
     * <strong>Note:</strong> This value does not contain the actual value of the ERP's {@link SapClient}, it just
     * logically represents the unknown default value that the respective ERP uses.
     */
    public static final SapClient DEFAULT = new SapClient("");

    /**
     * Represents a {@link SapClient} with an empty value. How this is value is interpreted depends on the respective
     * business scenario.
     * <p>
     * <strong>Important:</strong> While this value currently corresponds to {@link #DEFAULT}, this equivalence must not
     * be taken for granted and may change in the future.
     */
    public static final SapClient EMPTY = new SapClient("");

    /**
     * Creates a new {@code SapClient} representing the given ERP String.
     *
     * @param value
     *            The String value to represent.
     * @throws IllegalArgumentException
     *             If {@code value} is null or the length of {@code value} is longer than the length specified by
     *             {@link #getMaxLength()}.
     */
    public SapClient( @Nonnull final String value ) throws IllegalArgumentException
    {
        super(value);
    }

    /**
     * Creates a new {@code SapClient} based on the given String {@code value}.
     *
     * @param value
     *            The String value the {@code SapClient} should represent.
     * @return A newly created {@code SapClient}, or null, if {@code value} is null.
     * @throws IllegalArgumentException
     *             If the length of {@code value} is longer than the length specified by {@link #getMaxLength()}.
     */
    @Nullable
    public static SapClient of( @Nullable final String value )
        throws IllegalArgumentException
    {
        if( value == null ) {
            return null;
        }

        return new SapClient(value);
    }

    @Nonnull
    @Override
    public ErpTypeConverter<SapClient> getTypeConverter()
    {
        return SapClientConverter.INSTANCE;
    }

    @Nonnull
    @Override
    public Class<SapClient> getType()
    {
        return SapClient.class;
    }

    @Override
    public int getMaxLength()
    {
        return 3;
    }

    @Nonnull
    @Override
    public FillCharStrategy getFillCharStrategy()
    {
        return FillCharStrategy.FILL_LEADING;
    }

    /**
     * Transforms the given collection of Strings representing {@code SapClient}s to a set of {@code SapClient}s via the
     * {@code SapClientConverter}.
     *
     * @param values
     *            The collection of strings to convert.
     *
     * @return The set of {@code SapClient}s.
     */
    @Nonnull
    public static Set<SapClient> toSapClients( @Nonnull final Collection<String> values )
    {
        return values.stream().map(transformToType(new SapClientConverter())).collect(Collectors.toSet());
    }

    /**
     * Transforms the given collection of {@code SapClient}s to a set of Strings representing those {@code SapClient}s
     * via the {@code SapClientConverter}.
     *
     * @param values
     *            The collection of {@code SapClient}s to convert.
     *
     * @return The set of Strings.
     */
    @Nonnull
    public static Set<String> toStrings( @Nonnull final Collection<SapClient> values )
    {
        return values.stream().map(transformToString(new SapClientConverter())).collect(Collectors.toSet());
    }
}
