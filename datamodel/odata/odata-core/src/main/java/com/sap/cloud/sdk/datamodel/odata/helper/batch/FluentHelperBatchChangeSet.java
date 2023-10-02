/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;

/**
 * Contains methods applicable to enrich the currently open change set.
 *
 * @param <FluentHelperT>
 *            The same fluent helper
 */
public interface FluentHelperBatchChangeSet<FluentHelperT>
{
    /**
     * Adds a function import call to the currently opened changeset. Only use {@code executeRequest} to issue the batch
     * request.
     *
     * @param functionImport
     *            The {@link FluentHelperFunction} that represents the function import call
     * @return The same fluent helper
     * @throws IllegalStateException
     *             If the batch request contains a function import call within a change set which uses the HTTP GET
     *             method.
     */
    @Nonnull
    FluentHelperT addFunctionImport( @Nonnull final FluentHelperFunction<?, ?, ?> functionImport );
}
