/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.AddressSelectable;

/**
 * Fluent helper to fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address
 * Address} entities. This fluent helper allows methods which modify the underlying query to be called before executing
 * the query itself.
 *
 */
public class AddressFluentHelper extends FluentHelperRead<AddressFluentHelper, Address, AddressSelectable>
{

    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     *
     * @param entityCollection
     *            The entity collection to direct the requests to.
     * @param servicePath
     *            The service path to direct the read requests to.
     */
    public AddressFluentHelper( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<Address> getEntityClass()
    {
        return Address.class;
    }

}
