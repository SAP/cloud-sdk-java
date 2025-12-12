package com.sap.cloud.sdk.datamodel.odata.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.olingo.odata2.api.edm.Edm;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ArrayListMultimap;
import com.sap.cloud.sdk.datamodel.odata.utility.ServiceNameMappings;

class EdmServiceTest
{

    @ParameterizedTest
    @MethodSource( "getServiceNameMappingScenarios" )
    void testServiceNameMappingsGeneration(
        @Nonnull final String serviceName,
        @Nonnull final String expectedPackageName,
        @Nonnull final String expectedClassName )
    {

        // Mock ServiceNameMappings to return empty Optional (no stored mappings)
        final ServiceNameMappings mockMappings = mock(ServiceNameMappings.class);
        doReturn(Optional.empty()).when(mockMappings).getString(serviceName + ".packageName");
        doReturn(Optional.empty()).when(mockMappings).getString(serviceName + ".className");

        final EdmService service =
            new EdmService(
                serviceName,
                mockMappings,
                mock(Edm.class),
                mock(ServiceDetails.class),
                ArrayListMultimap.create(),
                false);

        assertThat(service.getJavaPackageName()).isEqualTo(expectedPackageName);
        assertThat(service.getJavaClassName()).isEqualTo(expectedClassName);
    }

    @ParameterizedTest
    @MethodSource( "getServiceNameMappingScenarios" )
    void testStoredServiceNameMappingsAreUnchanged(
        @Nonnull final String serviceName,
        @Nonnull final String expectedPackageName,
        @Nonnull final String expectedClassName )
    {
        // Mock ServiceNameMappings to return stored mappings
        final ServiceNameMappings mockMappings = mock(ServiceNameMappings.class);
        doReturn(Optional.of(expectedPackageName)).when(mockMappings).getString(serviceName + ".packageName");
        doReturn(Optional.of(expectedClassName)).when(mockMappings).getString(serviceName + ".className");

        final EdmService service =
            new EdmService(
                serviceName,
                mockMappings,
                mock(Edm.class),
                mock(ServiceDetails.class),
                ArrayListMultimap.create(),
                false);

        assertThat(service.getJavaPackageName()).isEqualTo(expectedPackageName);
        assertThat(service.getJavaClassName()).isEqualTo(expectedClassName);
    }

    private static Stream<Arguments> getServiceNameMappingScenarios()
    {
        return Stream
            .of(
                // Non-breaking fix for https://github.com/SAP/cloud-sdk-java/issues/1024
                Arguments.of("API_MATERIAL_DOCUMENT_SRV", "materialdocumentsrv", "MATERIALDOCUMENTSRV"),
                Arguments.of("Product_Api_Service", "product", "Product" // "Api" and "Service" removed
                ));
    }
}
