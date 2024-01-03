/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link FunctionImportNameClashServiceBatch} interface exposed in the {@link testcomparison.services.FunctionImportNameClashService FunctionImportNameClashService}, allowing you to create multiple changesets and finally execute the batch request.
 * 
 */
public class DefaultFunctionImportNameClashServiceBatch
    extends BatchFluentHelperBasic<FunctionImportNameClashServiceBatch, FunctionImportNameClashServiceBatchChangeSet>
    implements FunctionImportNameClashServiceBatch
{

    @Nonnull
    private final testcomparison.services.FunctionImportNameClashService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultFunctionImportNameClashServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultFunctionImportNameClashServiceBatch(
        @Nonnull
        final testcomparison.services.FunctionImportNameClashService service) {
        this(service, testcomparison.services.FunctionImportNameClashService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultFunctionImportNameClashServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultFunctionImportNameClashServiceBatch(
        @Nonnull
        final testcomparison.services.FunctionImportNameClashService service,
        @Nonnull
        final String servicePath) {
        this.service = service;
        this.servicePath = servicePath;
    }

    @Nonnull
    @Override
    protected String getServicePathForBatchRequest() {
        return servicePath;
    }

    @Nonnull
    @Override
    protected DefaultFunctionImportNameClashServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public FunctionImportNameClashServiceBatchChangeSet beginChangeSet() {
        return new DefaultFunctionImportNameClashServiceBatchChangeSet(this, service);
    }

}
