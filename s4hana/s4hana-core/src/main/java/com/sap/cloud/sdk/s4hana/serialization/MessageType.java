/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import lombok.Getter;

/**
 * The MessageType ERP type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public enum MessageType implements ErpType<MessageType>
{
    /**
     * SUCCESS
     */
    SUCCESS("S"),
    /**
     * INFORMATION
     */
    INFORMATION("I"),
    /**
     * WARNING
     */
    WARNING("W"),
    /**
     * ERROR
     */
    ERROR("E"),
    /**
     * ABORT
     */
    ABORT("A"),
    /**
     * EXIT
     */
    EXIT("X");

    @Getter
    private final String identifier;

    /**
     * Constructor.
     *
     * @param identifier
     *            The expected String identifier.
     */
    MessageType( @Nonnull final String identifier )
    {
        this.identifier = identifier;
    }

    @Nonnull
    @Override
    public String toString()
    {
        return identifier;
    }

    @Nonnull
    @Override
    public ErpTypeConverter<MessageType> getTypeConverter()
    {
        return MessageTypeConverter.INSTANCE;
    }

    /**
     * Static factory method.
     *
     * @param identifier
     *            A String representation of the message type.
     * @return The respective Enum value.
     * @throws IllegalArgumentException
     *             when the identifier could not be parsed accordingly.
     */
    @Nonnull
    public static MessageType ofIdentifier( @Nonnull final String identifier )
        throws IllegalArgumentException
    {
        for( final MessageType messageType : values() ) {
            if( messageType.getIdentifier().equals(identifier) ) {
                return messageType;
            }
        }

        throw new IllegalArgumentException(
            "Unknown " + MessageType.class.getSimpleName() + " identifier: " + identifier + ".");
    }
}
