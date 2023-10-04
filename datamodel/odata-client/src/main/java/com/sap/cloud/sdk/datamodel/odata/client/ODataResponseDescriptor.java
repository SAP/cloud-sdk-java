/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.client;

import javax.annotation.Nonnull;

/**
 * Descriptor for protocol specific information on deserializing OData responses.
 */
public interface ODataResponseDescriptor
{
    /**
     * The JSON path(s) to a set of result elements. The last element of this array refers to the result array.
     *
     * @return The path(s) to result set.
     */
    @Nonnull
    JsonLookup getPathToResultSet();

    /**
     * The JSON path(s) to a result object. The last element of this array refers to the result object.
     *
     * @return The path(s) to single result.
     */
    @Nonnull
    JsonLookup getPathToResultSingle();

    /**
     * The JSON path(s) to a result primitive. The last element of this array refers to the result value.
     *
     * @return The path(s) to primitive result.
     */
    @Nonnull
    JsonLookup getPathToResultPrimitive();

    /**
     * The JSON path(s) to an inline count. The last element of this array refers to the result value.
     *
     * @return The path(s) to inline count value.
     */
    @Nonnull
    JsonLookup getPathToInlineCount();

    /**
     * The JSON path(s) to the next link of a multi page response. The last element of this array refers to the result
     * value.
     *
     * @return The path(s) to inline count value.
     */
    @Nonnull
    JsonLookup getPathToNextLink();

    /**
     * The JSON path(s) to the delta link of a versioned response. The last element of this array refers to the result
     * value.
     *
     * @return The path(s) to inline count value.
     */
    @Nonnull
    JsonLookup getPathToDeltaLink();
}
