package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.test.selectable.UnrelatedSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.test.Unrelated Unrelated} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class UnrelatedFluentHelper
    extends FluentHelperRead<UnrelatedFluentHelper, Unrelated, UnrelatedSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     *
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public UnrelatedFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<Unrelated> getEntityClass() {
        return Unrelated.class;
    }

}
