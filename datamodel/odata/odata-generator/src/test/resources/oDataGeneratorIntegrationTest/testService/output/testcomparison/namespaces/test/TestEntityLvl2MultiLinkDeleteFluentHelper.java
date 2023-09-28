package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class TestEntityLvl2MultiLinkDeleteFluentHelper
    extends FluentHelperDelete<TestEntityLvl2MultiLinkDeleteFluentHelper, TestEntityLvl2MultiLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be deleted in the S/4HANA system.
     *
     */
    private final TestEntityLvl2MultiLink entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityLvl2MultiLink to delete from the endpoint.
     */
    public TestEntityLvl2MultiLinkDeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityLvl2MultiLink entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityLvl2MultiLink getEntity() {
        return entity;
    }

}
