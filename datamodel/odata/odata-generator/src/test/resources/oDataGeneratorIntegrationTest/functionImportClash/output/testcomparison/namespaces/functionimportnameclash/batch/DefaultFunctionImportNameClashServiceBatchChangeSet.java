/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.functionimportnameclash.BP;


/**
 * Implementation of the {@link FunctionImportNameClashServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.FunctionImportNameClashService FunctionImportNameClashService}.
 * 
 */
public class DefaultFunctionImportNameClashServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<FunctionImportNameClashServiceBatch, FunctionImportNameClashServiceBatchChangeSet>
    implements FunctionImportNameClashServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.FunctionImportNameClashService service;

    DefaultFunctionImportNameClashServiceBatchChangeSet(
        @Nonnull
        final DefaultFunctionImportNameClashServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.FunctionImportNameClashService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultFunctionImportNameClashServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public FunctionImportNameClashServiceBatchChangeSet createBP(
        @Nonnull
        final BP bP) {
        return addRequestCreate(service::createBP, bP);
    }

    @Nonnull
    @Override
    public FunctionImportNameClashServiceBatchChangeSet updateBP(
        @Nonnull
        final BP bP) {
        return addRequestUpdate(service::updateBP, bP);
    }

    @Nonnull
    @Override
    public FunctionImportNameClashServiceBatchChangeSet deleteBP(
        @Nonnull
        final BP bP) {
        return addRequestDelete(service::deleteBP, bP);
    }

}
