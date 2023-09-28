package testcomparison.namespaces.entitywithkeynamedfield;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class EntityWithoutKeyLabelCreateFluentHelper
    extends FluentHelperCreate<EntityWithoutKeyLabelCreateFluentHelper, EntityWithoutKeyLabel>
{

    /**
     * {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity object that will be created in the S/4HANA system.
     *
     */
    private final EntityWithoutKeyLabel entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The EntityWithoutKeyLabel to create.
     */
    public EntityWithoutKeyLabelCreateFluentHelper(
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
