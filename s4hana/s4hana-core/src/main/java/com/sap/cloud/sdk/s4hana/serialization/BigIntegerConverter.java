/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link BigInteger}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class BigIntegerConverter extends AbstractErpTypeConverter<BigInteger>
{
    /**
     * Statically created instance of this converter.
     */
    public static final BigIntegerConverter INSTANCE = new BigIntegerConverter();

    @Nonnull
    @Override
    public Class<BigInteger> getType()
    {
        return BigInteger.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final BigInteger object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<BigInteger> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new BigInteger(domainObject));
    }
}
