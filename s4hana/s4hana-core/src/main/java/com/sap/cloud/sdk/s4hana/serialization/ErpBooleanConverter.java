/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link ErpBoolean}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ErpBooleanConverter extends AbstractErpTypeConverter<ErpBoolean>
{
    /**
     * Statically created instance of this converter.
     */
    public static final ErpBooleanConverter INSTANCE = new ErpBooleanConverter();

    @Nonnull
    @Override
    public Class<ErpBoolean> getType()
    {
        return ErpBoolean.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final ErpBoolean object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<ErpBoolean> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new ErpBoolean(domainObject));
    }
}
