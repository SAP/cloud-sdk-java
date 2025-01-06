/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.test.TestEntityLvl2SingleLink;
import testcomparison.namespaces.test.selectable.TestEntityLvl2SingleLinkSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.test.TestEntityLvl2SingleLink TestEntityLvl2SingleLink} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class TestEntityLvl2SingleLinkLink<ObjectT extends VdmObject<?> >
    extends EntityLink<TestEntityLvl2SingleLinkLink<ObjectT> , TestEntityLvl2SingleLink, ObjectT>
    implements TestEntityLvl2SingleLinkSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public TestEntityLvl2SingleLinkLink(final String fieldName) {
        super(fieldName);
    }

    private TestEntityLvl2SingleLinkLink(final EntityLink<TestEntityLvl2SingleLinkLink<ObjectT> , TestEntityLvl2SingleLink, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected TestEntityLvl2SingleLinkLink<ObjectT> translateLinkType(final EntityLink<TestEntityLvl2SingleLinkLink<ObjectT> , TestEntityLvl2SingleLink, ObjectT> link) {
        return new TestEntityLvl2SingleLinkLink<ObjectT>(link);
    }

}
