/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link MessageType}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class MessageTypeConverter extends AbstractErpTypeConverter<MessageType>
{
    /**
     * Statically created instance of this converter.
     */
    public static final MessageTypeConverter INSTANCE = new MessageTypeConverter();

    @Nonnull
    @Override
    public Class<MessageType> getType()
    {
        return MessageType.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final MessageType object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<MessageType> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(MessageType.ofIdentifier(domainObject));
    }
}
