package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Class representing a request calling a Business API (BAPI) in an ERP system.
 * <p>
 * Use the class {@link RfmRequest} to call remote-enabled function modules (RFC modules).
 * <p>
 * The signature of BAPIs consists of importing, exporting, and tables parameters.
 * <p>
 * From the perspective of the BAPI caller, input data is considered as exporting, output data is considered as
 * importing and tables parameters can be used for both directions. The methods of this class are named following the
 * caller's perspective.
 * <p>
 * From the perspective of the BAPI, importing means input data, exporting means output data, and tables parameters can
 * be used for both directions.
 * <p>
 * Example: <br>
 * Calling a BAPI with one importing parameter (from BAPI perspective) requires to utilize the method
 * {@link #withExporting(String, String)}. Consider all existing variants of this method depending on the Java data type
 * of the parameter, e.g. use {@link #withExporting(String, String, String)} to pass a String object.
 * <p>
 * The data type (i.e. the data dictionary object) of importing and exporting parameters (regardless of the perspective)
 * can either be a data element, a structure, or a table type.
 * <ul>
 * <li>Use {@link #withExporting(String, String)} and its data type dependent variants to supply an exporting BAPI
 * parameter reflected by a data element.</li>
 * <li>Use {@link #withExportingFields(String, String, Fields)} to supply an exporting BAPI parameter reflected by a
 * structure.</li>
 * <li>Use {@link #withExportingTable(String, String)} to supply an exporting BAPI parameter reflected by a table
 * type.</li>
 * </ul>
 * <p>
 * After calling {@link #execute(Destination)} use the class {@link BapiRequestResult} to access the results of the BAPI
 * call (e.g. the exporting parameters from the BAPI perspective respectively the importing parameters from the caller's
 * perspective).
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@EqualsAndHashCode( callSuper = true )
@ToString( callSuper = true )
@Data
@Deprecated
public class BapiRequest extends AbstractRemoteFunctionRequest<BapiRequest, BapiRequestResult>
{
    @Nonnull
    @Override
    public BapiRequestResult execute( @Nonnull final Destination destination )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException,
            com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationNotFoundException,
            com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException
    {
        return new RemoteFunctionRequestExecutor<>(new BapiTransactionFactory()).execute(destination, this);
    }

    @Nonnull
    @Override
    protected BapiRequest getThis()
    {
        return this;
    }

    /**
     * Constructs a synchronous BAPI request for which the result will be committed.
     *
     * @param functionName
     *            The name of the BAPI to be called.
     *
     * @throws IllegalArgumentException
     *             If the given function is not a valid BAPI, i.e., its name does not start with the prefix "BAPI".
     */
    public BapiRequest( @Nonnull final String functionName ) throws IllegalArgumentException
    {
        super(functionName, CommitStrategy.COMMIT_SYNC, Thread.currentThread().getStackTrace()[2].toString());

        assertFunctionIsBapi(functionName);
    }

    /**
     * Constructs a BAPI request.
     *
     * @param functionName
     *            The name of the BAPI to be called.
     * @param commit
     *            Decides whether to commit the result of the BAPI call. If the commit parameter is true, the
     *            transaction is executed synchronously by default. The transaction can also be executed asynchronously
     *            by using the constructor {@link #BapiRequest(String, CommitStrategy)}.
     *
     * @throws IllegalArgumentException
     *             If the given function is not a valid BAPI, i.e., its name does not start with the prefix "BAPI".
     */
    public BapiRequest( @Nonnull final String functionName, final boolean commit ) throws IllegalArgumentException
    {
        super(
            functionName,
            commit ? CommitStrategy.COMMIT_SYNC : CommitStrategy.NO_COMMIT,
            Thread.currentThread().getStackTrace()[2].toString());
        assertFunctionIsBapi(functionName);
    }

    /**
     * Constructs a BAPI request.
     *
     * @param functionName
     *            The name of the BAPI to be called.
     * @param commitStrategy
     *            Decides on the {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.CommitStrategy}
     *
     * @throws IllegalArgumentException
     *             If the given function is not a valid BAPI, i.e., its name does not start with the prefix "BAPI". Or
     *             if the request commit strategy is not supported.
     */
    public BapiRequest( @Nonnull final String functionName, @Nonnull final CommitStrategy commitStrategy )
        throws IllegalArgumentException
    {
        super(functionName, commitStrategy, Thread.currentThread().getStackTrace()[2].toString());
        assertFunctionIsBapi(functionName);
    }

    private void assertFunctionIsBapi( @Nonnull final String functionName )
        throws IllegalArgumentException
    {
        if( !functionName.toLowerCase(Locale.ENGLISH).startsWith("bapi") ) {
            throw new IllegalArgumentException(
                "The given function '"
                    + functionName
                    + "' is not a valid BAPI. For calling remote-enabled functions, use "
                    + RfmRequest.class.getSimpleName()
                    + " instead.");
        }
    }

    @Nonnull
    @Override
    public BapiRequest withTypeConverters(
        @Nonnull final Iterable<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters )
    {
        return (BapiRequest) super.withTypeConverters(typeConverters);
    }

    @Nonnull
    @Override
    public BapiRequest withTypeConverters(
        @Nonnull final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>... typeConverters )
    {
        return (BapiRequest) super.withTypeConverters(typeConverters);
    }

    @Nonnull
    @Override
    public BapiRequest withExporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (BapiRequest) super.withExporting(name, dataType);
    }

    @Nonnull
    @Override
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public <T> BapiRequest withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return (BapiRequest) super.withExporting(name, dataType, value, typeConverter);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public BapiRequest withExporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        ParameterFields<BapiRequest>
        withExportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withExportingFields(name, dataType);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return (BapiRequest) super.withExportingFields(name, dataType, fields);
    }

    @Nonnull
    @Override
    public Table<BapiRequest> withExportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withExportingTable(name, dataType);
    }

    @Nonnull
    @Override
    public BapiRequest withImporting( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (BapiRequest) super.withImporting(name, dataType);
    }

    @Nonnull
    @Override
    public <
        T extends com.sap.cloud.sdk.s4hana.serialization.ErpType<T>>
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final T value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public <T> BapiRequest withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final T value,
        @Nullable final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<T> typeConverter )
    {
        return (BapiRequest) super.withImporting(name, dataType, value, typeConverter);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Boolean value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Byte value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withExporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final byte[] value )
    {
        return (BapiRequest) super.withExporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Character value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final String value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Short value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Integer value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Long value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Float value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Double value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigInteger value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final BigDecimal value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final Locale value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public BapiRequest withImporting(
        @Nonnull final String name,
        @Nonnull final String dataType,
        @Nullable final com.sap.cloud.sdk.s4hana.types.Year value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalDate value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImporting( @Nonnull final String name, @Nonnull final String dataType, @Nullable final LocalTime value )
    {
        return (BapiRequest) super.withImporting(name, dataType, value);
    }

    @Nonnull
    @Override
    public
        ParameterFields<BapiRequest>
        withImportingFields( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withImportingFields(name, dataType);
    }

    @Nonnull
    @Override
    public
        BapiRequest
        withImportingFields( @Nonnull final String name, @Nonnull final String dataType, @Nonnull final Fields fields )
    {
        return (BapiRequest) super.withImportingFields(name, dataType, fields);
    }

    @Nonnull
    @Override
    public Table<BapiRequest> withImportingTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withImportingTable(name, dataType);
    }

    @Nonnull
    @Override
    public BapiRequest withImportingAsReturn( @Nonnull final String dataType )
    {
        return (BapiRequest) super.withImportingAsReturn(dataType);
    }

    @Nonnull
    @Override
    public BapiRequest withImportingAsReturn( @Nonnull final String name, @Nonnull final String dataType )
    {
        return (BapiRequest) super.withImportingAsReturn(name, dataType);
    }

    @Nonnull
    @Override
    public Table<BapiRequest> withTable( @Nonnull final String name, @Nonnull final String dataType )
    {
        return super.withTable(name, dataType);
    }

    @Nonnull
    @Override
    public BapiRequest withTableAsReturn( @Nonnull final String dataType )
    {
        return (BapiRequest) super.withTableAsReturn(dataType);
    }

    @Nonnull
    @Override
    public BapiRequest withTableAsReturn( @Nonnull final String name, final @Nonnull String dataType )
    {
        return (BapiRequest) super.withTableAsReturn(name, dataType);
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
    public BapiRequest withErrorHandler(
        @Nonnull final RemoteFunctionRequestErrorHandler remoteFunctionRequestErrorHandler )
    {
        return (BapiRequest) super.withErrorHandler(remoteFunctionRequestErrorHandler);
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which does <b>not</b>
     * inspect the {@link BapiRequestResult} and, therefore, does <b>not</b> throw a
     * {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException}.
     */
    @Nonnull
    @Override
    public BapiRequest ignoringErrors()
    {
        return (BapiRequest) super.ignoringErrors();
    }

    /**
     * Specifies to invoke a {@link RemoteFunctionRequestErrorHandler} after the request execution which inspects the
     * {@link BapiRequestResult} and throws a
     * {@link com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException} or one of its more-specific
     * exceptions depending on the returned error messages of the request.
     */
    @Nonnull
    @Override
    public BapiRequest propagatingErrorsAsExceptions()
    {
        return (BapiRequest) super.propagatingErrorsAsExceptions();
    }
}
