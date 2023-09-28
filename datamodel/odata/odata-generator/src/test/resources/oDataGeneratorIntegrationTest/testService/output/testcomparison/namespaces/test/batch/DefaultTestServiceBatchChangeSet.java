package testcomparison.namespaces.test.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.test.MediaEntity;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.TestEntityOtherMultiLink;
import testcomparison.namespaces.test.TestEntitySingleLink;
import testcomparison.namespaces.test.TestEntityV2;
import testcomparison.namespaces.test.Unrelated;


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

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestCreate(service::createTestEntityMultiLink, testEntityMultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestUpdate(service::updateTestEntityMultiLink, testEntityMultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntityMultiLink(
        @Nonnull
        final TestEntityMultiLink testEntityMultiLink) {
        return addRequestDelete(service::deleteTestEntityMultiLink, testEntityMultiLink);
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
    public TestServiceBatchChangeSet updateTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return addRequestUpdate(service::updateTestEntityOtherMultiLink, testEntityOtherMultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntityOtherMultiLink(
        @Nonnull
        final TestEntityOtherMultiLink testEntityOtherMultiLink) {
        return addRequestDelete(service::deleteTestEntityOtherMultiLink, testEntityOtherMultiLink);
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
    public TestServiceBatchChangeSet deleteTestEntityLvl2MultiLink(
        @Nonnull
        final TestEntityLvl2MultiLink testEntityLvl2MultiLink) {
        return addRequestDelete(service::deleteTestEntityLvl2MultiLink, testEntityLvl2MultiLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return addRequestCreate(service::createTestEntitySingleLink, testEntitySingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return addRequestUpdate(service::updateTestEntitySingleLink, testEntitySingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntitySingleLink(
        @Nonnull
        final TestEntitySingleLink testEntitySingleLink) {
        return addRequestDelete(service::deleteTestEntitySingleLink, testEntitySingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return addRequestCreate(service::createTestEntityLvl2SingleLink, testEntityLvl2SingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return addRequestUpdate(service::updateTestEntityLvl2SingleLink, testEntityLvl2SingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntityLvl2SingleLink(
        @Nonnull
        final TestEntityLvl2SingleLink testEntityLvl2SingleLink) {
        return addRequestDelete(service::deleteTestEntityLvl2SingleLink, testEntityLvl2SingleLink);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return addRequestCreate(service::createMediaEntity, mediaEntity);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return addRequestUpdate(service::updateMediaEntity, mediaEntity);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteMediaEntity(
        @Nonnull
        final MediaEntity mediaEntity) {
        return addRequestDelete(service::deleteMediaEntity, mediaEntity);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityEndsWithCollection(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestCreate(service::createTestEntityEndsWithCollection, unrelated);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntityEndsWithCollection(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestUpdate(service::updateTestEntityEndsWithCollection, unrelated);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntityEndsWithCollection(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestDelete(service::deleteTestEntityEndsWithCollection, unrelated);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet createTestEntityEndsWithSomethingElse(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestCreate(service::createTestEntityEndsWithSomethingElse, unrelated);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet updateTestEntityEndsWithSomethingElse(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestUpdate(service::updateTestEntityEndsWithSomethingElse, unrelated);
    }

    @Nonnull
    @Override
    public TestServiceBatchChangeSet deleteTestEntityEndsWithSomethingElse(
        @Nonnull
        final Unrelated unrelated) {
        return addRequestDelete(service::deleteTestEntityEndsWithSomethingElse, unrelated);
    }

}
