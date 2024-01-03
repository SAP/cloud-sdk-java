/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperByKey;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.CustomerSelectable;

/**
 * Fluent helper to fetch a single {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer
 * Customer} entity using key fields. This fluent helper allows methods which modify the underlying query to be called
 * before executing the query itself.
 *
 */
public class CustomerByKeyFluentHelper
    extends
    FluentHelperByKey<CustomerByKeyFluentHelper, Customer, CustomerSelectable>
{

    private final Map<String, Object> key = Maps.newHashMap();

    /**
     * Creates a fluent helper object that will fetch a single
     * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Customer Customer} entity with the
     * provided key field values. To perform execution, call the {@link #executeRequest executeRequest} method on the
     * fluent helper object.
     *
     * @param entityCollection
     *            Entity Collection to be used to fetch a single {@code Customer}
     * @param servicePath
     *            Service path to be used to fetch a single {@code Customer}
     * @param id
     *            Customer identifier used as entity key value.
     *            <p>
     *            Constraints: Not nullable
     *            </p>
     */
    public CustomerByKeyFluentHelper(
        @Nonnull final String servicePath,
        @Nonnull final String entityCollection,
        final Integer id )
    {
        super(servicePath, entityCollection);
        this.key.put("Id", id);
    }

    @Override
    @Nonnull
    protected Class<Customer> getEntityClass()
    {
        return Customer.class;
    }

    @Override
    @Nonnull
    protected Map<String, Object> getKey()
    {
        return key;
    }

}
