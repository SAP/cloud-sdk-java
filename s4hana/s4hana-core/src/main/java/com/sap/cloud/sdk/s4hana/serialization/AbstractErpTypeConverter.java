/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.util.function.Function;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Abstract type converter base class for converting types to and from their ERP counterparts.
 *
 * @param <T>
 *            The type to convert.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public abstract class AbstractErpTypeConverter<T> extends AbstractTypeConverter<T, String>
    implements
    ErpTypeConverter<T>
{
    @Nonnull
    @Override
    public Class<String> getDomainType()
    {
        return String.class;
    }

    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    <InputT, PrimitiveT, DomainT> ConvertedObject<PrimitiveT> fromInputToDomainToPrimitive(
        final AbstractTypeConverter<DomainT, InputT> typeConverter,
        final InputT domainObject,
        final Function<DomainT, PrimitiveT> mapValue )
        throws Exception
    {
        final ConvertedObject<DomainT> convertedDomain = typeConverter.fromDomainNonNull(domainObject);
        if( convertedDomain.isNotConvertible() ) {
            return ConvertedObject.ofNotConvertible();
        }

        final DomainT domainT = convertedDomain.get();
        if( domainT == null ) {
            return ConvertedObject.ofNull();
        }

        return ConvertedObject.of(mapValue.apply(domainT));
    }
}
