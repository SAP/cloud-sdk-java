/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.sdkgrocerystore.Vendor;
import testcomparison.namespaces.sdkgrocerystore.selectable.VendorSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.sdkgrocerystore.Vendor Vendor} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class VendorLink<ObjectT extends VdmObject<?> >
    extends EntityLink<VendorLink<ObjectT> , Vendor, ObjectT>
    implements VendorSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public VendorLink(final String fieldName) {
        super(fieldName);
    }

    private VendorLink(final EntityLink<VendorLink<ObjectT> , Vendor, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected VendorLink<ObjectT> translateLinkType(final EntityLink<VendorLink<ObjectT> , Vendor, ObjectT> link) {
        return new VendorLink<ObjectT>(link);
    }

}
