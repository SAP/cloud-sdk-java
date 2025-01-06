/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.generator;

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.vavr.control.Option;
import lombok.Getter;

class EdmxFile
{
    @Getter
    @Nonnull
    private final Path filePath;

    @Nullable
    private final Service service;

    @Getter
    private boolean successfullyGenerated;

    EdmxFile( @Nonnull final Path filePath, @Nullable final Service service )
    {
        this.filePath = filePath;
        this.service = service;
    }

    Option<Service> getService()
    {
        return Option.of(service);
    }

    boolean isSuccessfullyParsed()
    {
        return service != null;
    }

    Option<String> getServiceName()
    {
        return getService().isDefined() ? getService().map(Service::getName) : Option.none();
    }

    void setSuccessfullyGenerated()
    {
        successfullyGenerated = true;
    }
}
