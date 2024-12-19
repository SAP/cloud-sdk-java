/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.entitywithkeynamedfield;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import testcomparison.namespaces.entitywithkeynamedfield.field.SomeTypeLabelField;
import testcomparison.namespaces.entitywithkeynamedfield.selectable.SomeTypeLabelSelectable;


/**
 * Some Type Label<p></p><p>Original entity name from the Odata EDM: <b>SomeType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class SomeTypeLabel
    extends VdmEntity<SomeTypeLabel>
{

    /**
     * Selector for all available fields of SomeTypeLabel.
     * 
     */
    public final static SomeTypeLabelSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyFieldWithKeyLabel</b></p>
     * 
     * @return
     *     Key
     */
    @Key
    @SerializedName("KeyFieldWithKeyLabel")
    @JsonProperty("KeyFieldWithKeyLabel")
    @Nullable
    @ODataField(odataName = "KeyFieldWithKeyLabel")
    private UUID key_2;
    /**
     * Use with available fluent helpers to apply the <b>KeyFieldWithKeyLabel</b> field to query operations.
     * 
     */
    public final static SomeTypeLabelField<UUID> KEY_2 = new SomeTypeLabelField<UUID>("KeyFieldWithKeyLabel");

    @Nonnull
    @Override
    public Class<SomeTypeLabel> getType() {
        return SomeTypeLabel.class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyFieldWithKeyLabel</b></p>
     * 
     * @param key_2
     *     Key
     */
    public void setKey_2(
        @Nullable
        final UUID key_2) {
        rememberChangedField("KeyFieldWithKeyLabel", this.key_2);
        this.key_2 = key_2;
    }

    @Override
    protected String getEntityCollection() {
        return "SomeConreteType";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("KeyFieldWithKeyLabel", getKey_2());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyFieldWithKeyLabel", getKey_2());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyFieldWithKeyLabel")) {
                final Object value = cloudSdkValues.remove("KeyFieldWithKeyLabel");
                if ((value == null)||(!value.equals(getKey_2()))) {
                    setKey_2(((UUID) value));
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
    public static<T >SomeTypeLabelField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new SomeTypeLabelField<T>(fieldName);
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
    public static<T,DomainT >SomeTypeLabelField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new SomeTypeLabelField<T>(fieldName, typeConverter);
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
