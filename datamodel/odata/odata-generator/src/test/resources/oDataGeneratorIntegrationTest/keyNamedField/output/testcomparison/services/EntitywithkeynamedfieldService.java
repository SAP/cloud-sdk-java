package testcomparison.services;

import java.util.UUID;
import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.batch.BatchService;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelByKeyFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelCreateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelDeleteFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelUpdateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelByKeyFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelCreateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelDeleteFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelUpdateFluentHelper;
import testcomparison.namespaces.entitywithkeynamedfield.batch.EntitywithkeynamedfieldServiceBatch;


/**
 * <p>Reference: <a href='https://api.sap.com/shell/discover/contentpackage/SAPS4HANACloud/api/EntityWithKeyNamedField?section=OVERVIEW'>SAP Business Accelerator Hub</a></p><h3>Details:</h3><table summary='Details'><tr><td align='right'>OData Service:</td><td>EntityWithKeyNamedField</td></tr></table>
 *
 */
public interface EntitywithkeynamedfieldService
    extends BatchService<EntitywithkeynamedfieldServiceBatch>
{

    /**
     * If no other path was provided via the {@link #withServicePath(String)} method, this is the default service path used to access the endpoint.
     *
     */
    String DEFAULT_SERVICE_PATH = "/sap/opu/odata/sap/API_TEST_SRV";

    /**
     * Overrides the default service path and returns a new service instance with the specified service path. Also adjusts the respective entity URLs.
     *
     * @param servicePath
     *     Service path that will override the default.
     * @return
     *     A new service instance with the specified service path.
     */
    @Nonnull
    EntitywithkeynamedfieldService withServicePath(
        @Nonnull
        final String servicePath);

    /**
     * Fetch multiple {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SomeTypeLabelFluentHelper getAllSomeTypeLabel();

    /**
     * Fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity using key fields.
     *
     * @param key_2
     *     Key<p>Constraints: Not nullable</p>
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SomeTypeLabelByKeyFluentHelper getSomeTypeLabelByKey(final UUID key_2);

    /**
     * Create a new {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity and save it to the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SomeTypeLabelCreateFluentHelper createSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity and save it to the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SomeTypeLabelUpdateFluentHelper updateSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Deletes an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity in the S/4HANA system.
     *
     * @param someTypeLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabelDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    SomeTypeLabelDeleteFluentHelper deleteSomeTypeLabel(
        @Nonnull
        final SomeTypeLabel someTypeLabel);

    /**
     * Fetch multiple {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entities.
     *
     * @return
     *     A fluent helper to fetch multiple {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    EntityWithoutKeyLabelFluentHelper getAllEntityWithoutKeyLabel();

    /**
     * Fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity using key fields.
     *
     * @return
     *     A fluent helper to fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelByKeyFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    EntityWithoutKeyLabelByKeyFluentHelper getEntityWithoutKeyLabelByKey();

    /**
     * Create a new {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be created in the S/4HANA system.
     * @return
     *     A fluent helper to create a new {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelCreateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    EntityWithoutKeyLabelCreateFluentHelper createEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

    /**
     * Update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be updated in the S/4HANA system.
     * @return
     *     A fluent helper to update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelUpdateFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    EntityWithoutKeyLabelUpdateFluentHelper updateEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

    /**
     * Deletes an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity in the S/4HANA system.
     *
     * @param entityWithoutKeyLabel
     *     {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be deleted in the S/4HANA system.
     * @return
     *     A fluent helper to delete an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity. To perform execution, call the {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabelDeleteFluentHelper#execute execute} method on the fluent helper object.
     */
    @Nonnull
    EntityWithoutKeyLabelDeleteFluentHelper deleteEntityWithoutKeyLabel(
        @Nonnull
        final EntityWithoutKeyLabel entityWithoutKeyLabel);

}
