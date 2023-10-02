/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.typeconverter.ConvertedObject;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import lombok.SneakyThrows;

@RunWith( MockitoJUnitRunner.class )
@Deprecated
public class JCoTransactionValueTest
{
    private static final String BAPI_NAME = "BAPI_NAME";
    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String FIELD_TYPE = "FIELD_TYPE";
    private static final String STRUCTURE_NAME = "STRUCTURE_NAME";
    private static final String STRUCTURE_TYPE = "STRUCTURE_TYPE";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String TABLE_TYPE = "TABLE_TYPE";

    @Mock
    Destination destination;
    @Mock
    JCoDestination jCoDestination;
    @Mock
    JCoRepository jCoRepository;
    @Mock
    JCoFunction jCoFunction;

    @Mock
    JCoParameterList importParameters;
    @Mock
    JCoParameterList exportParameters;
    @Mock
    JCoParameterList changingParameters;
    @Mock
    JCoParameterList tableParameters;

    @Mock
    JCoStructure structure;
    @Mock
    JCoTable table;

    JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction;
    BapiRequest request;

    @Before
    @SneakyThrows
    public void setupMockedObjects()
    {
        when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        when(jCoRepository.getFunction(eq(BAPI_NAME))).thenReturn(jCoFunction);

        when(importParameters.iterator()).thenReturn(Collections.emptyIterator());
        when(jCoFunction.getImportParameterList()).thenReturn(importParameters);

        when(exportParameters.iterator()).thenReturn(Collections.emptyIterator());
        when(jCoFunction.getExportParameterList()).thenReturn(exportParameters);

        when(changingParameters.iterator()).thenReturn(Collections.emptyIterator());
        when(jCoFunction.getChangingParameterList()).thenReturn(changingParameters);

        when(tableParameters.iterator()).thenReturn(Collections.emptyIterator());
        when(jCoFunction.getTableParameterList()).thenReturn(tableParameters);

        jCoTransaction = new JCoTransaction<>(jCoDestination, BapiRequestResult::new);
        request = new BapiRequest(BAPI_NAME);

        when(exportParameters.getStructure(STRUCTURE_NAME)).thenReturn(structure);
        when(exportParameters.getTable(TABLE_NAME)).thenReturn(table);
    }

    @SneakyThrows
    @Test
    public void testRootValueString()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, "STRING");
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "STRING");
    }

    @SneakyThrows
    @Test
    public void testRootValueBoolean()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, true);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "X");// better would be: setValue(String, boolean)
    }

    @SneakyThrows
    @Test
    public void testRootValueByte()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, (byte) 42);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "42"); // better would be: setValue(String, byte)
    }

    @SneakyThrows
    @Test
    public void testRootValueByteArray()
    {
        request.withExporting(FIELD_NAME, FIELD_TYPE, "foo".getBytes());
        jCoTransaction.execute(destination, request);
        verify(importParameters).setValue(FIELD_NAME, "foo".getBytes());
    }

    @SneakyThrows
    @Test
    public void testRootValueCharacter()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, 'A');
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "A"); // better would be: setValue(String, char)
    }

    @SneakyThrows
    @Test
    public void testRootValueShort()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, (short) 1);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1"); // better would be: setValue(String, short)
    }

    @SneakyThrows
    @Test
    public void testRootValueInteger()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, 1);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1"); // better would be: setValue(String, int)
    }

    @SneakyThrows
    @Test
    public void testRootValueLong()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, 1L);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1"); // better would be: setValue(String, long)
    }

    @SneakyThrows
    @Test
    public void testRootValueFloat()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, 1F);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1"); // better would be: setValue(String, float)
    }

    @SneakyThrows
    @Test
    public void testRootValueDouble()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, 1.0);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1");// better would be: setValue(String, double)
    }

    @SneakyThrows
    @Test
    public void testRootValueBigDecimal()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, BigDecimal.ONE);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1");// better would be: setValue(String, BigDecimal)
    }

    @SneakyThrows
    @Test
    public void testRootValueBigInteger()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, BigInteger.ONE);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1");
    }

    @SneakyThrows
    @Test
    public void testRootValueLocale()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, Locale.ENGLISH);
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "EN");
    }

    @SneakyThrows
    @Test
    public void testRootValueYear()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, new com.sap.cloud.sdk.s4hana.types.Year(1996));
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "1996");
    }

    @SneakyThrows
    @Test
    public void testRootValueLocalTime()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, LocalTime.of(13, 37));
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "133700");
    }

    @SneakyThrows
    @Test
    public void testRootValueLocalDate()
    {
        request.withImporting(FIELD_NAME, FIELD_TYPE, LocalDate.of(2000, 1, 1));
        jCoTransaction.execute(destination, request);
        verify(exportParameters).setValue(FIELD_NAME, "20000101");
    }

    @SneakyThrows
    @Test
    public void testInStructureValueString()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, "STRING").end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "STRING"); // Differs from root value case: setValue(String, Object)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueBoolean()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, true).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "X");// better would be: setValue(String, boolean)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueByte()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, (byte) 42).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "42"); // better would be: setValue(String, byte)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueCharacter()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, 'A').end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "A"); // better would be: setValue(String, char)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueShort()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, (short) 1).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1"); // better would be: setValue(String, short)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueInteger()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, 1).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1"); // better would be: setValue(String, int)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueLong()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, 1L).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1"); // better would be: setValue(String, long)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueFloat()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, 1F).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1"); // better would be: setValue(String, float)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueDouble()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, 1.0).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1");// better would be: setValue(String, double)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueBigDecimal()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, BigDecimal.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1");// better would be: setValue(String, BigDecimal)
    }

    @SneakyThrows
    @Test
    public void testInStructureValueBigInteger()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, BigInteger.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1");
    }

    @SneakyThrows
    @Test
    public void testInStructureValueLocale()
    {
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, Locale.ENGLISH).end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "EN");
    }

    @SneakyThrows
    @Test
    public void testInStructureValueYear()
    {
        request
            .withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE)
            .field(FIELD_NAME, FIELD_TYPE, new com.sap.cloud.sdk.s4hana.types.Year(1996))
            .end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "1996");
    }

    @SneakyThrows
    @Test
    public void testInStructureValueLocalTime()
    {
        request
            .withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE)
            .field(FIELD_NAME, FIELD_TYPE, LocalTime.of(13, 37))
            .end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "133700");
    }

    @SneakyThrows
    @Test
    public void testInStructureValueLocalDate()
    {
        request
            .withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE)
            .field(FIELD_NAME, FIELD_TYPE, LocalDate.of(2000, 1, 1))
            .end();
        jCoTransaction.execute(destination, request);
        verify(structure).setValue(FIELD_NAME, "20000101");
    }

    @SneakyThrows
    @Test
    public void testInTableValueString()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, "STRING").end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "STRING"); // Differs from root value case: setValue(String, String)
    }

    @SneakyThrows
    @Test
    public void testInTableValueBoolean()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, true).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "X");// better would be: setValue(String, boolean)
    }

    @SneakyThrows
    @Test
    public void testInTableValueByte()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, (byte) 42).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "42"); // better would be: setValue(String, byte)
    }

    @SneakyThrows
    @Test
    public void testInTableValueCharacter()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, 'A').end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "A"); // better would be: setValue(String, char)
    }

    @SneakyThrows
    @Test
    public void testInTableValueShort()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, (short) 1).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1"); // better would be: setValue(String, short)
    }

    @SneakyThrows
    @Test
    public void testInTableValueInteger()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, 1).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1"); // better would be: setValue(String, int)
    }

    @SneakyThrows
    @Test
    public void testInTableValueLong()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, 1L).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1"); // better would be: setValue(String, long)
    }

    @SneakyThrows
    @Test
    public void testInTableValueFloat()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, 1F).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1"); // better would be: setValue(String, float)
    }

    @SneakyThrows
    @Test
    public void testInTableValueDouble()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, 1.0).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1");// better would be: setValue(String, double)
    }

    @SneakyThrows
    @Test
    public void testInTableValueBigDecimal()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, BigDecimal.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1");// better would be:setValue(String, BigDecimal)
    }

    @SneakyThrows
    @Test
    public void testInTableValueBigInteger()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, BigInteger.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1");
    }

    @SneakyThrows
    @Test
    public void testInTableValueLocale()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, Locale.ENGLISH).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "EN");
    }

    @SneakyThrows
    @Test
    public void testInTableValueYear()
    {
        request
            .withImportingTable(TABLE_NAME, TABLE_TYPE)
            .row()
            .field(FIELD_NAME, FIELD_TYPE, new com.sap.cloud.sdk.s4hana.types.Year(1996))
            .end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "1996");
    }

    @SneakyThrows
    @Test
    public void testInTableValueLocalTime()
    {
        request
            .withImportingTable(TABLE_NAME, TABLE_TYPE)
            .row()
            .field(FIELD_NAME, FIELD_TYPE, LocalTime.of(13, 37))
            .end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "133700");
    }

    @SneakyThrows
    @Test
    public void testInTableValueLocalDate()
    {
        request
            .withImportingTable(TABLE_NAME, TABLE_TYPE)
            .row()
            .field(FIELD_NAME, FIELD_TYPE, LocalDate.of(2000, 1, 1))
            .end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(FIELD_NAME, "20000101");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueString()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row("STRING").end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "STRING");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueBoolean()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(true).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "X");// better would be: setValue(String, boolean)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueByte()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row((byte) 42).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "42"); // better would be: setValue(String, byte)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueCharacter()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row('A').end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "A"); // better would be: setValue(String, char)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueShort()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row((short) 1).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1"); // better would be: setValue(String, short)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueInteger()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(1).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1"); // better would be: setValue(String, int)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueLong()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(1L).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1"); // better would be: setValue(String, long)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueFloat()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(1F).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1"); // better would be: setValue(String, float)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueDouble()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(1.0).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1");// better would be: setValue(String, double)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueBigDecimal()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(BigDecimal.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1");// better would be:setValue(String, BigDecimal)
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueBigInteger()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(BigInteger.ONE).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueLocale()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(Locale.ENGLISH).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "EN");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueYear()
    {
        request
            .withImportingTable(TABLE_NAME, TABLE_TYPE)
            .asVector()
            .row(new com.sap.cloud.sdk.s4hana.types.Year(1996))
            .end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "1996");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueLocalTime()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(LocalTime.of(13, 37)).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "133700");
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueLocalDate()
    {
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(LocalDate.of(2000, 1, 1)).end();
        jCoTransaction.execute(destination, request);
        verify(table).setValue(0, "20000101");
    }

    @SneakyThrows
    @Test
    public void testRootValueErpType()
    {
        final com.sap.cloud.sdk.s4hana.serialization.SapClient sapClient =
            spy(com.sap.cloud.sdk.s4hana.serialization.SapClient.of("012"));
        request.withImporting(FIELD_NAME, FIELD_TYPE, sapClient);
        jCoTransaction.execute(destination, request);

        verify(exportParameters).setValue(FIELD_NAME, "012");
        verify(sapClient).getTypeConverter();
    }

    @SneakyThrows
    @Test
    public void testInStructureValueErpType()
    {
        final com.sap.cloud.sdk.s4hana.serialization.SapClient sapClient =
            spy(com.sap.cloud.sdk.s4hana.serialization.SapClient.of("012"));
        request.withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE).field(FIELD_NAME, FIELD_TYPE, sapClient).end();
        jCoTransaction.execute(destination, request);

        verify(structure).setValue(FIELD_NAME, "012"); // success
        verify(sapClient).getTypeConverter(); // success
    }

    @SneakyThrows
    @Test
    public void testInTableValueErpType()
    {
        final com.sap.cloud.sdk.s4hana.serialization.SapClient sapClient =
            spy(com.sap.cloud.sdk.s4hana.serialization.SapClient.of("012"));
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).row().field(FIELD_NAME, FIELD_TYPE, sapClient).end();
        jCoTransaction.execute(destination, request);

        verify(table).setValue(FIELD_NAME, "012"); // success
        verify(sapClient).getTypeConverter(); // success
    }

    @SneakyThrows
    @Test
    public void testInVectorTableValueErpType()
    {
        final com.sap.cloud.sdk.s4hana.serialization.SapClient sapClient =
            spy(com.sap.cloud.sdk.s4hana.serialization.SapClient.of("012"));
        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(sapClient).end();
        jCoTransaction.execute(destination, request);

        verify(table).setValue(0, "012"); // success
        verify(sapClient).getTypeConverter(); // success
    }

    @Ignore // CLOUDECOSYSTEM-9104
    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    @Test
    public void testRootValueCustomType()
    {
        final Object valueRaw = new Object();
        final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<Object> valueTypeConverter =
            mock(com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter.class);
        when(valueTypeConverter.getType()).thenReturn(Object.class);
        when(valueTypeConverter.getDomainType()).thenReturn(String.class);
        when(valueTypeConverter.toDomain(any())).thenReturn(ConvertedObject.of("SUCCESS"));

        request.withImporting(FIELD_NAME, FIELD_TYPE, valueRaw, valueTypeConverter);
        jCoTransaction.execute(destination, request);

        verify(exportParameters).setValue(FIELD_NAME, "SUCCESS"); // fail
        verify(valueTypeConverter).toDomain(eq(valueRaw)); // fail
    }

    @Ignore // CLOUDECOSYSTEM-9104
    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    @Test
    public void testInStructureCustomType()
    {
        final Object valueRaw = new Object();
        final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<Object> valueTypeConverter =
            mock(com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter.class);
        when(valueTypeConverter.getType()).thenReturn(Object.class);
        when(valueTypeConverter.getDomainType()).thenReturn(String.class);
        when(valueTypeConverter.toDomain(any())).thenReturn(ConvertedObject.of("SUCCESS"));

        request
            .withImportingFields(STRUCTURE_NAME, STRUCTURE_TYPE)
            .field(FIELD_NAME, FIELD_TYPE, valueRaw, valueTypeConverter)
            .end();
        jCoTransaction.execute(destination, request);

        verify(structure).setValue(FIELD_NAME, "SUCCESS"); // fail
        verify(valueTypeConverter).toDomain(eq(valueRaw)); // fail
    }

    @Ignore // CLOUDECOSYSTEM-9104
    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    @Test
    public void testInTableCustomType()
    {
        final Object valueRaw = new Object();
        final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<Object> valueTypeConverter =
            mock(com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter.class);
        when(valueTypeConverter.getType()).thenReturn(Object.class);
        when(valueTypeConverter.getDomainType()).thenReturn(String.class);
        when(valueTypeConverter.toDomain(any())).thenReturn(ConvertedObject.of("SUCCESS"));

        request
            .withImportingTable(TABLE_NAME, TABLE_TYPE)
            .row()
            .field(FIELD_NAME, FIELD_TYPE, valueRaw, valueTypeConverter)
            .end();
        jCoTransaction.execute(destination, request);

        verify(table).setValue(FIELD_NAME, "SUCCESS"); // fail
        verify(valueTypeConverter).toDomain(eq(valueRaw)); // fail
    }

    @Ignore // CLOUDECOSYSTEM-9104
    @SuppressWarnings( "unchecked" )
    @SneakyThrows
    @Test
    public void testInVectorTableCustomType()
    {
        final Object valueRaw = new Object();
        final com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter<Object> valueTypeConverter =
            mock(com.sap.cloud.sdk.s4hana.serialization.ErpTypeConverter.class);
        when(valueTypeConverter.getType()).thenReturn(Object.class);
        when(valueTypeConverter.getDomainType()).thenReturn(String.class);
        when(valueTypeConverter.toDomain(any())).thenReturn(ConvertedObject.of("SUCCESS"));

        request.withImportingTable(TABLE_NAME, TABLE_TYPE).asVector().row(valueRaw, valueTypeConverter).end();
        jCoTransaction.execute(destination, request);

        verify(table).setValue(0, "SUCCESS"); // fail
        verify(valueTypeConverter).toDomain(eq(valueRaw)); // fail
    }
}
