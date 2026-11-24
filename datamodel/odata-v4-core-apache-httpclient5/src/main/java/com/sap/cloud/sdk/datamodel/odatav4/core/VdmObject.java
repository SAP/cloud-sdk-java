package com.sap.cloud.sdk.datamodel.odatav4.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Superclass of all entities which contains common elements such as a generic representation of custom fields.
 *
 * @param <ObjectT>
 *            The type of the implementing object.
 */
@Slf4j
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
    /**
     * The OData V4 JSON key to access an entity EDM type definition.
     */
    public static final String[] ODATA_TYPE_ANNOTATIONS = { "@odata.type", "@type" };

    /**
     * The OData V4 JSON key to access an entity version identifier.
     */
    public static final String[] ODATA_VERSION_ANNOTATIONS = { "@odata.etag", "@etag" };

    @JsonIgnore
    @Nonnull
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
     * Returns the names of the custom fields of this object.
     *
     * @return The names of the custom fields of this object.
     */
    @Nonnull
    public Set<String> getCustomFieldNames()
    {
        return customFields.keySet();
    }

    /**
     * Returns the names and values of a custom field.
     *
     * @return All the names &amp; values of custom fields as a map.
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
    public <
        FieldT> void setCustomField( @Nonnull final SimpleProperty<ObjectT> customField, @Nullable final FieldT value )
    {
        setCustomField(customField.getFieldName(), value);
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
    public boolean hasCustomField( @Nonnull final SimpleProperty<ObjectT> customField )
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
     * @throws NoSuchElementException
     *             if no field with the given name could be found.
     */
    @SuppressWarnings( "unchecked" )
    @Nullable
    public <FieldT> FieldT getCustomField( @Nonnull final String customFieldName )
        throws NoSuchElementException
    {
        if( !hasCustomField(customFieldName) ) {
            final String msg = "Object has no field with name '" + customFieldName + "'.";
            log.debug(msg);
            throw new NoSuchElementException(msg);
        }
        return (FieldT) customFields.get(customFieldName);
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
     * @throws NoSuchElementException
     *             if no field with the given name could be found.
     */
    @Nullable
    public <FieldT> FieldT getCustomField( @Nonnull final SimpleProperty<ObjectT> customField )
        throws NoSuchElementException
    {
        return getCustomField(customField.getFieldName());
    }

    /**
     * Returns the annotation properties.
     *
     * @return List of OData annotation properties.
     */
    @Nonnull
    public Map<String, String> getAnnotationProperties()
    {
        final Map<String, String> properties = new HashMap<>();
        properties.put(ODATA_TYPE_ANNOTATIONS[0], "#" + getOdataType());

        return properties;
    }

    /**
     * Returns the EDMX type of this entity.
     *
     * @return The EDMX type of this entity.
     */
    @Nonnull
    public abstract String getOdataType();

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
    protected ODataEntityKey getKey()
    {
        return new ODataEntityKey(ODataProtocol.V4);
    }

    /**
     * Read entity data from generic map.
     *
     * @param values
     *            The key-value map.
     */
    protected void fromMap( final Map<String, Object> values )
    {
        for( final Map.Entry<String, Object> entry : values.entrySet() ) {
            setCustomField(entry.getKey(), entry.getValue());
        }

        resetChangedFields();
    }

    /**
     * Get the custom fields as value map.
     *
     * @return The custom fields.
     */
    @Nonnull
    protected Map<String, Object> toMapOfCustomFields()
    {
        return new HashMap<>(getCustomFields());
    }

    /**
     * Get the custom field names.
     *
     * @return The custom field names.
     */
    @Nonnull
    protected Set<String> getSetOfCustomFields()
    {
        return new HashSet<>(getCustomFields().keySet());
    }

    /**
     * Get all fields as map.
     *
     * @return The fields as map.
     */
    @Nonnull
    protected Map<String, Object> toMapOfFields()
    {
        return new HashMap<>();
    }

    /**
     * Get the field names.
     *
     * @return The field names.
     */
    @Nonnull
    protected Set<String> getSetOfFields()
    {
        return Sets.newHashSet(toMapOfFields().keySet());
    }

    /**
     * Get navigation properties as map.
     *
     * @return The navigation properties.
     */
    @Nonnull
    protected Map<String, Object> toMapOfNavigationProperties()
    {
        return new HashMap<>();
    }

    /**
     * Get navigation property names as set.
     *
     * @return The navigation property names.
     */
    @Nonnull
    protected Set<String> getSetOfNavigationProperties()
    {
        return Sets.newHashSet(toMapOfNavigationProperties().keySet());
    }

    /**
     * Translate the entity data to key-value map.
     *
     * @return The map representation of the entity.
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
        final Map<String, Object> currentFields = new HashMap<>();
        currentFields.putAll(toMapOfFields());
        currentFields.putAll(getCustomFields());

        return Maps.filterEntries(currentFields, f -> f != null && isFieldChanged(f.getKey(), f.getValue()));
    }

    private boolean isFieldChanged( @Nonnull final String fieldName, @Nullable final Object currentValue )
    {
        final Object overriddenValue = changedOriginalFields.get(fieldName);
        if( currentValue == null && overriddenValue == null ) {
            return false;
        }

        if( changedOriginalFields.containsKey(fieldName) && !Objects.equals(currentValue, overriddenValue) ) {
            return true;
        }

        // property was either not updated directly, or the values are still "equal" (according to Objects.equals)
        if( currentValue instanceof VdmObject ) {
            return !((VdmObject<?>) currentValue).getChangedFields().isEmpty();
        }

        if( currentValue instanceof Iterable ) {
            return Streams
                .stream((Iterable<?>) currentValue)
                .filter(VdmObject.class::isInstance)
                .anyMatch(obj -> !((VdmObject<?>) obj).getChangedFields().isEmpty());
        }

        return false;
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
