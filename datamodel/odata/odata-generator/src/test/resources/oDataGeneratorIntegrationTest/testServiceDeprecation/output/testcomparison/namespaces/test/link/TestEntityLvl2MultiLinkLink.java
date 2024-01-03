/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;
import testcomparison.namespaces.test.selectable.TestEntityLvl2MultiLinkSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * 
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 * 
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class TestEntityLvl2MultiLinkLink<ObjectT extends VdmObject<?> >
    extends EntityLink<TestEntityLvl2MultiLinkLink<ObjectT> , TestEntityLvl2MultiLink, ObjectT>
    implements TestEntityLvl2MultiLinkSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public TestEntityLvl2MultiLinkLink(final String fieldName) {
        super(fieldName);
    }

    private TestEntityLvl2MultiLinkLink(final EntityLink<TestEntityLvl2MultiLinkLink<ObjectT> , TestEntityLvl2MultiLink, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected TestEntityLvl2MultiLinkLink<ObjectT> translateLinkType(final EntityLink<TestEntityLvl2MultiLinkLink<ObjectT> , TestEntityLvl2MultiLink, ObjectT> link) {
        return new TestEntityLvl2MultiLinkLink<ObjectT>(link);
    }

}
