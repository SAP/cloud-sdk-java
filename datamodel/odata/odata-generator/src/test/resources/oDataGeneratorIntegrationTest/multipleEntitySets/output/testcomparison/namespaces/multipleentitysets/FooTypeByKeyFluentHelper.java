package testcomparison.namespaces.multipleentitysets;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.multipleentitysets.selectable.FooTypeSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself.
 *
 */
public class FooTypeByKeyFluentHelper
    extends FluentHelperByKey<FooTypeByKeyFluentHelper, FooType, FooTypeSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.multipleentitysets.FooType FooType} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     *
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code FooType}
     * @param servicePath
     *     Service path to be used to fetch a single {@code FooType}
     * @param foo
     *
     */
    public FooTypeByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final String foo) {
        super(servicePath, entityCollection);
        this.key.put("Foo", foo);
    }

    @Override
    @Nonnull
    protected Class<FooType> getEntityClass() {
        return FooType.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
