/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;

/**
 * Interface to provide execute methods.
 *
 */
public interface FluentHelperServiceBatchExecute
{
    /**
     * Executes the underlying batch query including the stored changeset operations.
     *
     * @param destination
     *            Destination object for resolving the {@code HttpClient} when executing the underlying OData query.
     *
     * @return A single result element, holding each changeset response.
     */
    @Nonnull
    BatchResponse executeRequest( @Nonnull final Destination destination );
}
