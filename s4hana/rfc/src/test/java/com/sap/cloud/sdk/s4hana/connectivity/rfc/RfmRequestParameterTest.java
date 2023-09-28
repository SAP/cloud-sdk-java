package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
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
public class RfmRequestParameterTest
{
    private static final String FUNCTION_NAME = "someFunction";
    private static final String FIELD_NAME = "field";
    private static final String FIELD_TYPE = "does-not-matter";
    private static final Destination destination = mock(Destination.class);

    @Test
    public void testSupplyByteArrayParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final byte[] fieldValueSuppliedFromOutside = "foo".getBytes();

        final RfmRequest rfmRequest =
            new RfmRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, rfmRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(byte[].class);
        assertThat(fieldValueInsideJCoParameterList).isEqualTo(fieldValueSuppliedFromOutside);
    }

    @Test
    public void testSupplyDateParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final LocalDate fieldValueSuppliedFromOutside = LocalDate.now();

        final RfmRequest rfmRequest =
            new RfmRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJcoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, rfmRequest);

        assertThat(fieldValueInsideJcoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJcoParameterList)
            .isEqualTo(
                (com.sap.cloud.sdk.s4hana.serialization.LocalDateConverter.INSTANCE
                    .toDomainNonNull(fieldValueSuppliedFromOutside)
                    .get()));
    }

    @Test
    public void testSupplyStringParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final String fieldValueSuppliedFromOutside = "foo";

        final RfmRequest rfmRequest =
            new RfmRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, rfmRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJCoParameterList).isEqualTo(fieldValueSuppliedFromOutside);
    }

    @Test
    public void testSupplyBooleanParameter()
        throws RemoteFunctionException,
            JCoException
    {
        final boolean fieldValueSuppliedFromOutside = true;

        final RfmRequest rfmRequest =
            new RfmRequest(FUNCTION_NAME).withExporting(FIELD_NAME, FIELD_TYPE, fieldValueSuppliedFromOutside);

        final Object fieldValueInsideJCoParameterList =
            getFieldValueInsideJCo(fieldValueSuppliedFromOutside, rfmRequest);

        assertThat(fieldValueInsideJCoParameterList).isInstanceOf(String.class);
        assertThat(fieldValueInsideJCoParameterList)
            .isEqualTo(
                com.sap.cloud.sdk.s4hana.serialization.BooleanConverter.INSTANCE
                    .toDomainNonNull(fieldValueSuppliedFromOutside)
                    .get());
    }

    private Object getFieldValueInsideJCo( final Object fieldValueSuppliedFromOutside, final RfmRequest request )
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

        final JCoTransaction<RfmRequest, RfmRequestResult> jCoTransaction =
            new JCoTransaction<>(jCoDestination, RfmRequestResult::new);
        jCoTransaction.execute(destination, request);

        return fieldValueInsideJCoParameterList.get();
    }
}
