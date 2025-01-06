/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Float}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class FloatConverter extends AbstractErpTypeConverter<Float>
{
    /**
     * Statically created instance of this converter.
     */
    public static final FloatConverter INSTANCE = new FloatConverter();

    @Nonnull
    @Override
    public Class<Float> getType()
    {
        return Float.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Float object )
    {
        return ErpDecimalConverter.INSTANCE.toDomainNonNull(new ErpDecimal(object));
    }

    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    @Override
    public ConvertedObject<Float> fromDomainNonNull( @Nonnull final String domainObject )
        throws Exception
    {
        return fromInputToDomainToPrimitive(ErpDecimalConverter.INSTANCE, domainObject, ErpDecimal::floatValue);
    }
}
