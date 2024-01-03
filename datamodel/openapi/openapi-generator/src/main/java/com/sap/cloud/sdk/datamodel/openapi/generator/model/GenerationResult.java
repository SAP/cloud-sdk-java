/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.openapi.generator.model;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.openapi.generator.DataModelGenerator;

import io.vavr.control.Option;
import lombok.Getter;

/**
 * Stores the result of the code generation performed by {@link DataModelGenerator}.
 */
public class GenerationResult
{
    @Getter
    private final List<File> generatedFiles;
    @Nullable
    private final String serviceName;

    /**
     * Creates a {@link GenerationResult} based on a given list of generated files.
     *
     * @param generatedFiles
     *            The files resulting from the code generation
     * @param serviceName
     *            The name of the service, if present.
     */
    public GenerationResult( @Nonnull final List<File> generatedFiles, @Nullable final String serviceName )
    {
        this.generatedFiles = Collections.unmodifiableList(generatedFiles);
        this.serviceName = serviceName;
    }

    /**
     * Get the service name, if present.
     *
     * @return An {@link Option} containing the service name, if present.
     */
    @Nonnull
    public Option<String> getServiceName()
    {
        return Option.of(serviceName);
    }
}
