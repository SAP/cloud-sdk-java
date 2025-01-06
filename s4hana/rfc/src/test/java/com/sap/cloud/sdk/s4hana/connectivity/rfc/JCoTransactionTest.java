/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionCommitFailedException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionRollbackFailedException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecordFieldIterator;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Deprecated
class JCoTransactionTest
{
    private static final String BAPI_NAME = "BAPI_AWESOME_MAGIC";
    private static final String RFM_NAME = "RFM_AWESOME_MAGIC";
    private static final String SIMPLE_FIELD_NAME = "AWESOME_FIELD";
    private static final String DATA_TYPE = "AWESOME_TYPE";
    private static final Integer INTEGER_VALUE = 2;
    private static final String STRING_VALUE = "magic-value";
    private static final String IMPORTING_FIELD_NAME = "importing-field";
    private static final String EXPORTING_FIELD_NAME = "exporting-field";
    private static final String CHANGING_FIELD_NAME = "changing-field";
    private static final JCoException JCO_EXCEPTION = new JCoException(42, "foo", "foo");
    private static final String RETURN_PARAMETER = "RETURN";

    @Test
    @SuppressWarnings( "unchecked" )
    void testExportingSimpleFieldWithTypeConverter()
        throws RemoteFunctionException,
            JCoException
    {
        final BapiRequest bapiRequest =
            new BapiRequest(BAPI_NAME).withExporting(SIMPLE_FIELD_NAME, DATA_TYPE, INTEGER_VALUE);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(BAPI_NAME))).thenReturn(jCoFunction);

        final JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);

        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(importParameterList);

        final JCoField jCoField = Mockito.mock(JCoField.class);

        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(importParameterList.iterator()).thenReturn(iterator);

        Mockito.when(iterator.next()).thenReturn(jCoField);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        jCoTransaction.execute(destination, bapiRequest);

        Mockito
            .verify(importParameterList)
            .setValue(SIMPLE_FIELD_NAME, jCoTransaction.getErpTypeSerializer().toErp(INTEGER_VALUE).get());
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testExportingSimpleFieldWithoutTypeConverter()
        throws RemoteFunctionException,
            JCoException
    {
        final BapiRequest bapiRequest =
            new BapiRequest(BAPI_NAME).withExporting(SIMPLE_FIELD_NAME, DATA_TYPE, STRING_VALUE);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(BAPI_NAME))).thenReturn(jCoFunction);

        final JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);

        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(importParameterList);

        final JCoField jCoField = Mockito.mock(JCoField.class);

        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(importParameterList.iterator()).thenReturn(iterator);

        Mockito.when(iterator.next()).thenReturn(jCoField);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        jCoTransaction.execute(destination, bapiRequest);

        Mockito.verify(importParameterList).setValue(SIMPLE_FIELD_NAME, STRING_VALUE);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testImportingSimpleField()
        throws RemoteFunctionException,
            JCoException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME).withImporting(SIMPLE_FIELD_NAME, DATA_TYPE);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(BAPI_NAME))).thenReturn(jCoFunction);

        final JCoParameterList exportParameterList = Mockito.mock(JCoParameterList.class);

        final JCoField jCoField = Mockito.mock(JCoField.class);

        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(exportParameterList.iterator()).thenReturn(iterator);

        Mockito.when(iterator.next()).thenReturn(jCoField);
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);

        Mockito.when(jCoField.getName()).thenReturn(SIMPLE_FIELD_NAME);
        Mockito.when(jCoField.getValue()).thenReturn(INTEGER_VALUE);

        Mockito.when(jCoFunction.getExportParameterList()).thenReturn(exportParameterList);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        final BapiRequestResult result = jCoTransaction.execute(destination, bapiRequest);

        Mockito
            .verify(exportParameterList)
            .setValue(ArgumentMatchers.eq(SIMPLE_FIELD_NAME), ArgumentMatchers.<String> isNull());

        final int i = result.get(SIMPLE_FIELD_NAME).getAsPrimitive().asInteger();

        assertThat(i).isEqualTo(INTEGER_VALUE);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testImportingAndExportingAndChangingSimpleFields()
        throws RemoteFunctionException,
            JCoException
    {
        final RfmRequest rfmRequest =
            new RfmRequest(RFM_NAME)
                .withImporting(IMPORTING_FIELD_NAME, DATA_TYPE)
                .withExporting(EXPORTING_FIELD_NAME, DATA_TYPE, STRING_VALUE)
                .withChanging(CHANGING_FIELD_NAME, DATA_TYPE, STRING_VALUE);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(RFM_NAME))).thenReturn(jCoFunction);

        final JCoParameterList exportParameterList = Mockito.mock(JCoParameterList.class);
        final JCoParameterList importParameterList = Mockito.mock(JCoParameterList.class);
        final JCoParameterList changingParameterList = Mockito.mock(JCoParameterList.class);

        final JCoField exportingJcoField = Mockito.mock(JCoField.class);
        Mockito.when(exportingJcoField.getName()).thenReturn(IMPORTING_FIELD_NAME);
        Mockito.when(exportingJcoField.getValue()).thenReturn(INTEGER_VALUE);

        final Iterator<JCoField> exportingParameterIterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(exportParameterList.iterator()).thenReturn(exportingParameterIterator);
        Mockito.when(exportingParameterIterator.next()).thenReturn(exportingJcoField);
        Mockito.when(exportingParameterIterator.hasNext()).thenReturn(true).thenReturn(false);

        final JCoField importingJcoField = Mockito.mock(JCoField.class);

        final Iterator<JCoField> importingParameterIterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(importParameterList.iterator()).thenReturn(importingParameterIterator);
        Mockito.when(importingParameterIterator.next()).thenReturn(importingJcoField);
        Mockito.when(importingParameterIterator.hasNext()).thenReturn(true).thenReturn(false);

        final JCoField changingJcoField = Mockito.mock(JCoField.class);
        Mockito.when(changingJcoField.getName()).thenReturn(CHANGING_FIELD_NAME);
        Mockito.when(changingJcoField.getValue()).thenReturn(INTEGER_VALUE);

        final Iterator<JCoField> changingParameterIterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(changingParameterList.iterator()).thenReturn(changingParameterIterator);
        Mockito.when(changingParameterIterator.next()).thenReturn(changingJcoField);
        Mockito.when(changingParameterIterator.hasNext()).thenReturn(true).thenReturn(false);

        Mockito.when(jCoFunction.getExportParameterList()).thenReturn(exportParameterList);
        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(importParameterList);
        Mockito.when(jCoFunction.getChangingParameterList()).thenReturn(changingParameterList);

        final JCoTransaction<RfmRequest, RfmRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, RfmRequestResult::new);

        final RfmRequestResult result = jCoTransaction.execute(destination, rfmRequest);

        Mockito
            .verify(exportParameterList)
            .setValue(ArgumentMatchers.eq(IMPORTING_FIELD_NAME), ArgumentMatchers.<String> isNull());
        Mockito
            .verify(importParameterList, atLeastOnce())
            .setValue(ArgumentMatchers.eq(EXPORTING_FIELD_NAME), ArgumentMatchers.eq(STRING_VALUE));
        Mockito
            .verify(changingParameterList)
            .setValue(ArgumentMatchers.eq(CHANGING_FIELD_NAME), ArgumentMatchers.eq(STRING_VALUE));

        final int importingFieldValue = result.get(IMPORTING_FIELD_NAME).getAsPrimitive().asInteger();
        assertThat(importingFieldValue).isEqualTo(INTEGER_VALUE);

        final int changingFieldValue = result.get(CHANGING_FIELD_NAME).getAsPrimitive().asInteger();
        assertThat(changingFieldValue).isEqualTo(INTEGER_VALUE);
    }

    @Test
    void testExceptionDuringFunctionProcessing()
        throws JCoException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(BAPI_NAME))).thenReturn(jCoFunction);

        Mockito.doThrow(JCO_EXCEPTION).when(jCoFunction).execute(ArgumentMatchers.eq(jCoDestination));

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        assertThatExceptionOfType(RemoteFunctionException.class)
            .isThrownBy(() -> jCoTransaction.execute(destination, bapiRequest))
            .withCauseInstanceOf(JCoException.class);
    }

    @Test
    void testSuccessfulCommit()
        throws JCoException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);

        final JCoFunction commitFunction = mock(JCoFunction.class);
        Mockito
            .when(jCoRepository.getFunction(ArgumentMatchers.eq(JCoTransaction.COMMIT_FUNCTION_NAME)))
            .thenReturn(commitFunction);

        final JCoParameterList commitFunctionParameterList = mock(JCoParameterList.class);
        Mockito.when(commitFunction.getImportParameterList()).thenReturn(commitFunctionParameterList);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        final JCoStructure structure = mock(JCoStructure.class);
        Mockito.when(structure.getString(ArgumentMatchers.eq("TYPE"))).thenReturn("S");
        final JCoParameterList parameterList = mock(JCoParameterList.class);
        Mockito.when(parameterList.getStructure(ArgumentMatchers.eq("RETURN"))).thenReturn(structure);

        Mockito.when(commitFunction.getExportParameterList()).thenReturn(parameterList);

        jCoTransaction.commit(destination, bapiRequest);

        Mockito.verify(commitFunctionParameterList).setValue("WAIT", "X");
        Mockito.verify(commitFunction).execute(ArgumentMatchers.eq(jCoDestination));
    }

    @Test
    void testCommitFunctionReturnsError()
        throws JCoException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);

        final JCoFunction commitFunction = mock(JCoFunction.class);
        Mockito
            .when(jCoRepository.getFunction(ArgumentMatchers.eq(JCoTransaction.COMMIT_FUNCTION_NAME)))
            .thenReturn(commitFunction);

        final JCoParameterList commitFunctionParameterList = mock(JCoParameterList.class);
        Mockito.when(commitFunction.getImportParameterList()).thenReturn(commitFunctionParameterList);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        final JCoStructure structure = mock(JCoStructure.class);
        Mockito.when(structure.getString(ArgumentMatchers.eq("TYPE"))).thenReturn("E");
        final JCoParameterList parameterList = mock(JCoParameterList.class);
        Mockito.when(parameterList.getStructure(ArgumentMatchers.eq("RETURN"))).thenReturn(structure);

        Mockito.when(commitFunction.getExportParameterList()).thenReturn(parameterList);

        assertThatExceptionOfType(RemoteFunctionCommitFailedException.class)
            .isThrownBy(() -> jCoTransaction.commit(destination, bapiRequest));
    }

    @Test
    void testCommitFunctionFailsWithJCoException()
        throws JCoException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);

        final JCoFunction commitFunction = mock(JCoFunction.class);
        Mockito
            .when(jCoRepository.getFunction(ArgumentMatchers.eq(JCoTransaction.COMMIT_FUNCTION_NAME)))
            .thenReturn(commitFunction);

        final JCoParameterList commitFunctionParameterList = mock(JCoParameterList.class);
        Mockito.when(commitFunction.getImportParameterList()).thenReturn(commitFunctionParameterList);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        final JCoStructure structure = mock(JCoStructure.class);
        Mockito.when(structure.getString(ArgumentMatchers.eq("TYPE"))).thenReturn("S");
        final JCoParameterList parameterList = mock(JCoParameterList.class);
        Mockito.when(parameterList.getStructure(ArgumentMatchers.eq("RETURN"))).thenReturn(structure);

        Mockito.when(commitFunction.getExportParameterList()).thenReturn(parameterList);

        Mockito.doThrow(JCO_EXCEPTION).when(commitFunction).execute(ArgumentMatchers.eq(jCoDestination));

        assertThatExceptionOfType(com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException.class)
            .isThrownBy(() -> jCoTransaction.commit(destination, bapiRequest));
    }

    @Test
    void testSuccessfulRollback()
        throws JCoException,
            com.sap.cloud.sdk.s4hana.connectivity.exception.RequestExecutionException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);

        final JCoFunction rollbackFunction = mock(JCoFunction.class);
        Mockito
            .when(jCoRepository.getFunction(ArgumentMatchers.eq(JCoTransaction.ROLLBACK_FUNCTION_NAME)))
            .thenReturn(rollbackFunction);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        jCoTransaction.rollback(destination, bapiRequest);

        Mockito.verify(rollbackFunction).execute(ArgumentMatchers.eq(jCoDestination));
    }

    @Test
    void testFailingRollback()
        throws JCoException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME);

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);

        final JCoFunction rollbackFunction = mock(JCoFunction.class);
        Mockito
            .when(jCoRepository.getFunction(ArgumentMatchers.eq(JCoTransaction.ROLLBACK_FUNCTION_NAME)))
            .thenReturn(rollbackFunction);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        Mockito.doThrow(JCO_EXCEPTION).when(rollbackFunction).execute(ArgumentMatchers.eq(jCoDestination));

        assertThatExceptionOfType(RemoteFunctionRollbackFailedException.class)
            .isThrownBy(() -> jCoTransaction.rollback(destination, bapiRequest))
            .withCauseInstanceOf(JCoException.class);
    }

    @Test
    @SuppressWarnings( "unchecked" )
    void testTablesAsReturnParameter()
        throws JCoException,
            RemoteFunctionException
    {
        final BapiRequest bapiRequest = new BapiRequest(BAPI_NAME).withTableAsReturn("BAPIRET2");

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final Destination destination = Mockito.mock(Destination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(ArgumentMatchers.eq(BAPI_NAME))).thenReturn(jCoFunction);

        final JCoParameterList tablesParameterList = Mockito.mock(JCoParameterList.class);
        Mockito.when(jCoFunction.getTableParameterList()).thenReturn(tablesParameterList);

        final JCoField simpleField = mock(JCoField.class);
        Mockito.when(simpleField.getName()).thenReturn(SIMPLE_FIELD_NAME);
        Mockito.when(simpleField.getValue()).thenReturn(STRING_VALUE);

        final JCoRecordFieldIterator fieldIterator = mock(JCoRecordFieldIterator.class);
        Mockito.when(fieldIterator.hasNextField()).thenReturn(true).thenReturn(false);
        Mockito.when(fieldIterator.nextField()).thenReturn(simpleField);

        final JCoTable typedResultTable = mock(JCoTable.class);
        Mockito.when(typedResultTable.getNumRows()).thenReturn(1);
        Mockito.when(typedResultTable.getRecordFieldIterator()).thenReturn(fieldIterator);

        Mockito.when(tablesParameterList.getTable(ArgumentMatchers.eq(RETURN_PARAMETER))).thenReturn(typedResultTable);

        final JCoField untypedResultTable = Mockito.mock(JCoField.class);
        Mockito.when(untypedResultTable.getName()).thenReturn(RETURN_PARAMETER);
        Mockito.when(untypedResultTable.isTable()).thenReturn(true);
        Mockito.when(untypedResultTable.getTable()).thenReturn(typedResultTable);

        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(tablesParameterList.iterator()).thenReturn(iterator);
        Mockito.when(iterator.next()).thenReturn(untypedResultTable);
        Mockito.when(iterator.hasNext()).thenReturn(true).thenReturn(false);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);

        final BapiRequestResult requestResult = jCoTransaction.execute(destination, bapiRequest);

        assertThat(requestResult.get(RETURN_PARAMETER).getAsCollection().collect(SIMPLE_FIELD_NAME).asStringList())
            .containsExactly(STRING_VALUE);
    }
}
