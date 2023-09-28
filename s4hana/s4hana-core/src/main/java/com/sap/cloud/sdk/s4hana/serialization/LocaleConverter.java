package com.sap.cloud.sdk.s4hana.serialization;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Locale}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class LocaleConverter extends AbstractErpTypeConverter<Locale>
{
    /**
     * Statically created instance of this converter.
     */
    public static final LocaleConverter INSTANCE = new LocaleConverter();

    @Nonnull
    @Override
    public Class<Locale> getType()
    {
        return Locale.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Locale object )
    {
        return ConvertedObject.of(object.getLanguage().toUpperCase(Locale.ENGLISH));
    }

    @Nonnull
    @Override
    public ConvertedObject<Locale> fromDomainNonNull( @Nonnull final String domainObject )
    {
        return ConvertedObject.of(new Locale.Builder().setLanguage(domainObject).build());
    }
}
