package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.FluentHelperRead;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.VendorSelectable;

/**
 * Fluent helper to fetch multiple {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Vendor
 * Vendor} entities. This fluent helper allows methods which modify the underlying query to be called before executing
 * the query itself.
 *
 */
public class VendorFluentHelper extends FluentHelperRead<VendorFluentHelper, Vendor, VendorSelectable>
{

    /**
     * Creates a fluent helper using the specified service path and entity collection to send the read requests.
     *
     * @param entityCollection
     *            The entity collection to direct the requests to.
     * @param servicePath
     *            The service path to direct the read requests to.
     */
    public VendorFluentHelper( @Nonnull final String servicePath, @Nonnull final String entityCollection )
    {
        super(servicePath, entityCollection);
    }

    @Override
    @Nonnull
    protected Class<Vendor> getEntityClass()
    {
        return Vendor.class;
    }

}
