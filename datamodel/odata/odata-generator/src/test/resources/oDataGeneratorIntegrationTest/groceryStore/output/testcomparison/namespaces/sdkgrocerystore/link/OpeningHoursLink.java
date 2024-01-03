/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.sdkgrocerystore.OpeningHours;
import testcomparison.namespaces.sdkgrocerystore.selectable.OpeningHoursSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.sdkgrocerystore.OpeningHours OpeningHours} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class OpeningHoursLink<ObjectT extends VdmObject<?> >
    extends EntityLink<OpeningHoursLink<ObjectT> , OpeningHours, ObjectT>
    implements OpeningHoursSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public OpeningHoursLink(final String fieldName) {
        super(fieldName);
    }

    private OpeningHoursLink(final EntityLink<OpeningHoursLink<ObjectT> , OpeningHours, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected OpeningHoursLink<ObjectT> translateLinkType(final EntityLink<OpeningHoursLink<ObjectT> , OpeningHours, ObjectT> link) {
        return new OpeningHoursLink<ObjectT>(link);
    }

}
