/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nullable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

class GenerationConfigurationConverterTest
{
    @TempDir
    Path outputDirectory = null;

    @Test
    void testCopyrightHeaderResolution()
    {
        final String customHeader = "Copyright (c) by me, myself and I.";
        final GenerationConfiguration configWithSapHeader = createBasicConfig().withSapCopyrightHeader(true).build();
        final GenerationConfiguration configWithCustomHeader =
            createBasicConfig().copyrightHeader(customHeader).build();
        final GenerationConfiguration configNoHeader = createBasicConfig().build();

        final String resultSapHeader = getCopyrightHeaderFromConfig(configWithSapHeader);
        final String resultCustomHeader = getCopyrightHeaderFromConfig(configWithCustomHeader);
        final String resultNoHeader = getCopyrightHeaderFromConfig(configNoHeader);

        assertThat(resultSapHeader).isEqualTo(GenerationConfigurationConverter.SAP_COPYRIGHT_HEADER);
        assertThat(resultCustomHeader).isEqualTo(customHeader);
        assertThat(resultNoHeader).isNull();
    }

    private GenerationConfiguration.GenerationConfigurationBuilder createBasicConfig()
    {
        return GenerationConfiguration
            .builder()
            .apiPackage("package")
            .modelPackage("package")
            .inputSpec("src/test/resources/" + DataModelGeneratorUnitTest.class.getSimpleName() + "/sodastore.yaml")
            .outputDirectory(outputDirectory.toAbsolutePath().toString());
    }

    @Nullable
    @SuppressWarnings( "deprecation" )
    private String getCopyrightHeaderFromConfig( final GenerationConfiguration config )
    {
        final Object maybeHeader =
            GenerationConfigurationConverter
                .convertGenerationConfiguration(config, Paths.get(config.getInputSpec()))
                .getConfig()
                .additionalProperties()
                .get(GenerationConfigurationConverter.COPYRIGHT_PROPERTY_KEY);
        return maybeHeader != null ? maybeHeader.toString() : null;
    }
}
