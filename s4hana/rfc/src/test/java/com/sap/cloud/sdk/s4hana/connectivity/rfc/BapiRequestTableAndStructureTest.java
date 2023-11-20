/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.s4hana.connectivity.rfc.exception.RemoteFunctionException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Deprecated
class BapiRequestTableAndStructureTest
{
    private static final String FUNCTION_NAME = "BAPI_FUNCTION_NAME";
    private static final String TABLE_NAME = "IT_REQUEST";
    private static final String STRUCTURE_NAME = "STRUCTURE_REQUEST";
    private static final Destination destination = mock(Destination.class);

    @Test
    void testSupplyTableParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME)
                .withExportingTable(TABLE_NAME, "SOME_TYPE")
                .row()
                .field("ROW_UUID", "STRING", "aez62992bh")
                .field("LIMIT_TYPE", "STRING", "limitType")
                .row()
                .field("PartnerName", "STRING", "AVT")
                .field("PartnerCountry", "STRING", "IN")
                .end();

        final int rowValueInsideJCoTable = getRowValueInsideJCo(bapiRequest);

        assertThat(rowValueInsideJCoTable).isEqualTo(2);
    }

    @Test
    void testSupplyStructureParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME)
                .withExportingFields(STRUCTURE_NAME, "STRUCTURE_TYPE")
                .field("latitude", "Axis", "52")
                .field("Longitude", "Axis", "13")
                .end();

        final Map<String, String> valueMap = getStructureValueInsideJCo(bapiRequest);
        assertThat(valueMap).isNotEmpty();
        assertThat(valueMap.get("latitude")).isEqualTo("52");
        assertThat(valueMap.get("Longitude")).isEqualTo("13");
    }

    private int getRowValueInsideJCo( final BapiRequest request )
        throws JCoException,
            RemoteFunctionException
    {
        final AtomicInteger row = new AtomicInteger();

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);
        final JCoParameterList jCoParameterList = Mockito.mock(JCoParameterList.class);
        final JCoTable jCoTable = Mockito.mock(JCoTable.class);
        @SuppressWarnings( "unchecked" )
        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(jCoParameterList.iterator()).thenReturn(iterator);

        doAnswer(invocation -> {
            row.getAndIncrement();
            return null;
        }).when(jCoTable).appendRow();

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(FUNCTION_NAME)).thenReturn(jCoFunction);
        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(jCoParameterList);
        Mockito.when(jCoParameterList.getTable(TABLE_NAME)).thenReturn(jCoTable);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);
        jCoTransaction.execute(destination, request);

        return row.get();
    }

    private Map<String, String> getStructureValueInsideJCo( final BapiRequest request )
        throws JCoException,
            RemoteFunctionException
    {
        final Map<String, String> valueMap = new HashMap<>();
        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);
        final JCoParameterList jCoParameterList = Mockito.mock(JCoParameterList.class);
        final JCoStructure jCoStructure = Mockito.mock(JCoStructure.class);
        @SuppressWarnings( "unchecked" )
        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(jCoParameterList.iterator()).thenReturn(iterator);

        doAnswer(invocation -> {
            valueMap.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jCoStructure).setValue(any(String.class), any(String.class));

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(FUNCTION_NAME)).thenReturn(jCoFunction);
        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(jCoParameterList);
        Mockito.when(jCoParameterList.getStructure(STRUCTURE_NAME)).thenReturn(jCoStructure);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);
        jCoTransaction.execute(destination, request);

        return valueMap;
    }
}
