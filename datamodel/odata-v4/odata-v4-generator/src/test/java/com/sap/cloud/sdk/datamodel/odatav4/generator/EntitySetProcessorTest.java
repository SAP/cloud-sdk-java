/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;

class EntitySetProcessorTest
{
    @Test
    void processFunctionImportsShouldThrowODataExceptionOnNonExistentHttpMethod()
    {
        final Service service = mockServiceClassWithSingleFunctionImportWithoutHttMethod();
        final EntitySetProcessor esp = new EntitySetProcessor(service, null, null, null);

        assertThatThrownBy(() -> esp.processFunctionImports(null, new NamingContext()))
            .isInstanceOf(ODataGeneratorReadException.class);
    }

    private Service mockServiceClassWithSingleFunctionImportWithoutHttMethod()
    {
        final Service.ServiceFunction mockedFunctionImport = mock(Service.ServiceFunction.class);
        final Service mockedService = mock(Service.class);

        when(mockedFunctionImport.getHttpMethod()).thenReturn(null);
        when(mockedService.getAllServiceFunctions()).thenReturn(Collections.singleton(mockedFunctionImport));

        return mockedService;
    }
}
