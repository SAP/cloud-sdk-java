package testcomparison.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.sdkgrocerystore.selectable.CustomerSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class CustomerFluentHelper
    extends FluentHelperRead<CustomerFluentHelper, Customer, CustomerSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     *
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public CustomerFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

}
