/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.s4hana.connectivity.rfc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sap.cloud.sdk.cloudplatform.connectivity.exception.DestinationAccessException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;

@Deprecated
class DefaultJCoFunctionRetrieverTest
{
    @Test
    void testSuccessfulFunctionRetrieval()
        throws JCoException
    {
        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction("function")).thenReturn(jCoFunction);

        final DefaultJCoFunctionRetriever cut = new DefaultJCoFunctionRetriever();

        final JCoFunction function = cut.retrieveJCoFunction("function", jCoDestination);

        assertThat(function).isSameAs(jCoFunction);
    }

    @Test
    void testRetrieveFunctionWhichDoesNotExist()
        throws JCoException
    {
        final JCoDestination jCoDestination = Mockito.mock(JCoDestination.class);
        final JCoFunction jCoFunction = Mockito.mock(JCoFunction.class);
        final JCoRepository jCoRepository = Mockito.mock(JCoRepository.class);

        Mockito.when(jCoDestination.getRepository()).thenReturn(jCoRepository);
        Mockito.when(jCoRepository.getFunction("function")).thenReturn(null);
        Mockito.when(jCoDestination.getDestinationName()).thenReturn("destination");

        final DefaultJCoFunctionRetriever cut = new DefaultJCoFunctionRetriever();

        assertThatExceptionOfType(DestinationAccessException.class)
            .isThrownBy(() -> cut.retrieveJCoFunction("function", jCoDestination));
    }
}
