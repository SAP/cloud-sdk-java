package testcomparison.namespaces.entitywithkeynamedfield.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchChangeSetFluentHelperBasic;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel;


/**
 * Implementation of the {@link EntitywithkeynamedfieldServiceBatchChangeSet} interface, enabling you to combine multiple operations into one changeset. For further information have a look into the {@link testcomparison.services.EntitywithkeynamedfieldService EntitywithkeynamedfieldService}.
 *
 */
public class DefaultEntitywithkeynamedfieldServiceBatchChangeSet
    extends BatchChangeSetFluentHelperBasic<EntitywithkeynamedfieldServiceBatch, EntitywithkeynamedfieldServiceBatchChangeSet>
    implements EntitywithkeynamedfieldServiceBatchChangeSet
{

    @Nonnull
    private final testcomparison.services.EntitywithkeynamedfieldService service;

    DefaultEntitywithkeynamedfieldServiceBatchChangeSet(
        @Nonnull
        final DefaultEntitywithkeynamedfieldServiceBatch batchFluentHelper,
        @Nonnull
        final testcomparison.services.EntitywithkeynamedfieldService service) {
        super(batchFluentHelper, batchFluentHelper);
        this.service = service;
    }

    @Nonnull
    @Override
    protected DefaultEntitywithkeynamedfieldServiceBatchChangeSet getThis() {
        return this;
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet createSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return addRequestCreate(service::createSomeTypeLabel, someTypeLabel);
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet updateSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return addRequestUpdate(service::updateSomeTypeLabel, someTypeLabel);
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet deleteSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel) {
        return addRequestDelete(service::deleteSomeTypeLabel, someTypeLabel);
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet createEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return addRequestCreate(service::createEntityWithoutKeyLabel, entityWithoutKeyLabel);
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet updateEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return addRequestUpdate(service::updateEntityWithoutKeyLabel, entityWithoutKeyLabel);
    }

    @Nonnull
    @Override
    public EntitywithkeynamedfieldServiceBatchChangeSet deleteEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel) {
        return addRequestDelete(service::deleteEntityWithoutKeyLabel, entityWithoutKeyLabel);
    }

}
