/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing a request calling a remote-enabled function module (RFC module or RFM) in an ERP system.
 * <p>
 * Use the class {@link BapiRequest} to call BAPIs in an ERP system.
 * <p>
 * The signature of an RFM consists of importing, exporting, changing, and tables parameters.
 * <p>
 * From the perspective of the RFM caller, input data is considered as exporting, output data is considered as
 * importing, tables, and changing parameters can be used for both directions. The methods of this class are named
 * following the caller's perspective.
 * <p>
 * From the perspective of the RFM, importing means input data, exporting means output data, changing, and tables
 * parameters can be used for both directions.
 * <p>
 * Example: <br>
 * Calling a RFM with one importing parameter (from RFM perspective) requires to utilize the method
 * {@link #withExporting(String, String)}. Consider all existing variants of this method depending on the Java data type
 * of the parameter, e.g. use {@link #withExporting(String, String, String)} to pass a String object.
 * <p>
 * The data type (i.e. the data dictionary object) of importing and exporting parameters (regardless of the perspective)
 * can either be a data element, a structure, or a table type.
 * <ul>
 * <li>Use {@link #withExporting(String, String)} and its data type dependent variants to supply an exporting RFM
 * parameter reflected by a data element.</li>
 * <li>Use {@link #withExportingFields(String, String, Fields)} to supply an exporting RFM parameter reflected by a
 * structure.</li>
 * <li>Use {@link #withExportingTable(String, String)} to supply an exporting RFM parameter reflected by a table
 * type.</li>
 * </ul>
 * <p>
 * After calling {@link #execute(Destination)} use the class {@link RfmRequestResult} to access the results of the RFM
 * call (e.g. the exporting parameters from the RFM perspective respectively the importing parameters from the caller's
 * perspective).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
@Deprecated
public class RfmRequest extends AbstractRemoteFunctionRequest<RfmRequest, RfmRequestResult>
{
    private final Set<String> exceptionNames = Sets.newHashSet();

    @Setter( AccessLevel.PACKAGE )
    private RfmTransactionFactory transactionFactory = new RfmTransactionFactory();

    @Nonnull
    @Override
    public RfmRequestResult execute( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            DestinationNotFoundException,
            DestinationAccessException
    {
        return new RemoteFunctionRequestExecutor<>(transactionFactory).execute(destination, this);
    }

    @Nonnull
    @Override
    protected RfmRequest getThis()
    {
        return this;
    }

    /**
     * Constructs a synchronous remote function call request for which the result will be committed.
     *
     * @param functionName
     *            The name of the function to be called.
     *
     * @throws IllegalArgumentException
     *             If the given function is a BAPI, i.e., its name starts with the prefix "BAPI".
     */
    public RfmRequest( @Nonnull final String functionName ) throws IllegalArgumentException
    {
        super(functionName, CommitStrategy.COMMIT_SYNC, Thread.currentThread().getStackTrace()[3].toString());
        assertFunctionIsNotBapi(functionName);
    }

    /**
     * Constructs a remote function call request.
     *
     * @param functionName
     *            The name of the function to be called.
     * @param commit
     *            Decides whether to commit the result of the function call. If the commit parameter is true, the
     *            transaction is executed synchronously by default. The transaction can also be executed asynchronously
     *            by using the constructor {@link #RfmRequest(String, CommitStrategy)}.
     *
     * @throws IllegalArgumentException
     *             If the given function is a BAPI, i.e., its name starts with the prefix "BAPI".
     */
    public RfmRequest( @Nonnull final String functionName, final boolean commit ) throws IllegalArgumentException
    {
        super(
            functionName,
            commit ? CommitStrategy.COMMIT_SYNC : CommitStrategy.NO_COMMIT,
            Thread.currentThread().getStackTrace()[3].toString());
        assertFunctionIsNotBapi(functionName);
    }

    /**
     * Constructs a remote function call request.
     *
     * @param functionName
     *            The name of the function to be called.
     * @param commitStrategy
     *            Decides on the {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.CommitStrategy}
     *
     * @throws IllegalArgumentException
     *             If the given function is a BAPI, i.e., its name starts with the prefix "BAPI". Or if the request
     *             commit strategy is not supported.
     */
    public RfmRequest( @Nonnull final String functionName, @Nonnull final CommitStrategy commitStrategy )
        throws IllegalArgumentException
    {
        super(functionName, commitStrategy, Thread.currentThread().getStackTrace()[3].toString());
        assertFunctionIsNotBapi(functionName);
    }

    private void assertFunctionIsNotBapi( @Nonnull final String functionName )
        throws IllegalArgumentException
    {
        if( functionName.toLowerCase(Locale.ENGLISH).startsWith("bapi") ) {
            throw new IllegalArgumentException(
                "The given function '"
                    + functionName
                    + "' refers to a BAPI. For calling BAPIs, use "
                    + BapiRequest.class.getSimpleName()
                    + " instead.");
        }
    }

    @Nonnull
    @Override
    public RfmRequest withTypeConverters(
        @Nonnull final Iterable<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        return (RfmRequest) super.withTypeConverters(typeConverters);
    }

    @Nonnull
    @Override
    public RfmRequest withTypeConverters(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>... typeConverters )
    {
        return (RfmRequest) super.withTypeConverters(typeConverters);
    }

    @Nonnull
    @Override
    public RfmRequest withExporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (RfmRequest) super.withExporting(name, dataType);
    }

    @Nonnull
    @Override
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public <T> RfmRequest withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return (RfmRequest) super.withExporting(name, dataType, value, typeConverter);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final byte[] value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public RfmRequest withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return (RfmRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public ParameterFields<RfmRequest> withExportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withExportingFields(name, dataType);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withExportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return (RfmRequest) super.withExportingFields(name, dataType, fields);
    }

    @Nonnull
    @Override
    public Table<RfmRequest> withExportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withExportingTable(name, dataType);
    }

    @Nonnull
    @Override
    public RfmRequest withImporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (RfmRequest) super.withImporting(name, dataType);
    }

    @Nonnull
    @Override
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public <T> RfmRequest withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return (RfmRequest) super.withImporting(name, dataType, value, typeConverter);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public RfmRequest withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return (RfmRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public ParameterFields<RfmRequest> withImportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withImportingFields(name, dataType);
    }

    @Nonnull
    @Override
    public
        RfmRequest
        withImportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return (RfmRequest) super.withImportingFields(name, dataType, fields);
    }

    @Nonnull
    @Override
    public Table<RfmRequest> withImportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withImportingTable(name, dataType);
    }

    @Nonnull
    @Override
    public RfmRequest withImportingAsReturn( @Nonnull final String dataType )
    {
        return (RfmRequest) super.withImportingAsReturn(dataType);
    }

    @Nonnull
    @Override
    public RfmRequest withImportingAsReturn( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (RfmRequest) super.withImportingAsReturn(name, dataType);
    }

    @Nonnull
    @Override
    public Table<RfmRequest> withTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withTable(name, dataType);
    }

    @Nonnull
    @Override
    public RfmRequest withTableAsReturn( @Nonnull final String dataType )
    {
        return (RfmRequest) super.withTableAsReturn(dataType);
    }

    @Nonnull
    @Override
    public RfmRequest withTableAsReturn( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (RfmRequest) super.withTableAsReturn(name, dataType);
    }

    /**
     * Adds a changing parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     */
    @Nonnull
    public RfmRequest withChanging( @Nonnull final String name, @Nonnull final String dataType )
    {
        addParameter(new Parameter(ParameterKind.CHANGING, Value.ofField(name, null, null)));
        return this;
    }

    /**
     * Adds a changing parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The value of the parameter.
     * @param <T>
     *            The type of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     */
    @Nonnull
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        addParameter(new Parameter(ParameterKind.CHANGING, Value.ofField(name, value, null)));
        return this;
    }

    /**
     * Adds a changing parameter.
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
     *            The type of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     */
    @Nonnull
    public <T> RfmRequest withChanging(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nonnull final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        addParameter(new Parameter(ParameterKind.CHANGING, Value.ofField(name, value, typeConverter)));
        return this;
    }

    /**
     * Adds a changing {@code Boolean} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Boolean} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Boolean value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Byte} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Byte} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Byte value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ByteConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Character} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Character} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Character value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.CharacterConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code String} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code String} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final String value )
    {
        return withChanging(name, dataType, value, null);
    }

    /**
     * Adds a changing {@code Short} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Short} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Short value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.ShortConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Integer} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Integer} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Integer value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.IntegerConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Long} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Long} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Long value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LongConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Float} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Float} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Float value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.FloatConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Double} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Double} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Double value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.DoubleConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code BigInteger} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code BigInteger} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final BigInteger value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigIntegerConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code BigDecimal} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code BigDecimal} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final BigDecimal value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.BigDecimalConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Locale} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Locale} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Locale value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocaleConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code Year} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code Year} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public RfmRequest withChanging(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nonnull final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.YearConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code LocalDate} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code LocalDate} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final LocalDate value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE);
    }

    /**
     * Adds a changing {@code LocalTime} parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param value
     *            The {@code LocalTime} value of the parameter.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     *
     * @see #withChanging(String, String, Object, com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter)
     */
    @Nonnull
    public
        RfmRequest
        withChanging( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final LocalTime value )
    {
        return withChanging(name, dataType, value, com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter.INSTANCE);
    }

    /**
     * Adds a changing fields parameter for multiple values.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     *
     * @return A new {@code ParameterFields} object, collecting multiple values. Calling the {@code end()} method on it
     *         will return this {@code RfmRequest}.
     */
    @Nonnull
    public ParameterFields<RfmRequest> withChangingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<Value<?>> values = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.CHANGING, Value.ofStructure(name, values)));
        return new ParameterFields<>(getThis(), values);
    }

    /**
     * Adds a changing fields parameter for multiple values.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     * @param fields
     *            The changing fields.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     */
    @Nonnull
    public
        RfmRequest
        withChangingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return withChangingFields(name, dataType).fields(fields).end();
    }

    /**
     * Adds a changing table parameter.
     *
     * @param name
     *            The parameter name.
     * @param dataType
     *            The ABAP data type of the parameter.
     *
     * @return A new {@code Table} object, collecting multiple values. Calling the {@code end()} method on it will
     *         return this {@code RfmRequest}.
     */
    @Nonnull
    public Table<RfmRequest> withChangingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        final List<List<Value<?>>> cells = new ArrayList<>();
        addParameter(new Parameter(ParameterKind.CHANGING, Value.ofTable(name, cells)));
        return new Table<>(getThis(), cells);
    }

    /**
     * Declares an expected exception from calling a remote function module.
     *
     * @param name
     *            The name of the expected exception.
     *
     * @return This {@code RfmRequest} to facilitate a fluent interface.
     */
    @Nonnull
    public RfmRequest withException( @Nonnull final String name )
    {
        exceptionNames.add(name);
        return this;
    }

    /**
     * Specifies to invoke the provided {@link RemoteFunctionRequestErrorHandler} after the request has been executed.
     *
     * @param remoteFunctionRequestErrorHandler
     *            The result handler to invoke after request execution
     *
     */
    @Nonnull
    @Override
    public RfmRequest withErrorHandler(
        @Nonnull final RemoteFunctionRequestErrorHandler remoteFunctionRequestErrorHandler )
    {
        return (RfmRequest) super.withErrorHandler(remoteFunctionRequestErrorHandler);
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which does <b>not</b>
     * inspect the {@link RfmRequestResult} and, therefore, does <b>not</b> throw a
     * {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException}.
     */
    @Nonnull
    @Override
    public RfmRequest ignoringErrors()
    {
        return (RfmRequest) super.ignoringErrors();
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which inspects the
     * {@link RfmRequestResult} and throws a
     * {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException} or one of its more-specific
     * exceptions depending on the returned error messages of the request.
     */
    @Nonnull
    @Override
    public RfmRequest propagatingErrorsAsExceptions()
    {
        return (RfmRequest) super.propagatingErrorsAsExceptions();
    }
}
