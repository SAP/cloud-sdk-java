/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Boolean}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class BooleanConverter extends AbstractErpTypeConverter<Boolean>
{
    /**
     * Statically created instance of this converter.
     */
    public static final BooleanConverter INSTANCE = new BooleanConverter();

    @Nonnull
    @Override
    public Class<Boolean> getType()
    {
        return Boolean.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Boolean object )
    {
        return ErpBooleanConverter.INSTANCE.toDomainNonNull(new ErpBoolean(object));
    }

    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    @Override
    public ConvertedObject<Boolean> fromDomainNonNull( @Nonnull final String domainObject )
        throws Exception
    {
        return fromInputToDomainToPrimitive(ErpBooleanConverter.INSTANCE, domainObject, ErpBoolean::getValue);
    }
}
