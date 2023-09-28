package testcomparison.namespaces.nameclash.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.nameclash.TestEntityV2;
import testcomparison.namespaces.nameclash.selectable.TestEntityV2Selectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.nameclash.TestEntityV2 TestEntityV2} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class TestEntityV2Link<ObjectT extends VdmObject<?> >
    extends EntityLink<TestEntityV2Link<ObjectT> , TestEntityV2, ObjectT>
    implements TestEntityV2Selectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public TestEntityV2Link(final String fieldName) {
        super(fieldName);
    }

    private TestEntityV2Link(final EntityLink<TestEntityV2Link<ObjectT> , TestEntityV2, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected TestEntityV2Link<ObjectT> translateLinkType(final EntityLink<TestEntityV2Link<ObjectT> , TestEntityV2, ObjectT> link) {
        return new TestEntityV2Link<ObjectT>(link);
    }

}
