package com.sap.cloud.sdk.s4hana.serialization;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

/**
 * Type converter for {@link Double}.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Deprecated
public class DoubleConverter extends AbstractErpTypeConverter<Double>
{
    /**
     * Statically created instance of this converter.
     */
    public static final DoubleConverter INSTANCE = new DoubleConverter();

    @Nonnull
    @Override
    public Class<Double> getType()
    {
        return Double.class;
    }

    @Nonnull
    @Override
    public ConvertedObject<String> toDomainNonNull( @Nonnull final Double object )
    {
        return ErpDecimalConverter.INSTANCE.toDomainNonNull(new ErpDecimal(object));
    }

    @SuppressWarnings( "PMD.SignatureDeclareThrowsException" )
    @Nonnull
    @Override
    public ConvertedObject<Double> fromDomainNonNull( @Nonnull final String domainObject )
        throws Exception
    {
        return fromInputToDomainToPrimitive(ErpDecimalConverter.INSTANCE, domainObject, ErpDecimal::doubleValue);
    }
}
