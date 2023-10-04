/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link SapClient}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class SapClientConverter extends AbstractErpTypeConverter<SapClient>
{
    /**
     * Statically created instance of this converter.
     */
    public static final SapClientConverter INSTANCE = new SapClientConverter();

    @Nonnull
    @Override
    public Class<SapClient> getType()
    {
        return SapClient.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final SapClient object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<SapClient> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new SapClient(domainObject));
    }
}
