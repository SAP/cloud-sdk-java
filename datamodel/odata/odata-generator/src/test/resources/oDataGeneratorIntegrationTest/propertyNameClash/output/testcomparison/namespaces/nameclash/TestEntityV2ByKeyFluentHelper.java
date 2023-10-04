/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.nameclash;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.nameclash.selectable.TestEntityV2Selectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class TestEntityV2ByKeyFluentHelper
    extends FluentHelperByKey<TestEntityV2ByKeyFluentHelper, TestEntityV2, TestEntityV2Selectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code TestEntityV2}
     * @param servicePath
     *     Service path to be used to fetch a single {@code TestEntityV2}
     * @param keyPropertyGuid
     *     
     */
    public TestEntityV2ByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final UUID keyPropertyGuid) {
        super(servicePath, entityCollection);
        this.key.put("KeyPropertyGuid", keyPropertyGuid);
    }

    @Override
    @Nonnull
    protected Class<TestEntityV2> getEntityClass() {
        return TestEntityV2 .class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
