package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class AddressDeleteFluentHelper
    extends FluentHelperDelete<AddressDeleteFluentHelper, Address>
{

    /**
     * {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be deleted in the S/4HANA system.
     *
     */
    private final Address entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The Address to delete from the endpoint.
     */
    public AddressDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final Address entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected Address getEntity() {
        return entity;
    }

}
