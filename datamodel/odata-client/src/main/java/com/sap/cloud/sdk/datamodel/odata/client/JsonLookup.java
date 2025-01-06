/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * An ordered collection of {@link JsonPath}.
 */
@Getter
@Value
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public class JsonLookup
{
    @Nonnull
    List<JsonPath> paths;

    /**
     * Static factory method for {@link JsonLookup}.
     *
     * @param paths
     *            An ordered enumeration of {@link JsonPath}, that is used for JSON lookup.
     * @return A new instance.
     */
    @Nonnull
    public static JsonLookup of( @Nonnull final JsonPath... paths )
    {
        return new JsonLookup(Arrays.asList(paths));
    }

    /**
     * Static factory method for an empty {@link JsonLookup}.
     *
     * @return A new instance.
     */
    @Nonnull
    public static JsonLookup empty()
    {
        return new JsonLookup(Collections.emptyList());
    }
}
