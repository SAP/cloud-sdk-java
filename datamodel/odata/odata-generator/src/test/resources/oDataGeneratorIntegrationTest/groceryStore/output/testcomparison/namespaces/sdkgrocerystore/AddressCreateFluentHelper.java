package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class AddressCreateFluentHelper
    extends FluentHelperCreate<AddressCreateFluentHelper, Address>
{

    /**
     * {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity object that will be created in the S/4HANA system.
     *
     */
    private final Address entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The Address to create.
     */
    public AddressCreateFluentHelper(
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
