package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestBatch;

/**
 * Common interface for items on root level of an OData batch request.
 */
interface BatchRequestOperation
{
    void addToRequestBuilder( @Nonnull final ODataRequestBatch requestBatch );
}
