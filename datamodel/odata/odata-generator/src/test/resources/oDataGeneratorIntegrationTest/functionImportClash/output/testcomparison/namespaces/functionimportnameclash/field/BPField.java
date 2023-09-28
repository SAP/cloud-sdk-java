package testcomparison.namespaces.functionimportnameclash.field;

import com.sap.cloud.sdk.datamodel.odata.helper.EntityField;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import testcomparison.namespaces.functionimportnameclash.BP;
import testcomparison.namespaces.functionimportnameclash.selectable.BPSelectable;


/**
 * Template class to represent entity fields of the Entity {@link testcomparison.namespaces.functionimportnameclash.BP BP}. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 *
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <FieldT>
 * Field type
 *
 */
public class BPField<FieldT >
    extends EntityField<BP, FieldT>
    implements BPSelectable
{


    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.
     *
     * @param fieldName
     *     OData field name. Must match the field returned by the underlying OData service.
     */
    public BPField(final String fieldName) {
        super(fieldName);
    }

    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData field names, so use with caution.When creating instances for custom fields, this constructor can be used to add a type converter that will be automatically used by the respective entity when getting or setting custom fields.
     *
     * @param typeConverter
     *     An implementation of a TypeConverter. The first type must match FieldT, the second type must match the type Olingo returns.
     * @param fieldName
     *     OData field name. Must match the field returned by the underlying OData service.
     */
    public BPField(final String fieldName, final TypeConverter<FieldT, ?> typeConverter) {
        super(fieldName, typeConverter);
    }

}
