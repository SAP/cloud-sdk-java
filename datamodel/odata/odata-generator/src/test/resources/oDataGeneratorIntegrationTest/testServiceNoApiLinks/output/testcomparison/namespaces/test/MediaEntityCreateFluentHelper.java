package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperCreate;


/**
 * Fluent helper to create a new {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 *
 */
public class MediaEntityCreateFluentHelper
    extends FluentHelperCreate<MediaEntityCreateFluentHelper, MediaEntity>
{

    /**
     * {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be created in the S/4HANA system.
     *
     */
    private final MediaEntity entity;

    /**
     * Creates a fluent helper object that will create a {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection  to direct the create requests to.
     * @param servicePath
     *     The service path to direct the create requests to.
     * @param entity
     *     The MediaEntity to create.
     */
    public MediaEntityCreateFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final MediaEntity entity,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
        this.entity = entity;
    }

    @Override
    @Nonnull
    protected MediaEntity getEntity() {
        return entity;
    }

}
