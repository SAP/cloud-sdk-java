package com.sap.cloud.sdk.datamodel.odata.generator;

import com.google.common.collect.ArrayListMultimap;
import com.sap.cloud.sdk.datamodel.odata.utility.ServiceNameMappings;
import org.apache.olingo.odata2.api.edm.Edm;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class EdmServiceTest {

    private static final String SERVICE_NAME = "API_MATERIAL_DOCUMENT_SRV";

    @Test
    void testServiceNameMappingsGenerated() {

        // Mock ServiceNameMappings to return empty Optional (no stored mappings)
        final ServiceNameMappings mockMappings = mock(ServiceNameMappings.class);
        doReturn(Optional.empty()).when(mockMappings).getString(SERVICE_NAME + ".packageName");
        doReturn(Optional.empty()).when(mockMappings).getString(SERVICE_NAME + ".className");

        final EdmService service = new EdmService(
                SERVICE_NAME, mockMappings, mock(Edm.class), mock(ServiceDetails.class),
                ArrayListMultimap.create(), false);

        assertThat(service.getJavaPackageName()).isEqualTo("apimaterialdocumentsrv");
        assertThat(service.getJavaClassName()).isEqualTo("APIMATERIALDOCUMENTSRV");
    }


    @Test
    void testStoredServiceNameMappingsAreUnchanged() {
        final String expectedPackageName = "apimaterialdocumentsrv";
        final String expectedClassName = "APIMATERIALDOCUMENTSRV";

        // Mock ServiceNameMappings to return stored mappings
        final ServiceNameMappings mockMappings = mock(ServiceNameMappings.class);
        doReturn(Optional.of(expectedPackageName)).when(mockMappings).getString(SERVICE_NAME + ".packageName");
        doReturn(Optional.of(expectedClassName)).when(mockMappings).getString(SERVICE_NAME + ".className");

        final EdmService service = new EdmService(
                SERVICE_NAME, mockMappings, mock(Edm.class), mock(ServiceDetails.class),
                ArrayListMultimap.create(), false);

        assertThat(service.getJavaPackageName()).isEqualTo(expectedPackageName);
        assertThat(service.getJavaClassName()).isEqualTo(expectedClassName);
    }
}
