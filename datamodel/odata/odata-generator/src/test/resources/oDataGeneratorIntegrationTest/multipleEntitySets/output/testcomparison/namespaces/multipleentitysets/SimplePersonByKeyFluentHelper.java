/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.multipleentitysets.selectable.SimplePersonSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class SimplePersonByKeyFluentHelper
    extends FluentHelperByKey<SimplePersonByKeyFluentHelper, SimplePerson, SimplePersonSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code SimplePerson}
     * @param person
     *     
     * @param servicePath
     *     Service path to be used to fetch a single {@code SimplePerson}
     */
    public SimplePersonByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final String person) {
        super(servicePath, entityCollection);
        this.key.put("Person", person);
    }

    @Override
    @Nonnull
    protected Class<SimplePerson> getEntityClass() {
        return SimplePerson.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
