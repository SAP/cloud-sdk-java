package testcomparison.namespaces.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sap.cloud.sdk.datamodel.odata.helper.VdmEntity;
import com.sap.cloud.sdk.s4hana.datamodel.odata.adapter.ODataField;
import com.sap.cloud.sdk.s4hana.datamodel.odata.annotation.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
    private List<TestEntityMultiLink> toMultiLink;
    /**
     * Navigation property <b>to_OtherMultiLink</b> for <b>TestEntityV2</b> to multiple <b>TestEntityOtherMultiLink</b>.
     *
     */
    @SerializedName("to_OtherMultiLink")
    @JsonProperty("to_OtherMultiLink")
    @ODataField(odataName = "to_OtherMultiLink")
    private List<TestEntityOtherMultiLink> toOtherMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntityV2</b> to single <b>TestEntitySingleLink</b>.
     *
     */
    @SerializedName("to_SingleLink")
    @JsonProperty("to_SingleLink")
    @ODataField(odataName = "to_SingleLink")
    @Nullable
    private TestEntitySingleLink toSingleLink;

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
