/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.minimalmetadata.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link MinimalMetadataServiceBatch} interface exposed in the {@link testcomparison.services.MinimalMetadataService MinimalMetadataService}, allowing you to create multiple changesets and finally execute the batch request.
 * 
 */
public class DefaultMinimalMetadataServiceBatch
    extends BatchFluentHelperBasic<MinimalMetadataServiceBatch, MinimalMetadataServiceBatchChangeSet>
    implements MinimalMetadataServiceBatch
{

    @Nonnull
    @SuppressWarnings("deprecation")
    private final testcomparison.services.MinimalMetadataService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultMinimalMetadataServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    @SuppressWarnings("deprecation")
    public DefaultMinimalMetadataServiceBatch(
        @Nonnull
        final testcomparison.services.MinimalMetadataService service) {
        this(service, testcomparison.services.MinimalMetadataService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultMinimalMetadataServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    @SuppressWarnings("deprecation")
    public DefaultMinimalMetadataServiceBatch(
        @Nonnull
        final testcomparison.services.MinimalMetadataService service,
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
    protected DefaultMinimalMetadataServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public MinimalMetadataServiceBatchChangeSet beginChangeSet() {
        return new DefaultMinimalMetadataServiceBatchChangeSet(this, service);
    }

}
