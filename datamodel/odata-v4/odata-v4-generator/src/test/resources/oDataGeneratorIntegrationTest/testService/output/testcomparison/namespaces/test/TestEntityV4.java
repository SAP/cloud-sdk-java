/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.DecimalDescriptor;
import com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntity;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEntitySet;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;
import com.sap.cloud.sdk.result.ElementName;
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
import testcomparison.services.TestService;


/**
 * <p>Original entity name from the Odata EDM: <b>A_TestEntityV4</b></p>
 * 
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, callSuper = true)
@EqualsAndHashCode(doNotUseGetters = true, callSuper = true)
@JsonAdapter(com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class)
@JsonSerialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class)
@JsonDeserialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class)
public class TestEntityV4
    extends VdmEntity<TestEntityV4>
    implements VdmEntitySet
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestEntityV4";
    /**
     * Selector for all available fields of TestEntityV4.
     * 
     */
    public final static SimpleProperty<TestEntityV4> ALL_FIELDS = all();
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyGuid</b></p>
     * 
     * @return
     *     The keyPropertyGuid contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyPropertyGuid")
    private UUID keyPropertyGuid;
    public final static SimpleProperty.Guid<TestEntityV4> KEY_PROPERTY_GUID = new SimpleProperty.Guid<TestEntityV4>(TestEntityV4 .class, "KeyPropertyGuid");
    /**
     * (Key Field) Constraints: Not nullable<p>Original property name from the Odata EDM: <b>KeyPropertyString</b></p>
     * 
     * @return
     *     The keyPropertyString contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("KeyPropertyString")
    private java.lang.String keyPropertyString;
    public final static SimpleProperty.String<TestEntityV4> KEY_PROPERTY_STRING = new SimpleProperty.String<TestEntityV4>(TestEntityV4 .class, "KeyPropertyString");
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("StringProperty")
    private java.lang.String stringProperty;
    public final static SimpleProperty.String<TestEntityV4> STRING_PROPERTY = new SimpleProperty.String<TestEntityV4>(TestEntityV4 .class, "StringProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("BooleanProperty")
    private java.lang.Boolean booleanProperty;
    public final static SimpleProperty.Boolean<TestEntityV4> BOOLEAN_PROPERTY = new SimpleProperty.Boolean<TestEntityV4>(TestEntityV4 .class, "BooleanProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("GuidProperty")
    private UUID guidProperty;
    public final static SimpleProperty.Guid<TestEntityV4> GUID_PROPERTY = new SimpleProperty.Guid<TestEntityV4>(TestEntityV4 .class, "GuidProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int16Property")
    private Short int16Property;
    public final static SimpleProperty.NumericInteger<TestEntityV4> INT16_PROPERTY = new SimpleProperty.NumericInteger<TestEntityV4>(TestEntityV4 .class, "Int16Property");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int32Property</b></p>
     * 
     * @return
     *     The int32Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int32Property")
    private Integer int32Property;
    public final static SimpleProperty.NumericInteger<TestEntityV4> INT32_PROPERTY = new SimpleProperty.NumericInteger<TestEntityV4>(TestEntityV4 .class, "Int32Property");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int64Property</b></p>
     * 
     * @return
     *     The int64Property contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("Int64Property")
    private Long int64Property;
    public final static SimpleProperty.NumericInteger<TestEntityV4> INT64_PROPERTY = new SimpleProperty.NumericInteger<TestEntityV4>(TestEntityV4 .class, "Int64Property");
    /**
     * Constraints: Nullable, Precision: 5, Scale: 2 <p>Original property name from the Odata EDM: <b>DecimalProperty</b></p>
     * 
     * @return
     *     The decimalProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DecimalProperty")
    @DecimalDescriptor(precision = 5, scale = 2)
    private BigDecimal decimalProperty;
    public final static SimpleProperty.NumericDecimal<TestEntityV4> DECIMAL_PROPERTY = new SimpleProperty.NumericDecimal<TestEntityV4>(TestEntityV4 .class, "DecimalProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SingleProperty</b></p>
     * 
     * @return
     *     The singleProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("SingleProperty")
    private Float singleProperty;
    public final static SimpleProperty.NumericDecimal<TestEntityV4> SINGLE_PROPERTY = new SimpleProperty.NumericDecimal<TestEntityV4>(TestEntityV4 .class, "SingleProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>DoubleProperty</b></p>
     * 
     * @return
     *     The doubleProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DoubleProperty")
    private Double doubleProperty;
    public final static SimpleProperty.NumericDecimal<TestEntityV4> DOUBLE_PROPERTY = new SimpleProperty.NumericDecimal<TestEntityV4>(TestEntityV4 .class, "DoubleProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeOfDayProperty</b></p>
     * 
     * @return
     *     The timeOfDayProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("TimeOfDayProperty")
    private LocalTime timeOfDayProperty;
    public final static SimpleProperty.Time<TestEntityV4> TIME_OF_DAY_PROPERTY = new SimpleProperty.Time<TestEntityV4>(TestEntityV4 .class, "TimeOfDayProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateProperty</b></p>
     * 
     * @return
     *     The dateProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DateProperty")
    private LocalDate dateProperty;
    public final static SimpleProperty.Date<TestEntityV4> DATE_PROPERTY = new SimpleProperty.Date<TestEntityV4>(TestEntityV4 .class, "DateProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @return
     *     The dateTimeOffSetProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DateTimeOffSetProperty")
    private OffsetDateTime dateTimeOffSetProperty;
    public final static SimpleProperty.DateTime<TestEntityV4> DATE_TIME_OFF_SET_PROPERTY = new SimpleProperty.DateTime<TestEntityV4>(TestEntityV4 .class, "DateTimeOffSetProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DurationProperty</b></p>
     * 
     * @return
     *     The durationProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("DurationProperty")
    private BigDecimal durationProperty;
    public final static SimpleProperty.Duration<TestEntityV4> DURATION_PROPERTY = new SimpleProperty.Duration<TestEntityV4>(TestEntityV4 .class, "DurationProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ByteProperty</b></p>
     * 
     * @return
     *     The byteProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("ByteProperty")
    private Short byteProperty;
    public final static SimpleProperty.NumericInteger<TestEntityV4> BYTE_PROPERTY = new SimpleProperty.NumericInteger<TestEntityV4>(TestEntityV4 .class, "ByteProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SByteProperty</b></p>
     * 
     * @return
     *     The sByteProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("SByteProperty")
    private Byte sByteProperty;
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BinaryProperty</b></p>
     * 
     * @return
     *     The binaryProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("BinaryProperty")
    private byte[] binaryProperty;
    public final static SimpleProperty.Binary<TestEntityV4> BINARY_PROPERTY = new SimpleProperty.Binary<TestEntityV4>(TestEntityV4 .class, "BinaryProperty");
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>CollectionProperty</b></p>
     * 
     * @return
     *     The collectionProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("CollectionProperty")
    private java.util.Collection<java.lang.String> collectionProperty;
    public final static SimpleProperty.Collection<TestEntityV4, java.lang.String> COLLECTION_PROPERTY = new SimpleProperty.Collection<TestEntityV4, java.lang.String>(TestEntityV4 .class, "CollectionProperty", java.lang.String.class);
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @return
     *     The complexTypeProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("ComplexTypeProperty")
    private A_TestComplexType complexTypeProperty;
    /**
     * Use with available request builders to apply the <b>ComplexTypeProperty</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<TestEntityV4, A_TestComplexType> COMPLEX_TYPE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<TestEntityV4, A_TestComplexType>(TestEntityV4 .class, "ComplexTypeProperty", A_TestComplexType.class);
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeCollectionProperty</b></p>
     * 
     * @return
     *     The complexTypeCollectionProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("ComplexTypeCollectionProperty")
    private java.util.Collection<A_TestComplexType> complexTypeCollectionProperty;
    /**
     * Use with available request builders to apply the <b>ComplexTypeCollectionProperty</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<TestEntityV4, A_TestComplexType> COMPLEX_TYPE_COLLECTION_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<TestEntityV4, A_TestComplexType>(TestEntityV4 .class, "ComplexTypeCollectionProperty", A_TestComplexType.class);
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>EnumProperty</b></p>
     * 
     * @return
     *     The enumProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("EnumProperty")
    private A_TestEnumType enumProperty;
    public final static SimpleProperty.Enum<TestEntityV4, A_TestEnumType> ENUM_PROPERTY = new SimpleProperty.Enum<TestEntityV4, A_TestEnumType>(TestEntityV4 .class, "EnumProperty", "API_TEST_SRV.A_TestEnumType");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>EnumCollectionProperty</b></p>
     * 
     * @return
     *     The enumCollectionProperty contained in this {@link VdmEntity}.
     */
    @Nullable
    @ElementName("EnumCollectionProperty")
    private java.util.Collection<A_TestEnumType> enumCollectionProperty;
    public final static SimpleProperty.Collection<TestEntityV4, A_TestEnumType> ENUM_COLLECTION_PROPERTY = new SimpleProperty.Collection<TestEntityV4, A_TestEnumType>(TestEntityV4 .class, "EnumCollectionProperty", A_TestEnumType.class);
    /**
     * Navigation property <b>to_MultiLink</b> for <b>TestEntityV4</b> to multiple <b>TestEntityMultiLink</b>.
     * 
     */
    @ElementName("to_MultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityMultiLink> toMultiLink;
    /**
     * Navigation property <b>to_OtherMultiLink</b> for <b>TestEntityV4</b> to multiple <b>TestEntityMultiLink</b>.
     * 
     */
    @ElementName("to_OtherMultiLink")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<TestEntityMultiLink> toOtherMultiLink;
    /**
     * Navigation property <b>to_SingleLink</b> for <b>TestEntityV4</b> to single <b>TestEntitySingleLink</b>.
     * 
     */
    @ElementName("to_SingleLink")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntitySingleLink toSingleLink;
    /**
     * Navigation property <b>to_StreamLink</b> for <b>TestEntityV4</b> to single <b>TestEntityStream</b>.
     * 
     */
    @ElementName("to_StreamLink")
    @Nullable
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private TestEntityStream toStreamLink;
    /**
     * Use with available request builders to apply the <b>to_MultiLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityV4, TestEntityMultiLink> TO_MULTI_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityV4, TestEntityMultiLink>(TestEntityV4 .class, "to_MultiLink", TestEntityMultiLink.class);
    /**
     * Use with available request builders to apply the <b>to_OtherMultiLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityV4, TestEntityMultiLink> TO_OTHER_MULTI_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Collection<TestEntityV4, TestEntityMultiLink>(TestEntityV4 .class, "to_OtherMultiLink", TestEntityMultiLink.class);
    /**
     * Use with available request builders to apply the <b>to_SingleLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityV4, TestEntitySingleLink> TO_SINGLE_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityV4, TestEntitySingleLink>(TestEntityV4 .class, "to_SingleLink", TestEntitySingleLink.class);
    /**
     * Use with available request builders to apply the <b>to_StreamLink</b> navigation property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityV4, TestEntityStream> TO_STREAM_LINK = new com.sap.cloud.sdk.datamodel.odatav4.core.NavigationProperty.Single<TestEntityV4, TestEntityStream>(TestEntityV4 .class, "to_StreamLink", TestEntityStream.class);

    @Nonnull
    @Override
    public Class<TestEntityV4> getType() {
        return TestEntityV4 .class;
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
        final java.lang.String keyPropertyString) {
        rememberChangedField("KeyPropertyString", this.keyPropertyString);
        this.keyPropertyString = keyPropertyString;
    }

    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @param stringProperty
     *     The stringProperty to set.
     */
    public void setStringProperty(
        @Nullable
        final java.lang.String stringProperty) {
        rememberChangedField("StringProperty", this.stringProperty);
        this.stringProperty = stringProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @param booleanProperty
     *     The booleanProperty to set.
     */
    public void setBooleanProperty(
        @Nullable
        final java.lang.Boolean booleanProperty) {
        rememberChangedField("BooleanProperty", this.booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int32Property</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int64Property</b></p>
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
     * Constraints: Nullable, Precision: 5, Scale: 2 <p>Original property name from the Odata EDM: <b>DecimalProperty</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SingleProperty</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>DoubleProperty</b></p>
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
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeOfDayProperty</b></p>
     * 
     * @param timeOfDayProperty
     *     The timeOfDayProperty to set.
     */
    public void setTimeOfDayProperty(
        @Nullable
        final LocalTime timeOfDayProperty) {
        rememberChangedField("TimeOfDayProperty", this.timeOfDayProperty);
        this.timeOfDayProperty = timeOfDayProperty;
    }

    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateProperty</b></p>
     * 
     * @param dateProperty
     *     The dateProperty to set.
     */
    public void setDateProperty(
        @Nullable
        final LocalDate dateProperty) {
        rememberChangedField("DateProperty", this.dateProperty);
        this.dateProperty = dateProperty;
    }

    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @param dateTimeOffSetProperty
     *     The dateTimeOffSetProperty to set.
     */
    public void setDateTimeOffSetProperty(
        @Nullable
        final OffsetDateTime dateTimeOffSetProperty) {
        rememberChangedField("DateTimeOffSetProperty", this.dateTimeOffSetProperty);
        this.dateTimeOffSetProperty = dateTimeOffSetProperty;
    }

    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DurationProperty</b></p>
     * 
     * @param durationProperty
     *     The durationProperty to set.
     */
    public void setDurationProperty(
        @Nullable
        final BigDecimal durationProperty) {
        rememberChangedField("DurationProperty", this.durationProperty);
        this.durationProperty = durationProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ByteProperty</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SByteProperty</b></p>
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BinaryProperty</b></p>
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
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>CollectionProperty</b></p>
     * 
     * @param collectionProperty
     *     The collectionProperty to set.
     */
    public void setCollectionProperty(
        @Nullable
        final java.util.Collection<java.lang.String> collectionProperty) {
        rememberChangedField("CollectionProperty", this.collectionProperty);
        this.collectionProperty = collectionProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
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

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeCollectionProperty</b></p>
     * 
     * @param complexTypeCollectionProperty
     *     The complexTypeCollectionProperty to set.
     */
    public void setComplexTypeCollectionProperty(
        @Nullable
        final java.util.Collection<A_TestComplexType> complexTypeCollectionProperty) {
        rememberChangedField("ComplexTypeCollectionProperty", this.complexTypeCollectionProperty);
        this.complexTypeCollectionProperty = complexTypeCollectionProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>EnumProperty</b></p>
     * 
     * @param enumProperty
     *     The enumProperty to set.
     */
    public void setEnumProperty(
        @Nullable
        final A_TestEnumType enumProperty) {
        rememberChangedField("EnumProperty", this.enumProperty);
        this.enumProperty = enumProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>EnumCollectionProperty</b></p>
     * 
     * @param enumCollectionProperty
     *     The enumCollectionProperty to set.
     */
    public void setEnumCollectionProperty(
        @Nullable
        final java.util.Collection<A_TestEnumType> enumCollectionProperty) {
        rememberChangedField("EnumCollectionProperty", this.enumCollectionProperty);
        this.enumCollectionProperty = enumCollectionProperty;
    }

    @Override
    protected java.lang.String getEntityCollection() {
        return "A_TestEntity";
    }

    @Nonnull
    @Override
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        entityKey.addKeyProperty("KeyPropertyGuid", getKeyPropertyGuid());
        entityKey.addKeyProperty("KeyPropertyString", getKeyPropertyString());
        return entityKey;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("KeyPropertyGuid", getKeyPropertyGuid());
        cloudSdkValues.put("KeyPropertyString", getKeyPropertyString());
        cloudSdkValues.put("StringProperty", getStringProperty());
        cloudSdkValues.put("BooleanProperty", getBooleanProperty());
        cloudSdkValues.put("GuidProperty", getGuidProperty());
        cloudSdkValues.put("Int16Property", getInt16Property());
        cloudSdkValues.put("Int32Property", getInt32Property());
        cloudSdkValues.put("Int64Property", getInt64Property());
        cloudSdkValues.put("DecimalProperty", getDecimalProperty());
        cloudSdkValues.put("SingleProperty", getSingleProperty());
        cloudSdkValues.put("DoubleProperty", getDoubleProperty());
        cloudSdkValues.put("TimeOfDayProperty", getTimeOfDayProperty());
        cloudSdkValues.put("DateProperty", getDateProperty());
        cloudSdkValues.put("DateTimeOffSetProperty", getDateTimeOffSetProperty());
        cloudSdkValues.put("DurationProperty", getDurationProperty());
        cloudSdkValues.put("ByteProperty", getByteProperty());
        cloudSdkValues.put("SByteProperty", getSByteProperty());
        cloudSdkValues.put("BinaryProperty", getBinaryProperty());
        cloudSdkValues.put("CollectionProperty", getCollectionProperty());
        cloudSdkValues.put("ComplexTypeProperty", getComplexTypeProperty());
        cloudSdkValues.put("ComplexTypeCollectionProperty", getComplexTypeCollectionProperty());
        cloudSdkValues.put("EnumProperty", getEnumProperty());
        cloudSdkValues.put("EnumCollectionProperty", getEnumCollectionProperty());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("KeyPropertyGuid")) {
                final Object value = cloudSdkValues.remove("KeyPropertyGuid");
                if ((value == null)||(!value.equals(getKeyPropertyGuid()))) {
                    setKeyPropertyGuid(((UUID) value));
                }
            }
            if (cloudSdkValues.containsKey("KeyPropertyString")) {
                final Object value = cloudSdkValues.remove("KeyPropertyString");
                if ((value == null)||(!value.equals(getKeyPropertyString()))) {
                    setKeyPropertyString(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("StringProperty")) {
                final Object value = cloudSdkValues.remove("StringProperty");
                if ((value == null)||(!value.equals(getStringProperty()))) {
                    setStringProperty(((java.lang.String) value));
                }
            }
            if (cloudSdkValues.containsKey("BooleanProperty")) {
                final Object value = cloudSdkValues.remove("BooleanProperty");
                if ((value == null)||(!value.equals(getBooleanProperty()))) {
                    setBooleanProperty(((java.lang.Boolean) value));
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
            if (cloudSdkValues.containsKey("TimeOfDayProperty")) {
                final Object value = cloudSdkValues.remove("TimeOfDayProperty");
                if ((value == null)||(!value.equals(getTimeOfDayProperty()))) {
                    setTimeOfDayProperty(((LocalTime) value));
                }
            }
            if (cloudSdkValues.containsKey("DateProperty")) {
                final Object value = cloudSdkValues.remove("DateProperty");
                if ((value == null)||(!value.equals(getDateProperty()))) {
                    setDateProperty(((LocalDate) value));
                }
            }
            if (cloudSdkValues.containsKey("DateTimeOffSetProperty")) {
                final Object value = cloudSdkValues.remove("DateTimeOffSetProperty");
                if ((value == null)||(!value.equals(getDateTimeOffSetProperty()))) {
                    setDateTimeOffSetProperty(((OffsetDateTime) value));
                }
            }
            if (cloudSdkValues.containsKey("DurationProperty")) {
                final Object value = cloudSdkValues.remove("DurationProperty");
                if ((value == null)||(!value.equals(getDurationProperty()))) {
                    setDurationProperty(((BigDecimal) value));
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
            if (cloudSdkValues.containsKey("BinaryProperty")) {
                final Object value = cloudSdkValues.remove("BinaryProperty");
                if ((value == null)||(!value.equals(getBinaryProperty()))) {
                    setBinaryProperty(((byte[]) value));
                }
            }
            if (cloudSdkValues.containsKey("CollectionProperty")) {
                final Object value = cloudSdkValues.remove("CollectionProperty");
                if (value instanceof Iterable) {
                    final LinkedList<java.lang.String> collectionProperty = new LinkedList<java.lang.String>();
                    for (Object item: ((Iterable<?> ) value)) {
                        collectionProperty.add(((java.lang.String) item));
                    }
                    setCollectionProperty(collectionProperty);
                }
            }
            if (cloudSdkValues.containsKey("EnumProperty")) {
                final Object value = cloudSdkValues.remove("EnumProperty");
                if ((value instanceof java.lang.String)||(value == null)) {
                    final A_TestEnumType enumProperty = VdmEnum.getConstant(A_TestEnumType.class, ((java.lang.String) value));
                    if (!Objects.equals(enumProperty, getEnumProperty())) {
                        setEnumProperty(enumProperty);
                    }
                }
            }
            if (cloudSdkValues.containsKey("EnumCollectionProperty")) {
                final Object value = cloudSdkValues.remove("EnumCollectionProperty");
                if ((value == null)&&(getEnumCollectionProperty()!= null)) {
                    setEnumCollectionProperty(null);
                }
                if (value instanceof Iterable) {
                    final LinkedList<A_TestEnumType> enumCollectionProperty = new LinkedList<A_TestEnumType>();
                    for (Object item: ((Iterable<?> ) value)) {
                        if (item instanceof java.lang.String) {
                            final A_TestEnumType enumConstant = VdmEnum.getConstant(A_TestEnumType.class, ((java.lang.String) item));
                            enumCollectionProperty.add(enumConstant);
                        }
                    }
                    if (!Objects.equals(enumCollectionProperty, getEnumCollectionProperty())) {
                        setEnumCollectionProperty(enumCollectionProperty);
                    }
                }
            }
        }
        // structured properties
        {
            if (cloudSdkValues.containsKey("ComplexTypeProperty")) {
                final Object value = cloudSdkValues.remove("ComplexTypeProperty");
                if (value instanceof Map) {
                    if (getComplexTypeProperty() == null) {
                        setComplexTypeProperty(new A_TestComplexType());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                    getComplexTypeProperty().fromMap(inputMap);
                }
                if ((value == null)&&(getComplexTypeProperty()!= null)) {
                    setComplexTypeProperty(null);
                }
            }
            if (cloudSdkValues.containsKey("ComplexTypeCollectionProperty")) {
                final Object value = cloudSdkValues.remove("ComplexTypeCollectionProperty");
                if (value instanceof Iterable) {
                    final LinkedList<A_TestComplexType> complexTypeCollectionProperty = new LinkedList<A_TestComplexType>();
                    for (Object properties: ((Iterable<?> ) value)) {
                        if (properties instanceof Map) {
                            final A_TestComplexType item = new A_TestComplexType();
                            @SuppressWarnings("unchecked")
                            final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) value);
                            item.fromMap(inputMap);
                            complexTypeCollectionProperty.add(item);
                        }
                    }
                    setComplexTypeCollectionProperty(complexTypeCollectionProperty);
                }
                if ((value == null)&&(getComplexTypeCollectionProperty()!= null)) {
                    setComplexTypeCollectionProperty(null);
                }
            }
        }
        // navigation properties
        {
            if ((cloudSdkValues).containsKey("to_MultiLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_MultiLink");
                if (cloudSdkValue instanceof Iterable) {
                    if (toMultiLink == null) {
                        toMultiLink = Lists.newArrayList();
                    } else {
                        toMultiLink = Lists.newArrayList(toMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) cloudSdkValue)) {
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
                        final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
            if ((cloudSdkValues).containsKey("to_OtherMultiLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_OtherMultiLink");
                if (cloudSdkValue instanceof Iterable) {
                    if (toOtherMultiLink == null) {
                        toOtherMultiLink = Lists.newArrayList();
                    } else {
                        toOtherMultiLink = Lists.newArrayList(toOtherMultiLink);
                    }
                    int i = 0;
                    for (Object item: ((Iterable<?> ) cloudSdkValue)) {
                        if (!(item instanceof Map)) {
                            continue;
                        }
                        TestEntityMultiLink entity;
                        if (toOtherMultiLink.size()>i) {
                            entity = toOtherMultiLink.get(i);
                        } else {
                            entity = new TestEntityMultiLink();
                            toOtherMultiLink.add(entity);
                        }
                        i = (i + 1);
                        @SuppressWarnings("unchecked")
                        final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) item);
                        entity.fromMap(inputMap);
                    }
                }
            }
            if ((cloudSdkValues).containsKey("to_SingleLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_SingleLink");
                if (cloudSdkValue instanceof Map) {
                    if (toSingleLink == null) {
                        toSingleLink = new TestEntitySingleLink();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toSingleLink.fromMap(inputMap);
                }
            }
            if ((cloudSdkValues).containsKey("to_StreamLink")) {
                final Object cloudSdkValue = (cloudSdkValues).remove("to_StreamLink");
                if (cloudSdkValue instanceof Map) {
                    if (toStreamLink == null) {
                        toStreamLink = new TestEntityStream();
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    toStreamLink.fromMap(inputMap);
                }
            }
        }
        super.fromMap(cloudSdkValues);
    }

    @Override
    protected java.lang.String getDefaultServicePath() {
        return TestService.DEFAULT_SERVICE_PATH;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfNavigationProperties() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfNavigationProperties();
        if (toMultiLink!= null) {
            (cloudSdkValues).put("to_MultiLink", toMultiLink);
        }
        if (toOtherMultiLink!= null) {
            (cloudSdkValues).put("to_OtherMultiLink", toOtherMultiLink);
        }
        if (toSingleLink!= null) {
            (cloudSdkValues).put("to_SingleLink", toSingleLink);
        }
        if (toStreamLink!= null) {
            (cloudSdkValues).put("to_StreamLink", toStreamLink);
        }
        return cloudSdkValues;
    }

    /**
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV4</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
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
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV4</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param cloudSdkValue
     *     List of <b>TestEntityMultiLink</b> entities.
     */
    public void setMultiLink(
        @Nonnull
        final List<TestEntityMultiLink> cloudSdkValue) {
        if (toMultiLink == null) {
            toMultiLink = Lists.newArrayList();
        }
        toMultiLink.clear();
        toMultiLink.addAll(cloudSdkValue);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityMultiLink</b> entities. This corresponds to the OData navigation property <b>to_MultiLink</b>.
     * <p>
     * If the navigation property <b>to_MultiLink</b> of a queried <b>TestEntityV4</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
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
     * Retrieval of associated <b>TestEntityMultiLink</b> entities (one to many). This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV4</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_OtherMultiLink</b> is already loaded, the result will contain the <b>TestEntityMultiLink</b> entities. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<List<TestEntityMultiLink>> getOtherMultiLinkIfPresent() {
        return Option.of(toOtherMultiLink);
    }

    /**
     * Overwrites the list of associated <b>TestEntityMultiLink</b> entities for the loaded navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property <b>to_OtherMultiLink</b> of a queried <b>TestEntityV4</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param cloudSdkValue
     *     List of <b>TestEntityMultiLink</b> entities.
     */
    public void setOtherMultiLink(
        @Nonnull
        final List<TestEntityMultiLink> cloudSdkValue) {
        if (toOtherMultiLink == null) {
            toOtherMultiLink = Lists.newArrayList();
        }
        toOtherMultiLink.clear();
        toOtherMultiLink.addAll(cloudSdkValue);
    }

    /**
     * Adds elements to the list of associated <b>TestEntityMultiLink</b> entities. This corresponds to the OData navigation property <b>to_OtherMultiLink</b>.
     * <p>
     * If the navigation property <b>to_OtherMultiLink</b> of a queried <b>TestEntityV4</b> is operated lazily, an <b>ODataException</b> can be thrown in case of an OData query error.
     * <p>
     * Please note: <i>Lazy</i> loading of OData entity associations is the process of asynchronous retrieval and persisting of items from a navigation property. If a <i>lazy</i> property is requested by the application for the first time and it has not yet been loaded, an OData query will be run in order to load the missing information and its result will get cached for future invocations.
     * 
     * @param entity
     *     Array of <b>TestEntityMultiLink</b> entities.
     */
    public void addOtherMultiLink(TestEntityMultiLink... entity) {
        if (toOtherMultiLink == null) {
            toOtherMultiLink = Lists.newArrayList();
        }
        toOtherMultiLink.addAll(Lists.newArrayList(entity));
    }

    /**
     * Retrieval of associated <b>TestEntitySingleLink</b> entity (one to one). This corresponds to the OData navigation property <b>to_SingleLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV4</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
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
     * @param cloudSdkValue
     *     New <b>TestEntitySingleLink</b> entity.
     */
    public void setSingleLink(final TestEntitySingleLink cloudSdkValue) {
        toSingleLink = cloudSdkValue;
    }

    /**
     * Retrieval of associated <b>TestEntityStream</b> entity (one to one). This corresponds to the OData navigation property <b>to_StreamLink</b>.
     * <p>
     * If the navigation property for an entity <b>TestEntityV4</b> has not been resolved yet, this method will <b>not query</b> further information. Instead its <code>Option</code> result state will be <code>empty</code>.
     * 
     * @return
     *     If the information for navigation property <b>to_StreamLink</b> is already loaded, the result will contain the <b>TestEntityStream</b> entity. If not, an <code>Option</code> with result state <code>empty</code> is returned.
     */
    @Nonnull
    public Option<TestEntityStream> getStreamLinkIfPresent() {
        return Option.of(toStreamLink);
    }

    /**
     * Overwrites the associated <b>TestEntityStream</b> entity for the loaded navigation property <b>to_StreamLink</b>.
     * 
     * @param cloudSdkValue
     *     New <b>TestEntityStream</b> entity.
     */
    public void setStreamLink(final TestEntityStream cloudSdkValue) {
        toStreamLink = cloudSdkValue;
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, java.lang.Boolean> testFunctionBoundToEntity() {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, java.lang.Boolean>(TestEntityV4 .class, java.lang.Boolean.class, "API_TEST_SRV.TestFunctionBoundToEntity", parameters);
    }

    /**
     * Function that can be applied to a collection of entities of this class.</p>
     * 
     * @return
     *     Function object prepared with the given parameters to be applied to a collection of entities of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.CollectionToSingle<TestEntityV4, java.lang.Boolean> testFunctionBoundToEntityCollection() {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.CollectionToSingle<TestEntityV4, java.lang.Boolean>(TestEntityV4 .class, java.lang.Boolean.class, "API_TEST_SRV.TestFunctionBoundToEntityCollection", parameters);
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingleEntity.Composable<TestEntityV4, TestEntityV4> testFunctionBoundToEntityComposable(
        @Nullable
        final java.lang.String stringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingleEntity.Composable<TestEntityV4, TestEntityV4>(TestEntityV4 .class, TestEntityV4 .class, "API_TEST_SRV.TestFunctionBoundToEntityComposable", parameters);
    }

    /**
     * Function that can be applied to a collection of entities of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to a collection of entities of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.CollectionToSingleEntity.Composable<TestEntityV4, TestEntityV4> testFunctionBoundToEntityCollectionComposable(
        @Nullable
        final java.lang.String stringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.CollectionToSingleEntity.Composable<TestEntityV4, TestEntityV4>(TestEntityV4 .class, TestEntityV4 .class, "API_TEST_SRV.TestFunctionBoundToEntityCollectionComposable", parameters);
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function> testFunction_Returning_New_Entity(
        @Nullable
        final java.lang.String stringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function>(TestEntityV4 .class, TestEntityV4_Only_Function.class, "API_TEST_SRV.TestFunction_Returning_New_Entity", parameters);
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function> testFunctionOverload(
        @Nullable
        final java.lang.String stringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function>(TestEntityV4 .class, TestEntityV4_Only_Function.class, "API_TEST_SRV.TestFunctionOverload", parameters);
    }

    /**
     * Function that can be applied to any entity object of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param otherStringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>OtherStringParam</b></p>
     * @return
     *     Function object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyFunction(thisFunction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function> testFunctionOverload(
        @Nullable
        final java.lang.String stringParam,
        @Nullable
        final java.lang.String otherStringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        parameters.put("OtherStringParam", otherStringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundFunction.SingleToSingle<TestEntityV4, TestEntityV4_Only_Function>(TestEntityV4 .class, TestEntityV4_Only_Function.class, "API_TEST_SRV.TestFunctionOverload", parameters);
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, TestEntityV4> testActionBoundToEntity() {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, TestEntityV4>(TestEntityV4 .class, TestEntityV4 .class, "API_TEST_SRV.TestActionBoundToEntity", parameters);
    }

    /**
     * Action that can be applied to a collection of entities of this class.</p>
     * 
     * @return
     *     Action object prepared with the given parameters to be applied to a collection of entities of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.CollectionToSingle<TestEntityV4, TestEntityV4> testActionBoundToEntityCollection() {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.CollectionToSingle<TestEntityV4, TestEntityV4>(TestEntityV4 .class, TestEntityV4 .class, "API_TEST_SRV.TestActionBoundToEntityCollection", parameters);
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void> testActionBoundToEntityNoReturnType() {
        final Map<java.lang.String, Object> parameters = Collections.emptyMap();
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void>(TestEntityV4 .class, Void.class, "API_TEST_SRV.TestActionBoundToEntityNoReturnType", parameters);
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void> testActionBoundToEntityWithParamsOverloaded(
        @Nullable
        final java.lang.String stringParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void>(TestEntityV4 .class, Void.class, "API_TEST_SRV.TestActionBoundToEntityWithParamsOverloaded", parameters);
    }

    /**
     * Action that can be applied to any entity object of this class.</p>
     * 
     * @param collectionComplexParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>CollectionComplexParam</b></p>
     * @param stringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>StringParam</b></p>
     * @param collectionStringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>CollectionStringParam</b></p>
     * @param entityParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>EntityParam</b></p>
     * @param enumParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>EnumParam</b></p>
     * @param collectionEntityParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>CollectionEntityParam</b></p>
     * @param otherStringParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>OtherStringParam</b></p>
     * @param complexParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>ComplexParam</b></p>
     * @param collectionEnumParam
     *     Constraints: Nullable<p>Original parameter name from the Odata EDM: <b>CollectionEnumParam</b></p>
     * @return
     *     Action object prepared with the given parameters to be applied to any entity object of this class.</p> To execute it use the {@code service.forEntity(entity).applyAction(thisAction)} API.
     */
    @Nonnull
    public static com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void> testActionBoundToEntityWithParamsOverloaded(
        @Nullable
        final java.lang.String stringParam,
        @Nullable
        final java.lang.String otherStringParam,
        @Nullable
        final java.util.Collection<java.lang.String> collectionStringParam,
        @Nullable
        final TestEntityMultiLink entityParam,
        @Nullable
        final java.util.Collection<TestEntityMultiLink> collectionEntityParam,
        @Nullable
        final A_TestComplexType complexParam,
        @Nullable
        final java.util.Collection<A_TestComplexType> collectionComplexParam,
        @Nullable
        final A_TestEnumType enumParam,
        @Nullable
        final java.util.Collection<A_TestEnumType> collectionEnumParam) {
        final Map<java.lang.String, Object> parameters = new HashMap<java.lang.String, Object>();
        parameters.put("StringParam", stringParam);
        parameters.put("OtherStringParam", otherStringParam);
        parameters.put("CollectionStringParam", collectionStringParam);
        parameters.put("EntityParam", entityParam);
        parameters.put("CollectionEntityParam", collectionEntityParam);
        parameters.put("ComplexParam", complexParam);
        parameters.put("CollectionComplexParam", collectionComplexParam);
        parameters.put("EnumParam", enumParam);
        parameters.put("CollectionEnumParam", collectionEnumParam);
        return new com.sap.cloud.sdk.datamodel.odatav4.core.BoundAction.SingleToSingle<TestEntityV4, Void>(TestEntityV4 .class, Void.class, "API_TEST_SRV.TestActionBoundToEntityWithParamsOverloaded", parameters);
    }


    /**
     * Helper class to allow for fluent creation of TestEntityV4 instances.
     * 
     */
    public final static class TestEntityV4Builder {

        private List<TestEntityMultiLink> toMultiLink = Lists.newArrayList();
        private List<TestEntityMultiLink> toOtherMultiLink = Lists.newArrayList();
        private TestEntitySingleLink toSingleLink;
        private TestEntityStream toStreamLink;

        private TestEntityV4 .TestEntityV4Builder toMultiLink(final List<TestEntityMultiLink> cloudSdkValue) {
            toMultiLink.addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>to_MultiLink</b> for <b>TestEntityV4</b> to multiple <b>TestEntityMultiLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityMultiLinks to build this TestEntityV4 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV4 .TestEntityV4Builder multiLink(TestEntityMultiLink... cloudSdkValue) {
            return toMultiLink(Lists.newArrayList(cloudSdkValue));
        }

        private TestEntityV4 .TestEntityV4Builder toOtherMultiLink(final List<TestEntityMultiLink> cloudSdkValue) {
            toOtherMultiLink.addAll(cloudSdkValue);
            return this;
        }

        /**
         * Navigation property <b>to_OtherMultiLink</b> for <b>TestEntityV4</b> to multiple <b>TestEntityMultiLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityMultiLinks to build this TestEntityV4 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV4 .TestEntityV4Builder otherMultiLink(TestEntityMultiLink... cloudSdkValue) {
            return toOtherMultiLink(Lists.newArrayList(cloudSdkValue));
        }

        private TestEntityV4 .TestEntityV4Builder toSingleLink(final TestEntitySingleLink cloudSdkValue) {
            toSingleLink = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_SingleLink</b> for <b>TestEntityV4</b> to single <b>TestEntitySingleLink</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntitySingleLink to build this TestEntityV4 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV4 .TestEntityV4Builder singleLink(final TestEntitySingleLink cloudSdkValue) {
            return toSingleLink(cloudSdkValue);
        }

        private TestEntityV4 .TestEntityV4Builder toStreamLink(final TestEntityStream cloudSdkValue) {
            toStreamLink = cloudSdkValue;
            return this;
        }

        /**
         * Navigation property <b>to_StreamLink</b> for <b>TestEntityV4</b> to single <b>TestEntityStream</b>.
         * 
         * @param cloudSdkValue
         *     The TestEntityStream to build this TestEntityV4 with.
         * @return
         *     This Builder to allow for a fluent interface.
         */
        @Nonnull
        public TestEntityV4 .TestEntityV4Builder streamLink(final TestEntityStream cloudSdkValue) {
            return toStreamLink(cloudSdkValue);
        }

    }

}
