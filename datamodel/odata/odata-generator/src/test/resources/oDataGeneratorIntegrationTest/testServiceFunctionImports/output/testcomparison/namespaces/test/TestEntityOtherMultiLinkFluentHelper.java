package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import testcomparison.namespaces.test.selectable.TestEntityOtherMultiLinkSelectable;


/**
 * Fluent helper to fetch multiple {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entities. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class TestEntityOtherMultiLinkFluentHelper
    extends FluentHelperRead<TestEntityOtherMultiLinkFluentHelper, TestEntityOtherMultiLink, TestEntityOtherMultiLinkSelectable>
{


    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     *
     * @param entityCollection
     *     The entity collection to direct the requests to.
     * @param servicePath
     *     The service path to direct the read requests to.
     */
    public TestEntityOtherMultiLinkFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<TestEntityOtherMultiLink> getEntityClass() {
        return TestEntityOtherMultiLink.class;
    }

}
