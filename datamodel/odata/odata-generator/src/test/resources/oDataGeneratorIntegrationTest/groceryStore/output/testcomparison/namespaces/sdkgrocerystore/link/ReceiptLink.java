package testcomparison.namespaces.sdkgrocerystore.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.sdkgrocerystore.Receipt;
import testcomparison.namespaces.sdkgrocerystore.selectable.ReceiptSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.sdkgrocerystore.Receipt Receipt} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class ReceiptLink<ObjectT extends VdmObject<?> >
    extends EntityLink<ReceiptLink<ObjectT> , Receipt, ObjectT>
    implements ReceiptSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public ReceiptLink(final String fieldName) {
        super(fieldName);
    }

    private ReceiptLink(final EntityLink<ReceiptLink<ObjectT> , Receipt, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected ReceiptLink<ObjectT> translateLinkType(final EntityLink<ReceiptLink<ObjectT> , Receipt, ObjectT> link) {
        return new ReceiptLink<ObjectT>(link);
    }

}
