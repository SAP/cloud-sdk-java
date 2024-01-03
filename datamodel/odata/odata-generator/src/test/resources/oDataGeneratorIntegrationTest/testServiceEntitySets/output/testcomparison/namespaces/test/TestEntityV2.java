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
import testcomparison.namespaces.test.field.TestEntityV2Field;
import testcomparison.namespaces.test.selectable.TestEntityV2Selectable;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityV2</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataVdmEntityAdapterFactory.class)
public class TestEntityV2
    extends VdmEntity<TestEntityV2>
{

    /**
     * Selector for all available fields of TestEntityV2.
     * 
     */
    public final static TestEntityV2Selectable ALL_FIELDS = () -> "*";
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyGuid</b></p>
     * 
     * @return
     *     The keyPropertyGuid contained in this entity.
     */
    @Key
    @SerializedName("KeyPropertyGuid")
    @JsonProperty("KeyPropertyGuid")
    @Nullable
    @ODataField(odataName = "KeyPropertyGuid")
    private UUID keyPropertyGuid;
    /**
     * Use with available fluent helpers to apply the <b>KeyPropertyGuid</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<UUID> KEY_PROPERTY_GUID = new TestEntityV2Field<UUID>("KeyPropertyGuid");
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyString</b></p>
     * 
     * @return
     *     The keyPropertyString contained in this entity.
     */
    @Key
    @SerializedName("KeyPropertyString")
    @JsonProperty("KeyPropertyString")
    @Nullable
    @ODataField(odataName = "KeyPropertyString")
    private String keyPropertyString;
    /**
     * Use with available fluent helpers to apply the <b>KeyPropertyString</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<String> KEY_PROPERTY_STRING = new TestEntityV2Field<String>("KeyPropertyString");
    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
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
    public final static TestEntityV2Field<String> STRING_PROPERTY = new TestEntityV2Field<String>("StringProperty");
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
    public final static TestEntityV2Field<Boolean> BOOLEAN_PROPERTY = new TestEntityV2Field<Boolean>("BooleanProperty");
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
    public final static TestEntityV2Field<UUID> GUID_PROPERTY = new TestEntityV2Field<UUID>("GuidProperty");
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
    public final static TestEntityV2Field<Short> INT16_PROPERTY = new TestEntityV2Field<Short>("Int16Property");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int32Property</b></p>
     * 
     * @return
     *     The int32Property contained in this entity.
     */
    @SerializedName("Int32Property")
    @JsonProperty("Int32Property")
    @Nullable
    @ODataField(odataName = "Int32Property")
    private Integer int32Property;
    /**
     * Use with available fluent helpers to apply the <b>Int32Property</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Integer> INT32_PROPERTY = new TestEntityV2Field<Integer>("Int32Property");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int64Property</b></p>
     * 
     * @return
     *     The int64Property contained in this entity.
     */
    @SerializedName("Int64Property")
    @JsonProperty("Int64Property")
    @Nullable
    @ODataField(odataName = "Int64Property")
    private Long int64Property;
    /**
     * Use with available fluent helpers to apply the <b>Int64Property</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Long> INT64_PROPERTY = new TestEntityV2Field<Long>("Int64Property");
    /**
     * Constraints: Not nullable, Precision: 5, Scale: 2 <p>Original property name from the Odata EDM: <b>DecimalProperty</b></p>
     * 
     * @return
     *     The decimalProperty contained in this entity.
     */
    @SerializedName("DecimalProperty")
    @JsonProperty("DecimalProperty")
    @Nullable
    @ODataField(odataName = "DecimalProperty")
    private BigDecimal decimalProperty;
    /**
     * Use with available fluent helpers to apply the <b>DecimalProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<BigDecimal> DECIMAL_PROPERTY = new TestEntityV2Field<BigDecimal>("DecimalProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>SingleProperty</b></p>
     * 
     * @return
     *     The singleProperty contained in this entity.
     */
    @SerializedName("SingleProperty")
    @JsonProperty("SingleProperty")
    @Nullable
    @ODataField(odataName = "SingleProperty")
    private Float singleProperty;
    /**
     * Use with available fluent helpers to apply the <b>SingleProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Float> SINGLE_PROPERTY = new TestEntityV2Field<Float>("SingleProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>DoubleProperty</b></p>
     * 
     * @return
     *     The doubleProperty contained in this entity.
     */
    @SerializedName("DoubleProperty")
    @JsonProperty("DoubleProperty")
    @Nullable
    @ODataField(odataName = "DoubleProperty")
    private Double doubleProperty;
    /**
     * Use with available fluent helpers to apply the <b>DoubleProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Double> DOUBLE_PROPERTY = new TestEntityV2Field<Double>("DoubleProperty");
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeProperty</b></p>
     * 
     * @return
     *     The timeProperty contained in this entity.
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
     * Use with available fluent helpers to apply the <b>TimeProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<LocalTime> TIME_PROPERTY = new TestEntityV2Field<LocalTime>("TimeProperty");
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeProperty</b></p>
     * 
     * @return
     *     The dateTimeProperty contained in this entity.
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
     * Use with available fluent helpers to apply the <b>DateTimeProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<LocalDateTime> DATE_TIME_PROPERTY = new TestEntityV2Field<LocalDateTime>("DateTimeProperty");
    /**
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @return
     *     The dateTimeOffSetProperty contained in this entity.
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
     * Use with available fluent helpers to apply the <b>DateTimeOffSetProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<ZonedDateTime> DATE_TIME_OFF_SET_PROPERTY = new TestEntityV2Field<ZonedDateTime>("DateTimeOffSetProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>ByteProperty</b></p>
     * 
     * @return
     *     The byteProperty contained in this entity.
     */
    @SerializedName("ByteProperty")
    @JsonProperty("ByteProperty")
    @Nullable
    @ODataField(odataName = "ByteProperty")
    private Short byteProperty;
    /**
     * Use with available fluent helpers to apply the <b>ByteProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Short> BYTE_PROPERTY = new TestEntityV2Field<Short>("ByteProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>SByteProperty</b></p>
     * 
     * @return
     *     The sByteProperty contained in this entity.
     */
    @SerializedName("SByteProperty")
    @JsonProperty("SByteProperty")
    @Nullable
    @ODataField(odataName = "SByteProperty")
    private Byte sByteProperty;
    /**
     * Use with available fluent helpers to apply the <b>SByteProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<Byte> S_BYTE_PROPERTY = new TestEntityV2Field<Byte>("SByteProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>BinaryProperty</b></p>
     * 
     * @return
     *     The binaryProperty contained in this entity.
     */
    @SerializedName("BinaryProperty")
    @JsonProperty("BinaryProperty")
    @Nullable
    @JsonAdapter(com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataBinaryAdapter.class)
    @ODataField(odataName = "BinaryProperty")
    private byte[] binaryProperty;
    /**
     * Use with available fluent helpers to apply the <b>BinaryProperty</b> field to query operations.
     * 
     */
    public final static TestEntityV2Field<byte[]> BINARY_PROPERTY = new TestEntityV2Field<byte[]>("BinaryProperty");
    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @return
     *     The complexTypeProperty contained in this entity.
     */
    @SerializedName("ComplexTypeProperty")
    @JsonProperty("ComplexTypeProperty")
    @Nullable
    @ODataField(odataName = "ComplexTypeProperty")
    private A_TestComplexType complexTypeProperty;

    @Nonnull
    @Override
    public Class<TestEntityV2> getType() {
        return TestEntityV2 .class;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyGuid</b></p>
     * 
     * @param keyPropertyGuid
     *     The keyPropertyGuid to set.
     */
    public void setKeyPropertyGuid(
        @Nullable
        final UUID keyPropertyGuid) {
        rememberChangedField("KeyPropertyGuid", this.keyPropertyGuid);
        this.keyPropertyGuid = keyPropertyGuid;
    }

    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyString</b></p>
     * 
     * @param keyPropertyString
     *     The keyPropertyString to set.
     */
    public void setKeyPropertyString(
        @Nullable
        final String keyPropertyString) {
        rememberChangedField("KeyPropertyString", this.keyPropertyString);
        this.keyPropertyString = keyPropertyString;
    }

    /**
     * Constraints: Not nullable, Maximum length: 100 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
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

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int32Property</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>Int64Property</b></p>
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
     * Constraints: Not nullable, Precision: 5, Scale: 2 <p>Original property name from the Odata EDM: <b>DecimalProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>SingleProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>DoubleProperty</b></p>
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
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeProperty</b></p>
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
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeProperty</b></p>
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
     * Constraints: Not nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>ByteProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>SByteProperty</b></p>
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
     * Constraints: none<p>Original property name from the Odata EDM: <b>BinaryProperty</b></p>
     * 
     * @param binaryProperty
     *     The binaryProperty to set.
     */
    public void setBinaryProperty(
        @Nullable
        final byte[] binaryProperty) {
        rememberChangedField("BinaryProperty", this.binaryProperty);
        this.binaryProperty = binaryProperty;
    }

    /**
     * Constraints: none<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @param complexTypeProperty
     *     The complexTypeProperty to set.
     */
    public void setComplexTypeProperty(
        @Nullable
        final A_TestComplexType complexTypeProperty) {
        rememberChangedField("ComplexTypeProperty", this.complexTypeProperty);
        this.complexTypeProperty = complexTypeProperty;
    }

    @Override
    protected String getEntityCollection() {
        return "A_TestEntity";
    }

    @Nonnull
    @Override
    protected Map<String, Object> getKey() {
        final Map<String, Object> result = Maps.newHashMap();
        result.put("KeyPropertyGuid", getKeyPropertyGuid());
        result.put("KeyPropertyString", getKeyPropertyString());
        return result;
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfFields() {
        final Map<String, Object> values = super.toMapOfFields();
        values.put("KeyPropertyGuid", getKeyPropertyGuid());
        values.put("KeyPropertyString", getKeyPropertyString());
        values.put("StringProperty", getStringProperty());
        values.put("BooleanProperty", getBooleanProperty());
        values.put("GuidProperty", getGuidProperty());
        values.put("Int16Property", getInt16Property());
        values.put("Int32Property", getInt32Property());
        values.put("Int64Property", getInt64Property());
        values.put("DecimalProperty", getDecimalProperty());
        values.put("SingleProperty", getSingleProperty());
        values.put("DoubleProperty", getDoubleProperty());
        values.put("TimeProperty", getTimeProperty());
        values.put("DateTimeProperty", getDateTimeProperty());
        values.put("DateTimeOffSetProperty", getDateTimeOffSetProperty());
        values.put("ByteProperty", getByteProperty());
        values.put("SByteProperty", getSByteProperty());
        values.put("BinaryProperty", getBinaryProperty());
        values.put("ComplexTypeProperty", getComplexTypeProperty());
        return values;
    }

    @Override
    protected void fromMap(final Map<String, Object> inputValues) {
        final Map<String, Object> values = Maps.newHashMap(inputValues);
        // simple properties
        {
            if (values.containsKey("KeyPropertyGuid")) {
                final Object value = values.remove("KeyPropertyGuid");
                if ((value == null)||(!value.equals(getKeyPropertyGuid()))) {
                    setKeyPropertyGuid(((UUID) value));
                }
            }
            if (values.containsKey("KeyPropertyString")) {
                final Object value = values.remove("KeyPropertyString");
                if ((value == null)||(!value.equals(getKeyPropertyString()))) {
                    setKeyPropertyString(((String) value));
                }
            }
            if (values.containsKey("StringProperty")) {
                final Object value = values.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((String) value));
                }
            }
            if (values.containsKey("BooleanProperty")) {
                final Object value = values.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((Boolean) value));
                }
            }
            if (values.containsKey("GuidProperty")) {
                final Object value = values.remove("GuidProperty");
                if ((value == null)||(!value.equals(getGuidProperty()))) {
                    setGuidProperty(((UUID) value));
                }
            }
            if (values.containsKey("Int16Property")) {
                final Object value = values.remove("Int16Property");
                if ((value == null)||(!value.equals(getInt16Property()))) {
                    setInt16Property(((Short) value));
                }
            }
            if (values.containsKey("Int32Property")) {
                final Object value = values.remove("Int32Property");
                if ((value == null)||(!value.equals(getInt32Property()))) {
                    setInt32Property(((Integer) value));
                }
            }
            if (values.containsKey("Int64Property")) {
                final Object value = values.remove("Int64Property");
                if ((value == null)||(!value.equals(getInt64Property()))) {
                    setInt64Property(((Long) value));
                }
            }
            if (values.containsKey("DecimalProperty")) {
                final Object value = values.remove("DecimalProperty");
                if ((value == null)||(!value.equals(getDecimalProperty()))) {
                    setDecimalProperty(((BigDecimal) value));
                }
            }
            if (values.containsKey("SingleProperty")) {
                final Object value = values.remove("SingleProperty");
                if ((value == null)||(!value.equals(getSingleProperty()))) {
                    setSingleProperty(((Float) value));
                }
            }
            if (values.containsKey("DoubleProperty")) {
                final Object value = values.remove("DoubleProperty");
                if ((value == null)||(!value.equals(getDoubleProperty()))) {
                    setDoubleProperty(((Double) value));
                }
            }
            if (values.containsKey("TimeProperty")) {
                final Object value = values.remove("TimeProperty");
                if ((value == null)||(!value.equals(getTimeProperty()))) {
                    setTimeProperty(((LocalTime) value));
                }
            }
            if (values.containsKey("DateTimeProperty")) {
                final Object value = values.remove("DateTimeProperty");
                if ((value == null)||(!value.equals(getDateTimeProperty()))) {
                    setDateTimeProperty(((LocalDateTime) value));
                }
            }
            if (values.containsKey("DateTimeOffSetProperty")) {
                final Object value = values.remove("DateTimeOffSetProperty");
                if ((value == null)||(!value.equals(getDateTimeOffSetProperty()))) {
                    setDateTimeOffSetProperty(((ZonedDateTime) value));
                }
            }
            if (values.containsKey("ByteProperty")) {
                final Object value = values.remove("ByteProperty");
                if ((value == null)||(!value.equals(getByteProperty()))) {
                    setByteProperty(((Short) value));
                }
            }
            if (values.containsKey("SByteProperty")) {
                final Object value = values.remove("SByteProperty");
                if ((value == null)||(!value.equals(getSByteProperty()))) {
                    setSByteProperty(((Byte) value));
                }
            }
            if (values.containsKey("BinaryProperty")) {
                final Object value = values.remove("BinaryProperty");
                if ((value == null)||(!value.equals(getBinaryProperty()))) {
                    setBinaryProperty(((byte[]) value));
                }
            }
        }
        // structured properties
        {
            if (values.containsKey("ComplexTypeProperty")) {
                final Object value = values.remove("ComplexTypeProperty");
                if (value instanceof Map) {
                    if (getComplexTypeProperty() == null) {
                        setComplexTypeProperty(new A_TestComplexType());
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
        super.fromMap(values);
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
    public static<T >TestEntityV2Field<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final Class<T> fieldType) {
        return new TestEntityV2Field<T>(fieldName);
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
    public static<T,DomainT >TestEntityV2Field<T> field(
        @Nonnull
        final String fieldName,
        @Nonnull
        final TypeConverter<T, DomainT> typeConverter) {
        return new TestEntityV2Field<T>(fieldName, typeConverter);
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

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> values = super.toMapOfNavigationProperties();
        return values;
    }

}
