/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

/**
 * Interface to expose the batch feature for service class.
 *
 * @param <FluentHelperBatchT>
 *            The type of the Batch instance.
 */
public interface BatchService<FluentHelperBatchT>
{
    /**
     * Instantiate a new FluentHelper instance for a single OData batch request.
     *
     * @return A new instance of an OData batch request associated with the service object.
     */
    @Nonnull
    FluentHelperBatchT batch();
}
