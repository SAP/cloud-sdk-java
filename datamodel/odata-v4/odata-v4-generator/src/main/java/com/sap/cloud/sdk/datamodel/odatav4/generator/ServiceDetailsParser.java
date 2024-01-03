/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odatav4.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cloud.sdk.result.ElementNameGsonFieldNamingStrategy;
import com.sap.cloud.sdk.result.GsonResultElementFactory;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.result.ResultElementFactory;
import com.sap.cloud.sdk.result.ResultObject;

class ServiceDetailsParser
{
    // pattern to match a version string
    private static final String VERSION2_IDENTIFIER_PATTERN = Pattern.compile("^2\\.0$").pattern();
    // pattern to match a version string with a fixed major version and flexible minor and patch version
    private static final String VERSION3_IDENTIFIER_PATTERN = Pattern.compile("^3\\.\\d+\\.\\d+$").pattern();

    @Nonnull
    static ServiceDetails parse( @Nonnull final File serviceSwaggerFile, final Charset encoding )
    {
        final ResultElement serviceDetailsResult = readSwaggerJsonFile(serviceSwaggerFile, encoding);
        final Class<? extends ServiceDetails> implementationClass =
            determineServiceDetailsImplementation(serviceDetailsResult.getAsObject());
        return serviceDetailsResult.getAsObject().as(implementationClass);
    }

    private static Class<? extends ServiceDetails> determineServiceDetailsImplementation( final ResultObject object )
    {
        final ResultElement version3Identifier = object.get("openapi");
        if( version3Identifier != null ) {
            final String version = version3Identifier.asString();
            if( version.matches(VERSION3_IDENTIFIER_PATTERN) ) {
                return ServiceDetailsOpenApi3.class;
            }
        }

        final ResultElement version2Identifier = object.get("swagger");
        if( version2Identifier != null ) {
            final String version = version2Identifier.asString();
            if( version.matches(VERSION2_IDENTIFIER_PATTERN) ) {
                return ServiceDetailsSwagger2.class;
            }
        }
        throw new ODataGeneratorReadException(
            "The given swagger is no valid Swagger 2.0 (missing or invalid 'swagger' property) or OpenAPI 3.x.x (missing or invalid 'openapi' property).");
    }

    private static ResultElement readSwaggerJsonFile( @Nonnull final File serviceSwaggerFile, final Charset encoding )
    {
        try( Reader reader = new InputStreamReader(Files.newInputStream(serviceSwaggerFile.toPath()), encoding) ) {
            return parseResponse(reader);
        }
        catch( final IOException e ) {
            throw new ODataGeneratorReadException(e);
        }
    }

    /**
     * Parses the content of the given {@code InputStream} into a {@code ResultElement}.
     * <p>
     * The parser uses the information provided by the {@code ElementName} annotation.
     *
     * @param reader
     *            The file stream to parse.
     * @return The parsed {@code ResultElement}.
     */
    private static ResultElement parseResponse( final Reader reader )
    {
        final JsonElement responseElement = JsonParser.parseReader(reader);
        final JsonObject resultContainer = responseElement.getAsJsonObject();
        final ResultElementFactory<JsonElement> resultElementFactory = new GsonResultElementFactory(getGsonBuilder());
        return resultElementFactory.create(resultContainer);
    }

    private static GsonBuilder getGsonBuilder()
    {
        /* Create a GsonBuilder with the necessary configuration for the ServiceDetails. */
        return new GsonBuilder().setFieldNamingStrategy(new ElementNameGsonFieldNamingStrategy());
    }
}
