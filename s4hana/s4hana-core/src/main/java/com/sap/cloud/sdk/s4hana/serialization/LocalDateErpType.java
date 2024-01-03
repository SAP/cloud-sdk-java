/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.typeconverter.AbstractTypeConverter;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for local date ERP types.
 *
 * @param <T>
 *            The generic sub class type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@EqualsAndHashCode( of = "value" )
@ToString
@Slf4j
@Deprecated
public abstract class LocalDateErpType<T extends LocalDateErpType<T>> implements ErpType<T>
{
    private static final long serialVersionUID = 1292571065102886266L;

    @Getter
    @Expose
    @Nullable
    private final LocalDate value;

    /**
     * Constructor.
     *
     * @param year
     *            The year.
     * @param month
     *            The month.
     * @param day
     *            The day.
     */
    public LocalDateErpType( final int year, final int month, final int day )
    {
        this(LocalDate.of(year, month, day));
    }

    /**
     * Get the sub class type.
     *
     * @return The sub class type.
     */
    @Nonnull
    protected abstract Class<T> getType();

    @Nonnull
    @Override
    public ErpTypeConverter<T> getTypeConverter()
    {
        return new LocalDateErpTypeConverter<T>(this);
    }

    @RequiredArgsConstructor
    private static class LocalDateErpTypeConverter<T extends LocalDateErpType<T>>
        extends
        AbstractTypeConverter<T, String>
        implements
        ErpTypeConverter<T>
    {
        private final LocalDateErpType<T> obj;
        private final LocalDateConverter delegate = new LocalDateConverter(null);

        @Nonnull
        @Override
        public Class<T> getType()
        {
            return obj.getType();
        }

        @Nonnull
        @Override
        public Class<String> getDomainType()
        {
            return String.class;
        }

        @Nonnull
        @Override
        public ConvertedObject<String> toDomainNonNull( @Nonnull final T object )
        {
            return object.getValue() == null ? ConvertedObject.ofNull() : delegate.toDomain(object.getValue());
        }

        @Nonnull
        @Override
        public ConvertedObject<T> fromDomainNonNull( @Nonnull final String domainObject )
        {
            if( Strings.isNullOrEmpty(domainObject) ) {
                return ConvertedObject.ofNull();
            }

            try {
                final LocalDate localDate = delegate.fromDomainNonNull(domainObject.trim()).get();
                return ConvertedObject.of(getType().getConstructor(LocalDate.class).newInstance(localDate));
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
                            getType().getSimpleName(),
                            LocalDate.class.getSimpleName()),
                    e);
            }
            catch( final InvocationTargetException e ) {
                log.error("Failed to convert ERP object to " + obj.getType().getName() + ": " + domainObject + ".");
                return ConvertedObject.ofNotConvertible();
            }
        }
    }
}
