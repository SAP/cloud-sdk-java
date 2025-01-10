package com.sap.cloud.sdk.datamodel.odata.helper.batch;

import javax.annotation.Nonnull;

/**
 * Interface to finish the definition of a single changeset.
 *
 * @param <FluentHelperT>
 *            Type of the implementing OData batch request class
 */
public interface FluentHelperBatchEndChangeSet<FluentHelperT>
{
    /**
     * Finish the definition of a single changeset and return to the parent OData batch request instance. All changesets
     * will be evaluated independently from each other.
     *
     * @return The current OData batch request instance.
     */
    @Nonnull
    FluentHelperT endChangeSet();
}
