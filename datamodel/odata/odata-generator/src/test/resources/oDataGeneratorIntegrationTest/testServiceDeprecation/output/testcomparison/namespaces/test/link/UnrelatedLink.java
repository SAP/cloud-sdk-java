/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.test.Unrelated;
import testcomparison.namespaces.test.selectable.UnrelatedSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.test.Unrelated Unrelated} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class UnrelatedLink<ObjectT extends VdmObject<?> >
    extends EntityLink<UnrelatedLink<ObjectT> , Unrelated, ObjectT>
    implements UnrelatedSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public UnrelatedLink(final String fieldName) {
        super(fieldName);
    }

    private UnrelatedLink(final EntityLink<UnrelatedLink<ObjectT> , Unrelated, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected UnrelatedLink<ObjectT> translateLinkType(final EntityLink<UnrelatedLink<ObjectT> , Unrelated, ObjectT> link) {
        return new UnrelatedLink<ObjectT>(link);
    }

}
