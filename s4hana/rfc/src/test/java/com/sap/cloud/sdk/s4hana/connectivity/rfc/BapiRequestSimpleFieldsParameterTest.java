/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

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

@Deprecated
class BapiRequestSimpleFieldsParameterTest
{
    private static final String FUNCTION_NAME = "BAPI_FUNCTION_NAME";
    private static final String FIELD_NAME = "field";
    private static final String FIELD_TYPE = "does-not-matter";
    private static final Destination destination = mock(Destination.class);

    @Test
    void testSupplyByteArrayParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final byte[] fieldValueSuppliedFromOutside = "foo".getBytes();

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, bapiRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(byte[].class);
        assertThat(fieldValueInsideJCoParameterList).isEqualTo(fieldValueSuppliedFromOutside);
    }

    @Test
    void testSupplyDateParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final LocalDate fieldValueSuppliedFromOutside = LocalDate.now();

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJcoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, bapiRequest);

        assertThat(fieldValueInsideJcoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJcoParameterList)
            .isEqualTo(
                (com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE
                    .toDomainNonNull(fieldValueSuppliedFromOutside)
                    .get()));
    }

    @Test
    void testSupplyRecursiveTableParameter()
    {
        final LocalDate fieldValueSuppliedFromOutside = LocalDate.now();

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME)
                .withExporting("exname", "datatype", "somevalue")
                .withImporting("imname", "datatype", "somevalue")
                .withExportingTable("IT_REQUEST", "FTBI_T_CTPTY_LMT_REQ_EXT")
                .row()
                .field("ROW_UUID", "STRING", "aez62992bh")
                .field("LIMIT_TYPE", "STRING", "limitType")
                // START nested table
                .table("T_CUSTOM_ENTITY", "FTBI_T_CUSTOM_ENTITY")
                .row()
                .field("MyName", "STRING", "AVT")
                .field("MyCountry", "STRING", "IN")
                .row()
                .field("MyName", "STRING", "ECCO")
                .field("MyCountry", "STRING", "FR")
                .end()
                // END nested table
                // START nested structure
                .fields("SOME_STRUCTURE", "TYPE_SOME_STRUCTURE")
                .field("latitude", "Axis", "52")
                .field("Longitude", "Axis", "13")
                .end()
                // END nested structure
                .end()
                .withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<BapiRequest> serializedRequest =
            new SoapRemoteFunctionRequestSerializer<>(BapiRequestResult.class).serialize(bapiRequest);

        assertThat(serializedRequest.getRequestBody())
            .isEqualToIgnoringWhitespace(
                String
                    .format(
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\">"
                            + "   <soapenv:Header />"
                            + "   <soapenv:Body>"
                            + "      <urn:FunctionName>"
                            + "         <Exname>somevalue</Exname>"
                            + "         <ItRequest>"
                            + "            <item>"
                            + "               <RowUuid>aez62992bh</RowUuid>"
                            + "               <LimitType>limitType</LimitType>"
                            + "               <TCustomEntity>"
                            + "                  <item>"
                            + "                     <Myname>AVT</Myname>"
                            + "                     <Mycountry>IN</Mycountry>"
                            + "                  </item>"
                            + "                  <item>"
                            + "                     <Myname>ECCO</Myname>"
                            + "                     <Mycountry>FR</Mycountry>"
                            + "                  </item>"
                            + "               </TCustomEntity>"
                            + "               <SomeStructure>"
                            + "                  <Latitude>52</Latitude>"
                            + "                  <Longitude>13</Longitude>"
                            + "               </SomeStructure>"
                            + "            </item>"
                            + "         </ItRequest>"
                            + "         <Field>%s</Field>"
                            + "      </urn:FunctionName>"
                            + "   </soapenv:Body>"
                            + "</soapenv:Envelope>",
                        fieldValueSuppliedFromOutside));
    }

    @Test
    void testSupplyMultilevelRecursiveTableParameter()
    {
        final LocalDate fieldValueSuppliedFromOutside = LocalDate.now();

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME)
                .withExporting("exname", "datatype", "somevalue")
                .withImporting("imname", "datatype", "somevalue")
                .withExportingTable("IT_REQUEST", "FTBI_T_CTPTY_LMT_REQ_EXT")
                .row()
                .field("ROW_UUID", "STRING", "aez62992bh")
                .field("LIMIT_TYPE", "STRING", "limitType")
                // START nested table
                .table("T_CUSTOM_ENTITY", "FTBI_T_CUSTOM_ENTITY")
                .row()
                .field("MyName", "STRING", "AVT")
                .field("MyCountry", "STRING", "IN")
                .table("INTERNAL_TABLE", "internalTableDataType")
                .row()
                .field("field1", "STRING", "value1")
                .field("field2", "INTEGER", "value2")
                .row()
                .field("field1", "STRING", "value3")
                .field("field2", "INTEGER", "value4")
                .end()
                .row()
                .field("MyName", "STRING", "ECCO")
                .field("MyCountry", "STRING", "FR")
                .end()
                // END nested table
                // START nested structure
                .fields("SOME_STRUCTURE", "TYPE_SOME_STRUCTURE")
                .field("latitude", "Axis", "52")
                .field("Longitude", "Axis", "13")
                .end()
                // END nested structure
                .end()
                .withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final com.sap.cloud.sdk.s4hana.connectivity.SerializedRequest<BapiRequest> serializedRequest =
            new SoapRemoteFunctionRequestSerializer<>(BapiRequestResult.class).serialize(bapiRequest);

        assertThat(serializedRequest.getRequestBody())
            .isEqualToIgnoringWhitespace(
                String
                    .format(
                        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:soap:functions:mc-style\">"
                            + "   <soapenv:Header />"
                            + "   <soapenv:Body>"
                            + "      <urn:FunctionName>"
                            + "         <Exname>somevalue</Exname>"
                            + "         <ItRequest>"
                            + "            <item>"
                            + "               <RowUuid>aez62992bh</RowUuid>"
                            + "               <LimitType>limitType</LimitType>"
                            + "               <TCustomEntity>"
                            + "                  <item>"
                            + "                     <Myname>AVT</Myname>"
                            + "                     <Mycountry>IN</Mycountry>"
                            + "               <InternalTable>"
                            + "                  <item>"
                            + "                     <Field1>value1</Field1>"
                            + "                     <Field2>value2</Field2>"
                            + "                  </item>"
                            + "                  <item>"
                            + "                     <Field1>value3</Field1>"
                            + "                     <Field2>value4</Field2>"
                            + "                  </item>"
                            + "               </InternalTable>"
                            + "                  </item>"
                            + "                  <item>"
                            + "                     <Myname>ECCO</Myname>"
                            + "                     <Mycountry>FR</Mycountry>"
                            + "                  </item>"
                            + "               </TCustomEntity>"
                            + "               <SomeStructure>"
                            + "                  <Latitude>52</Latitude>"
                            + "                  <Longitude>13</Longitude>"
                            + "               </SomeStructure>"
                            + "            </item>"
                            + "         </ItRequest>"
                            + "         <Field>%s</Field>"
                            + "      </urn:FunctionName>"
                            + "   </soapenv:Body>"
                            + "</soapenv:Envelope>",
                        fieldValueSuppliedFromOutside));
    }

    @Test
    void testSupplyStringParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final String fieldValueSuppliedFromOutside = "foo";

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, bapiRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJCoParameterList).isEqualTo(fieldValueSuppliedFromOutside);
    }

    @Test
    void testSupplyBooleanParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final boolean fieldValueSuppliedFromOutside = true;

        final BapiRequest bapiRequest =
            new BapiRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, bapiRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJCoParameterList)
            .isEqualTo(
                com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE
                    .toDomainNonNull(fieldValueSuppliedFromOutside)
                    .get());
    }

    private Object getFieldValueInsideJCo( final Object fieldValueSuppliedFromOutside, final BapiRequest request )
        throws JCoException,
            RemoteFunctionException
    {
        final AtomicReference<Object> fieldValueInsideJCoParameterList = new AtomicReference<>();

        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);
        final JCoParameterList jCoParameterList = Mockito.mock(JCoParameterList.class);
        @SuppressWarnings( "unchecked" )
        final Iterator<JCoField> iterator = (Iterator<JCoField>) mock(Iterator.class);
        Mockito.when(jCoParameterList.iterator()).thenReturn(iterator);

        doAnswer(invocation -> {
            fieldValueInsideJCoParameterList.set(invocation.getArgument(1));
            return null;
        }).when(jCoParameterList).setValue(any(String.class), any(byte[].class));

        doAnswer(invocation -> {
            fieldValueInsideJCoParameterList.set(invocation.getArgument(1));
            return null;
        }).when(jCoParameterList).setValue(any(String.class), any(String.class));

        final JCoField jcoField = Mockito.mock(JCoField.class);
        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction(FUNCTION_NAME)).thenReturn(jCoFunction);
        Mockito.when(jCoFunction.getImportParameterList()).thenReturn(jCoParameterList);
        Mockito.when(iterator.next()).thenReturn(jcoField);
        Mockito.when(jcoField.getName()).thenReturn(FIELD_NAME);
        Mockito.when(jcoField.getValue()).thenReturn(fieldValueSuppliedFromOutside);

        final JCoTransaction<BapiRequest, BapiRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, BapiRequestResult::new);
        jCoTransaction.execute(destination, request);

        return fieldValueInsideJCoParameterList.get();
    }
}
