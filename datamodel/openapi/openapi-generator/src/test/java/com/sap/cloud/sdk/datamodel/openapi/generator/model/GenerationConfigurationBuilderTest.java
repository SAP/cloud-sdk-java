package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GenerationConfigurationBuilderTest
{
    @Test
    void testContainsDefaultValues()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec("/path/to/inputspec")
                .apiPackage("my.api.package")
                .modelPackage("my.model.package")
                .outputDirectory("/path/to/output/")
                .build();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.RELEASED);
        assertThat(configuration.isVerbose()).isFalse();
    }

    @Test
    void testOverrideDefaultValues()
    {
        final GenerationConfiguration configuration =
            GenerationConfiguration
                .builder()
                .inputSpec("/path/to/inputspec")
                .apiPackage("my.api.package")
                .modelPackage("my.model.package")
                .outputDirectory("/path/to/output/")
                .apiMaturity(ApiMaturity.BETA)
                .verbose(true)
                .build();

        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.BETA);
        assertThat(configuration.isVerbose());
    }
}
