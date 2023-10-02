/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client.request;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

/**
 * Fluent API class to build and hold function parameters.
 */
public class ODataFunctionParameters extends AbstractODataParameters
{
    private final ParameterFormat parameterFormat;

    /**
     * Create a new, empty set of parameters for an OData function.
     *
     * @param protocol
     *            The {@link ODataProtocol} version the parameters should conform to.
     */
    public ODataFunctionParameters( @Nonnull final ODataProtocol protocol )
    {
        super(protocol);
        parameterFormat = protocol.isEqualTo(ODataProtocol.V2) ? ParameterFormat.QUERY : ParameterFormat.PATH;
    }

    /**
     * Create an instance of {@link ODataFunctionParameters} from a set of parameters.
     *
     * @param parameters
     *            Key-value pairs for parameter values.
     * @param protocol
     *            The {@link ODataProtocol} version these parameters should conform to.
     * @return A new instance of {@link ODataFunctionParameters}.
     *
     * @throws IllegalArgumentException
     *             When the map contains a primitive type that is not supported.
     *
     * @see #addParameter(String, Object)
     */
    @Nonnull
    public static
        ODataFunctionParameters
        of( @Nonnull final Map<String, Object> parameters, @Nonnull final ODataProtocol protocol )
    {
        final ODataFunctionParameters functionParameters = new ODataFunctionParameters(protocol);
        functionParameters.addParameterSetInternal(parameters);

        return functionParameters;
    }

    /**
     * Convenience method to create an empty set of function parameters.
     *
     * @param protocol
     *            The OData protocol version that the parameters should conform to.
     *
     * @return A new instance of {@link ODataFunctionParameters}
     */
    @Nonnull
    public static ODataFunctionParameters empty( @Nonnull final ODataProtocol protocol )
    {
        return new ODataFunctionParameters(protocol);
    }

    /**
     * Add a parameter to function parameters.
     *
     * @param parameterName
     *            Name of the property (derived from the EDMX)
     * @param value
     *            Property value, assumed to be a primitive.
     * @param <PrimitiveT>
     *            Type of the primitive value.
     * @return The modified instance.
     *
     * @throws IllegalArgumentException
     *             When a parameter by that idenfitier already exists or primitive type is not supported.
     */
    @Nonnull
    public <PrimitiveT> ODataFunctionParameters addParameter(
        @Nonnull final String parameterName,
        @Nullable final PrimitiveT value )
    {
        addParameterInternal(parameterName, value);
        return this;
    }

    /**
     * Serializes all parameters into an <strong>encoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>One or more parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @return Encoded URL string representation of the parameters.
     */
    @Nonnull
    @Override
    public String toEncodedString()
    {
        return super.toStringInternal(UriEncodingStrategy.REGULAR, parameterFormat);
    }

    /**
     * Serializes all parameters into an <strong>encoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>One or more parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @return Encoded URL string representation of the parameters.
     */
    @Nonnull
    @Override
    public String toEncodedString( @Nonnull final UriEncodingStrategy strategy )
    {
        return super.toStringInternal(strategy, parameterFormat);
    }

    /**
     * Serializes all parameters into an <strong>unencoded</strong> URL path segment. The format is as follows:
     * <p>
     * <ul>
     * <li>An empty set of parameters will yield {@code ()}</li>
     * <li>One or more parameters will yield {@code (key1=val,key2=val)}</li>
     * </ul>
     * </p>
     *
     * @return String representation of the parameters.
     */
    @Nonnull
    @Override
    public String toString()
    {
        return super.toStringInternal(UriEncodingStrategy.NONE, parameterFormat);
    }
}
