/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.expression.Expressions;

import io.vavr.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract class to build parameter expressions for the URL path. Parameters can resemble an entity key or function
 * parameters.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
public abstract class AbstractODataParameters
{
    @Getter( AccessLevel.PACKAGE )
    @Nonnull
    private final Map<String, Expressions.OperandSingle> parameters = new LinkedHashMap<>();

    /**
     * The {@link ODataProtocol} these parameters should conform to.
     */
    @Getter
    @Nonnull
    private final ODataProtocol protocol;

    /**
     * Add a parameter.
     *
     * @param parameterName
     *            Name of the entity property or function parameters.
     * @param value
     *            Property value, assumed to be a primitive.
     * @param <PrimitiveT>
     *            Type of the primitive value.
     * @throws IllegalArgumentException
     *             When a parameter by that idenfitier already exists or primitive type is not supported.
     */
    <PrimitiveT> void addParameterInternal( @Nonnull final String parameterName, @Nullable final PrimitiveT value )
    {
        if( parameters.containsKey(parameterName) ) {
            throw new IllegalArgumentException(
                "Cannot add parameter \"" + parameterName + "\": A parameter by that name already exists.");
        }
        parameters.put(parameterName, Expressions.createOperand(value));
    }

    /**
     * Convenience method to add multpiple parameters at once.
     *
     * @throws IllegalArgumentException
     *             When one of the values is null.
     *
     * @see #addParameterInternal(String, Object)
     */
    void addParameterSetInternal( @Nonnull final Map<String, Object> properties )
    {
        properties.forEach(this::addParameterInternal);
    }

    /**
     * Serializes all parameters into an <strong>encoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>A single parameter entry will yield {@code (value)}</li>
     * <li>Multiple of parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @return Encoded URL string representation of the parameters.
     */
    @Nonnull
    public String toEncodedString()
    {
        return toStringInternal(UriEncodingStrategy.REGULAR, ParameterFormat.PATH_SHORT);
    }

    /**
     * Serializes all parameters into an <strong>encoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>A single parameter entry will yield {@code (value)}</li>
     * <li>Multiple of parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @param strategy
     *            The URI encoding strategy.
     * @return Encoded URL string representation of the parameters.
     */
    @Nonnull
    public String toEncodedString( @Nonnull final UriEncodingStrategy strategy )
    {
        return toStringInternal(strategy, ParameterFormat.PATH_SHORT);
    }

    /**
     * Serializes all parameters into an <strong>unencoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>A single parameter entry will yield {@code (value)}</li>
     * <li>Multiple of parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @return String representation of the parameters.
     */
    @Nonnull
    @Override
    public String toString()
    {
        return toStringInternal(UriEncodingStrategy.NONE, ParameterFormat.PATH_SHORT);
    }

    @Nonnull
    String toStringInternal( @Nonnull final UriEncodingStrategy strategy, final ParameterFormat format )
    {
        final Function<String, String> encoder =
            format.isQuery() ? strategy.getQueryPercentEscaper()::escape : strategy.getPathPercentEscaper()::escape;

        // case short format: single key value without field-name
        if( format == ParameterFormat.PATH_SHORT && parameters.size() == 1 ) {
            final Expressions.OperandSingle singleValue = parameters.values().iterator().next();
            String parameterValue = singleValue.getExpression(protocol);
            parameterValue = encoder.apply(parameterValue);
            parameterValue = String.format("(%s)", parameterValue);

            return parameterValue;
        }

        // case long format: compound key with field-name/value pair(s)
        final String keyDelimiter = format.isQuery() ? "&" : ",";
        String keys =
            parameters
                .entrySet()
                .stream()
                .map(param -> Tuple.of(param.getKey(), param.getValue()))
                .map(param -> param.map2(val -> val.getExpression(protocol)))
                .map(param -> param.map2(encoder))
                .map(param -> param.apply(( key, val ) -> String.format("%s=%s", key, val)))
                .collect(Collectors.joining(keyDelimiter));

        if( !format.isQuery() ) {
            keys = String.format("(%s)", keys);
        }
        return keys;
    }

    enum ParameterFormat
    {
        PATH(false, false),
        PATH_SHORT(false, true),
        QUERY(true, false);

        @Getter
        private final boolean isQuery;

        @Getter
        private final boolean isShort;

        ParameterFormat( final boolean isQuery, final boolean isShort )
        {
            this.isQuery = isQuery;
            this.isShort = isShort;
        }
    }
}
