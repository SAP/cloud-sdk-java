/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Integer}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class IntegerConverter extends AbstractErpTypeConverter<Integer>
{
    /**
     * Statically created instance of this converter.
     */
    public static final IntegerConverter INSTANCE = new IntegerConverter();

    @Nonnull
    @Override
    public Class<Integer> getType()
    {
        return Integer.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Integer object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<Integer> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(Integer.valueOf(domainObject));
    }
}
