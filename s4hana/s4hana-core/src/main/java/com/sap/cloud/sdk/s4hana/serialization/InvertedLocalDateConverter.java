package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link InvertedLocalDate}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class InvertedLocalDateConverter extends AbstractErpTypeConverter<InvertedLocalDate>
{
    /**
     * Statically created instance of this converter.
     */
    public static final InvertedLocalDateConverter INSTANCE = new InvertedLocalDateConverter();

    @Nonnull
    @Override
    public Class<InvertedLocalDate> getType()
    {
        return InvertedLocalDate.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final InvertedLocalDate object )
    {
        return ConvertedObject.of(object.toString());
    }

    @Nonnull
    @Override
    public ConvertedObject<InvertedLocalDate> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new InvertedLocalDate(domainObject));
    }
}
