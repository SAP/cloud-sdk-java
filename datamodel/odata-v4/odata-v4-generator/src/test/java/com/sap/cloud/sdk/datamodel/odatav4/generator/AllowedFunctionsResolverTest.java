/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.CREATE;
import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.DELETE;
import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.NAVIGATE;
import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.READ;
import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.READ_BY_KEY;
import static com.sap.cloud.sdk.datamodel.odatav4.generator.ApiFunction.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.serialization.ClientODataDeserializer;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

class AllowedFunctionsResolverTest
{
    private static Edm readMetadata( final File edmxFile )
        throws Exception
    {
        final List<CsdlSchema> termFiles = ODataToVdmGenerator.loadEdmxSchemas();
        final ODataClient client = ODataClientFactory.getClient();
        final ClientODataDeserializer deserializer = client.getDeserializer(ContentType.APPLICATION_XML);
        try( final InputStream stream = Files.newInputStream(edmxFile.toPath()) ) {
            final Map<String, CsdlSchema> schemas = deserializer.toMetadata(stream).getSchemaByNsOrAlias();
            return client.getReader().readMetadata(schemas, termFiles);
        }
    }

    @Test
    void testValidSwaggerFile()
        throws Exception
    {
        final File swaggerFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.json");
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, swaggerFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(NAVIGATE, READ, READ_BY_KEY, CREATE, UPDATE, DELETE);
        assertThat(details.get("FullEntitySet")).containsOnly(NAVIGATE, UPDATE, DELETE);
    }

    @Test
    void testWithMissingSwaggerFile()
        throws Exception
    {
        final File swaggerFile = new File("some/non/existing/path.json");
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, swaggerFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ_BY_KEY, READ, NAVIGATE);
        assertThat(details.get("FullEntitySet")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE, NAVIGATE);
    }

    @Test
    void testWithoutSwaggerFile()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ_BY_KEY, READ, NAVIGATE);
        assertThat(details.get("FullEntitySet")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE, NAVIGATE);
    }

    @Test
    void testWithInvalidAttributeText()
        throws Exception
    {
        final File metadataFile =
            new File("src/test/resources/AllowedFunctionsResolverTest/InvalidAttributeValue.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE, NAVIGATE);
    }

    @Test
    void testReadSpecsWithIrrelevantAnnotationsOnFieldReferenceAndEntityReference()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/UnrelatedAnnotations.edmx");
        final Edm metadata = readMetadata(metadataFile);
        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("TestEntity", "FieldValues");
        assertThat(details.get("TestEntity")).containsOnly(READ_BY_KEY, CREATE, NAVIGATE);
        assertThat(details.get("FieldValues")).containsOnly(READ_BY_KEY, CREATE, NAVIGATE);
    }

    @Test
    void testReadSpecsWithAnnotationsForASubsetOfEntities()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/PartlyNoAnnotations.edmx");
        final Edm metadata = readMetadata(metadataFile);
        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("Config", "PageConfig", "SchemaSizes", "TableSizes", "UserConfig");
        assertThat(details.get("Config")).containsOnly(READ_BY_KEY, READ, NAVIGATE);
        assertThat(details.get("PageConfig")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE, NAVIGATE);
        assertThat(details.get("SchemaSizes")).containsOnly(READ_BY_KEY, READ, NAVIGATE, UPDATE);
        assertThat(details.get("TableSizes")).containsOnly(READ_BY_KEY, READ, NAVIGATE);
        assertThat(details.get("UserConfig")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE, NAVIGATE);
    }
}
