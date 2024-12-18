/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.test.selectable.TestEntityLvl2MultiLinkSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class TestEntityLvl2MultiLinkByKeyFluentHelper
    extends FluentHelperByKey<TestEntityLvl2MultiLinkByKeyFluentHelper, TestEntityLvl2MultiLink, TestEntityLvl2MultiLinkSelectable>
{

    private final Map<String, Object> key = Maps.newLinkedHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code TestEntityLvl2MultiLink}
     * @param keyProperty
     *     
     * @param servicePath
     *     Service path to be used to fetch a single {@code TestEntityLvl2MultiLink}
     */
    public TestEntityLvl2MultiLinkByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final String keyProperty) {
        super(servicePath, entityCollection);
        this.key.put("KeyProperty", keyProperty);
    }

    @Override
    @Nonnull
    protected Class<TestEntityLvl2MultiLink> getEntityClass() {
        return TestEntityLvl2MultiLink.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
