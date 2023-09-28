package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class TestEntityOtherMultiLinkDeleteFluentHelper
    extends FluentHelperDelete<TestEntityOtherMultiLinkDeleteFluentHelper, TestEntityOtherMultiLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity object that will be deleted in the S/4HANA system.
     *
     */
    private final TestEntityOtherMultiLink entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityOtherMultiLink to delete from the endpoint.
     */
    public TestEntityOtherMultiLinkDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityOtherMultiLink entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityOtherMultiLink getEntity() {
        return entity;
    }

}
