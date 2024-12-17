/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmComplex;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * <p>Original complex type name from the Odata EDM: <b>A_TestComplexType</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class A_TestComplexType
    extends VdmComplex<A_TestComplexType>
{

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     
     */
    @SerializedName("StringProperty")
    @JsonProperty("StringProperty")
    @Nullable
    @ODataField(odataName = "StringProperty")
    private String stringProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @param booleanProperty
     *     
     */
    @SerializedName("BooleanProperty")
    @JsonProperty("BooleanProperty")
    @Nullable
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBooleanAdapter.class)
    @ODataField(odataName = "BooleanProperty")
    private Boolean booleanProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @param guidProperty
     *     
     */
    @SerializedName("GuidProperty")
    @JsonProperty("GuidProperty")
    @Nullable
    @ODataField(odataName = "GuidProperty")
    private UUID guidProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @param int16Property
     *     
     */
    @SerializedName("Int16Property")
    @JsonProperty("Int16Property")
    @Nullable
    @ODataField(odataName = "Int16Property")
    private Short int16Property;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>Int32Property</b></p>
     * 
     * @param int32Property
     *     
     */
    @SerializedName("Int32Property")
    @JsonProperty("Int32Property")
    @Nullable
    @ODataField(odataName = "Int32Property")
    private Integer int32Property;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>Int64Property</b></p>
     * 
     * @param int64Property
     *     
     */
    @SerializedName("Int64Property")
    @JsonProperty("Int64Property")
    @Nullable
    @ODataField(odataName = "Int64Property")
    private Long int64Property;
    /**
     * Constraints: Not nullable, Precision: 5, Scale: 2 <p>Original property from the Odata EDM: <b>DecimalProperty</b></p>
     * 
     * @param decimalProperty
     *     
     */
    @SerializedName("DecimalProperty")
    @JsonProperty("DecimalProperty")
    @Nullable
    @ODataField(odataName = "DecimalProperty")
    private BigDecimal decimalProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>SingleProperty</b></p>
     * 
     * @param singleProperty
     *     
     */
    @SerializedName("SingleProperty")
    @JsonProperty("SingleProperty")
    @Nullable
    @ODataField(odataName = "SingleProperty")
    private Float singleProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>DoubleProperty</b></p>
     * 
     * @param doubleProperty
     *     
     */
    @SerializedName("DoubleProperty")
    @JsonProperty("DoubleProperty")
    @Nullable
    @ODataField(odataName = "DoubleProperty")
    private Double doubleProperty;
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>TimeProperty</b></p>
     * 
     * @param timeProperty
     *     
     */
    @SerializedName("TimeProperty")
    @JsonProperty("TimeProperty")
    @Nullable
    @JsonSerialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeSerializer.class)
    @JsonDeserialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalTimeDeserializer.class)
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeAdapter.class)
    @ODataField(odataName = "TimeProperty", converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalTimeCalendarConverter.class)
    private LocalTime timeProperty;
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>DateTimeProperty</b></p>
     * 
     * @param dateTimeProperty
     *     
     */
    @SerializedName("DateTimeProperty")
    @JsonProperty("DateTimeProperty")
    @Nullable
    @JsonSerialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeSerializer.class)
    @JsonDeserialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonLocalDateTimeDeserializer.class)
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeAdapter.class)
    @ODataField(odataName = "DateTimeProperty", converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.LocalDateTimeCalendarConverter.class)
    private LocalDateTime dateTimeProperty;
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @param dateTimeOffSetProperty
     *     
     */
    @SerializedName("DateTimeOffSetProperty")
    @JsonProperty("DateTimeOffSetProperty")
    @Nullable
    @JsonSerialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeSerializer.class)
    @JsonDeserialize(using = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.JacksonZonedDateTimeDeserializer.class)
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeAdapter.class)
    @ODataField(odataName = "DateTimeOffSetProperty", converter = com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ZonedDateTimeCalendarConverter.class)
    private ZonedDateTime dateTimeOffSetProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>ByteProperty</b></p>
     * 
     * @param byteProperty
     *     
     */
    @SerializedName("ByteProperty")
    @JsonProperty("ByteProperty")
    @Nullable
    @ODataField(odataName = "ByteProperty")
    private Short byteProperty;
    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>SByteProperty</b></p>
     * 
     * @param sByteProperty
     *     
     */
    @SerializedName("SByteProperty")
    @JsonProperty("SByteProperty")
    @Nullable
    @ODataField(odataName = "SByteProperty")
    private Byte sByteProperty;
    /**
     * Constraints: Nullable<p>Original property from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @param complexTypeProperty
     *     
     */
    @SerializedName("ComplexTypeProperty")
    @JsonProperty("ComplexTypeProperty")
    @Nullable
    @ODataField(odataName = "ComplexTypeProperty")
    private A_TestNestedComplexType complexTypeProperty;

    @Nonnull
    @Override
    public Class<A_TestComplexType> getType() {
        return A_TestComplexType.class;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("StringProperty", getStringProperty());
        cloudSdkValues.put("BooleanProperty", getBooleanProperty());
        cloudSdkValues.put("GuidProperty", getGuidProperty());
        cloudSdkValues.put("Int16Property", getInt16Property());
        cloudSdkValues.put("Int32Property", getInt32Property());
        cloudSdkValues.put("Int64Property", getInt64Property());
        cloudSdkValues.put("DecimalProperty", getDecimalProperty());
        cloudSdkValues.put("SingleProperty", getSingleProperty());
        cloudSdkValues.put("DoubleProperty", getDoubleProperty());
        cloudSdkValues.put("TimeProperty", getTimeProperty());
        cloudSdkValues.put("DateTimeProperty", getDateTimeProperty());
        cloudSdkValues.put("DateTimeOffSetProperty", getDateTimeOffSetProperty());
        cloudSdkValues.put("ByteProperty", getByteProperty());
        cloudSdkValues.put("SByteProperty", getSByteProperty());
        cloudSdkValues.put("ComplexTypeProperty", getComplexTypeProperty());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> cloudSdkValues = Maps.newHashMap(inputValues);
        // simple properties
        {
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
            if (cloudSdkValues.containsKey("Int32Property")) {
                final Object value = cloudSdkValues.remove("Int32Property");
                if ((value == null)||(!value.equals(getInt32Property()))) {
                    setInt32Property(((Integer) value));
                }
            }
            if (cloudSdkValues.containsKey("Int64Property")) {
                final Object value = cloudSdkValues.remove("Int64Property");
                if ((value == null)||(!value.equals(getInt64Property()))) {
                    setInt64Property(((Long) value));
                }
            }
            if (cloudSdkValues.containsKey("DecimalProperty")) {
                final Object value = cloudSdkValues.remove("DecimalProperty");
                if ((value == null)||(!value.equals(getDecimalProperty()))) {
                    setDecimalProperty(((BigDecimal) value));
                }
            }
            if (cloudSdkValues.containsKey("SingleProperty")) {
                final Object value = cloudSdkValues.remove("SingleProperty");
                if ((value == null)||(!value.equals(getSingleProperty()))) {
                    setSingleProperty(((Float) value));
                }
            }
            if (cloudSdkValues.containsKey("DoubleProperty")) {
                final Object value = cloudSdkValues.remove("DoubleProperty");
                if ((value == null)||(!value.equals(getDoubleProperty()))) {
                    setDoubleProperty(((Double) value));
                }
            }
            if (cloudSdkValues.containsKey("TimeProperty")) {
                final Object value = cloudSdkValues.remove("TimeProperty");
                if ((value == null)||(!value.equals(getTimeProperty()))) {
                    setTimeProperty(((LocalTime) value));
                }
            }
            if (cloudSdkValues.containsKey("DateTimeProperty")) {
                final Object value = cloudSdkValues.remove("DateTimeProperty");
                if ((value == null)||(!value.equals(getDateTimeProperty()))) {
                    setDateTimeProperty(((LocalDateTime) value));
                }
            }
            if (cloudSdkValues.containsKey("DateTimeOffSetProperty")) {
                final Object value = cloudSdkValues.remove("DateTimeOffSetProperty");
                if ((value == null)||(!value.equals(getDateTimeOffSetProperty()))) {
                    setDateTimeOffSetProperty(((ZonedDateTime) value));
                }
            }
            if (cloudSdkValues.containsKey("ByteProperty")) {
                final Object value = cloudSdkValues.remove("ByteProperty");
                if ((value == null)||(!value.equals(getByteProperty()))) {
                    setByteProperty(((Short) value));
                }
            }
            if (cloudSdkValues.containsKey("SByteProperty")) {
                final Object value = cloudSdkValues.remove("SByteProperty");
                if ((value == null)||(!value.equals(getSByteProperty()))) {
                    setSByteProperty(((Byte) value));
                }
            }
        }
        // structured properties
        {
            if (cloudSdkValues.containsKey("ComplexTypeProperty")) {
                final Object value = cloudSdkValues.remove("ComplexTypeProperty");
                if (value instanceof Map) {
                    if (getComplexTypeProperty() == null) {
                        setComplexTypeProperty(new A_TestNestedComplexType());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    getComplexTypeProperty().fromMap(inputMap);
                }
                if ((value == null)&&(getComplexTypeProperty()!= null)) {
                    setComplexTypeProperty(null);
                }
            }
        }
        // navigation properties
        {
        }
        super.fromMap(cloudSdkValues);
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newHashMap();
        return result;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property from the Odata EDM: <b>StringProperty</b></p>
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
     * Constraints: none<p>Original property from the Odata EDM: <b>BooleanProperty</b></p>
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
     * Constraints: none<p>Original property from the Odata EDM: <b>GuidProperty</b></p>
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
     * Constraints: none<p>Original property from the Odata EDM: <b>Int16Property</b></p>
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

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>Int32Property</b></p>
     * 
     * @param int32Property
     *     The int32Property to set.
     */
    public void setInt32Property(
        @Nullable
        final Integer int32Property) {
        rememberChangedField("Int32Property", this.int32Property);
        this.int32Property = int32Property;
    }

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>Int64Property</b></p>
     * 
     * @param int64Property
     *     The int64Property to set.
     */
    public void setInt64Property(
        @Nullable
        final Long int64Property) {
        rememberChangedField("Int64Property", this.int64Property);
        this.int64Property = int64Property;
    }

    /**
     * Constraints: Not nullable, Precision: 5, Scale: 2 <p>Original property from the Odata EDM: <b>DecimalProperty</b></p>
     * 
     * @param decimalProperty
     *     The decimalProperty to set.
     */
    public void setDecimalProperty(
        @Nullable
        final BigDecimal decimalProperty) {
        rememberChangedField("DecimalProperty", this.decimalProperty);
        this.decimalProperty = decimalProperty;
    }

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>SingleProperty</b></p>
     * 
     * @param singleProperty
     *     The singleProperty to set.
     */
    public void setSingleProperty(
        @Nullable
        final Float singleProperty) {
        rememberChangedField("SingleProperty", this.singleProperty);
        this.singleProperty = singleProperty;
    }

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>DoubleProperty</b></p>
     * 
     * @param doubleProperty
     *     The doubleProperty to set.
     */
    public void setDoubleProperty(
        @Nullable
        final Double doubleProperty) {
        rememberChangedField("DoubleProperty", this.doubleProperty);
        this.doubleProperty = doubleProperty;
    }

    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>TimeProperty</b></p>
     * 
     * @param timeProperty
     *     The timeProperty to set.
     */
    public void setTimeProperty(
        @Nullable
        final LocalTime timeProperty) {
        rememberChangedField("TimeProperty", this.timeProperty);
        this.timeProperty = timeProperty;
    }

    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>DateTimeProperty</b></p>
     * 
     * @param dateTimeProperty
     *     The dateTimeProperty to set.
     */
    public void setDateTimeProperty(
        @Nullable
        final LocalDateTime dateTimeProperty) {
        rememberChangedField("DateTimeProperty", this.dateTimeProperty);
        this.dateTimeProperty = dateTimeProperty;
    }

    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @param dateTimeOffSetProperty
     *     The dateTimeOffSetProperty to set.
     */
    public void setDateTimeOffSetProperty(
        @Nullable
        final ZonedDateTime dateTimeOffSetProperty) {
        rememberChangedField("DateTimeOffSetProperty", this.dateTimeOffSetProperty);
        this.dateTimeOffSetProperty = dateTimeOffSetProperty;
    }

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>ByteProperty</b></p>
     * 
     * @param byteProperty
     *     The byteProperty to set.
     */
    public void setByteProperty(
        @Nullable
        final Short byteProperty) {
        rememberChangedField("ByteProperty", this.byteProperty);
        this.byteProperty = byteProperty;
    }

    /**
     * Constraints: none<p>Original property from the Odata EDM: <b>SByteProperty</b></p>
     * 
     * @param sByteProperty
     *     The sByteProperty to set.
     */
    public void setSByteProperty(
        @Nullable
        final Byte sByteProperty) {
        rememberChangedField("SByteProperty", this.sByteProperty);
        this.sByteProperty = sByteProperty;
    }

    /**
     * Constraints: Nullable<p>Original property from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @param complexTypeProperty
     *     The complexTypeProperty to set.
     */
    public void setComplexTypeProperty(
        @Nullable
        final A_TestNestedComplexType complexTypeProperty) {
        rememberChangedField("ComplexTypeProperty", this.complexTypeProperty);
        this.complexTypeProperty = complexTypeProperty;
    }

}
