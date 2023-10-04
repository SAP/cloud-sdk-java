/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * A simple JSON Path.
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
@Value
public class JsonPath
{
    static String WILDCARD = "*";

    @Nonnull
    List<String> nodes;

    /**
     * Static factory method for {@link JsonPath}.
     *
     * @param nodes
     *            An ordered enumeration of node names to form a JSON path.
     * @return A new instance.
     */
    @Nonnull
    public static JsonPath of( @Nonnull final String... nodes )
    {
        return new JsonPath(Arrays.asList(nodes));
    }

    /**
     * Static factory method for an empty {@link JsonPath}. Pointing at the root JSON tree.
     *
     * @return A new instance.
     */
    @Nonnull
    public static JsonPath ofRoot()
    {
        return new JsonPath(Collections.emptyList());
    }
}
