/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.functionimportnameclash.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.functionimportnameclash.BP;
import testcomparison.namespaces.functionimportnameclash.selectable.BPSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.functionimportnameclash.BP BP} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class BPLink<ObjectT extends VdmObject<?> >
    extends EntityLink<BPLink<ObjectT> , BP, ObjectT>
    implements BPSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public BPLink(final String fieldName) {
        super(fieldName);
    }

    private BPLink(final EntityLink<BPLink<ObjectT> , BP, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected BPLink<ObjectT> translateLinkType(final EntityLink<BPLink<ObjectT> , BP, ObjectT> link) {
        return new BPLink<ObjectT>(link);
    }

}
