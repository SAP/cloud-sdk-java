/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataException;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import com.sap.cloud.sdk.typeconverter.TypeConverter;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import testcomparison.namespaces.test.field.TestEntityV2Field;
import testcomparison.namespaces.test.link.TestEntityV2Link;
import testcomparison.namespaces.test.link.TestEntityV2OneToOneLink;
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
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityMultiLink</b>.
     * 
     */
    @SerializedName("to_MultiLink")
    @JsonProperty("to_MultiLink")
    @ODataField(odataName = "to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityMultiLink> toMultiLink;
    /**
     * Navigation property <b>to_OtherMultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityOtherMultiLink</b>.
     * 
     */
    @SerializedName("to_OtherMultiLink")
    @JsonProperty("to_OtherMultiLink")
    @ODataField(odataName = "to_OtherMultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityOtherMultiLink> toOtherMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntityV2</b> to single <b>TestEntitySingleLink</b>.
     * 
     */
    @SerializedName("to_SingleLink")
    @JsonProperty("to_SingleLink")
    @ODataField(odataName = "to_SingleLink")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntitySingleLink toSingleLink;
    /**
     * Use with available fluent helpers to apply the <b>to_MultiLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityV2Link<TestEntityMultiLink> TO_MULTI_LINK = new TestEntityV2Link<TestEntityMultiLink>("to_MultiLink");
    /**
     * Use with available fluent helpers to apply the <b>to_OtherMultiLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityV2Link<TestEntityOtherMultiLink> TO_OTHER_MULTI_LINK = new TestEntityV2Link<TestEntityOtherMultiLink>("to_OtherMultiLink");
    /**
     * Use with available fluent helpers to apply the <b>to_SingleLink</b> navigation property to query operations.
     * 
     */
    public final static TestEntityV2OneToOneLink<TestEntitySingleLink> TO_SINGLE_LINK = new TestEntityV2OneToOneLink<TestEntitySingleLink>("to_SingleLink");

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
            if ((values).containsKey("to_MultiLink")) {
                final Object value = (values).remove("to_MultiLink");
                if (value instanceof Iterable) {
                    if (toMultiLink == null) {
                        toMultiLink = Lists.newArrayList();
                    } else {
                        toMultiLink = Lists.newArrayList(toMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) value)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        TestEntityMultiLink entity;
                        if (toMultiLink.size()>i) {
                            entity = toMultiLink.get(i);
                        } else {
                            entity = new TestEntityMultiLink();
                            toMultiLink.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> inputMap = ((Map<String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
            if ((values).containsKey("to_OtherMultiLink")) {
                final Object value = (values).remove("to_OtherMultiLink");
                if (value instanceof Iterable) {
                    if (toOtherMultiLink == null) {
                        toOtherMultiLink = Lists.newArrayList();
                    } else {
                        toOtherMultiLink = Lists.newArrayList(toOtherMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) value)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        TestEntityOtherMultiLink entity;
                        if (toOtherMultiLink.size()>i) {
                            entity = toOtherMultiLink.get(i);
                        } else {
                            entity = new TestEntityOtherMultiLink();
                            toOtherMultiLink.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> inputMap = ((Map<String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
            if ((values).containsKey("to_SingleLink")) {
                final Object value = (values).remove("to_SingleLink");
                if (value instanceof Map) {
                    if (toSingleLink == null) {
                        toSingleLink = new TestEntitySingleLink();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> inputMap = ((Map<String, Object> ) value);
                    toSingleLink.fromMap(inputMap);
                }
            }
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
    @SuppressWarnings("deprecation")
    protected String getDefaultServicePath() {
        return (testcomparison.services.TestService.DEFAULT_SERVICE_PATH);
    }

    @Nonnull
    @Override
    protected Map<String, Object> toMapOfNavigationProperties() {
        final Map<String, Object> values = super.toMapOfNavigationProperties();
        if (toMultiLink!= null) {
            (values).put("to_MultiLink", toMultiLink);
        }
        if (toOtherMultiLink!= null) {
            (values).put("to_OtherMultiLink", toOtherMultiLink);
        }
        if (toSingleLink!= null) {
            (values).put("to_SingleLink", toSingleLink);
        }
        return values;
    }

    /**
     * Fetches the <b>TestEntityMultiLink</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>TestEntityMultiLink</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityMultiLink> fetchMultiLink() {
        return fetchFieldAsList("to_MultiLink", TestEntityMultiLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntityMultiLink</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityMultiLink> getMultiLinkOrFetch() {
        if (toMultiLink == null) {
            toMultiLink = fetchMultiLink();
        }
        return toMultiLink;
    }

    /**
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV2</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_MultiLink</b> is already loaded, the result will contain the <b>TestEntityMultiLink</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<TestEntityMultiLink>> getMultiLinkIfPresent() {
        return Option.of(toMultiLink);
    }

    /**
     * Overwrites the list of associated <b>TestEntityMultiLink</b> entities for the loaded navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param value
     *     List of <b>TestEntityMultiLink</b> entities.
     */
    public void setMultiLink(
        @Nonnull
        final List<TestEntityMultiLink> value) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.clear();
        toMultiLink.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityMultiLink</b> entities. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>TestEntityMultiLink</b> entities.
     */
    public void addMultiLink(TestEntityMultiLink... entity) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.addAll(Lists.newArrayList(entity));
    }

    /**
     * Fetches the <b>TestEntityOtherMultiLink</b> entities (one to many) associated with this entity. This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     List containing one or more associated <b>TestEntityOtherMultiLink</b> entities. If no entities are associated then an empty list is returned. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityOtherMultiLink> fetchOtherMultiLink() {
        return fetchFieldAsList("to_OtherMultiLink", TestEntityOtherMultiLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntityOtherMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property <b>to_OtherMultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntityOtherMultiLink</b> entities.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nonnull
    public List<TestEntityOtherMultiLink> getOtherMultiLinkOrFetch() {
        if (toOtherMultiLink == null) {
            toOtherMultiLink = fetchOtherMultiLink();
        }
        return toOtherMultiLink;
    }

    /**
     * Retrieval of associated <b>TestEntityOtherMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV2</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_OtherMultiLink</b> is already loaded, the result will contain the <b>TestEntityOtherMultiLink</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<TestEntityOtherMultiLink>> getOtherMultiLinkIfPresent() {
        return Option.of(toOtherMultiLink);
    }

    /**
     * Overwrites the list of associated <b>TestEntityOtherMultiLink</b> entities for the loaded navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property <b>to_OtherMultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param value
     *     List of <b>TestEntityOtherMultiLink</b> entities.
     */
    public void setOtherMultiLink(
        @Nonnull
        final List<TestEntityOtherMultiLink> value) {
        if (toOtherMultiLink == null) {
            toOtherMultiLink = Lists.newArrayList();
        }
        toOtherMultiLink.clear();
        toOtherMultiLink.addAll(value);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityOtherMultiLink</b> entities. This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property <b>to_OtherMultiLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>TestEntityOtherMultiLink</b> entities.
     */
    public void addOtherMultiLink(TestEntityOtherMultiLink... entity) {
        if (toOtherMultiLink == null) {
            toOtherMultiLink = Lists.newArrayList();
        }
        toOtherMultiLink.addAll(Lists.newArrayList(entity));
    }

    /**
     * Fetches the <b>TestEntitySingleLink</b> entity (one to one) associated with this entity. This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * Please note: This method will not cache or persist the query results.
     * 
     * @return
     *     The single associated <b>TestEntitySingleLink</b> entity, or {@code null} if an entity is not associated. 
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public TestEntitySingleLink fetchSingleLink() {
        return fetchFieldAsSingle("to_SingleLink", TestEntitySingleLink.class);
    }

    /**
     * Retrieval of associated <b>TestEntitySingleLink</b> entity (one to one). This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * If the navigation property <b>to_SingleLink</b> of a queried <b>TestEntityV2</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @return
     *     List of associated <b>TestEntitySingleLink</b> entity.
     * @throws ODataException
     *     If the entity is unmanaged, i.e. it has not been retrieved using the OData VDM's services and therefore has no ERP configuration context assigned. An entity is managed if it has been either retrieved using the VDM's services or returned from the VDM's services as the result of a CREATE or UPDATE call. 
     */
    @Nullable
    public TestEntitySingleLink getSingleLinkOrFetch() {
        if (toSingleLink == null) {
            toSingleLink = fetchSingleLink();
        }
        return toSingleLink;
    }

    /**
     * Retrieval of associated <b>TestEntitySingleLink</b> entity (one to one). This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV2</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_SingleLink</b> is already loaded, the result will contain the <b>TestEntitySingleLink</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<TestEntitySingleLink> getSingleLinkIfPresent() {
        return Option.of(toSingleLink);
    }

    /**
     * Overwrites the associated <b>TestEntitySingleLink</b> entity for the loaded navigation property <b>to_SingleLink</b>.
     * 
     * @param value
     *     New <b>TestEntitySingleLink</b> entity.
     */
    public void setSingleLink(final TestEntitySingleLink value) {
        toSingleLink = value;
    }


    /**
     * Helper class to allow for fluent creation of TestEntityV2 instances.
     * 
     */
    public final static class TestEntityV2Builder {

        private List<TestEntityMultiLink> toMultiLink = Lists.newArrayList();
        private List<TestEntityOtherMultiLink> toOtherMultiLink = Lists.newArrayList();
        private TestEntitySingleLink toSingleLink;

        private TestEntityV2 .TestEntityV2Builder toMultiLink(final List<TestEntityMultiLink> value) {
            toMultiLink.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityMultiLink</b>.
         * 
         * @param value
         *     The TestEntityMultiLinks to build this TestEntityV2 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV2 .TestEntityV2Builder multiLink(TestEntityMultiLink... value) {
            return toMultiLink(Lists.newArrayList(value));
        }

        private TestEntityV2 .TestEntityV2Builder toOtherMultiLink(final List<TestEntityOtherMultiLink> value) {
            toOtherMultiLink.addAll(value);
            return this;
        }

        /**
         * Navigation property <b>to_OtherMultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityOtherMultiLink</b>.
         * 
         * @param value
         *     The TestEntityOtherMultiLinks to build this TestEntityV2 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV2 .TestEntityV2Builder otherMultiLink(TestEntityOtherMultiLink... value) {
            return toOtherMultiLink(Lists.newArrayList(value));
        }

        private TestEntityV2 .TestEntityV2Builder toSingleLink(final TestEntitySingleLink value) {
            toSingleLink = value;
            return this;
        }

        /**
         * Navigation property <b>to_SingleLink</b> for <b>TestEntityV2</b> to single <b>TestEntitySingleLink</b>.
         * 
         * @param value
         *     The TestEntitySingleLink to build this TestEntityV2 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV2 .TestEntityV2Builder singleLink(final TestEntitySingleLink value) {
            return toSingleLink(value);
        }

    }

}
