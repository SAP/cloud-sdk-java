/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperModification;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;

/**
 * Interface to the batch object of an OData service.
 *
 * @param <FluentHelperT>
 *            Type of the implementing OData batch request class
 * @param <FluentHelperChangeSetT>
 *            Type of the associated changeset class
 */
public interface FluentHelperServiceBatch<FluentHelperT, FluentHelperChangeSetT extends FluentHelperBatchEndChangeSet<FluentHelperT>>
    extends
    FluentHelperServiceBatchExecute
{
    /**
     * Method to define a new OData batch changeset. Modifying operations on entities in a batch request can only be
     * done as part of a changeset. All changesets will be evaluated independently from each other.
     *
     * @return A new instance of a batch changeset associated with the OData service.
     */
    @Nonnull
    FluentHelperChangeSetT beginChangeSet();

    /**
     * Add read request to the OData batch request builder.
     *
     * @param readOperations
     *            A var-arg array of read operations.
     * @return The current OData batch request instance.
     */
    @Nonnull
    FluentHelperT addReadOperations( @Nonnull final FluentHelperRead<?, ?, ?>... readOperations );

    /**
     * Adds a single OData batch changeset that includes all of the given data modification requests. All changesets
     * will be evaluated independently from each other.
     *
     * @param modifications
     *            The data modification requests to be performed as part of a single changeset.
     * @return The current OData batch request instance.
     */
    @Nonnull
    FluentHelperT addChangeSet( @Nonnull final FluentHelperModification<?, ?>... modifications );

    /**
     * Add read-by-key request to the OData batch request builder.
     *
     * @param readByKeyOperations
     *            A var-arg array of read operations.
     * @return The current OData batch request instance.
     */
    @Nonnull
    FluentHelperT addReadOperations( @Nonnull final FluentHelperByKey<?, ?, ?>... readByKeyOperations );

    /**
     * Add function requests to the OData batch request builder.Only functions that use GET can be added.
     *
     * @param functionOperations
     *            A var-arg array of function operations.
     * @return The current OData batch request instance
     */
    @Nonnull
    FluentHelperT addReadOperations( @Nonnull final FluentHelperFunction<?, ?, ?>... functionOperations );
}
