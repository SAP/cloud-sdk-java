/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.datamodel.odata.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;
import com.sap.cloud.sdk.typeconverter.TypeConverter;

/**
 * Default implementation of the {@link TypeConverter} interface, returning all given objects unchanged.
 */
public class IdentityConverter implements TypeConverter<Object, Object>
{
    @Override
    @Nonnull
    public ConvertedObject<Object> toDomain( @Nullable final Object object )
    {
        return ConvertedObject.of(object);
    }

    @Override
    @Nonnull
    public ConvertedObject<Object> fromDomain( @Nullable final Object domainObject )
    {
        return ConvertedObject.of(domainObject);
    }

    @Override
    @Nonnull
    public Class<Object> getType()
    {
        return Object.class;
    }

    @Override
    @Nonnull
    public Class<Object> getDomainType()
    {
        return Object.class;
    }
}
