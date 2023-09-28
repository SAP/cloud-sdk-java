package testcomparison.namespaces.entitywithkeynamedfield;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class EntityWithoutKeyLabelUpdateFluentHelper
    extends FluentHelperUpdate<EntityWithoutKeyLabelUpdateFluentHelper, EntityWithoutKeyLabel>
{

    /**
     * {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be updated in the S/4HANA system.
     *
     */
    private final EntityWithoutKeyLabel entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The EntityWithoutKeyLabel to take the updated values from.
     */
    public EntityWithoutKeyLabelUpdateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final EntityWithoutKeyLabel entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected EntityWithoutKeyLabel getEntity() {
        return entity;
    }

}
