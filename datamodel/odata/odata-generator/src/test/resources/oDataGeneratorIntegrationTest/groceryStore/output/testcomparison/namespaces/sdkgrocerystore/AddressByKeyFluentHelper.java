/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore;

import java.util.Map;
import javax.annotation.Nonnull;
import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import testcomparison.namespaces.sdkgrocerystore.selectable.AddressSelectable;


/**
 * Fluent helper to fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity using key fields. This fluent helper allows methods which modify the underlying query to be called before executing the query itself. 
 * 
 */
public class AddressByKeyFluentHelper
    extends FluentHelperByKey<AddressByKeyFluentHelper, Address, AddressSelectable>
{

    private final Map<String, Object> key = Maps.newLinkedHashMap();

    /**
     * Creates a fluent helper object that will fetch a single {@link testcomparison.namespaces.sdkgrocerystore.Address Address} entity with the provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the fluent helper object.
     * 
     * @param entityCollection
     *     Entity Collection to be used to fetch a single {@code Address}
     * @param servicePath
     *     Service path to be used to fetch a single {@code Address}
     * @param id
     *     
     */
    public AddressByKeyFluentHelper(
        @Nonnull
        final String servicePath,
        @Nonnull
        final String entityCollection, final Integer id) {
        super(servicePath, entityCollection);
        this.key.put("Id", id);
    }

    @Override
    @Nonnull
    protected Class<Address> getEntityClass() {
        return Address.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey() {
        return key;
    }

}
