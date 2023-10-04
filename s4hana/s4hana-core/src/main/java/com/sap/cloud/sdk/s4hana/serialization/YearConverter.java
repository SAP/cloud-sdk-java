/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link com.sap.cloud.sdk.s4hana.types.Year}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class YearConverter extends AbstractErpTypeConverter<com.sap.cloud.sdk.s4hana.types.Year>
{
    /**
     * Statically created instance of this converter.
     */
    public static final YearConverter INSTANCE = new YearConverter();

    @Nonnull
    @Override
    public Class<com.sap.cloud.sdk.s4hana.types.Year> getType()
    {
        return com.sap.cloud.sdk.s4hana.types.Year.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final com.sap.cloud.sdk.s4hana.types.Year object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<com.sap.cloud.sdk.s4hana.types.Year> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(com.sap.cloud.sdk.s4hana.types.Year.fromString(domainObject));
    }
}
