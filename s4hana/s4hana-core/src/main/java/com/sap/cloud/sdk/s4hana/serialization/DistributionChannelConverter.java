/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link DistributionChannel}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class DistributionChannelConverter extends AbstractErpTypeConverter<DistributionChannel>
{
    /**
     * Statically created instance of this converter.
     */
    public static final DistributionChannelConverter INSTANCE = new DistributionChannelConverter();

    @Nonnull
    @Override
    public Class<DistributionChannel> getType()
    {
        return DistributionChannel.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final DistributionChannel object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<DistributionChannel> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new DistributionChannel(domainObject));
    }
}
