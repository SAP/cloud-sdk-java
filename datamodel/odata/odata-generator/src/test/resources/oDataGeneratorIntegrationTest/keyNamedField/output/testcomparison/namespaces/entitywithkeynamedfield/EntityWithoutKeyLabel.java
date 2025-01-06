/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.namespaces.entitywithkeynamedfield.field.EntityWithoutKeyLabelField;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.EntityWithoutKeyLabelSelectable;


/**
 * Entity without keyLabel<p></p><p>Original entity name from the Odata EDM: <b>WithoutKeyType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class EntityWithoutKeyLabel
    extends VdmEntity<EntityWithoutKeyLabel>
{

    /**
     * Selector for all available fields of EntityWithoutKeyLabel.
     * 
     */
    public final static EntityWithoutKeyLabelSelectable ALL_FIELDS = () -> "*";
    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>SomeField</b></p>
     * 
     * @return
     *     Some Field
     */
    @SerializedName("SomeField")
    @JsonProperty("SomeField")
    @Nullable
    @ODataField(odataName = "SomeField")
    private String someField;
    /**
     * Use with available fluent helpers to apply the <b>SomeField</b> field to query operations.
     * 
     */
    public final static EntityWithoutKeyLabelField<String> SOME_FIELD = new EntityWithoutKeyLabelField<String>("SomeField");

    @Nonnull
    @Override
    public Class<EntityWithoutKeyLabel> getType() {
        return EntityWithoutKeyLabel.class;
    }

    /**
     * Constraints: Not nullable<p>Original property name from the Odata EDM: <b>SomeField</b></p>
     * 
     * @param someField
     *     Some Field
     */
    public void setSomeField(
        @Nullable
        final String someField) {
        rememberChangedField("SomeField", this.someField);
        this.someField = someField;
    }

    @Override
    protected String getEntityCollection() {
        return "WithoutKeyType";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("SomeField", getSomeField());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("SomeField")) {
                final Object value = cloudSdkValues.remove("SomeField");
                if ((value == null)||(!value.equals(getSomeField()))) {
                    setSomeField(((String) value));
                }
            }
        }
        // structured properties
        {
        }
        // navigation properties
        {
        }
        super.fromMap(cloudSdkValues);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param fieldType
     *     The Java type to use for the extension field when performing value comparisons.
     * @return
     *     A representation of an extension field from this entity.
     */
    @Nonnull
    public static<T >EntityWithoutKeyLabelField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new EntityWithoutKeyLabelField<T>(fieldName);
    }

    /**
     * Use with available fluent helpers to apply an extension field to query operations.
     * 
     * @param typeConverter
     *     A TypeConverter<T, DomainT> instance whose first generic type matches the Java type of the field
     * @param fieldName
     *     The name of the extension field as returned by the OData service.
     * @param <T>
     *     The type of the extension field when performing value comparisons.
     * @param <DomainT>
     *     The type of the extension field as returned by the OData service.
     * @return
     *     A representation of an extension field from this entity, holding a reference to the given TypeConverter.
     */
    @Nonnull
    public static<T,DomainT >EntityWithoutKeyLabelField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new EntityWithoutKeyLabelField<T>(fieldName, typeConverter);
    }

    @Override
    @Nullable
    public Destination getDestinationForFetch() {
        return super.getDestinationForFetch();
    }

    @Override
    protected void setServicePathForFetch(
        @Nullable
        final String servicePathForFetch) {
        super.setServicePathForFetch(servicePathForFetch);
    }

    @Override
    public void attachToService(
        @Nullable
        final String servicePath,
        @Nonnull
        final Destination destination) {
        super.attachToService(servicePath, destination);
    }

    @Override
    protected String getDefaultServicePath() {
        return (testcomparison.services.EntitywithkeynamedfieldService.DEFAULT_SERVICE_PATH);
    }

}
