/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.CREATE;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.DELETE;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.READ;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.READ_BY_KEY;
import static com.sap.cloud.sdk.datamodel.odata.generator.ApiFunction.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

class AllowedFunctionsResolverTest
{
    private static Edm readMetadata( final File edmxFile )
        throws Exception
    {
        try( InputStream stream = new FileInputStream(edmxFile) ) {
            return EntityProvider.readMetadata(stream, false);
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
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, swaggerFile, metadataFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ, READ_BY_KEY, CREATE, UPDATE, DELETE);
        assertThat(details.get("FullEntitySet")).containsOnly(UPDATE, DELETE);
    }

    @Test
    void testWithMissingSwaggerFile()
        throws Exception
    {
        final File swaggerFile = new File("some/non/existing/path.json");
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, swaggerFile, metadataFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ_BY_KEY, READ);
        assertThat(details.get("FullEntitySet")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE);
    }

    @Test
    void testWithoutSwaggerFile()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/Baseline.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null, metadataFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("SomeEntitySet", "FullEntitySet");
        assertThat(details.get("SomeEntitySet")).containsOnly(READ_BY_KEY, READ);
        assertThat(details.get("FullEntitySet")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE);
    }

    @Test
    void testWithInvalidAttributeText()
        throws Exception
    {
        final File metadataFile =
            new File("src/test/resources/AllowedFunctionsResolverTest/InvalidAttributeValue.edmx");
        final Edm metadata = readMetadata(metadataFile);

        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);

        assertThatExceptionOfType(ODataGeneratorReadException.class)
            .isThrownBy(() -> sut.findAllowedFunctions(metadata, null, metadataFile));
    }

    @Test
    void testReadSpecsWithIrrelevantAnnotationsOnFieldReferenceAndEntityReference()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/UnrelatedAnnotations.edmx");
        final Edm metadata = readMetadata(metadataFile);
        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null, metadataFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("TestEntity", "FieldValues");
        assertThat(details.get("TestEntity")).containsOnly(READ_BY_KEY, CREATE);
        assertThat(details.get("FieldValues")).containsOnly(READ_BY_KEY, CREATE);
    }

    @Test
    void testReadSpecsWithAnnotationsForASubsetOfEntities()
        throws Exception
    {
        final File metadataFile = new File("src/test/resources/AllowedFunctionsResolverTest/PartlyNoAnnotations.edmx");
        final Edm metadata = readMetadata(metadataFile);
        final AllowedFunctionsResolver sut = new AllowedFunctionsResolver(StandardCharsets.UTF_8);
        final Multimap<String, ApiFunction> details = sut.findAllowedFunctions(metadata, null, metadataFile);

        assertThat(details).isNotNull();
        assertThat(details.keys()).containsOnly("Config", "PageConfig", "SchemaSizes", "TableSizes", "UserConfig");
        assertThat(details.get("Config")).containsOnly(READ_BY_KEY);
        assertThat(details.get("PageConfig")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE);
        assertThat(details.get("SchemaSizes")).containsOnly(READ_BY_KEY, READ);
        assertThat(details.get("TableSizes")).containsOnly(READ_BY_KEY, READ);
        assertThat(details.get("UserConfig")).containsOnly(READ_BY_KEY, READ, CREATE, UPDATE, DELETE);
    }
}
