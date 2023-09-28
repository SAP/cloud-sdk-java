package testcomparison.namespaces.multipleentitysets.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.ExpressionFluentHelper;
import com.sap.cloud.sdk.datamodel.odata.helper.OneToOneLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.multipleentitysets.FooType;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.multipleentitysets.FooType FooType} to other entities, where the cardinality of the related entity is at most 1. This class extends {@link testcomparison.namespaces.multipleentitysets.link.FooTypeLink FooTypeLink} and provides an additional filter function.
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class FooTypeOneToOneLink<ObjectT extends VdmObject<?> >
    extends FooTypeLink<ObjectT>
    implements OneToOneLink<FooType, ObjectT>
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public FooTypeOneToOneLink(final String fieldName) {
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
    public ExpressionFluentHelper<FooType> filter(
        @Nonnull
        final ExpressionFluentHelper<ObjectT> filterExpression) {
        return super.filterOnOneToOneLink(filterExpression);
    }

}
