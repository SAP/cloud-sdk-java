/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchFluentHelperBasic;


/**
 * Default implementation of the {@link TestServiceBatch} interface exposed in the {@link testcomparison.services.TestService TestService}, allowing you to create multiple changesets and finally execute the batch request.
 * 
 */
public class DefaultTestServiceBatch
    extends BatchFluentHelperBasic<TestServiceBatch, TestServiceBatchChangeSet>
    implements TestServiceBatch
{

    @Nonnull
    private final testcomparison.services.TestService service;
    @Nonnull
    private final String servicePath;

    /**
     * Creates a new instance of this DefaultTestServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     */
    public DefaultTestServiceBatch(
        @Nonnull
        final testcomparison.services.TestService service) {
        this(service, testcomparison.services.TestService.DEFAULT_SERVICE_PATH);
    }

    /**
     * Creates a new instance of this DefaultTestServiceBatch.
     * 
     * @param service
     *     The service to execute all operations in this changeset on.
     * @param servicePath
     *     The custom service path to operate on.
     */
    public DefaultTestServiceBatch(
        @Nonnull
        final testcomparison.services.TestService service,
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
    protected DefaultTestServiceBatch getThis() {
        return this;
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet beginChangeSet() {
        return new DefaultTestServiceBatchChangeSet(this, service);
    }

}
