/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestAction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestDelete;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestGeneric;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestUpdate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperFunction;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;

import lombok.RequiredArgsConstructor;

/**
 * Representation of any changeset in a OData batch request as a fluent interface.
 *
 * @param <FluentHelperBatchT>
 *            The fluent helper type of the associated OData batch request.
 * @param <ThisT>
 *            The current type of the implementing fluent helper instance.
 */
@RequiredArgsConstructor
public abstract class BatchChangeSetFluentHelperBasic<FluentHelperBatchT extends FluentHelperServiceBatch<?, ?>, ThisT>
    implements
    FluentHelperBatchEndChangeSet<FluentHelperBatchT>
{
    private final FluentHelperBatchT typedBatchFluentHelper;
    private final BatchFluentHelperBasic<?, ?> basicBatchFluentHelper;

    private final List<BatchRequestChangeSetOperation> operations = new ArrayList<>();

    /**
     * Method to safely return the current fluent helper instance upon public method calls.
     *
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected abstract ThisT getThis();

    /**
     * Add a delete operation to the current changeset request.
     *
     * @param serviceMethod
     *            The method of a delete fluent helper from the given service class.
     * @param entity
     *            The entity instance to be deleted.
     * @param <EntityT>
     *            The entity type to guarantee type safety.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected <EntityT extends VdmEntity<EntityT>> ThisT addRequestDelete(
        final Function<EntityT, FluentHelperDelete<?, EntityT>> serviceMethod,
        final EntityT entity )
    {
        final FluentHelperDelete<?, EntityT> delete = serviceMethod.apply(entity);
        return addRequest(delete);
    }

    /**
     * Adds a delete operation to the current changeset request.
     *
     * @param deleteRequest
     *            The {@link FluentHelperDelete} that represents the delete operation.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected ThisT addRequest( @Nonnull final FluentHelperDelete<?, ?> deleteRequest )
    {
        final ODataRequestDelete request = deleteRequest.toRequest();
        basicBatchFluentHelper.requestMapping.put(deleteRequest, request);

        final BatchRequestChangeSetOperation operation =
            new BatchRequestChangeSetOperation(( changeSet ) -> changeSet.addDelete(request), deleteRequest);

        operations.add(operation);
        return getThis();
    }

    /**
     * Add a create operation to the current changeset request.
     *
     * @param serviceMethod
     *            The method of a create fluent helper from the given service class.
     * @param entity
     *            The entity instance to be created.
     * @param <EntityT>
     *            The entity type to guarantee type safety.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected <EntityT extends VdmEntity<EntityT>> ThisT addRequestCreate(
        final Function<EntityT, FluentHelperCreate<?, EntityT>> serviceMethod,
        final EntityT entity )
    {
        final FluentHelperCreate<?, EntityT> create = serviceMethod.apply(entity);
        return addRequest(create);
    }

    /**
     * Adds a create operation to the current changeset request.
     *
     * @param createRequest
     *            The {@link FluentHelperCreate} that represents the create operation.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected ThisT addRequest( @Nonnull final FluentHelperCreate<?, ?> createRequest )
    {
        final ODataRequestCreate request = createRequest.toRequest();
        basicBatchFluentHelper.requestMapping.put(createRequest, request);

        final BatchRequestChangeSetOperation operation =
            new BatchRequestChangeSetOperation(( changeSet ) -> changeSet.addCreate(request), createRequest);

        operations.add(operation);
        return getThis();
    }

    /**
     * Add an update operation to the current changeset request.
     *
     * @param serviceMethod
     *            The method of an update fluent helper from the given service class.
     * @param entity
     *            The entity instance to be updated.
     * @param <EntityT>
     *            The entity type to guarantee type safety.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected <EntityT extends VdmEntity<EntityT>> ThisT addRequestUpdate(
        final Function<EntityT, FluentHelperUpdate<?, EntityT>> serviceMethod,
        final EntityT entity )
    {
        final FluentHelperUpdate<?, EntityT> update = serviceMethod.apply(entity);
        return addRequest(update);
    }

    /**
     * Adds an update operation to the current changeset request.
     *
     * @param updateRequest
     *            The {@link FluentHelperUpdate} that represents the update operation.
     * @return The current fluent helper instance.
     */
    @Nonnull
    protected ThisT addRequest( @Nonnull final FluentHelperUpdate<?, ?> updateRequest )
    {
        final ODataRequestUpdate request = updateRequest.toRequest();
        basicBatchFluentHelper.requestMapping.put(updateRequest, request);

        final BatchRequestChangeSetOperation operation =
            new BatchRequestChangeSetOperation(( changeSet ) -> changeSet.addUpdate(request), updateRequest);

        operations.add(operation);
        return getThis();
    }

    @Nonnull
    @Override
    public FluentHelperBatchT endChangeSet()
    {
        final BatchRequestChangeSet changeSet = new BatchRequestChangeSet(operations);
        basicBatchFluentHelper.addChangeSet(changeSet);
        return typedBatchFluentHelper;
    }

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
    public ThisT addFunctionImport( @Nonnull final FluentHelperFunction<?, ?, ?> functionImport )
    {
        final ODataRequestGeneric request = functionImport.toRequest();
        basicBatchFluentHelper.requestMapping.put(functionImport, request);

        final Consumer<ODataRequestBatch.Changeset> changesetConsumer;

        if( request instanceof ODataRequestAction ) {
            changesetConsumer = changeset -> changeset.addAction((ODataRequestAction) request);
        } else {
            throw new IllegalStateException(
                "Request for function imports in batch change sets must be "
                    + ODataRequestAction.class.getSimpleName()
                    + ", but was "
                    + request.getClass().getSimpleName()
                    + ". Only function imports using HTTP POST are allowed.");
        }

        final BatchRequestChangeSetOperation operation =
            new BatchRequestChangeSetOperation(changesetConsumer, functionImport);

        operations.add(operation);

        return getThis();
    }
}
