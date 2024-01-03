/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionCommitFailedException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionRollbackFailedException;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link Transaction} interface to be used for JCo queries.
 *
 * @param <RequestT>
 *            The type of the request to execute.
 * @param <RequestResultT>
 *            The type of the result to return.
 *
 * @deprecated This module will be discontinued, along with its classes and methods.
 */
@RequiredArgsConstructor( access = AccessLevel.PACKAGE )
@Slf4j
@Deprecated
public class JCoTransaction<RequestT extends AbstractRemoteFunctionRequest<RequestT, RequestResultT>, RequestResultT extends AbstractRemoteFunctionRequestResult<RequestT, RequestResultT>>
    implements
    Transaction<RequestT, RequestResultT>
{
    /**
     * Use an explicit destination name provided at construction.
     */
    private final JCoDestination jCoDestination;

    private final Supplier<RequestResultT> requestResultFactory;

    // BAPI for rolling back changes in the S/4HANA system
    static final String ROLLBACK_FUNCTION_NAME = "BAPI_TRANSACTION_ROLLBACK";

    // BAPI for committing changes in the S/4HANA system
    static final String COMMIT_FUNCTION_NAME = "BAPI_TRANSACTION_COMMIT";

    private static final String DATE_TIME_PATTERN = "yyyyMMdd HHmmss";

    @Getter( AccessLevel.PACKAGE )
    private final com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer erpTypeSerializer =
        JCoErpNoopConverter.overrideNumbers(new com.sap.cloud.sdk.s4hana.connectivity.ErpTypeSerializer());

    private final JCoFunctionRetriever jCoFunctionRetriever = new DefaultJCoFunctionRetriever();

    /**
     * Constructs a {@code JCoTransaction} for the given {@code destinationName} and {@code requestResultFactory}.
     *
     * @param destinationName
     *            Name of the destination to be used.
     * @param requestResultFactory
     *            Provider to be used to create a {@code RequestResultT} object.
     * @throws RemoteFunctionException
     *             If there was an error while getting the actual destination for the given {@code destinationName}.
     */
    public JCoTransaction(
        @Nonnull final String destinationName,
        @Nonnull final Supplier<RequestResultT> requestResultFactory )
        throws RemoteFunctionException
    {
        this.requestResultFactory = requestResultFactory;
        try {
            jCoDestination = JCoDestinationManager.getDestination(destinationName);
        }
        catch( final JCoException e ) {
            throw new RemoteFunctionException(e);
        }
    }

    @Override
    public void before( @Nonnull final Destination destination, @Nonnull final RequestT request )
    {
        if( request.isPerformingTransactionalCommit() ) {
            JCoContext.begin(jCoDestination);
        }
    }

    @Override
    @Nonnull
    public RequestResultT execute( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws RemoteFunctionException
    {
        try {
            final JCoFunction function = getJCoFunction(request, jCoDestination);

            passRequestParameterToJCoFunction(request, function);

            log.debug("Invoking function {} with name.", function.getName());

            function.execute(jCoDestination);

            return getRequestResultAfterFunctionInvocation(request, function);
        }
        catch( final JCoException | DestinationAccessException e ) {
            throw new RemoteFunctionException(e);
        }
    }

    private JCoFunction getJCoFunction( final RequestT request, final JCoDestination destination )
        throws JCoException
    {
        return jCoFunctionRetriever.retrieveJCoFunction(request.getFunctionName(), destination);
    }

    void passRequestParameterToJCoFunction( final RequestT request, final JCoFunction function )
    {
        for( final Parameter parameter : request.getParameters() ) {

            final ParameterKind parameterKind = parameter.getParameterKind();
            final Value<?> parameterValue = parameter.getParameterValue();

            final JCoParameterList parameterList;

            switch( parameterKind ) {
                case EXPORTING:
                    parameterList = function.getImportParameterList();
                    break;
                case IMPORTING:
                    parameterList = function.getExportParameterList();
                    break;
                case TABLES:
                    parameterList = function.getTableParameterList();
                    break;
                case CHANGING:
                    parameterList = function.getChangingParameterList();
                    break;
                default:
                    log
                        .warn(
                            "Parameter '{}' of function '{}' has an unsupported parameter type '{}', hence ignoring parameter.",
                            parameterValue.getName(),
                            function.getName(),
                            parameterKind);

                    continue;
            }

            if( parameterList == null ) {
                log
                    .warn(
                        "Parameter '{}' of function '{}' has no parameter list. Ignoring parameter.",
                        parameterValue.getName(),
                        function.getName());
                continue;
            }

            serializeValue(parameterValue, parameterList);
        }
    }

    private void serializeValue( @Nonnull final Value<?> value, @Nonnull final JCoRecord record )
    {
        final String valueName = value.getName();

        switch( value.getValueType() ) {
            case FIELD: {
                final Object valueRaw = value.getValue();
                if( valueRaw instanceof byte[] ) {
                    record.setValue(valueName, (byte[]) valueRaw);
                } else {
                    record.setValue(valueName, erpTypeSerializer.toErp(valueRaw).get());
                }
                break;
            }
            case TABLE: {
                final JCoTable table = record.getTable(valueName);
                final List<List<Value<?>>> cells = value.getAsTable();
                final boolean isTableVector =
                    !cells.isEmpty() && cells.get(0).size() == 1 && cells.get(0).get(0).getName() == null;

                for( final List<Value<?>> row : cells ) {
                    table.appendRow();
                    for( final Value<?> item : row ) {
                        if( isTableVector ) {
                            table.setValue(0, erpTypeSerializer.toErp(item.getValue()).get());
                        } else {
                            serializeValue(item, table);
                        }
                    }
                }
                break;
            }
            case STRUCTURE: {
                final JCoStructure structure = record.getStructure(valueName);
                for( final Value<?> item : value.getAsStructure() ) {
                    serializeValue(item, structure);
                }
                break;
            }
        }
    }

    private RequestResultT getRequestResultAfterFunctionInvocation(
        @Nonnull final RequestT request,
        @Nonnull final JCoFunction functionToInvoke )
    {
        final RequestResultT requestResult = requestResultFactory.get();
        requestResult.setRequest(request);

        final ArrayList<AbstractRemoteFunctionRequestResult.Result> resultList = new ArrayList<>();

        // FIXME type converters
        final List<com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<?>> typeConverters =
            Lists.newArrayList(request.getTypeConverters());
        typeConverters.add(new com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter(DATE_TIME_PATTERN));
        typeConverters.add(new com.sap.cloud.sdk.s4hana.serialization.LocalTimeConverter(DATE_TIME_PATTERN));

        final GsonResultElementFactory resultElementFactory =
            new GsonResultElementFactory(RemoteFunctionGsonBuilder.newJCoRequestResultGsonBuilder(typeConverters));

        final JCoFieldToResultReader resultReader = new JCoFieldToResultReader();

        final JCoParameterList exportParameterList = functionToInvoke.getExportParameterList();
        final JCoParameterList importParameterList = functionToInvoke.getImportParameterList();
        final JCoParameterList changingParameterList = functionToInvoke.getChangingParameterList();
        final JCoParameterList tableParameterList = functionToInvoke.getTableParameterList();

        if( exportParameterList != null ) {
            for( final JCoField field : exportParameterList ) {
                log.debug("Converting EXPORTING parameter {} from JCo result.", field.getName());

                resultList.add(resultReader.newResult(field, resultElementFactory));
            }
        }

        if( importParameterList != null ) {
            for( final JCoField field : importParameterList ) {
                log.debug("Converting IMPORTING parameter {} from JCo result.", field.getName());

                resultList.add(resultReader.newResult(field, resultElementFactory));
            }
        }

        if( changingParameterList != null ) {
            for( final JCoField field : changingParameterList ) {
                log.debug("Converting CHANGING parameter {} from JCo result.", field.getName());

                resultList.add(resultReader.newResult(field, resultElementFactory));
            }
        }

        if( tableParameterList != null ) {
            for( final JCoField field : tableParameterList ) {
                log.debug("Converting TABLES parameter {} from JCo result.", field.getName());

                resultList.add(resultReader.newResult(field, resultElementFactory));
            }
        }

        requestResult.setResultList(resultList);

        for( final AbstractRemoteFunctionRequestResult.Result returnParameterResult : getReturnParameterResults(
            requestResult) ) {

            final ResultElement resultElement = returnParameterResult.getValue();
            final Collection<ResultElement> elements = new ArrayList<>();

            if( resultElement.isResultCollection() ) {
                Iterables.addAll(elements, resultElement.getAsCollection());
            } else {
                elements.add(resultElement);
            }

            for( final ResultElement element : elements ) {
                if( element.isResultObject() ) {
                    MessageResultReader
                        .addMessageToResult(
                            requestResult,
                            element.getAsObject().as(AbstractRemoteFunctionRequestResult.MessageResult.class));
                }
            }
        }
        return requestResult;
    }

    /**
     * Returns the results of the return parameters of the given {@code result}.
     *
     * @param result
     *            The result to get the return parameter results from.
     * @return The results of the return parameters of the given {@code result}.
     */
    protected List<AbstractRemoteFunctionRequestResult.Result> getReturnParameterResults( final RequestResultT result )
    {
        final Set<String> returnParameterNames = result.getRequest().getReturnParameterNames();
        final List<AbstractRemoteFunctionRequestResult.Result> returnParameterResults = new ArrayList<>();

        final ArrayList<AbstractRemoteFunctionRequestResult.Result> resultList = result.getResultList();

        if( resultList != null ) {
            for( final AbstractRemoteFunctionRequestResult.Result resultItem : resultList ) {
                if( returnParameterNames.contains(resultItem.getName()) ) {
                    returnParameterResults.add(resultItem);
                }
            }
        }

        return returnParameterResults;
    }

    @Override
    public void commit( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final com.sap.cloud.sdk.s4hana.serialization.ErpBoolean waitValue =
            com.sap.cloud.sdk.s4hana.serialization.ErpBoolean
                .of(request.getCommitStrategy().isWaitingForCommitToFinish());

        try {
            final JCoRepository repo = jCoDestination.getRepository();
            final JCoFunction commitFunction = repo.getFunction(COMMIT_FUNCTION_NAME);
            commitFunction.getImportParameterList().setValue("WAIT", waitValue.toString());
            commitFunction.execute(jCoDestination);

            assureCommitResultHasNoErrors(commitFunction, request);
        }
        catch( final JCoException e ) {
            throw new com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException(e);
        }
    }

    private void assureCommitResultHasNoErrors( final JCoFunction commitFunction, final RequestT request )
        throws RemoteFunctionCommitFailedException
    {
        final JCoParameterList exportParameterList = commitFunction.getExportParameterList();
        final JCoStructure bapiReturnStructure = exportParameterList.getStructure("RETURN");
        final String bapiReturnType = bapiReturnStructure.getString("TYPE");

        if( com.sap.cloud.sdk.s4hana.serialization.MessageType.ERROR.getIdentifier().equals(bapiReturnType) ) {
            throw new RemoteFunctionCommitFailedException(
                "Failed to commit BAPI transaction due to an unknown error on ERP side. Please investigate the respective ABAP logs.");
        }

        log.debug("Successfully committed BAPI transaction for request: {}.", request);
    }

    @Override
    public void rollback( @Nonnull final Destination destination, @Nonnull final RequestT request )
        throws com.sap.cloud.sdk.s4hana.connectivity.exception.RequestSerializationException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        try {
            final JCoRepository repo = jCoDestination.getRepository();
            final JCoFunction rollbackFunction = repo.getFunction(ROLLBACK_FUNCTION_NAME);
            rollbackFunction.execute(jCoDestination);
        }
        catch( final JCoException e ) {
            throw new RemoteFunctionRollbackFailedException(e);
        }
    }

    @Override
    public void after()
        throws RemoteFunctionException
    {
        if( JCoContext.isStateful(jCoDestination) ) {
            try {
                JCoContext.end(jCoDestination);
            }
            catch( final JCoException e ) {
                throw new RemoteFunctionException(e);
            }
        }
    }
}
