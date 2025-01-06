/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link java.math.BigDecimal}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class BigDecimalConverter extends AbstractErpTypeConverter<BigDecimal>
{
    /**
     * Statically created instance of this converter.
     */
    public static final BigDecimalConverter INSTANCE = new BigDecimalConverter();

    @Nonnull
    @Override
    public Class<BigDecimal> getType()
    {
        return BigDecimal.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final BigDecimal object )
    {
        return ErpDecimalConverter.INSTANCE.toDomainNonNull(new ErpDecimal(object));
    }

    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    @Override
    public ConvertedObject<BigDecimal> fromDomainNonNull( @Nonnull final String domainObject )
        throws Exception
    {
        return fromInputToDomainToPrimitive(ErpDecimalConverter.INSTANCE, domainObject, ErpDecimal::getValue);
    }
}
