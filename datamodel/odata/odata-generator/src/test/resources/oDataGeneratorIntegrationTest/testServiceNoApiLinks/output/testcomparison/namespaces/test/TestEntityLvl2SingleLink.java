/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

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
import testcomparison.namespaces.test.field.TestEntityLvl2SingleLinkField;
import testcomparison.namespaces.test.selectable.TestEntityLvl2SingleLinkSelectable;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityLvl2SingleLinkType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class TestEntityLvl2SingleLink
    extends VdmEntity<TestEntityLvl2SingleLink>
{

    /**
     * Selector for all available fields of TestEntityLvl2SingleLink.
     * 
     */
    public final static TestEntityLvl2SingleLinkSelectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @return
     *     The keyProperty contained in this entity.
     */
    @Key
    @SerializedName("KeyProperty")
    @JsonProperty("KeyProperty")
    @Nullable
    @ODataField(odataName = "KeyProperty")
    private String keyProperty;
    /**
     * Use with available fluent helpers to apply the <b>KeyProperty</b> field to query operations.
     * 
     */
    public final static TestEntityLvl2SingleLinkField<String> KEY_PROPERTY = new TestEntityLvl2SingleLinkField<String>("KeyProperty");
    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this entity.
     */
    @SerializedName("StringProperty")
    @JsonProperty("StringProperty")
    @Nullable
    @ODataField(odataName = "StringProperty")
    private String stringProperty;
    /**
     * Use with available fluent helpers to apply the <b>StringProperty</b> field to query operations.
     * 
     */
    public final static TestEntityLvl2SingleLinkField<String> STRING_PROPERTY = new TestEntityLvl2SingleLinkField<String>("StringProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this entity.
     */
    @SerializedName("BooleanProperty")
    @JsonProperty("BooleanProperty")
    @Nullable
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class)
    @ODataField(odataName = "BooleanProperty")
    private Boolean booleanProperty;
    /**
     * Use with available fluent helpers to apply the <b>BooleanProperty</b> field to query operations.
     * 
     */
    public final static TestEntityLvl2SingleLinkField<Boolean> BOOLEAN_PROPERTY = new TestEntityLvl2SingleLinkField<Boolean>("BooleanProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this entity.
     */
    @SerializedName("GuidProperty")
    @JsonProperty("GuidProperty")
    @Nullable
    @ODataField(odataName = "GuidProperty")
    private UUID guidProperty;
    /**
     * Use with available fluent helpers to apply the <b>GuidProperty</b> field to query operations.
     * 
     */
    public final static TestEntityLvl2SingleLinkField<UUID> GUID_PROPERTY = new TestEntityLvl2SingleLinkField<UUID>("GuidProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this entity.
     */
    @SerializedName("Int16Property")
    @JsonProperty("Int16Property")
    @Nullable
    @ODataField(odataName = "Int16Property")
    private Short int16Property;
    /**
     * Use with available fluent helpers to apply the <b>Int16Property</b> field to query operations.
     * 
     */
    public final static TestEntityLvl2SingleLinkField<Short> INT16_PROPERTY = new TestEntityLvl2SingleLinkField<Short>("Int16Property");

    @Nonnull
    @Override
    public Class<TestEntityLvl2SingleLink> getType() {
        return TestEntityLvl2SingleLink.class;
    }

    /**
     * (Key Field) Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>KeyProperty</b></p>
     * 
     * @param keyProperty
     *     The keyProperty to set.
     */
    public void setKeyProperty(
        @Nullable
        final String keyProperty) {
        rememberChangedField("KeyProperty", this.keyProperty);
        this.keyProperty = keyProperty;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     The stringProperty to set.
     */
    public void setStringProperty(
        @Nullable
        final String stringProperty) {
        rememberChangedField("StringProperty", this.stringProperty);
        this.stringProperty = stringProperty;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @param booleanProperty
     *     The booleanProperty to set.
     */
    public void setBooleanProperty(
        @Nullable
        final Boolean booleanProperty) {
        rememberChangedField("BooleanProperty", this.booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @param guidProperty
     *     The guidProperty to set.
     */
    public void setGuidProperty(
        @Nullable
        final UUID guidProperty) {
        rememberChangedField("GuidProperty", this.guidProperty);
        this.guidProperty = guidProperty;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @param int16Property
     *     The int16Property to set.
     */
    public void setInt16Property(
        @Nullable
        final Short int16Property) {
        rememberChangedField("Int16Property", this.int16Property);
        this.int16Property = int16Property;
    }

    @Override
    protected String getEntityCollection() {
        return "A_TestEntityLvl2SingleLink";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        result.put("KeyProperty", getKeyProperty());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyProperty", getKeyProperty());
        cloudSdkValues.put("StringProperty", getStringProperty());
        cloudSdkValues.put("BooleanProperty", getBooleanProperty());
        cloudSdkValues.put("GuidProperty", getGuidProperty());
        cloudSdkValues.put("Int16Property", getInt16Property());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyProperty")) {
                final Object value = cloudSdkValues.remove("KeyProperty");
                if ((value == null)||(!value.equals(getKeyProperty()))) {
                    setKeyProperty(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("StringProperty")) {
                final Object value = cloudSdkValues.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((String) value));
                }
            }
            if (cloudSdkValues.containsKey("BooleanProperty")) {
                final Object value = cloudSdkValues.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((Boolean) value));
                }
            }
            if (cloudSdkValues.containsKey("GuidProperty")) {
                final Object value = cloudSdkValues.remove("GuidProperty");
                if ((value == null)||(!value.equals(getGuidProperty()))) {
                    setGuidProperty(((UUID) value));
                }
            }
            if (cloudSdkValues.containsKey("Int16Property")) {
                final Object value = cloudSdkValues.remove("Int16Property");
                if ((value == null)||(!value.equals(getInt16Property()))) {
                    setInt16Property(((Short) value));
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
    public static<T >TestEntityLvl2SingleLinkField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new TestEntityLvl2SingleLinkField<T>(fieldName);
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
    public static<T,DomainT >TestEntityLvl2SingleLinkField<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new TestEntityLvl2SingleLinkField<T>(fieldName, typeConverter);
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
        return (testcomparison.services.TestService.DEFAULT_SERVICE_PATH);
    }

}
