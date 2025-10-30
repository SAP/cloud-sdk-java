package com.sap.cloud.sdk.datamodel.odata.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldReference;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FieldUntyped;
import com.sap.cloud.sdk.datamodel.odata.client.expression.FilterExpressionString;
import com.sap.cloud.sdk.datamodel.odata.client.expression.ValueString;
import com.sap.cloud.sdk.typeconverter.TypeConverter;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Template class to represent entity fields. Instances of this object are used in query modifier methods of the entity
 * fluent helpers. Contains methods to compare a field's value with a provided value.
 * <p>
 * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying OData
 * field names, so use the constructor with caution.
 *
 * @param <EntityT>
 *            VdmObject that the field belongs to
 * @param <FieldT>
 *            Field type
 */
@EqualsAndHashCode
public class EntityField<EntityT, FieldT> implements EntitySelectable<EntityT>
{
    @Nonnull
    @Getter
    private final String fieldName;

    @Nullable
    @Getter
    private final TypeConverter<FieldT, ?> typeConverter;

    @Nonnull
    private final FieldUntyped fieldUntyped;

    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying
     * OData field names, so use with caution.
     *
     * @param fieldName
     *            OData field name. Must match the field returned by the underlying OData service.
     */
    public EntityField( @Nonnull final String fieldName )
    {
        this(fieldName, null);
    }

    /**
     * Use the constants declared in each entity inner class. Instantiating directly requires knowing the underlying
     * OData field names, so use with caution.
     * <p>
     * When creating instances for custom fields, this constructor can be used to add a type converter that will be
     * automatically used by the respective entity when getting or setting custom fields.
     *
     * @param fieldName
     *            OData field name. Must match the field returned by the underlying OData service.
     * @param typeConverter
     *            An implementation of a TypeConverter. The first type must match FieldT, the second type must match the
     *            type Olingo returns.
     */
    public EntityField( @Nonnull final String fieldName, @Nullable final TypeConverter<FieldT, ?> typeConverter )
    {
        this.fieldName = fieldName;
        this.typeConverter = typeConverter;
        fieldUntyped = FieldReference.of(fieldName);
    }

    /**
     * Equals-null expression fluent helper.
     *
     * @return Fluent helper that represents a <i>field == null</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> eqNull()
    {
        return new ExpressionFluentHelper<>(fieldUntyped.equalToNull());
    }

    /**
     * Equals expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field == value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> eq( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.equalTo(value));
    }

    /**
     * Not equals-null expression fluent helper.
     *
     * @return Fluent helper that represents a <i>field != null</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> neNull()
    {
        return new ExpressionFluentHelper<>(fieldUntyped.notEqualToNull());
    }

    /**
     * Not equals expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field != value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> ne( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.notEqualTo(value));
    }

    /**
     * Greater than expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field &gt; value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> gt( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.greaterThan(value));
    }

    /**
     * Greater than or equals expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field &ge; value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> ge( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.greaterThanEqual(value));
    }

    /**
     * Less than expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field &lt; value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> lt( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.lessThan(value));
    }

    /**
     * Less than or equals expression fluent helper.
     *
     * @param value
     *            Field value to compare with.
     *
     * @return Fluent helper that represents a <i>field &le; value</i> expression.
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> le( @Nullable final FieldT value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.lessThanEqual(value));
    }

    /**
     * Expression fluent helper supporting the filter function "substringof".
     *
     * @param value
     *            value String value to apply the function on
     *
     * @return Fluent helper that represents a {@code substringof(value,field)} expression
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> substringOf( @Nonnull final String value )
    {
        return new ExpressionFluentHelper<>(
            FilterExpressionString.substringOf(ValueString.literal(value), fieldUntyped.asString()));
    }

    /**
     * Expression fluent helper supporting the filter function "endswith".
     *
     * @param value
     *            String value to apply the function on
     * @return Fluent helper that represents a {@code endswith(field,value)} expression
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> endsWith( @Nonnull final String value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.asString().endsWith(value));
    }

    /**
     * Expression fluent helper supporting the filter function "startswith".
     *
     * @param value
     *            String value to apply the function on
     * @return Fluent helper that represents a {@code startswith(field,value)} expression
     */
    @Nonnull
    public ExpressionFluentHelper<EntityT> startsWith( @Nonnull final String value )
    {
        return new ExpressionFluentHelper<>(fieldUntyped.asString().startsWith(value));
    }
}
