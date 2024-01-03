/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link MessageNumber}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class MessageNumberConverter extends AbstractErpTypeConverter<MessageNumber>
{
    /**
     * Statically created instance of this converter.
     */
    public static final MessageNumberConverter INSTANCE = new MessageNumberConverter();

    @Nonnull
    @Override
    public Class<MessageNumber> getType()
    {
        return MessageNumber.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final MessageNumber object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<MessageNumber> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new MessageNumber(domainObject));
    }
}
