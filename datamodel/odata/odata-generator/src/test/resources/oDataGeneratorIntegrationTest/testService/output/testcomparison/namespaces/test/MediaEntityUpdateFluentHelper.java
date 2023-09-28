package testcomparison.namespaces.test;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperUpdate;


/**
 * Fluent helper to update an existing {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity and save it to the S/4HANA system.<p>
 * To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
 * 
 */
public class MediaEntityUpdateFluentHelper
    extends FluentHelperUpdate<MediaEntityUpdateFluentHelper, MediaEntity>
{

    /**
     * {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity object that will be updated in the S/4HANA system.
     * 
     */
    private final MediaEntity entity;

    /**
     * Creates a fluent helper object that will update a {@link testcomparison.namespaces.test.MediaEntity MediaEntity} entity on the OData endpoint. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param servicePath
     *     The service path to direct the update requests to.
     * @param entity
     *     The MediaEntity to take the updated values from.
     */
    public MediaEntityUpdateFluentHelper(
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
