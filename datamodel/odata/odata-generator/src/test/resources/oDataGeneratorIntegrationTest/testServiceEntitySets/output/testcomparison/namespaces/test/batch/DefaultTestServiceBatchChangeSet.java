/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.test.TestEntityV2;


/**
 * Implementation of the {@link TestServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.TestService TestService}.
 * 
 */
public class DefaultTestServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<TestServiceBatch, TestServiceBatchChangeSet>
    implements TestServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.TestService service;

    DefaultTestServiceBatchChangeSet(
        @Nonnull
        final DefaultTestServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.TestService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultTestServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestCreate(service::createTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestUpdate(service::updateTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestDelete(service::deleteTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestCreate(service::createOtherTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestUpdate(service::updateOtherTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteOtherTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestDelete(service::deleteOtherTestEntity, testEntityV2);
    }

}
