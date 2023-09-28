package testcomparison.namespaces.test.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.OneToOneLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.test.TestEntityLvl2MultiLink;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.test.TestEntityLvl2MultiLink TestEntityLvl2MultiLink} to other entities, where the cardinality of the related entity is at most 1. This class extends {@link testcomparison.namespaces.test.link.TestEntityLvl2MultiLinkLink TestEntityLvl2MultiLinkLink} and provides an additional filter function.
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 * 
 */
public class TestEntityLvl2MultiLinkOneToOneLink<ObjectT extends VdmObject<?> >
    extends TestEntityLvl2MultiLinkLink<ObjectT>
    implements OneToOneLink<TestEntityLvl2MultiLink, ObjectT>
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     * 
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public TestEntityLvl2MultiLinkOneToOneLink(final String fieldName) {
        super(fieldName);
    }

    /**
     * Query modifier to restrict the result set to entities for which this expression (formulated over a property of a <b>related</b> entity) evaluates to true. Note that filtering on a related entity does not expand the selection of the respective query to that entity.
     * 
     * @param filterExpression
     *     A filter expression on the related entity.
     * @return
     *     A filter expression over a related entity, scoped to the parent entity.
     */
    @Nonnull
    @Override
    public ExpressionFluentHelper<TestEntityLvl2MultiLink> filter(
        @Nonnull
        final ExpressionFluentHelper<ObjectT> filterExpression) {
        return super.filterOnOneToOneLink(filterExpression);
    }

}
