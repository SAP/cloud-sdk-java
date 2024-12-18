/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.test.selectable.TestEntityOtherMultiLinkSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class TestEntityOtherMultiLinkByKeyFluentHelper
    extends FluentHelperByKey<TestEntityOtherMultiLinkByKeyFluentHelper, TestEntityOtherMultiLink, TestEntityOtherMultiLinkSelectable>
{

    private final Map<String, Object> key = Maps.newLinkedHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.test.TestEntityOtherMultiLink TestEntityOtherMultiLink} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code TestEntityOtherMultiLink}
     * @param keyProperty
     *     
     * @param servicePath
     *     Service path to be used to fetch a single {@code TestEntityOtherMultiLink}
     */
    public TestEntityOtherMultiLinkByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final String keyProperty) {
        super(servicePath, entityCollection);
        this.key.put("KeyProperty", keyProperty);
    }

    @Override
    @Nonnull
    protected Class<TestEntityOtherMultiLink> getEntityClass() {
        return TestEntityOtherMultiLink.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
