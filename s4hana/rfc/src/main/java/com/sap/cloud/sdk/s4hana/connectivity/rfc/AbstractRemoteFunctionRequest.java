/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class representing a request calling a Business API (BAPI) in an ERP system.
 *
 * @param <RequestT>
 *            The generic request type.
 * @param <RequestResultT>
 *            The generic result type.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@ToString
@Deprecated
public abstract class AbstractRemoteFunctionRequest<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    extends
    com.sap.cloud.sdk.s4hana.connectivity.Request<RequestT, RequestResultT>
{
    private static final String RETURN_PARAMETER = "RETURN";

    /**
     * The name of the remote function to invoke.
     */
    protected final String functionName;

    /**
     * The strategy of commit handling in the remote system.
     *
     * @see CommitStrategy
     */
    protected final CommitStrategy commitStrategy;

    /**
     * The result handler that is invoked after the request execution.
     *
     * @see RemoteFunctionRequestErrorHandler
     */
    protected RemoteFunctionRequestErrorHandler remoteFunctionRequestErrorHandler;

    private final LinkedHashMap<String, Parameter> parametersByName = Maps.newLinkedHashMap();

    @Getter( AccessLevel.PACKAGE )
    private final LinkedHashSet<String> returnParameterNames = Sets.newLinkedHashSet();

    private final Map<Class<?>, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters =
        Maps.newIdentityHashMap();

    /**
     * Method name which lead to the construction.
     */
    @Getter
    @Nullable
    protected final String constructedByMethod;

    Iterable<Parameter> getParameters()
    {
        return parametersByName.values();
    }

    /**
     * Constructor for remote function request.
     *
     * @param functionName
     *            The function name.
     * @param commitStrategy
     *            The commit strategy.
     * @param constructedByMethod
     *            The name of the method from which this constructor is being called.
     */
    @SuppressWarnings( "this-escape" )
    protected AbstractRemoteFunctionRequest(
        @Nonnull final String functionName,
        @Nonnull final CommitStrategy commitStrategy,
        @Nullable final String constructedByMethod )
    {
        this.functionName = functionName;
        this.commitStrategy = commitStrategy;
        this.constructedByMethod = constructedByMethod;
        remoteFunctionRequestErrorHandler = new ExceptionPropagatingRemoteFunctionRequestErrorHandler();

        withTypeConverters(new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer().getTypeConverters());
    }

    /**
     * Convenience method that returns the current instance.
     *
     * @return The current instance.
     */
    @Nonnull
    protected abstract RequestT getThis();

    /**
     * Get the ERP type converters.
     *
     * @return The type converters.
     */
    @Nonnull
    public Collection<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> getTypeConverters()
    {
        return typeConverters.values();
    }

    /**
     * Registers the given {@link com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter}s. Replaces existing
     * converters for already existing types that have been added before.
     *
     * @param typeConverters
     *            The type converters to be added.
     * @return The same instance with additional type converters.
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withTypeConverters(
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
     *            The type converters to be added.
     * @return The same instance with additional type converters.
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withTypeConverters(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>... typeConverters )
    {
        return withTypeConverters(Arrays.asList(typeConverters));
    }

    /**
     * Get the function name.
     *
     * @return The name of the function to be executed.
     */
    @Nonnull
    public String getFunctionName()
    {
        return functionName;
    }

    /**
     * Get the commit strategy.
     *
     * @return The commit strategy with which the request is executed.
     */
    @Nonnull
    public CommitStrategy getCommitStrategy()
    {
        return commitStrategy;
    }

    /**
     * Returns the chosen {@link RemoteFunctionRequestErrorHandler}
     *
     * @return The request result handler that is being invoked after the request execution
     */
    RemoteFunctionRequestErrorHandler getRemoteFunctionRequestErrorHandler()
    {
        return remoteFunctionRequestErrorHandler;
    }

    /**
     * Get boolean indicator whether transactional commit is being performed.
     *
     * @return {@code true} if a transactional commit will be performed after invoking the function, {@code false}
     *         otherwise.
     */
    public boolean isPerformingTransactionalCommit()
    {
        return commitStrategy.isPerformingCommit();
    }

    void addParameter( @Nonnull final Parameter parameter )
    {
        parametersByName.put(parameter.getParameterValue().getName(), parameter);
    }

    /**
     * Adds an exporting parameter reflected by a data element.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        addParameter(new Parameter(ParameterKind.EXPORTING, Value.ofField(name, null, null)));
        return this;
    }

    /**
     * Adds an exporting parameter reflected by a data element.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The value of the parameter.
     * @param <T>
     *            The generic value type.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        addParameter(new Parameter(ParameterKind.EXPORTING, Value.ofField(name, value, null)));
        return this;
    }

    /**
     * Adds an exporting parameter reflected by a data element.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The value of the parameter.
     * @param typeConverter
     *            A type converter defining how to convert the type of the given value to its ERP representation.
     * @param <T>
     *            The generic value type.
     *
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public <T> AbstractRemoteFunctionRequest<RequestT, RequestResultT> withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        addParameter(new Parameter(ParameterKind.EXPORTING, Value.ofField(name, value, typeConverter)));
        return this;
    }

    /**
     * Adds an exporting {@code Boolean} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Byte} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Adds an exporting byte[] parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request, to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final byte[] value )
    {
        return withExporting(name, dataType, value, null);
    }

    /**
     * Adds an exporting {@code Character} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code String} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return withExporting(name, dataType, value, null);
    }

    /**
     * Adds an exporting {@code Short} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Integer} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Long} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Float} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Double} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code BigInteger} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return withExporting(
            name,
            dataType,
            value,
            com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code BigDecimal} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return withExporting(
            name,
            dataType,
            value,
            com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Locale} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code Year} parameter. reflected by a data element
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code LocalDate} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Adds an exporting {@code LocalTime} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withExporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return withExporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE);
    }

    /**
     * Adds an exporting parameter reflected by a structure.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public ParameterFields<RequestT> withExportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<Value<?>> values = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.EXPORTING, Value.ofStructure(name, values)));

        return new ParameterFields<>(getThis(), values);
    }

    /**
     * Adds an exporting parameter reflected by a structure.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param fields
     *            The exporting fields.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withExportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return withExportingFields(name, dataType).fields(fields).end();
    }

    /**
     * Adds an exporting parameter reflected by a table type.
     * <p>
     * Note: Exporting refers to the point of view of the caller. Thus, when a parameter is declared as importing on
     * ABAP side, you have to specify it as exporting here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The Table to allow for fluent formulation.
     */
    @Nonnull
    public Table<RequestT> withExportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<List<Value<?>>> cells = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.EXPORTING, Value.ofTable(name, cells)));

        return new Table<>(getThis(), cells);
    }

    /**
     * Adds an importing parameter reflected by a data element.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofField(name, null, null)));
        return this;
    }

    /**
     * Adds an importing parameter reflected by a data element.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The value of the parameter.
     * @param <T>
     *            The generic value type.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofField(name, value, null)));
        return this;
    }

    /**
     * Adds an importing parameter reflected by a data element.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The value of the parameter.
     * @param typeConverter
     *            A type converter defining how to convert the type of the given value to its ERP representation.
     * @param <T>
     *            The generic value type.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public <T> AbstractRemoteFunctionRequest<RequestT, RequestResultT> withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofField(name, value, typeConverter)));
        return this;
    }

    /**
     * Adds a importing {@code Boolean} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Byte} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Character} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code String} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return withImporting(name, dataType, value, null);
    }

    /**
     * Adds a importing {@code Short} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Integer} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Long} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Float} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Double} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code BigInteger} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return withImporting(
            name,
            dataType,
            value,
            com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code BigDecimal} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return withImporting(
            name,
            dataType,
            value,
            com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Locale} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code Year} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code LocalDate} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Adds a importing {@code LocalTime} parameter reflected by a data element.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The optional parameter value.
     * @see #withImporting(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return withImporting(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE);
    }

    /**
     * Adds an importing parameter reflected by a structure.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The ParameterFields to allow for fluent formulation.
     */
    @Nonnull
    public ParameterFields<RequestT> withImportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<Value<?>> values = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofStructure(name, values)));
        return new ParameterFields<>(getThis(), values);
    }

    /**
     * Adds an importing parameter reflected by a structure.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param fields
     *            The importing fields.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return withImportingFields(name, dataType).fields(fields).end();
    }

    /**
     * Adds an importing parameter reflected by a table type.
     * <p>
     * Note that importing is related to the point of view of the caller. Thus, when a parameter is declared as
     * exporting on ABAP side, you have to specify it as importing here.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The table to allow for fluent formulation.
     */
    @Nonnull
    public Table<RequestT> withImportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<List<Value<?>>> cells = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofTable(name, cells)));

        return new Table<>(getThis(), cells);
    }

    /**
     * Adds an importing parameter as a return parameter with the name defined by {@link #RETURN_PARAMETER}.
     * <p>
     * Note that return parameters refer to parameters for which return messages will be parsed and translated to
     * RemoteFunctionMessages.
     *
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withImportingAsReturn(
        @Nonnull final String dataType )
    {
        return withImportingAsReturn(RETURN_PARAMETER, dataType);
    }

    /**
     * Adds an importing parameter as a return parameter.
     * <p>
     * Note that return parameters refer to parameters for which return messages will be parsed and translated to
     * RemoteFunctionMessages.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withImportingAsReturn( @Nonnull final String name, @Nonnull final String dataType )
    {
        addParameter(new Parameter(ParameterKind.IMPORTING, Value.ofField(name, null, null)));
        returnParameterNames.add(name);
        return this;
    }

    /**
     * Adds a table parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return The Table to allow for fluent formulation.
     */
    @Nonnull
    public Table<RequestT> withTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<List<Value<?>>> cells = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.TABLES, Value.ofTable(name, cells)));

        return new Table<>(getThis(), cells);
    }

    /**
     * Adds a table parameter as a return parameter with the name defined by {@link #RETURN_PARAMETER}.
     * <p>
     * Note that return parameters refer to parameters for which return messages will be parsed and translated to
     * RemoteFunctionMessages.
     *
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withTableAsReturn( @Nonnull final String dataType )
    {
        return withTableAsReturn(RETURN_PARAMETER, dataType);
    }

    /**
     * Adds a table parameter as a return parameter.
     * <p>
     * Note that return parameters refer to parameters for which return messages will be parsed and translated to
     * RemoteFunctionMessages.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public
        AbstractRemoteFunctionRequest<RequestT, RequestResultT>
        withTableAsReturn( @Nonnull final String name, @Nonnull final String dataType )
    {
        addParameter(new Parameter(ParameterKind.TABLES, Value.ofTable(name, new ArrayList<>())));
        returnParameterNames.add(name);
        return this;
    }

    /**
     * Specifies to invoke the provided {@link RemoteFunctionRequestErrorHandler} after the request has been executed.
     *
     * @param remoteFunctionRequestErrorHandler
     *            The result handler to invoke after request execution
     * @return This request to allow for fluent formulation
     *
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> withErrorHandler(
        @Nonnull final RemoteFunctionRequestErrorHandler remoteFunctionRequestErrorHandler )
    {
        this.remoteFunctionRequestErrorHandler = remoteFunctionRequestErrorHandler;
        return this;
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which does <b>not</b>
     * inspect the request result and, therefore, does <b>not</b> throw a
     * {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException}.
     *
     * @return This request to allow for fluent formulation
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> ignoringErrors()
    {
        remoteFunctionRequestErrorHandler = new IgnoringErrorsRemoteFunctionRequestErrorHandler();
        return this;
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which inspects the
     * request result and throws a {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException}
     * or one of its more-specific exceptions depending on the returned error messages of the request.
     *
     * @return This request to allow for fluent formulation
     *
     */
    @Nonnull
    public AbstractRemoteFunctionRequest<RequestT, RequestResultT> propagatingErrorsAsExceptions()
    {
        remoteFunctionRequestErrorHandler = new ExceptionPropagatingRemoteFunctionRequestErrorHandler();
        return this;
    }
}
