/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Long}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class LongConverter extends AbstractErpTypeConverter<Long>
{
    /**
     * Statically created instance of this converter.
     */
    public static final LongConverter INSTANCE = new LongConverter();

    @Nonnull
    @Override
    public Class<Long> getType()
    {
        return Long.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Long object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<Long> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(Long.valueOf(domainObject));
    }
}
