/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.CollectionValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.helper.SingleValuedFluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;

import io.vavr.control.Try;

/**
 * Interface to access the OData batch response.
 */
public interface BatchResponse extends AutoCloseable
{
    /**
     * Get the result for a single changeset.
     *
     * @param index
     *            The zero-based index of the selected changeset.
     * @return A wrapper of the changeset result. It can be checked for errors upon evaluation.
     */
    @Nonnull
    Try<BatchResponseChangeSet> get( int index );

    /**
     * Convenience method to get the result for a read request on the OData batch response.
     *
     * @param helper
     *            The original fluent helper instance that was used to create the request.
     * @param <EntityT>
     *            The generic entity type.
     * @return A list of entities according to the original request.
     */
    @Nonnull
    default <EntityT extends VdmEntity<?>> List<EntityT> getReadResult(
        @Nonnull final FluentHelperRead<?, EntityT, ?> helper )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method to get the result for a read-by-key request on the OData batch response.
     *
     * @param helper
     *            The original fluent helper instance that was used to create the request.
     * @param <EntityT>
     *            The generic entity type.
     * @return A single entity according to the original request.
     */
    @Nonnull
    default <
        EntityT extends VdmEntity<?>> EntityT getReadResult( @Nonnull final FluentHelperByKey<?, EntityT, ?> helper )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method to get the result for a function import request that returns a single primitive value or
     * entity on the OData batch response.
     *
     * @param helper
     *            The original fluent helper instance that was used to create the request.
     * @param <ResultT>
     *            The result type of the function import request
     * @return A single primitive value or entity according to the original request
     */
    @Nonnull
    default <ResultT> ResultT getReadResult( @Nonnull final SingleValuedFluentHelperFunction<?, ResultT, ?> helper )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Convenience method to get the result for a function import request that returns a collection of primitive values
     * or entities on the OData batch response.
     *
     * @param helper
     *            The original fluent helper instance that was used to create the request.
     * @param <ResultT>
     *            The result type of the function import request
     * @return A collection of primitive values or entities according to the original request
     */
    @Nonnull
    default <ResultT> List<ResultT> getReadResult(
        @Nonnull final CollectionValuedFluentHelperFunction<?, ResultT, ?> helper )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Closes the underlying HTTP response entity.
     *
     * @since 4.15.0
     */
    @Override
    void close();
}
