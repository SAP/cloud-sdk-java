package com.sap.cloud.sdk.s4hana.serialization;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;

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
import lombok.extern.slf4j.Slf4j;

/**
 * Base class for an integer like ERP type.
 *
 * @param <T>
 *            The generic sub class type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor
@EqualsAndHashCode( of = "value" )
@Slf4j
@Deprecated
public abstract class IntegerBasedErpType<T extends IntegerBasedErpType<T>> implements ErpType<T>
{
    private static final long serialVersionUID = 6553092035796586330L;

    @Getter
    @Expose
    @Nullable
    private final BigInteger value;

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
    public IntegerBasedErpType( @Nullable final String str )
    {
        this(fromErpToJavaInteger(str));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The byte representation of the number.
     */
    public IntegerBasedErpType( final byte value )
    {
        this(BigInteger.valueOf(value));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The short representation of the number.
     */
    public IntegerBasedErpType( final short value )
    {
        this(BigInteger.valueOf(value));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The int representation of the number.
     */
    public IntegerBasedErpType( final int value )
    {
        this(BigInteger.valueOf((long) value));
    }

    /**
     * Constructor.
     *
     * @param value
     *            The long representation of the number.
     */
    public IntegerBasedErpType( final long value )
    {
        this(BigInteger.valueOf(value));
    }

    /**
     * Get the Byte value.
     *
     * @return The Byte value.
     */
    @Nullable
    public Byte byteValue()
    {
        return value != null ? value.byteValue() : null;
    }

    /**
     * Get the Short value.
     *
     * @return The Short value.
     */
    @Nullable
    public Short shortValue()
    {
        return value != null ? value.shortValue() : null;
    }

    /**
     * Get the Integer value.
     *
     * @return The Integer value.
     */
    @Nullable
    public Integer intValue()
    {
        return value != null ? value.intValue() : null;
    }

    /**
     * Get the Long value.
     *
     * @return The Long value.
     */
    @Nullable
    public Long longValue()
    {
        return value != null ? value.longValue() : null;
    }

    private static BigInteger fromErpToJavaInteger( @Nullable String str )
    {
        if( str == null || Strings.isNullOrEmpty(str) ) {
            return null;
        }
        final char signChar = str.charAt(str.length() - 1);
        if( signChar == '-' || signChar == ' ' ) {
            str = str.substring(0, str.length() - 1);
            return new BigInteger((signChar == '-' ? "-" : "") + str);
        }
        return new BigInteger(str);
    }

    @Nonnull
    @Override
    public String toString()
    {
        return toErpString();
    }

    private String toErpString()
    {
        if( value == null ) {
            return isSigned() ? "" : " ";
        }
        final String sign = isSigned() ? (value.signum() < 0 ? "-" : " ") : "";
        return value + sign;
    }

    @Nonnull
    @Override
    public ErpTypeConverter<T> getTypeConverter()
    {
        return new IntegerBasedErpTypeConverter<>(this);
    }

    @RequiredArgsConstructor
    private static class IntegerBasedErpTypeConverter<T extends IntegerBasedErpType<T>>
        extends
        AbstractTypeConverter<T, String>
        implements
        ErpTypeConverter<T>
    {
        private final IntegerBasedErpType<T> obj;

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
