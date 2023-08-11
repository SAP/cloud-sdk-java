/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;

import lombok.SneakyThrows;

public class DataModelGeneratorCliTest
{

    private static final String INPUT_SPEC = "myInputSpec.json";
    private static final String OUTPUT_DIR = "myOutputDir/";
    private static final String API_PACKAGE = "com.example.company.api";
    private static final String MODEL_PACKAGE = "com.example.company.model";
    private static final String API_MATURITY = "beta";
    private static final String USER_COPYRIGHT = "Copyright © 2021 Company";
    private static final String ADDITIONAL_PROPERTIES =
        "param1=val1,param2=val2=val2,param3=hello world?,useAbstractionForFiles=true";

    @Test
    @SneakyThrows
    public void testParseOptions()
    {
        // @formatter: off
        final String[] args =
            {
                "--" + DataModelGeneratorCli.OPTION_INPUT_SPEC,
                INPUT_SPEC,
                "--" + DataModelGeneratorCli.OPTION_OUTPUT_DIR,
                OUTPUT_DIR,
                "--" + DataModelGeneratorCli.OPTION_API_PACKAGE,
                API_PACKAGE,
                "--" + DataModelGeneratorCli.OPTION_MODEL_PACKAGE,
                MODEL_PACKAGE,
                "--" + DataModelGeneratorCli.OPTION_API_MATURITY,
                API_MATURITY,
                "--" + DataModelGeneratorCli.OPTION_USER_COPYRIGHT,
                USER_COPYRIGHT,
                "--" + DataModelGeneratorCli.OPTION_ADDITIONAL_PROPERTIES,
                ADDITIONAL_PROPERTIES,
                "--" + DataModelGeneratorCli.OPTION_DELETE_OUTPUT_DIR };
        // @formatter: on

        final DataModelGeneratorCli cli = new DataModelGeneratorCli();
        final CommandLine commandLine = cli.getCommandLine(args);
        final GenerationConfiguration configuration = cli.getGeneratorConfiguration(commandLine);

        assertThat(configuration.getInputSpec()).isEqualTo(INPUT_SPEC);
        assertThat(configuration.getOutputDirectory()).isEqualTo(OUTPUT_DIR);
        assertThat(configuration.getApiPackage()).isEqualTo(API_PACKAGE);
        assertThat(configuration.getModelPackage()).isEqualTo(MODEL_PACKAGE);
        assertThat(configuration.getApiMaturity()).isEqualTo(ApiMaturity.BETA);
        assertThat(configuration.useSapCopyrightHeader()).isFalse();
        assertThat(configuration.getCopyrightHeader()).isEqualTo(USER_COPYRIGHT);
        assertThat(configuration.isVerbose()).isFalse();
        assertThat(configuration.deleteOutputDirectory()).isTrue();
        assertThat(configuration.getAdditionalProperties())
            .containsEntry("param1", "val1")
            .containsEntry("param2", "val2=val2")
            .containsEntry("param3", "hello world?")
            .containsEntry("useAbstractionForFiles", "true");
    }

    @Test
    @SneakyThrows
    public void testPrintHelp()
    {
        final int exitCode = new DataModelGeneratorCli().run(new String[] { "--" + DataModelGeneratorCli.OPTION_HELP });

        assertThat(exitCode).isZero();
    }

    @Test
    @SneakyThrows
    public void testInvalidOption()
    {
        assertThatThrownBy(() -> new DataModelGeneratorCli().run(new String[] { "--invalid-option" }))
            .isExactlyInstanceOf(UnrecognizedOptionException.class);
    }

}
