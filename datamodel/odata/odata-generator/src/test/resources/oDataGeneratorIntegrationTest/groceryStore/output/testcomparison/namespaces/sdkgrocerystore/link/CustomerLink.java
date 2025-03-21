/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.sdkgrocerystore.Customer;
import testcomparison.namespaces.sdkgrocerystore.selectable.CustomerSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.sdkgrocerystore.Customer Customer} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class CustomerLink<ObjectT extends VdmObject<?> >
    extends EntityLink<CustomerLink<ObjectT> , Customer, ObjectT>
    implements CustomerSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public CustomerLink(final String fieldName) {
        super(fieldName);
    }

    private CustomerLink(final EntityLink<CustomerLink<ObjectT> , Customer, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected CustomerLink<ObjectT> translateLinkType(final EntityLink<CustomerLink<ObjectT> , Customer, ObjectT> link) {
        return new CustomerLink<ObjectT>(link);
    }

}
