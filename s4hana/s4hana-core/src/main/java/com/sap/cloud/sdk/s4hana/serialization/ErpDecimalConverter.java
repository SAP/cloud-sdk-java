/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link ErpDecimal}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class ErpDecimalConverter extends AbstractErpTypeConverter<ErpDecimal>
{
    /**
     * Statically created instance of this converter.
     */
    public static final ErpDecimalConverter INSTANCE = new ErpDecimalConverter();

    @Nonnull
    @Override
    public Class<ErpDecimal> getType()
    {
        return ErpDecimal.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final ErpDecimal object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<ErpDecimal> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new ErpDecimal(domainObject));
    }
}
