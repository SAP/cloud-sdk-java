/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Short}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ShortConverter extends AbstractErpTypeConverter<Short>
{
    /**
     * Statically created instance of this converter.
     */
    public static final ShortConverter INSTANCE = new ShortConverter();

    @Nonnull
    @Override
    public Class<Short> getType()
    {
        return Short.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Short object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<Short> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(Short.valueOf(domainObject));
    }
}
