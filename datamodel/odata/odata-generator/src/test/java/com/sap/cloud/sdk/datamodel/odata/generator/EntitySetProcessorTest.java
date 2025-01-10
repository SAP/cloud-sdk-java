package com.sap.cloud.sdk.datamodel.odata.generator;

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
        final EntitySetProcessor esp = new EntitySetProcessor(service, null, null, null, null, false);

        assertThatThrownBy(() -> esp.processFunctionImports(null, new NamingContext()))
            .isInstanceOf(ODataGeneratorReadException.class);
    }

    private Service mockServiceClassWithSingleFunctionImportWithoutHttMethod()
    {
        final Service.FunctionImport mockedFunctionImport = mock(Service.FunctionImport.class);
        final Service mockedService = mock(Service.class);

        when(mockedFunctionImport.getHttpMethod()).thenReturn(null);
        when(mockedService.getAllFunctionImports()).thenReturn(Collections.singleton(mockedFunctionImport));

        return mockedService;
    }
}
