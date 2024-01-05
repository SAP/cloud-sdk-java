/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link NameClashServiceBatch} interface exposed in the {@link testcomparison.services.NameClashService NameClashService}, allowing you to create multiple changesets and finally execute the batch request.
 * 
 */
public class DefaultNameClashServiceBatch
    extends BatchFluentHelperBasic<NameClashServiceBatch, NameClashServiceBatchChangeSet>
    implements NameClashServiceBatch
{

    @Nonnull
    private final testcomparison.services.NameClashService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultNameClashServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultNameClashServiceBatch(
        @Nonnull
        final testcomparison.services.NameClashService service) {
        this(service, testcomparison.services.NameClashService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultNameClashServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultNameClashServiceBatch(
        @Nonnull
        final testcomparison.services.NameClashService service,
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
    protected DefaultNameClashServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet beginChangeSet() {
        return new DefaultNameClashServiceBatchChangeSet(this, service);
    }

}
