/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.metadata.generator;

import javax.annotation.Nonnull;

import com.google.common.annotations.Beta;

import lombok.Builder;
import lombok.Value;

/**
 * Coordinate for a Maven module, consisting of group id and artifact id.
 */
@Value
@Builder
@Beta
public class MavenCoordinate
{
    @Nonnull
    String groupId;
    @Nonnull
    String artifactId;

    @Override
    @Nonnull
    public String toString()
    {
        return groupId + ":" + artifactId;
    }
}
