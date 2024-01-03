/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import java.util.List;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Instance of a OData batch request changeset, defined by one or many OData operations.
 */
@RequiredArgsConstructor
class BatchRequestChangeSet implements BatchRequestOperation
{
    @Getter( AccessLevel.PACKAGE )
    @Nonnull
    private final List<BatchRequestChangeSetOperation> operations;

    @Override
    public void addToRequestBuilder( @Nonnull final ODataRequestBatch requestBatch )
    {
        final ODataRequestBatch.Changeset requestChangeset = requestBatch.beginChangeset();
        for( final BatchRequestChangeSetOperation operation : getOperations() ) {
            operation.getChangeSetConsumer().accept(requestChangeset);
        }
        requestChangeset.endChangeset();
    }
}
