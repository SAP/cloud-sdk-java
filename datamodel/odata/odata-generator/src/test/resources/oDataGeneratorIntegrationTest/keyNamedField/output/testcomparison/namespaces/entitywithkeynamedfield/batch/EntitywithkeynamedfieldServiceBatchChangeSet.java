package testcomparison.namespaces.entitywithkeynamedfield.batch;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchChangeSet;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.FluentHelperBatchEndChangeSet;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel;


/**
 * This interface enables you to combine multiple operations into one change set. For further information have a look into the {@link testcomparison.services.EntitywithkeynamedfieldService EntitywithkeynamedfieldService}.
 *
 */
public interface EntitywithkeynamedfieldServiceBatchChangeSet
    extends FluentHelperBatchChangeSet<EntitywithkeynamedfieldServiceBatchChangeSet> , FluentHelperBatchEndChangeSet<EntitywithkeynamedfieldServiceBatch>
{


    /**
     * Create a new {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity and save it to the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet createSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity and save it to the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet updateSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Deletes an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity in the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet deleteSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Create a new {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be created in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet createEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

    /**
     * Update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be updated in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet updateEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

    /**
     * Deletes an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity in the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be deleted in the S/4HANA system.
     * @return
     *     This fluent helper to continue adding operations to the change set. To finalize the current change set call {@link #endChangeSet endChangeSet} on the returned fluent helper object.
     */
    @Nonnull
    EntitywithkeynamedfieldServiceBatchChangeSet deleteEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

}
