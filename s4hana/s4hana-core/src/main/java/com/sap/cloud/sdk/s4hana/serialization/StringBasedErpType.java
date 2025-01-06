/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.serialization;

import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Base class for string-based ERP types.
 *
 * @param <T>
 *            The type implementing this abstract class.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( of = "value" )
@Deprecated
public abstract class StringBasedErpType<T extends StringBasedErpType<T>>
    implements
    ErpType<T>,
    Comparable<T>,
    StringRepresentableKey
{
    /**
     * The strategy to be used to fill the String representation of a {@code StringBasedErpType} to the length specified
     * by {@link #getMaxLength()}.
     */
    public enum FillCharStrategy
    {
        /** Leaves the String unmodified. */
        DO_NOTHING,

        /** Fills the String until the specified length ist reached. */
        FILL_LEADING,

        /**
         * If the Strings contains only numeric values, fill the string until the specified length is reached, otherwise
         * leave the String unmodified.
         */
        FILL_LEADING_IF_NUMERIC,

        /** Removes all occurrences of the specified character from the start of the String. */
        STRIP_LEADING
    }

    /**
     * The casing of the contained value to be used by a {@code StringBasedErpType}.
     */
    public enum CharCasing
    {
        /** Does not change the casing of the given String. */
        DO_NOTHING {
            @Override
            String changeCasing( final String toChange, final Locale locale )
            {
                return toChange;
            }
        },
        /** Changes all letters of the given String to upper-case, depending on the given {@code Locale}. */
        UPPER_CASE {
            @Override
            String changeCasing( final String toChange, final Locale locale )
            {
                return toChange.toUpperCase(locale);
            }
        },
        /** Changes all letters of the given String to lower-case, depending on the given {@code Locale}. */
        LOWER_CASE {
            @Override
            String changeCasing( final String toChange, final Locale locale )
            {
                return toChange.toLowerCase(locale);
            }
        };

        abstract String changeCasing( final String toChange, final Locale locale );
    }

    private static final long serialVersionUID = -1;

    @Getter
    @Expose
    @Nonnull
    private final String value;

    /**
     * Constructs a new string-based type.
     *
     * @param value
     *            The value of the type (not {@code null}). The given string is trimmed. An empty or trimmed empty value
     *            results in an empty instance of the string-based type.
     *
     * @throws IllegalArgumentException
     *             If the given value cannot be converted to an instance of this type, or the given parameter is null.
     */
    @JsonCreator
    public StringBasedErpType( @Nullable final String value ) throws IllegalArgumentException
    {
        this(value, CharCasing.DO_NOTHING);
    }

    /**
     * Constructs a new string-based type with a certain character casing and the english locale.
     *
     * @param value
     *            The value of the type (not {@code null}). The given string is trimmed. An empty or trimmed empty value
     *            results in an empty instance of the string-based type.
     * @param charCasing
     *            Defines the casing of characters within the string.
     *
     * @throws IllegalArgumentException
     *             If the given value cannot be converted to an instance of this type, or any of the given parameter is
     *             {@code null}.
     */
    public StringBasedErpType( @Nullable final String value, @Nullable final CharCasing charCasing )
        throws IllegalArgumentException
    {
        this(value, charCasing, Locale.ENGLISH);
    }

    /**
     * Constructs a new string-based type with a certain character casing and the given locale.
     *
     * @param value
     *            The value of the type (not {@code null}). The given string is trimmed. An empty or trimmed empty value
     *            results in an empty instance of the string-based type.
     * @param charCasing
     *            Defines the casing of characters within the string.
     * @param locale
     *            The locale to be used to change the casing of {@code value}.
     * @throws IllegalArgumentException
     *             If the given value cannot be converted to an instance of this type, or any of the given parameter is
     *             {@code null}.
     */
    @SuppressWarnings( "this-escape" )
    public StringBasedErpType(
        @Nullable final String value,
        @Nullable final CharCasing charCasing,
        @Nullable final Locale locale )
        throws IllegalArgumentException
    {
        if( value == null ) {
            throw new IllegalArgumentException(
                "The value for an instance of " + getClass().getSimpleName() + " must not be null.");
        }

        if( charCasing == null ) {
            throw new IllegalArgumentException("The given CharCasing must not be null.");
        }

        if( locale == null ) {
            throw new IllegalArgumentException("The given Locale must not be null.");
        }

        if( value.length() > getMaxLength() ) {
            throw new IllegalArgumentException(
                "An instance of "
                    + getClass().getName()
                    + " must have at most "
                    + getMaxLength()
                    + " characters. The given value is too long: "
                    + value
                    + ".");
        }

        final String trimmed = value.trim();
        final String filled = applyFillCharStrategy(trimmed, getMaxLength(), getFillCharStrategy(), getFillChar());

        if( trimmed.isEmpty() || filled.isEmpty() ) {
            this.value = "";
        } else {
            this.value = charCasing.changeCasing(filled, locale);
        }
    }

    /**
     * Returns the class of the string-based ERP type.
     *
     * @return The class of the string-based ERP type.
     */
    @Nonnull
    public abstract Class<T> getType();

    /**
     * Returns the maximum length of the string-based ERP type.
     *
     * @return The maximum length of the string-based ERP type.
     */
    public abstract int getMaxLength();

    /**
     * Returns the strategy of how the type should handle fill characters.
     *
     * @return The strategy of how the type should handle fill characters.
     */
    @Nonnull
    public abstract FillCharStrategy getFillCharStrategy();

    /**
     * Returns the character that is used to add or strip fill characters.
     *
     * @return The character that is used to add or strip fill characters.
     */
    public char getFillChar()
    {
        return '0';
    }

    /**
     * Returns whether this string-based ERP type is empty.
     *
     * @return {@code true} if the contained string value is empty, {@code false} otherwise.
     */
    public final boolean isEmpty()
    {
        return Strings.isNullOrEmpty(value);
    }

    /**
     * Returns whether this instance represents the default value.
     *
     * @return {@code true} if this instance represents the default {@link SapClient#DEFAULT}, {@code false} otherwise.
     */
    public final boolean isDefault()
    {
        return SapClient.DEFAULT.equals(this);
    }

    @JsonValue
    @Nonnull
    @Override
    public final String toString()
    {
        return value;
    }

    /**
     * Returns this value, filled according to the given {@code FillCharStrategy} with the give fillChar.
     *
     * @param strategy
     *            The strategy to be used to fill this value.
     * @param fillChar
     *            The character to be used to fill this value.
     * @return The value according to the given fill character strategy.
     */
    @Nonnull
    public final String toString( @Nonnull final FillCharStrategy strategy, final char fillChar )
    {
        return applyFillCharStrategy(value, getMaxLength(), strategy, fillChar);
    }

    @Override
    public int compareTo( @Nonnull final T other )
    {
        return value.compareTo(other.getValue());
    }

    @Nonnull
    @Override
    public String getKeyAsString()
    {
        return value;
    }

    private static boolean isDigits( final String str )
    {
        if( Strings.isNullOrEmpty(str) ) {
            return false;
        }
        for( int i = 0; i < str.length(); ++i ) {
            if( !Character.isDigit(str.charAt(i)) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the given string according to the given fill character strategy.
     */
    private static String applyFillCharStrategy(
        final String str,
        final int maxLength,
        final FillCharStrategy strategy,
        final char fillChar )
    {
        switch( strategy ) {
            case FILL_LEADING:
                return Strings.padStart(str, maxLength, fillChar);
            case FILL_LEADING_IF_NUMERIC:
                return isDigits(str) ? Strings.padStart(str, maxLength, fillChar) : str;
            case STRIP_LEADING:
                return CharMatcher.is(fillChar).trimLeadingFrom(str);
            case DO_NOTHING:
            default:
                return str;
        }
    }

    /**
     * Creates a function which transforms a given {@code StringBasedErpType} to its String representation. This
     * function handles null gracefully and returns null itself.
     *
     * @param converter
     *            The converter that should transform the concrete implementation of {@code StringBasedErpType} to
     *            String.
     * @param <T>
     *            The type of the subclass to transform.
     * @return A null-safe function transforming a {@code StringBasedErpType} to String.
     */
    @Nonnull
    public static <T extends StringBasedErpType<T>> Function<T, String> transformToString(
        @Nonnull final ErpTypeConverter<T> converter )
    {
        return new Function<>()
        {
            @Nullable
            @Override
            public String apply( @Nullable final T input )
            {
                if( input == null ) {
                    return null;
                } else {
                    return converter.toDomain(input).orNull();
                }
            }
        };
    }

    /**
     * Creates a function which transforms a given String to its {@code StringBasedErpType} representation. This
     * function handles null gracefully and returns null itself.
     *
     * @param converter
     *            The converter that should transform the String representation to the concrete
     *            {@code StringBasedErpType} implementation.
     * @param <T>
     *            The type of the subclass to create.
     * @return A null-safe function transforming a String to a {@code StringBasedErpType}.
     */
    @Nonnull
    public static <T extends StringBasedErpType<T>> Function<String, T> transformToType(
        @Nonnull final ErpTypeConverter<T> converter )
    {
        return new Function<>()
        {
            @Nullable
            @Override
            public T apply( @Nullable final String input )
            {
                if( input == null ) {
                    return null;
                } else {
                    return converter.fromDomain(input).orNull();
                }
            }
        };
    }
}
