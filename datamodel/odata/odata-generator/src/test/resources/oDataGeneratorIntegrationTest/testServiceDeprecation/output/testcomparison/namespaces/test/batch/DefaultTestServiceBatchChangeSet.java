/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntitySingleLink;
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
    @SuppressWarnings("deprecation")
    private final testcomparison.services.TestService service;

    @SuppressWarnings("deprecation")
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
    public TestServiceBatchChangeSet createTestEntityV2(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestCreate(service::createTestEntityV2, testEntityV2);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestCreate(service::createTestEntityMultiLink, testEntityMultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return addRequestCreate(service::createTestEntityOtherMultiLink, testEntityOtherMultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return addRequestCreate(service::createTestEntityLvl2MultiLink, testEntityLvl2MultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return addRequestUpdate(service::updateTestEntityLvl2MultiLink, testEntityLvl2MultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return addRequestCreate(service::createTestEntitySingleLink, testEntitySingleLink);
    }

}
