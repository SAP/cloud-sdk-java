/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.multipleentitysets.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.multipleentitysets.SimplePerson;
import testcomparison.namespaces.multipleentitysets.selectable.SimplePersonSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.multipleentitysets.SimplePerson SimplePerson} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class SimplePersonLink<ObjectT extends VdmObject<?> >
    extends EntityLink<SimplePersonLink<ObjectT> , SimplePerson, ObjectT>
    implements SimplePersonSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public SimplePersonLink(final String fieldName) {
        super(fieldName);
    }

    private SimplePersonLink(final EntityLink<SimplePersonLink<ObjectT> , SimplePerson, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected SimplePersonLink<ObjectT> translateLinkType(final EntityLink<SimplePersonLink<ObjectT> , SimplePerson, ObjectT> link) {
        return new SimplePersonLink<ObjectT>(link);
    }

}
