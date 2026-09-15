package com.sap.cloud.sdk.datamodel.odata.helper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sap.cloud.sdk.s4hana.datamodel.odata.exception.NoSuchEntityFieldException;
import com.sap.cloud.sdk.typeconverter.TypeConverter;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Superclass of all entities which contains common elements such as a generic representation of custom fields.
 *
 * @param <ObjectT>
 *            The type of the implementing object.
 */
@ToString( doNotUseGetters = true )
@EqualsAndHashCode( doNotUseGetters = true )
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE )
public abstract class VdmObject<ObjectT>
{
    @JsonIgnore
    private final transient Map<String, Object> customFields = new LinkedHashMap<>();

    /**
     * A mapping of the OData field name to the original value.
     * <p>
     * This should be updated via {@link #rememberChangedField(String, Object)} on every set call of a property.
     */
    @JsonIgnore
    @Nonnull
    protected final transient Map<String, Object> changedOriginalFields = new HashMap<>();

    /**
     * Returns the names of all custom fields of this object.
     *
     * @return The names of the custom fields of this object.
     */
    @Nonnull
    public Set<String> getCustomFieldNames()
    {
        return customFields.keySet();
    }

    /**
     * Returns all custom field names and values of this object.
     *
     * @return All names &amp; values of custom fields as a map.
     */
    @JsonAnyGetter
    @Nonnull
    public Map<String, Object> getCustomFields()
    {
        return customFields;
    }

    /**
     * Sets the value of a single custom field.
     *
     * @param customFieldName
     *            Name of the custom field.
     * @param value
     *            Value of the custom field.
     */
    @JsonAnySetter
    public void setCustomField( @Nonnull final String customFieldName, @Nullable final Object value )
    {
        rememberChangedField(customFieldName, customFields.get(customFieldName));
        customFields.put(customFieldName, value);
    }

    /**
     * Sets the value of a single custom field. If the EntityField passed as parameter holds a TypeConverter, the value
     * will be converted before it's stored.
     *
     * @param customField
     *            Name of the custom field, represented as an EntityField object.
     * @param value
     *            Value of the custom field.
     * @param <FieldT>
     *            The type of the custom field to set.
     */
    public <FieldT> void setCustomField(
        @Nonnull final EntityField<ObjectT, FieldT> customField,
        @Nullable final FieldT value )
    {
        if( customField.getTypeConverter() != null ) {
            setCustomField(customField.getFieldName(), customField.getTypeConverter().toDomain(value).get());
        } else {
            setCustomField(customField.getFieldName(), value);
        }
    }

    /**
     * Checks whether this object contains a custom field with the given name.
     *
     * @param customFieldName
     *            Name of the custom field to check for
     *
     * @return {@code true} if this entity has a custom field with the given name, {@code false} otherwise.
     */
    public boolean hasCustomField( @Nonnull final String customFieldName )
    {
        return customFields.containsKey(customFieldName);
    }

    /**
     * Checks whether this object contains a value for the given custom field.
     *
     * @param customField
     *            Custom field to check for, represented as an {@code EntityField} object.
     *
     * @return {@code true} if this object has a custom field with the name of the given field, {@code false} otherwise.
     */
    public boolean hasCustomField( @Nonnull final EntityField<ObjectT, ?> customField )
    {
        return hasCustomField(customField.getFieldName());
    }

    /**
     * This method allows for retrieval of custom fields that are added to the underlying OData services.
     *
     * @param customFieldName
     *            Name of the field returned by the underlying OData service.
     * @param <FieldT>
     *            The type of the returned field.
     *
     * @return The value of the custom field. Actual type will depend on the type configured in the underlying OData
     *         service.
     *
     * @throws NoSuchEntityFieldException
     *             if no field with the given name could be found.
     */
    @SuppressWarnings( "unchecked" )
    @Nullable
    public <FieldT> FieldT getCustomField( @Nonnull final String customFieldName )
        throws NoSuchEntityFieldException
    {
        if( !hasCustomField(customFieldName) ) {
            throw new NoSuchEntityFieldException("Object has no field with name '" + customFieldName + "'.");
        }
        return (FieldT) customFields.get(customFieldName);
    }

    @SuppressWarnings( "unchecked" )
    @Nullable
    private <FieldT, T> FieldT getCustomField(
        @Nonnull final String customFieldName,
        @Nonnull final TypeConverter<FieldT, T> typeConverter )
        throws NoSuchEntityFieldException
    {
        if( !hasCustomField(customFieldName) ) {
            throw new NoSuchEntityFieldException("Object has no field with name '" + customFieldName + "'.");
        }
        return typeConverter.fromDomain((T) customFields.get(customFieldName)).get();
    }

    /**
     * This method allows for retrieval of custom fields that are added to the underlying OData services. If the
     * EntityField passed as parameter holds a TypeConverter, the value will be converted before it's returned.
     *
     * @param customField
     *            Field returned by the underlying OData service.
     * @param <FieldT>
     *            The type of the returned field.
     *
     * @return The value of the custom field. Actual type will depend on the type configured in the underlying OData
     *         service.
     *
     * @throws NoSuchEntityFieldException
     *             if no field with the given name could be found.
     */
    @Nullable
    public <FieldT> FieldT getCustomField( @Nonnull final EntityField<ObjectT, FieldT> customField )
        throws NoSuchEntityFieldException
    {
        if( customField.getTypeConverter() != null ) {
            return getCustomField(customField.getFieldName(), customField.getTypeConverter());
        } else {
            return getCustomField(customField.getFieldName());
        }
    }

    /**
     * Returns the class of this object.
     *
     * @return The class of this object.
     */
    @Nonnull
    public abstract Class<ObjectT> getType();

    /**
     * Returns the compound key of this object.
     *
     * @return The compound key of this object.
     */
    @Nonnull
    protected Map<String, Object> getKey()
    {
        return new HashMap<>();
    }

    /**
     * Sets the values of all custom fields contained in the given {@code values}.
     * <p>
     * Afterwards, marks all fields as unchanged.
     * </p>
     *
     * @param values
     *            The map of custom fields to set.
     */
    protected void fromMap( final Map<String, Object> values )
    {
        for( final Map.Entry<String, Object> entry : values.entrySet() ) {
            setCustomField(entry.getKey(), entry.getValue());
        }

        resetChangedFields();
    }

    /**
     * Returns a map of all custom fields contained in this object.
     *
     * @return A map of all custom fields contained in this object.
     */
    @Nonnull
    protected Map<String, Object> toMapOfCustomFields()
    {
        return Maps.newHashMap(getCustomFields());
    }

    /**
     * Returns a set of all custom field names contained in this object.
     *
     * @return A set of all custom field names contained in this object.
     */
    @Nonnull
    protected Set<String> getSetOfCustomFields()
    {
        return Sets.newHashSet(getCustomFields().keySet());
    }

    /**
     * Returns a map of all fields contained in this object.
     *
     * @return A map of all fields contained in this object.
     */
    @Nonnull
    protected Map<String, Object> toMapOfFields()
    {
        return new HashMap<>();
    }

    /**
     * Returns a set of all field names contained in this object.
     *
     * @return A set of all field names contained in this object.
     */
    @Nonnull
    protected Set<String> getSetOfFields()
    {
        return Sets.newHashSet(toMapOfFields().keySet());
    }

    /**
     * Returns a map of all navigation properties contained in this object.
     *
     * @return A map of all navigation properties contained in this object.
     */
    @Nonnull
    protected Map<String, Object> toMapOfNavigationProperties()
    {
        return new HashMap<>();
    }

    /**
     * Returns a set of all navigation property names contained in this object.
     *
     * @return A set of all navigation property names contained in this object.
     */
    @Nonnull
    protected Set<String> getSetOfNavigationProperties()
    {
        return Sets.newHashSet(toMapOfNavigationProperties().keySet());
    }

    /**
     * Returns a map of all fields, navigation properties, and custom fields contained in this object.
     *
     * @return A map of all fields, navigation properties, and custom fields contained in this object.
     */
    @Nonnull
    protected Map<String, Object> toMap()
    {
        final Map<String, Object> values = new HashMap<>();

        values.putAll(toMapOfFields());
        values.putAll(toMapOfNavigationProperties());
        values.putAll(toMapOfCustomFields());

        return values;
    }

    /**
     * Returns map of all fields which have been changed on this entity along with their updated values.
     *
     * @return Map containing all changed fields with their current value.
     */
    @Nonnull
    public Map<String, Object> getChangedFields()
    {
        final Map<String, Object> changedFields = new HashMap<>();

        final Map<String, Object> currentFields = toMapOfFields();
        currentFields.putAll(getCustomFields());

        for( final Map.Entry<String, Object> changedOriginalField : changedOriginalFields.entrySet() ) {
            final Object originalValue = changedOriginalField.getValue();
            final Object currentValue = currentFields.get(changedOriginalField.getKey());

            if( originalValue != null && !originalValue.equals(currentValue)
                || originalValue == null && currentValue != null ) {

                changedFields.put(changedOriginalField.getKey(), currentValue);
            }
        }

        return changedFields;
    }

    /**
     * Remembers the original value of a changed field.
     *
     * @param fieldName
     *            The name of the field that is changed.
     * @param valueBeforeChange
     *            The original value before the change.
     */
    protected void rememberChangedField( @Nonnull final String fieldName, @Nullable final Object valueBeforeChange )
    {
        if( !changedOriginalFields.containsKey(fieldName) ) {
            changedOriginalFields.put(fieldName, valueBeforeChange);
        }
    }

    /**
     * Resets the map of all fields which have been changed on this entity.
     * <p>
     * After calling this method, no field is considered changed, until you change the value of fields on this entity
     * afterwards.
     */
    public void resetChangedFields()
    {
        changedOriginalFields.clear();
    }
}
