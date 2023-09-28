package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Deprecated
class JCoErpNoopConverter<T extends Number> implements com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T>
{
    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Getter
    private final Class<T> type;
    private final Function<String, T> deserializer;
    private final Function<T, String> serializer;

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Getter
    private final Class<String> domainType = String.class;

    @Nonnull
    static com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer overrideNumbers(
        @Nonnull final com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer erpTypeSerializer )
    {
        final DecimalFormat numberFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        numberFormat.setMaximumFractionDigits(340); // 340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

        return erpTypeSerializer
            .withTypeConverters(
                new JCoErpNoopConverter<>(BigInteger.class, BigInteger::new, numberFormat::format),
                new JCoErpNoopConverter<>(BigDecimal.class, BigDecimal::new, numberFormat::format),
                new JCoErpNoopConverter<>(Float.class, Float::new, numberFormat::format),
                new JCoErpNoopConverter<>(Integer.class, Integer::new, numberFormat::format),
                new JCoErpNoopConverter<>(Long.class, Long::new, numberFormat::format),
                new JCoErpNoopConverter<>(Double.class, Double::new, numberFormat::format));
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public ConvertedObject<String> toDomain( @Nullable final T object )
    {
        return object == null ? ConvertedObject.ofNull() : ConvertedObject.of(serializer.apply(object));
    }

    /**
     * @deprecated This module will be discontinued, along with its classes and methods.
     */
    @Deprecated
    @Nonnull
    @Override
    public ConvertedObject<T> fromDomain( @Nullable final String domainObject )
    {
        return domainObject == null ? ConvertedObject.ofNull() : ConvertedObject.of(deserializer.apply(domainObject));
    }
}
