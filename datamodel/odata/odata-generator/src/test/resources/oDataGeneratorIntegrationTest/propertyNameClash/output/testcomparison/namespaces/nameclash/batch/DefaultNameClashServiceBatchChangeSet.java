/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.nameclash.TestEntityMultiLink;
import testcomparison.namespaces.nameclash.TestEntityV2;


/**
 * Implementation of the {@link NameClashServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.NameClashService NameClashService}.
 * 
 */
public class DefaultNameClashServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<NameClashServiceBatch, NameClashServiceBatchChangeSet>
    implements NameClashServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.NameClashService service;

    DefaultNameClashServiceBatchChangeSet(
        @Nonnull
        final DefaultNameClashServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.NameClashService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultNameClashServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet createTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestCreate(service::createTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet updateTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestUpdate(service::updateTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet deleteTestEntity(
        @Nonnull
        final TestEntityV2 testEntityV2) {
        return addRequestDelete(service::deleteTestEntity, testEntityV2);
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestCreate(service::createTestEntityMultiLink, testEntityMultiLink);
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestUpdate(service::updateTestEntityMultiLink, testEntityMultiLink);
    }

    @Nonnull
    @Override
    public NameClashServiceBatchChangeSet deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestDelete(service::deleteTestEntityMultiLink, testEntityMultiLink);
    }

}
