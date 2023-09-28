package testcomparison.namespaces.entitywithkeynamedfield;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.EntityWithoutKeyLabelSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class EntityWithoutKeyLabelByKeyFluentHelper
    extends FluentHelperByKey<EntityWithoutKeyLabelByKeyFluentHelper, EntityWithoutKeyLabel, EntityWithoutKeyLabelSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code EntityWithoutKeyLabel}
     * @param servicePath
     *     Service path to be used to fetch a single {@code EntityWithoutKeyLabel}
     */
    public EntityWithoutKeyLabelByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection) {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<EntityWithoutKeyLabel> getEntityClass() {
        return EntityWithoutKeyLabel.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
