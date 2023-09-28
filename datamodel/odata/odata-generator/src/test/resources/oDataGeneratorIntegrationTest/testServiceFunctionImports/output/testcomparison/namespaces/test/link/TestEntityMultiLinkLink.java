package testcomparison.namespaces.test.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.test.TestEntityMultiLink;
import testcomparison.namespaces.test.selectable.TestEntityMultiLinkSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.test.TestEntityMultiLink TestEntityMultiLink} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class TestEntityMultiLinkLink<ObjectT extends VdmObject<?> >
    extends EntityLink<TestEntityMultiLinkLink<ObjectT> , TestEntityMultiLink, ObjectT>
    implements TestEntityMultiLinkSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public TestEntityMultiLinkLink(final String fieldName) {
        super(fieldName);
    }

    private TestEntityMultiLinkLink(final EntityLink<TestEntityMultiLinkLink<ObjectT> , TestEntityMultiLink, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected TestEntityMultiLinkLink<ObjectT> translateLinkType(final EntityLink<TestEntityMultiLinkLink<ObjectT> , TestEntityMultiLink, ObjectT> link) {
        return new TestEntityMultiLinkLink<ObjectT>(link);
    }

}
