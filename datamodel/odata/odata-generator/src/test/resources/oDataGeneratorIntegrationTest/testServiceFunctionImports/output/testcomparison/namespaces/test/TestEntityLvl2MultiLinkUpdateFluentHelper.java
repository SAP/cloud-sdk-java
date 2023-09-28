package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class TestEntityLvl2MultiLinkUpdateFluentHelper
    extends FluentHelperUpdate<TestEntityLvl2MultiLinkUpdateFluentHelper, TestEntityLvl2MultiLink>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity object that will be updated in the S/4HANA system.
     *
     */
    private final TestEntityLvl2MultiLink entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityLvl2MultiLink to take the updated values from.
     */
    public TestEntityLvl2MultiLinkUpdateFluentHelper(
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
