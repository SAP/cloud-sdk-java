package testcomparison.namespaces.entitywithkeynamedfield.link;

import javax.annotation.Nonnull;
import com.sap.cloud.sdk.datamodel.odata.helper.EntityLink;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmObject;
import testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.EntityWithoutKeyLabelSelectable;


/**
 * Template class to represent entity navigation links of {@link testcomparison.namespaces.entitywithkeynamedfield.EntityWithoutKeyLabel EntityWithoutKeyLabel} to other entities. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <ObjectT>
 * Entity type of subclasses from {@link com.sap.cloud.sdk.datamodel.odata.helper.VdmObject VdmObject}.
 *
 */
public class EntityWithoutKeyLabelLink<ObjectT extends VdmObject<?> >
    extends EntityLink<EntityWithoutKeyLabelLink<ObjectT> , EntityWithoutKeyLabel, ObjectT>
    implements EntityWithoutKeyLabelSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData navigation field name. Must match the field returned by the underlying OData service.
     */
    public EntityWithoutKeyLabelLink(final String fieldName) {
        super(fieldName);
    }

    private EntityWithoutKeyLabelLink(final EntityLink<EntityWithoutKeyLabelLink<ObjectT> , EntityWithoutKeyLabel, ObjectT> toClone) {
        super(toClone);
    }

    @Nonnull
    @Override
    protected EntityWithoutKeyLabelLink<ObjectT> translateLinkType(final EntityLink<EntityWithoutKeyLabelLink<ObjectT> , EntityWithoutKeyLabel, ObjectT> link) {
        return new EntityWithoutKeyLabelLink<ObjectT>(link);
    }

}
