/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Byte}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ByteConverter extends AbstractErpTypeConverter<Byte>
{
    /**
     * Statically created instance of this converter.
     */
    public static final ByteConverter INSTANCE = new ByteConverter();

    @Nonnull
    @Override
    public Class<Byte> getType()
    {
        return Byte.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Byte object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<Byte> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(Byte.valueOf(domainObject));
    }
}
