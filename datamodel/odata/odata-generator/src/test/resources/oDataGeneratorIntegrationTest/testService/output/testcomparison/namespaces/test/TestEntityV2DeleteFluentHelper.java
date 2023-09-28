package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperDelete;


/**
 * Fluent helper to delete an existing {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity in the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class TestEntityV2DeleteFluentHelper
    extends FluentHelperDelete<TestEntityV2DeleteFluentHelper, TestEntityV2>
{

    /**
     * {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity object that will be deleted in the S/4HANA system.
     *
     */
    private final TestEntityV2 entity;

    /**
     * Creates a fluent helper object that will delete a {@link testcomparison.namespaces.test.TestEntityV2 TestEntityV2} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     The entity collection to direct the update requests to.
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The TestEntityV2 to delete from the endpoint.
     */
    public TestEntityV2DeleteFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final TestEntityV2 entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected TestEntityV2 getEntity() {
        return entity;
    }

}
