/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.extern.slf4j.Slf4j;

/**
 * Used for serialization and deserialization of ERP-based types.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@Slf4j
@Deprecated
public class ErpTypeSerializer
{
    private final Map<Class<?>, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters =
        Maps.newIdentityHashMap();

    /**
     * Initializes the {@link ErpTypeSerializer} with default ERP type converters.
     */
    @SuppressWarnings( "this-escape" )
    public ErpTypeSerializer()
    {
        withTypeConverters(
            com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.ErpDecimalConverter.INSTANCE,
            com.sap.cloud.sdk.s4hana.serialization.ErpBooleanConverter.INSTANCE);
    }

    /**
     * Registers the given {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter}s. Replaces existing
     * converters for already existing types that have been added before.
     *
     * @param typeConverters
     *            The ERP type converters to be added.
     * @return The same instance.
     */
    @Nonnull
    public ErpTypeSerializer withTypeConverters(
        @Nonnull final Iterable<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        for( final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?> typeConverter : typeConverters ) {
            this.typeConverters.put(typeConverter.getType(), typeConverter);
        }

        return this;
    }

    /**
     * Delegates to {@link #withTypeConverters(Iterable)}.
     *
     * @param typeConverters
     *            The ERP type converters to be added.
     * @return The same instance.
     */
    @Nonnull
    public ErpTypeSerializer withTypeConverters(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>... typeConverters )
    {
        return withTypeConverters(Arrays.asList(typeConverters));
    }

    /**
     * Gets registered {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter}s.
     *
     * @return All registered ERP type converters.
     */
    @Nonnull
    public Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> getTypeConverters()
    {
        return typeConverters.values();
    }

    /**
     * Gets registered {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter}s for each
     * {@link com.sap.cloud.sdk.s4hana.serialization.ErpType}.
     *
     * @return The converters for each ERP type.
     */
    @Nonnull
    public Map<Class<?>, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> getTypeConvertersByType()
    {
        return typeConverters;
    }

    @SuppressWarnings( "unchecked" )
    @Nullable
    private <T> com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> getTypeConverter( final Class<T> type )
    {
        return (com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T>) typeConverters.get(type);
    }

    /**
     * Convert given object to an ERP type using a registered
     * {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter}.
     * <p>
     * Example usage in the SDK {@link ErpTypeSerializer} test:
     *
     * <pre>
     * {@code}
     * final ErpTypeSerializer serializer = new ErpTypeSerializer();
     * assertThat(serializer.toErp(new CostCenter("123")).get()).isEqualTo("0000000123");
     * assertThat(serializer.toErp(-123.4d).get()).isEqualTo("123.4-");
     * </pre>
     *
     * @param object
     *            The ERP object to serialize.
     * @param <T>
     *            The generic type.
     * @return A wrapped instance of the serialized object.
     */
    @Nonnull
    public <T> ConvertedObject<String> toErp( @Nullable final T object )
    {
        if( object == null ) {
            return ConvertedObject.ofNull();
        }

        final ConvertedObject<String> erpObject;

        if( object instanceof com.sap.cloud.sdk.s4hana.serialization.ErpType<?> ) {
            @SuppressWarnings( "unchecked" )
            final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> converter =
                (com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T>) ((com.sap.cloud.sdk.s4hana.serialization.ErpType<?>) object)
                    .getTypeConverter();

            erpObject = converter.toDomain(object);
        } else {
            @SuppressWarnings( "unchecked" )
            final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> converter =
                (com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T>) getTypeConverter(object.getClass());

            if( converter != null ) {
                erpObject = converter.toDomain(object);
            } else {
                erpObject = ConvertedObject.of(object.toString());
            }
        }

        return erpObject;
    }

    /**
     * Convert a given String based erpObject in the ERP-based representation into an object of resultType. For the
     * conversion, uses a {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter} registered for resultType.
     * <p>
     * * Example usage in the SDK {@link ErpTypeSerializer} test:
     *
     * <pre>
     * {@code}
     * assertThat(serializer.fromErp("0000000123", CostCenter.class).get()).isEqualTo(new CostCenter("123"));
     * assertThat(serializer.fromErp("123.4 ", Double.class).get()).isEqualTo(123.4d);
     * assertThat(serializer.fromErp("123.4-", Double.class).get()).isEqualTo(-123.4d);
     * </pre>
     *
     * @param erpObject
     *            The serialized ERP object.
     * @param resultType
     *            The expected deserialization result type.
     * @param <T>
     *            The generic result type.
     * @return A wrapped instance of the deserialized object.
     */
    @Nonnull
    public <T> ConvertedObject<T> fromErp( @Nullable final String erpObject, @Nonnull final Class<T> resultType )
    {
        if( erpObject == null ) {
            return ConvertedObject.ofNull();
        }

        final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> converter = getTypeConverter(resultType);

        if( converter != null ) {
            return converter.fromDomain(erpObject);
        } else {
            try {
                final Constructor<T> stringConstructor = resultType.getConstructor(String.class);
                return ConvertedObject.of(stringConstructor.newInstance(erpObject));
            }
            catch( final
                NoSuchMethodException
                    | SecurityException
                    | IllegalAccessException
                    | InstantiationException e ) {
                throw new ShouldNotHappenException(
                    String
                        .format(
                            "Failed to instantiate object from %s: No constructor available with %s parameter.",
                            resultType.getSimpleName(),
                            String.class.getSimpleName()),
                    e);
            }
            catch( final InvocationTargetException e ) {
                if( log.isDebugEnabled() ) {
                    log.debug("Failed to convert ERP object to " + resultType.getName() + ": " + erpObject + ".");
                }
                return ConvertedObject.ofNotConvertible();
            }
        }
    }
}
