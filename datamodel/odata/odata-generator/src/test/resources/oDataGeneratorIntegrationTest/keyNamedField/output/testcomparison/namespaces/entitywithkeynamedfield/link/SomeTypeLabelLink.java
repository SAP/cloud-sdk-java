/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.SomeTypeLabelSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.entitywithkeynamedfield.SomeTypeLabel SomeTypeLabel} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class SomeTypeLabelLink<ObjectT extends VdmObject<?> >
    extends EntityLink<SomeTypeLabelLink<ObjectT> , SomeTypeLabel, ObjectT>
    implements SomeTypeLabelSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public SomeTypeLabelLink(final String fieldName) {
        super(fieldName);
    }

    private SomeTypeLabelLink(final EntityLink<SomeTypeLabelLink<ObjectT> , SomeTypeLabel, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected SomeTypeLabelLink<ObjectT> translateLinkType(final EntityLink<SomeTypeLabelLink<ObjectT> , SomeTypeLabel, ObjectT> link) {
        return new SomeTypeLabelLink<ObjectT>(link);
    }

}
