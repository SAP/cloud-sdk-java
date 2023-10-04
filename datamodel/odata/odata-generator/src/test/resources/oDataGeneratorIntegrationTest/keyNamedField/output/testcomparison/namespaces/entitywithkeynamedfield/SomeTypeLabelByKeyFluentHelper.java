/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.SomeTypeLabelSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class SomeTypeLabelByKeyFluentHelper
    extends FluentHelperByKey<SomeTypeLabelByKeyFluentHelper, SomeTypeLabel, SomeTypeLabelSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code SomeTypeLabel}
     * @param key_2
     *     Key<p>Constraints: Not nullable</p>
     * @param servicePath
     *     Service path to be used to fetch a single {@code SomeTypeLabel}
     */
    public SomeTypeLabelByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final UUID key_2) {
        super(servicePath, entityCollection);
        this.key.put("KeyFieldWithKeyLabel", key_2);
    }

    @Override
    @Nonnull
    protected Class<SomeTypeLabel> getEntityClass() {
        return SomeTypeLabel.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
