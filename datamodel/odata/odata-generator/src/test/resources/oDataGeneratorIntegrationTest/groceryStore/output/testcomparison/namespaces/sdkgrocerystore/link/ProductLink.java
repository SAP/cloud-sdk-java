/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.sdkgrocerystore.Product;
import testcomparison.namespaces.sdkgrocerystore.selectable.ProductSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.sdkgrocerystore.Product Product} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class ProductLink<ObjectT extends VdmObject<?> >
    extends EntityLink<ProductLink<ObjectT> , Product, ObjectT>
    implements ProductSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public ProductLink(final String fieldName) {
        super(fieldName);
    }

    private ProductLink(final EntityLink<ProductLink<ObjectT> , Product, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected ProductLink<ObjectT> translateLinkType(final EntityLink<ProductLink<ObjectT> , Product, ObjectT> link) {
        return new ProductLink<ObjectT>(link);
    }

}
