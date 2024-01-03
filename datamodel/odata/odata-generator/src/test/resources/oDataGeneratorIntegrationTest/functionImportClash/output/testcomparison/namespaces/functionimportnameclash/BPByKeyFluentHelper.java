/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.functionimportnameclash.selectable.BPSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class BPByKeyFluentHelper
    extends FluentHelperByKey<BPByKeyFluentHelper, BP, BPSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.functionimportnameclash.BP BP} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code BP}
     * @param code
     *     
     * @param servicePath
     *     Service path to be used to fetch a single {@code BP}
     */
    public BPByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final String code) {
        super(servicePath, entityCollection);
        this.key.put("Code", code);
    }

    @Override
    @Nonnull
    protected Class<BP> getEntityClass() {
        return BP.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
