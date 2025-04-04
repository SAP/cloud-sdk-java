package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import java.util.Map;

import com.sap.cloud.sdk.datamodel.openapi.generator.DataModelGenerator;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;

/**
 * Stores configuration parameters for the code generation performed by {@link DataModelGenerator}.
 */
@Value
@Builder
@SuppressWarnings( "cast" )
// Lombok @Builder generates an unnecessary cast for Map<String, String> (https://github.com/rzwitserloot/lombok/issues/1363)
public class GenerationConfiguration
{
    String inputSpec;
    String outputDirectory;
    String apiPackage;
    String modelPackage;

    @Builder.Default
    ApiMaturity apiMaturity = ApiMaturity.DEFAULT;

    @Builder.Default
    String copyrightHeader = "";

    /**
     * Indicates whether to use verbose output. <br>
     * <strong>Please note:</strong> This boolean property has no more effect.
     */
    @Builder.Default
    @Getter( AccessLevel.PUBLIC )
    boolean verbose = false;

    @Builder.Default
    @Getter( AccessLevel.NONE )
    boolean withSapCopyrightHeader = false;

    @Builder.Default
    @Getter( AccessLevel.NONE )
    boolean deleteOutputDirectory = false;

    @Singular( ignoreNullCollections = true )
    Map<String, String> additionalProperties;

    @Builder.Default
    boolean oneOfAnyOfGenerationEnabled = false;

    @Builder.Default
    boolean generateModels = true;

    @Builder.Default
    boolean generateApis = true;

    @Builder.Default
    boolean debugModels = false;

    /**
     * Indicates whether to use the default SAP copyright header for generated files.
     *
     * @return {@code true} if the default SAP copyright header should be used, {@code false} otherwise.
     */
    public boolean useSapCopyrightHeader()
    {
        return withSapCopyrightHeader;
    }

    /**
     * Indicates whether to delete the generated files from output directory prior to the generation.
     *
     * @return {@code true} if the generated files should be deleted from output directory before generating the OpenAPI
     *         client, {@code false} otherwise.
     */
    public boolean deleteOutputDirectory()
    {
        return deleteOutputDirectory;
    }
}
