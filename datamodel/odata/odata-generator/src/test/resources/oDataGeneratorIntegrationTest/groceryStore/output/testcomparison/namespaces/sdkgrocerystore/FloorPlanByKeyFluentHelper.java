package testcomparison.namespaces.sdkgrocerystore;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.sdkgrocerystore.selectable.FloorPlanSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class FloorPlanByKeyFluentHelper
    extends FluentHelperByKey<FloorPlanByKeyFluentHelper, FloorPlan, FloorPlanSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.sdkgrocerystore.FloorPlan FloorPlan} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code FloorPlan}
     * @param servicePath
     *     Service path to be used to fetch a single {@code FloorPlan}
     * @param id
     *
     */
    public FloorPlanByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final Integer id) {
        super(servicePath, entityCollection);
        this.key.put("Id", id);
    }

    @Override
    @Nonnull
    protected Class<FloorPlan> getEntityClass() {
        return FloorPlan.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
