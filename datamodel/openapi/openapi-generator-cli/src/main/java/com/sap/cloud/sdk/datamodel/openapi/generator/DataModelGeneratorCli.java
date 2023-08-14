package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.google.common.annotations.Beta;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.ApiMaturity;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationConfiguration;
import com.sap.cloud.sdk.datamodel.openapi.generator.model.GenerationResult;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * CLI wrapper of the {@link DataModelGenerator}.
 */
@Beta
@Slf4j
public final class DataModelGeneratorCli
{
    static final String OPTION_HELP = "help";
    static final String OPTION_INPUT_SPEC = "input-spec";
    static final String OPTION_OUTPUT_DIR = "output-dir";
    static final String OPTION_DELETE_OUTPUT_DIR = "delete-output-dir";
    static final String OPTION_API_PACKAGE = "api-package";
    static final String OPTION_MODEL_PACKAGE = "model-package";
    static final String OPTION_API_MATURITY = "api-maturity";
    static final String OPTION_USE_SAP_COPYRIGHT = "use-sap-copyright";
    static final String OPTION_USER_COPYRIGHT = "user-copyright";
    static final String OPTION_VERBOSE = "verbose";
    static final String OPTION_ADDITIONAL_PROPERTIES = "additional-properties";
    private final Options options = new Options();

    DataModelGeneratorCli()
    {
        options.addOption("h", OPTION_HELP, false, "Print help.");

        options.addOption("i", OPTION_INPUT_SPEC, true, "Input specification file.");
        options.addOption("o", OPTION_OUTPUT_DIR, true, "Output directory.");
        options
            .addOption(
                "d",
                OPTION_DELETE_OUTPUT_DIR,
                false,
                "Delete the output directory before generating new classes.");
        options.addOption("a", OPTION_API_PACKAGE, true, "The API package name.");
        options.addOption("m", OPTION_MODEL_PACKAGE, true, "The model package name.");
        options.addOption(null, OPTION_API_MATURITY, true, "The maturity of the API. Default: " + ApiMaturity.DEFAULT);
        options
            .addOption("u", OPTION_USE_SAP_COPYRIGHT, false, "Use the default SAP copyright header. Default: " + false);
        options.addOption("c", OPTION_USER_COPYRIGHT, true, "Custom copyright header to use.");
        options.addOption("v", OPTION_VERBOSE, false, "Run with verbose logging. Default: " + false);

        options
            .addOption(
                "p",
                OPTION_ADDITIONAL_PROPERTIES,
                true,
                "Additional properties to be passed to the Java generator implementation in the format: key1=val1,key2=val2");
    }

    /**
     * Starts the generation of an OpenAPI VDM, based on the given arguments.
     *
     * @param args
     *            The command line arguments.
     */
    public static void main( @Nonnull final String[] args )
    {
        try {
            System.exit(new DataModelGeneratorCli().run(args)); // ALLOW EXIT
        }
        catch( final Exception e ) {
            log.error("[Error] Exception during generation.", e);
            System.exit(1); // ALLOW EXIT
        }
    }

    int run( @Nonnull final String[] args )
        throws ParseException
    {
        final CommandLine commandLine = getCommandLine(args);

        if( commandLine.hasOption(OPTION_HELP) ) {
            printHelp(options);
            return 0;
        }

        final GenerationConfiguration configuration = getGeneratorConfiguration(commandLine);
        final DataModelGenerator generator = new DataModelGenerator();

        final long generationStartTime = System.nanoTime();
        final Try<GenerationResult> maybeResult = generator.generateDataModel(configuration);
        final long generationEndTime = System.nanoTime();

        if( maybeResult.isFailure() ) {
            log.error("[Error] Exception during generation.", maybeResult.getCause());
            Arrays
                .stream(maybeResult.getCause().getSuppressed())
                .forEach(
                    suppressedCause -> log.error("[Error] Suppressed exception during generation.", suppressedCause));
            return 1;
        }

        log.info("---------------------------------------------------------------------");
        log
            .info(
                "[Info] Generation SUCCEEDED after {} seconds.",
                TimeUnit.NANOSECONDS.toSeconds(generationEndTime - generationStartTime));

        return 0;
    }

    @Nonnull
    CommandLine getCommandLine( @Nonnull final String[] args )
        throws ParseException
    {
        final CommandLineParser parser = new DefaultParser();

        try {
            return parser.parse(options, args);
        }
        catch( final UnrecognizedOptionException e ) {
            log.error("[Error] Unrecognized option: {}.", e.getOption());
            printHelp(options);
            throw e;
        }
    }

    private void printHelp( @Nonnull final Options options )
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar generator.jar", options, true);
    }

    @Nonnull
    GenerationConfiguration getGeneratorConfiguration( @Nonnull final CommandLine commandLine )
    {
        final GenerationConfiguration.GenerationConfigurationBuilder builder = GenerationConfiguration.builder();

        if( commandLine.hasOption(OPTION_INPUT_SPEC) ) {
            builder.inputSpec(commandLine.getOptionValue(OPTION_INPUT_SPEC));
        }

        if( commandLine.hasOption(OPTION_OUTPUT_DIR) ) {
            builder.outputDirectory(commandLine.getOptionValue(OPTION_OUTPUT_DIR));
        }

        if( commandLine.hasOption(OPTION_API_PACKAGE) ) {
            builder.apiPackage(commandLine.getOptionValue(OPTION_API_PACKAGE));
        }

        if( commandLine.hasOption(OPTION_MODEL_PACKAGE) ) {
            builder.modelPackage(commandLine.getOptionValue(OPTION_MODEL_PACKAGE));
        }

        builder.apiMaturity(ApiMaturity.getValueOrDefault(commandLine.getOptionValue(OPTION_API_MATURITY)));

        if( commandLine.hasOption(OPTION_VERBOSE) ) {
            builder.verbose(true);
        }

        if( commandLine.hasOption(OPTION_USE_SAP_COPYRIGHT) ) {
            if( commandLine.hasOption(OPTION_USER_COPYRIGHT) ) {
                log
                    .warn(
                        "[Warning] Cannot use the options {} and {} in conjunction. Ignoring the value of {}.",
                        OPTION_USE_SAP_COPYRIGHT,
                        OPTION_USER_COPYRIGHT,
                        OPTION_USER_COPYRIGHT);
            }

            builder.withSapCopyrightHeader(true);
        } else if( commandLine.hasOption(OPTION_USER_COPYRIGHT) ) {
            builder.copyrightHeader(commandLine.getOptionValue(OPTION_USER_COPYRIGHT));
        }

        if( commandLine.hasOption(OPTION_DELETE_OUTPUT_DIR) ) {
            builder.deleteOutputDirectory(true);
        }

        if( commandLine.hasOption(OPTION_ADDITIONAL_PROPERTIES) ) {
            final String[] properties = commandLine.getOptionValue(OPTION_ADDITIONAL_PROPERTIES).split(",");

            for( final String property : properties ) {
                final String[] keyAndValue = property.split("=", 2);
                if( keyAndValue.length < 2 ) {
                    log.error("Invalid parameter value \"{}\" for option: {}", property, OPTION_ADDITIONAL_PROPERTIES);
                    printHelp(options);
                    throw new IllegalArgumentException();
                }
                builder.additionalProperty(keyAndValue[0], keyAndValue[1]);
            }
        }

        return builder.build();
    }
}
