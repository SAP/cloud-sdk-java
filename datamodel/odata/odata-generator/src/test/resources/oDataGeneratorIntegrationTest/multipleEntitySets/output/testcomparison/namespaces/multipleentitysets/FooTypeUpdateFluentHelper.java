package testcomparison.namespaces.multipleentitysets;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class FooTypeUpdateFluentHelper
    extends FluentHelperUpdate<FooTypeUpdateFluentHelper, FooType>
{

    /**
     * {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity object that will be updated in the S/4HANA system.
     *
     */
    private final FooType entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The FooType to take the updated values from.
     */
    public FooTypeUpdateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final FooType entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected FooType getEntity() {
        return entity;
    }

}
