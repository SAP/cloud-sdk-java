/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Joiner;
import com.sap.cloud.sdk.datamodel.openapi.generator.exception.OpenApiGeneratorException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

class OpenApiSpecUtil
{
    @RequiredArgsConstructor
    @Getter
    enum FileFormat
    {
        YAML(Arrays.asList("yaml", "yml"), () -> new ObjectMapper(new YAMLFactory())),
        JSON(Collections.singletonList("json"), ObjectMapper::new);

        private final List<String> fileExtensions;
        private final Supplier<ObjectMapper> objectMapperSupplier;
    }

    static FileFormat getFileFormat( @Nonnull final Path inputSpec )
    {
        final String fileName = inputSpec.getFileName().toString().toLowerCase(Locale.ENGLISH);

        for( final FileFormat fileFormat : FileFormat.values() ) {
            if( fileFormat.fileExtensions.stream().anyMatch(fileName::endsWith) ) {
                return fileFormat;
            }
        }

        throw new OpenApiGeneratorException(
            "Could not determine file format of input specification "
                + inputSpec
                + " . Supported file formats: "
                + Joiner
                    .on(",")
                    .join(
                        Arrays
                            .stream(FileFormat.values())
                            .map(FileFormat::getFileExtensions)
                            .collect(Collectors.toList())));
    }

    static JsonNode getJsonNodeFromInputSpec( @Nonnull final ObjectMapper objectMapper, @Nonnull final Path inputSpec )
        throws IOException
    {
        if( Files.exists(inputSpec) ) {
            return objectMapper.readTree(inputSpec.toFile());
        } else {
            try(
                InputStream inputStream =
                    ClassLoader.getSystemClassLoader().getResourceAsStream(inputSpec.toString()) ) {
                if( inputStream != null ) {
                    return objectMapper.readTree(inputStream);
                }
            }
        }
        throw new IOException("File or resource " + inputSpec + " doesn't exist.");
    }
}
