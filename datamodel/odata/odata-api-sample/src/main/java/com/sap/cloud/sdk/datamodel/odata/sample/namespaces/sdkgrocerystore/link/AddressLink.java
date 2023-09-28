package com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;

import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address;
import com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.selectable.AddressSelectable;

/**
 * Template class to represent entity navigation links of
 * {@link com.sap.cloud.sdk.datamodel.odata.sample.namespaces.sdkgrocerystore.Address Address} to other entities.
 * Instances of this object are used in query modifier methods of the entity fluent helpers. Contains methods to compare
 * a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <ObjectT>
 *            Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class AddressLink<ObjectT extends VdmObject<?>> extends EntityLink<AddressLink<ObjectT>, Address, ObjectT>
    implements
    AddressSelectable
{

    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying
     * OData field names, so use with caution.
     *
     * @param fieldName
     *            OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public AddressLink( final String fieldName )
    {
        super(fieldName);
    }

    private AddressLink( final EntityLink<AddressLink<ObjectT>, Address, ObjectT> toClone )
    {
        super(toClone);
    }

    @Nonnull
    @Override
    protected AddressLink<ObjectT> translateLinkType( final EntityLink<AddressLink<ObjectT>, Address, ObjectT> link )
    {
        return new AddressLink<ObjectT>(link);
    }

}
