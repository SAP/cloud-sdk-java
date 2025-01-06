/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
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
 * The base class for decimal based ERP types.
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
public abstract class DecimalBasedErpType<T extends DecimalBasedErpType<T>> implements ErpType<T>
{
    private static final long serialVersionUID = 5572827427883235410L;

    @Getter
    @Expose
    @Nullable
    private final BigDecimal value;

    /**
     * Sub class reference.
     *
     * @return The sub class.
     */
    @Nonnull
    protected abstract Class<T> getType();

    /**
     * Indicator for numeric sign; are values below zero allowed?
     *
     * @return The sign flag.
     */
    protected abstract boolean isSigned();

    /**
     * Indicator for decimals.
     *
     * @return The number of saved decimals.
     */
    protected abstract int getDecimals();

    /**
     * The maximum character length of the number.
     *
     * @return The maximum character length.
     */
    protected abstract int getMaxLength();

    /**
     * Constructor.
     *
     * @param str
     *            The String representation of the number.
     */
    @JsonCreator
    public DecimalBasedErpType( @Nullable final String str )
    {
        this(fromErpToJavaDecimal(str));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The Double value.
     */
    public DecimalBasedErpType( @Nonnull final Double value )
    {
        this(BigDecimal.valueOf(value));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The Float value.
     */
    public DecimalBasedErpType( @Nonnull final Float value )
    {
        this(BigDecimal.valueOf(value));
    }

    private static BigDecimal fromErpToJavaDecimal( @Nullable String str )
    {
        if( str == null || Strings.isNullOrEmpty(str) ) {
            return null;
        }
        final char signChar = str.charAt(str.length() - 1);
        if( signChar == '-' || signChar == ' ' ) {
            str = str.substring(0, str.length() - 1);
            final BigDecimal result = new BigDecimal(str);
            if( signChar == '-' ) {
                return result.negate();
            }
            return result;
        }
        return new BigDecimal(str);
    }

    @SuppressWarnings( "PMD.UselessParentheses" )
    private String toErpString()
    {
        if( value == null ) {
            return isSigned() ? "" : " ";
        }
        final String sign = isSigned() ? (value.signum() < 0 ? "-" : " ") : "";
        return value.abs().setScale(getDecimals(), RoundingMode.UNNECESSARY).toPlainString() + sign;
    }

    @Nonnull
    @Override
    public ErpTypeConverter<T> getTypeConverter()
    {
        return new DecimalBasedErpTypeConverter<>(this);
    }

    @RequiredArgsConstructor
    private static class DecimalBasedErpTypeConverter<T extends DecimalBasedErpType<T>>
        extends
        AbstractTypeConverter<T, String>
        implements
        ErpTypeConverter<T>
    {
        private final DecimalBasedErpType<T> obj;

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
            return object.getValue() == null ? ConvertedObject.ofNull() : ConvertedObject.of(obj.toErpString());
        }

        @Nonnull
        @Override
        public ConvertedObject<T> fromDomainNonNull( @Nonnull final String domainObject )
        {
            try {
                return Strings.isNullOrEmpty(domainObject)
                    ? ConvertedObject.ofNull()
                    : ConvertedObject.of(getType().getConstructor(String.class).newInstance(domainObject));
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
                            String.class.getSimpleName()),
                    e);
            }
            catch( final InvocationTargetException e ) {
                log.error("Failed to convert ERP object to " + obj.getType().getName() + ": " + domainObject + ".");
                return ConvertedObject.ofNotConvertible();
            }
        }
    }
}
