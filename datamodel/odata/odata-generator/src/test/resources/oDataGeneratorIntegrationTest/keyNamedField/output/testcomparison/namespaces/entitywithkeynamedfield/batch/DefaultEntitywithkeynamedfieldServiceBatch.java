/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link EntitywithkeynamedfieldServiceBatch} interface exposed in the {@link testcomparison.services.EntitywithkeynamedfieldService EntitywithkeynamedfieldService}, allowing you to create multiple changesets and finally execute the batch request.
 * 
 */
public class DefaultEntitywithkeynamedfieldServiceBatch
    extends BatchFluentHelperBasic<EntitywithkeynamedfieldServiceBatch, EntitywithkeynamedfieldServiceBatchChangeSet>
    implements EntitywithkeynamedfieldServiceBatch
{

    @Nonnull
    private final testcomparison.services.EntitywithkeynamedfieldService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultEntitywithkeynamedfieldServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultEntitywithkeynamedfieldServiceBatch(
        @Nonnull
        final testcomparison.services.EntitywithkeynamedfieldService service) {
        this(service, testcomparison.services.EntitywithkeynamedfieldService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultEntitywithkeynamedfieldServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultEntitywithkeynamedfieldServiceBatch(
        @Nonnull
        final testcomparison.services.EntitywithkeynamedfieldService service,
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
    protected DefaultEntitywithkeynamedfieldServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet beginChangeSet() {
        return new DefaultEntitywithkeynamedfieldServiceBatchChangeSet(this, service);
    }

}
