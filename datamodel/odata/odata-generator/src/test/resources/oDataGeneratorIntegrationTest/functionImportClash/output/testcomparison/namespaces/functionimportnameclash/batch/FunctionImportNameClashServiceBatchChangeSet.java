/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.functionimportnameclash.BP;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.FunctionImportNameClashService FunctionImportNameClashService}.
 * 
 */
public interface FunctionImportNameClashServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<FunctionImportNameClashServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<FunctionImportNameClashServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    FunctionImportNameClashServiceBatchChangeSet createBP(
        @Nonnull
        final BP bP);

    /**
     * Update an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity and save it to the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    FunctionImportNameClashServiceBatchChangeSet updateBP(
        @Nonnull
        final BP bP);

    /**
     * Deletes an existing {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity in the S/4HANA system.
     * 
     * @param bP
     *     {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    FunctionImportNameClashServiceBatchChangeSet deleteBP(
        @Nonnull
        final BP bP);

}
