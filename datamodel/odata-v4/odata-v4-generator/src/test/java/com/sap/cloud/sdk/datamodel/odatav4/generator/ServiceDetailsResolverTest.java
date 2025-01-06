/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ServiceDetailsResolverTest
{
    @Test
    void testWithS4HanaSwagger2File()
    {
        final File swaggerFile = new File("src/test/resources/ServiceDetailsResolverTest/v2S4StyleUrl.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails details = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(details).isNotNull();
        assertThat(details.getServiceUrl()).isEqualTo("/sap/opu/odata/my/custom/path/CompleteBusinessObject");
    }

    @Test
    void testWithS4HanaOpenAPI3File()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3S4StyleUrl.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails details = sut.createServiceDetails(metadataFile, openApiFile);

        assertThat(details).isNotNull();
        assertThat(details.getServiceUrl()).isEqualTo("/sap/opu/odata/my/custom/path/CompleteBusinessObject");
    }

    @Test
    void testWithCustomSwagger2File()
    {
        final File swaggerFile = new File("src/test/resources/ServiceDetailsResolverTest/v2Baseline.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails details = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(details).isNotNull();
        assertThat(details.getServiceUrl())
            .isEqualTo("/othercloud/sap/opu/odata/my/custom/path/CompleteBusinessObject");
    }

    @Test
    void testNoBasePathSwagger2File()
    {
        final File swaggerFile = new File("src/test/resources/ServiceDetailsResolverTest/v2NoBasePath.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails details = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(details).isNotNull();
        assertThat(details.getServiceUrl()).isEqualTo("/");
    }

    @Test
    void testWithCustomOpenApi3File()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3Baseline.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails details = sut.createServiceDetails(metadataFile, openApiFile);

        assertThat(details).isNotNull();
        assertThat(details.getServiceUrl())
            .isEqualTo("/othercloud/sap/opu/odata/my/custom/path/CompleteBusinessObject");
    }

    @Test
    void testWithMissingSwaggerFile()
    {
        final File swaggerFile = new File("some/non/existing/path.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService");
    }

    @Test
    void testWithDefaultBasePathNamespaceAsServiceName()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver("/custom/base/path/", StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/custom/base/path/CustomBusinessObjectNamespace");
    }

    @Test
    void testWithDefaultBasePathNoNamespace()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/NoNamespace.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver("/custom/base/path/", StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/custom/base/path/NoNamespace");
    }

    @Test
    void testWithDefaultBasePathEmptyNamespace()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/EmptyNamespace.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver("/custom/base/path/", StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/custom/base/path/EmptyNamespace");
    }

    @Test
    void testWithDefaultBasePathMultipleSchemas()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/MultipleSchemas.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver("/custom/base/path/", StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/custom/base/path/CustomBusinessService");
    }

    @Test
    void testWithDefaultBasePathMultipleNamespace()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/MultipleNamespace.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver("/custom/base/path/", StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/custom/base/path/SomeService1");
    }

    @Test
    void testWithOnlymetadataFile()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService");
    }

    @Test
    void testWithoutAnyBasePath()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/WithoutAtomLink.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        assertThatExceptionOfType(ODataGeneratorException.class)
            .isThrownBy(() -> sut.createServiceDetails(metadataFile, null));
    }

    @Test
    void testWithDoubleAtomLink()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/DoubleAtomLink.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService1");
    }

    @Test
    void testWithMultipleSchemasWithNoDefaultBasePath()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/MultipleSchemas.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService1");
    }

    @Test
    void testWithMalformedUrl()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/MalformedUrl.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        assertThatExceptionOfType(ODataGeneratorException.class)
            .isThrownBy(() -> sut.createServiceDetails(metadataFile, null));
    }

    @Test
    void testWithShortS4StyleUrl()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/ShortS4StyleAtomLink.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService");
    }

    @Test
    void testWithLongS4StyleUrl()
    {
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/LongS4StyleAtomLink.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, null);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/sap/opu/odata/sap/SomeService;v=0002");
    }

    @Test
    void testWithoutDeprecationFlagInSwagger2()
    {
        final File swaggerFile = new File("src/test/resources/ServiceDetailsResolverTest/v2Baseline.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.isDeprecated()).isFalse();
        assertThat(serviceDetails.getStateInfo()).isEmpty();
    }

    @Test
    void testWithoutDeprecationFlagInOpenApi3()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3Baseline.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, openApiFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.isDeprecated()).isFalse();
        assertThat(serviceDetails.getStateInfo()).isEmpty();
    }

    @Test
    void testWithDeprecationFlagInSwagger2()
    {
        final File swaggerFile = new File("src/test/resources/ServiceDetailsResolverTest/v2DeprecatedService.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, swaggerFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.isDeprecated()).isTrue();
        assertThat(serviceDetails.getStateInfo().get().getState()).isEqualTo(ServiceDetails.State.Deprecated);
        assertThat(serviceDetails.getStateInfo().get().getDeprecationDate()).isEqualTo("AUG-2019");
        assertThat(serviceDetails.getStateInfo().get().getDeprecationRelease()).isEqualTo("1908");
        assertThat(serviceDetails.getStateInfo().get().getSuccessorApi())
            .isEqualTo("https://api.sap.com/api/SOME_OTHER_API");
    }

    @Test
    void testWithDeprecationFlagInOpenApi3()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3DeprecatedService.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, openApiFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.isDeprecated()).isTrue();
        assertThat(serviceDetails.getStateInfo().get().getState()).isEqualTo(ServiceDetails.State.Deprecated);
        assertThat(serviceDetails.getStateInfo().get().getDeprecationDate()).isEqualTo("AUG-2019");
        assertThat(serviceDetails.getStateInfo().get().getDeprecationRelease()).isEqualTo("1908");
        assertThat(serviceDetails.getStateInfo().get().getSuccessorApi())
            .isEqualTo("https://api.sap.com/api/SOME_OTHER_API");
    }

    @Test
    void testParsingWithNoMeaningfulServerUrlsShouldFailInOpenApi3()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3NoMeaningfulUrl.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        assertThatThrownBy(() -> sut.createServiceDetails(metadataFile, openApiFile))
            .hasCauseInstanceOf(ODataGeneratorReadException.class);
    }

    @Test
    void testParsingWithNoServerUrlsShouldReturnDefaultServiceUrlInOpenApi3()
    {
        final File openApiFile = new File("src/test/resources/ServiceDetailsResolverTest/v3NoServersUrl.json");
        final File metadataFile = new File("src/test/resources/ServiceDetailsResolverTest/Baseline.edmx");

        final ServiceDetailsResolver sut = new ServiceDetailsResolver(StandardCharsets.UTF_8);
        final ServiceDetails serviceDetails = sut.createServiceDetails(metadataFile, openApiFile);

        assertThat(serviceDetails).isNotNull();
        assertThat(serviceDetails.getServiceUrl()).isEqualTo("/");
    }
}
