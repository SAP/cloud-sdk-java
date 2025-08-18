/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */

package testcomparison.namespaces.test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataEntityKey;
import com.sap.cloud.sdk.datamodel.odatav4.core.DecimalDescriptor;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmComplex;
import com.sap.cloud.sdk.datamodel.odatav4.core.VdmEnum;
import com.sap.cloud.sdk.result.ElementName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
@JsonAdapter(com.sap.cloud.sdk.datamodel.odatav4.adapter.GsonVdmAdapterFactory.class)
@JsonSerialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectSerializer.class)
@JsonDeserialize(using = com.sap.cloud.sdk.datamodel.odatav4.adapter.JacksonVdmObjectDeserializer.class)
public class A_TestComplexType
    extends VdmComplex<A_TestComplexType>
{

    @Getter
    private final java.lang.String odataType = "API_TEST_SRV.A_TestComplexType";
    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>BaseStringProperty</b></p>
     * 
     * @return
     *     The baseStringProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("BaseStringProperty")
    private java.lang.String baseStringProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<A_TestComplexType> BASE_STRING_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<A_TestComplexType>(A_TestComplexType.class, "BaseStringProperty");
    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
     * 
     * @return
     *     The stringProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("StringProperty")
    private java.lang.String stringProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<A_TestComplexType> STRING_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.String<A_TestComplexType>(A_TestComplexType.class, "StringProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>BooleanProperty</b></p>
     * 
     * @return
     *     The booleanProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("BooleanProperty")
    private java.lang.Boolean booleanProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Boolean<A_TestComplexType> BOOLEAN_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Boolean<A_TestComplexType>(A_TestComplexType.class, "BooleanProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>GuidProperty</b></p>
     * 
     * @return
     *     The guidProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("GuidProperty")
    private UUID guidProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Guid<A_TestComplexType> GUID_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Guid<A_TestComplexType>(A_TestComplexType.class, "GuidProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int16Property</b></p>
     * 
     * @return
     *     The int16Property contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Int16Property")
    private Short int16Property;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType> INT16_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType>(A_TestComplexType.class, "Int16Property");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int32Property</b></p>
     * 
     * @return
     *     The int32Property contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Int32Property")
    private Integer int32Property;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType> INT32_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType>(A_TestComplexType.class, "Int32Property");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>Int64Property</b></p>
     * 
     * @return
     *     The int64Property contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("Int64Property")
    private Long int64Property;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType> INT64_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType>(A_TestComplexType.class, "Int64Property");
    /**
     * Constraints: Nullable, Precision: 5, Scale: 2 <p>Original property name from the Odata EDM: <b>DecimalProperty</b></p>
     * 
     * @return
     *     The decimalProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("DecimalProperty")
    @DecimalDescriptor(precision = 5, scale = 2)
    private BigDecimal decimalProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType> DECIMAL_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType>(A_TestComplexType.class, "DecimalProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SingleProperty</b></p>
     * 
     * @return
     *     The singleProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("SingleProperty")
    private Float singleProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType> SINGLE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType>(A_TestComplexType.class, "SingleProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>DoubleProperty</b></p>
     * 
     * @return
     *     The doubleProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("DoubleProperty")
    private Double doubleProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType> DOUBLE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericDecimal<A_TestComplexType>(A_TestComplexType.class, "DoubleProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeProperty</b></p>
     * 
     * @return
     *     The timeProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("TimeProperty")
    private LocalTime timeProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Time<A_TestComplexType> TIME_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Time<A_TestComplexType>(A_TestComplexType.class, "TimeProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeProperty</b></p>
     * 
     * @return
     *     The dateTimeProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("DateTimeProperty")
    private OffsetDateTime dateTimeProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<A_TestComplexType> DATE_TIME_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<A_TestComplexType>(A_TestComplexType.class, "DateTimeProperty");
    /**
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeOffSetProperty</b></p>
     * 
     * @return
     *     The dateTimeOffSetProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("DateTimeOffSetProperty")
    private OffsetDateTime dateTimeOffSetProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<A_TestComplexType> DATE_TIME_OFF_SET_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.DateTime<A_TestComplexType>(A_TestComplexType.class, "DateTimeOffSetProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ByteProperty</b></p>
     * 
     * @return
     *     The byteProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("ByteProperty")
    private Short byteProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType> BYTE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.NumericInteger<A_TestComplexType>(A_TestComplexType.class, "ByteProperty");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>SByteProperty</b></p>
     * 
     * @return
     *     The sByteProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("SByteProperty")
    private Byte sByteProperty;
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>EnumProperty</b></p>
     * 
     * @return
     *     The enumProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("EnumProperty")
    private A_TestEnumType enumProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Enum<A_TestComplexType, A_TestEnumType> ENUM_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Enum<A_TestComplexType, A_TestEnumType>(A_TestComplexType.class, "EnumProperty", "API_TEST_SRV.A_TestEnumType");
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
     * 
     * @return
     *     The complexTypeProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("ComplexTypeProperty")
    private A_TestNestedComplexType complexTypeProperty;
    /**
     * Use with available request builders to apply the <b>ComplexTypeProperty</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<A_TestComplexType, A_TestNestedComplexType> COMPLEX_TYPE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Single<A_TestComplexType, A_TestNestedComplexType>(A_TestComplexType.class, "ComplexTypeProperty", A_TestNestedComplexType.class);
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>CollectionStringProperty</b></p>
     * 
     * @return
     *     The collectionStringProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("CollectionStringProperty")
    private java.util.Collection<java.lang.String> collectionStringProperty;
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Collection<A_TestComplexType, java.lang.String> COLLECTION_STRING_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.SimpleProperty.Collection<A_TestComplexType, java.lang.String>(A_TestComplexType.class, "CollectionStringProperty", java.lang.String.class);
    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>CollectionComplexTypeProperty</b></p>
     * 
     * @return
     *     The collectionComplexTypeProperty contained in this {@link VdmComplex}.
     */
    @Nullable
    @ElementName("CollectionComplexTypeProperty")
    private java.util.Collection<A_TestNestedComplexType> collectionComplexTypeProperty;
    /**
     * Use with available request builders to apply the <b>CollectionComplexTypeProperty</b> complex property to query operations.
     * 
     */
    public final static com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<A_TestComplexType, A_TestNestedComplexType> COLLECTION_COMPLEX_TYPE_PROPERTY = new com.sap.cloud.sdk.datamodel.odatav4.core.ComplexProperty.Collection<A_TestComplexType, A_TestNestedComplexType>(A_TestComplexType.class, "CollectionComplexTypeProperty", A_TestNestedComplexType.class);

    @Nonnull
    @Override
    public Class<A_TestComplexType> getType() {
        return A_TestComplexType.class;
    }

    @Nonnull
    @Override
    protected Map<java.lang.String, Object> toMapOfFields() {
        final Map<java.lang.String, Object> cloudSdkValues = super.toMapOfFields();
        cloudSdkValues.put("BaseStringProperty", getBaseStringProperty());
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
        cloudSdkValues.put("EnumProperty", getEnumProperty());
        cloudSdkValues.put("ComplexTypeProperty", getComplexTypeProperty());
        cloudSdkValues.put("CollectionStringProperty", getCollectionStringProperty());
        cloudSdkValues.put("CollectionComplexTypeProperty", getCollectionComplexTypeProperty());
        return cloudSdkValues;
    }

    @Override
    protected void fromMap(final Map<java.lang.String, Object> inputValues) {
        final Map<java.lang.String, Object> cloudSdkValues = Maps.newLinkedHashMap(inputValues);
        // simple properties
        {
            if (cloudSdkValues.containsKey("BaseStringProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("BaseStringProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getBaseStringProperty()))) {
                    setBaseStringProperty(((java.lang.String) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("StringProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("StringProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getStringProperty()))) {
                    setStringProperty(((java.lang.String) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("BooleanProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("BooleanProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getBooleanProperty()))) {
                    setBooleanProperty(((java.lang.Boolean) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("GuidProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("GuidProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getGuidProperty()))) {
                    setGuidProperty(((UUID) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("Int16Property")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Int16Property");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getInt16Property()))) {
                    setInt16Property(((Short) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("Int32Property")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Int32Property");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getInt32Property()))) {
                    setInt32Property(((Integer) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("Int64Property")) {
                final Object cloudSdkValue = cloudSdkValues.remove("Int64Property");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getInt64Property()))) {
                    setInt64Property(((Long) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("DecimalProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("DecimalProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getDecimalProperty()))) {
                    setDecimalProperty(((BigDecimal) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("SingleProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("SingleProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getSingleProperty()))) {
                    setSingleProperty(((Float) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("DoubleProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("DoubleProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getDoubleProperty()))) {
                    setDoubleProperty(((Double) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("TimeProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("TimeProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getTimeProperty()))) {
                    setTimeProperty(((LocalTime) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("DateTimeProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("DateTimeProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getDateTimeProperty()))) {
                    setDateTimeProperty(((OffsetDateTime) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("DateTimeOffSetProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("DateTimeOffSetProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getDateTimeOffSetProperty()))) {
                    setDateTimeOffSetProperty(((OffsetDateTime) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("ByteProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("ByteProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getByteProperty()))) {
                    setByteProperty(((Short) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("SByteProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("SByteProperty");
                if ((cloudSdkValue == null)||(!cloudSdkValue.equals(getSByteProperty()))) {
                    setSByteProperty(((Byte) cloudSdkValue));
                }
            }
            if (cloudSdkValues.containsKey("EnumProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("EnumProperty");
                if ((cloudSdkValue instanceof java.lang.String)||(cloudSdkValue == null)) {
                    final A_TestEnumType enumProperty = VdmEnum.getConstant(A_TestEnumType.class, ((java.lang.String) cloudSdkValue));
                    if (!Objects.equals(enumProperty, getEnumProperty())) {
                        setEnumProperty(enumProperty);
                    }
                }
            }
            if (cloudSdkValues.containsKey("CollectionStringProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("CollectionStringProperty");
                if (cloudSdkValue instanceof Iterable) {
                    final LinkedList<java.lang.String> collectionStringProperty = new LinkedList<java.lang.String>();
                    for (Object cloudSdkItem: ((Iterable<?> ) cloudSdkValue)) {
                        collectionStringProperty.add(((java.lang.String) cloudSdkItem));
                    }
                    setCollectionStringProperty(collectionStringProperty);
                }
            }
        }
        // structured properties
        {
            if (cloudSdkValues.containsKey("ComplexTypeProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("ComplexTypeProperty");
                if (cloudSdkValue instanceof Map) {
                    if (getComplexTypeProperty() == null) {
                        setComplexTypeProperty(new A_TestNestedComplexType());
                    }
                    @SuppressWarnings("unchecked")
                    final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                    getComplexTypeProperty().fromMap(inputMap);
                }
                if ((cloudSdkValue == null)&&(getComplexTypeProperty()!= null)) {
                    setComplexTypeProperty(null);
                }
            }
            if (cloudSdkValues.containsKey("CollectionComplexTypeProperty")) {
                final Object cloudSdkValue = cloudSdkValues.remove("CollectionComplexTypeProperty");
                if (cloudSdkValue instanceof Iterable) {
                    final LinkedList<A_TestNestedComplexType> collectionComplexTypeProperty = new LinkedList<A_TestNestedComplexType>();
                    for (Object cloudSdkProperties: ((Iterable<?> ) cloudSdkValue)) {
                        if (cloudSdkProperties instanceof Map) {
                            final A_TestNestedComplexType cloudSdkItem = new A_TestNestedComplexType();
                            @SuppressWarnings("unchecked")
                            final Map<java.lang.String, Object> inputMap = ((Map<java.lang.String, Object> ) cloudSdkValue);
                            cloudSdkItem.fromMap(inputMap);
                            collectionComplexTypeProperty.add(cloudSdkItem);
                        }
                    }
                    setCollectionComplexTypeProperty(collectionComplexTypeProperty);
                }
                if ((cloudSdkValue == null)&&(getCollectionComplexTypeProperty()!= null)) {
                    setCollectionComplexTypeProperty(null);
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
    protected ODataEntityKey getKey() {
        final ODataEntityKey entityKey = super.getKey();
        return entityKey;
    }

    /**
     * Constraints: Nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>BaseStringProperty</b></p>
     * 
     * @param baseStringProperty
     *     The baseStringProperty to set.
     */
    public void setBaseStringProperty(
        @Nullable
        final java.lang.String baseStringProperty) {
        rememberChangedField("BaseStringProperty", this.baseStringProperty);
        this.baseStringProperty = baseStringProperty;
    }

    /**
     * Constraints: Not nullable, Maximum length: 10 <p>Original property name from the Odata EDM: <b>StringProperty</b></p>
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
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>TimeProperty</b></p>
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
     * Constraints: Nullable, Precision: 0 <p>Original property name from the Odata EDM: <b>DateTimeProperty</b></p>
     * 
     * @param dateTimeProperty
     *     The dateTimeProperty to set.
     */
    public void setDateTimeProperty(
        @Nullable
        final OffsetDateTime dateTimeProperty) {
        rememberChangedField("DateTimeProperty", this.dateTimeProperty);
        this.dateTimeProperty = dateTimeProperty;
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
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>ComplexTypeProperty</b></p>
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

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>CollectionStringProperty</b></p>
     * 
     * @param collectionStringProperty
     *     The collectionStringProperty to set.
     */
    public void setCollectionStringProperty(
        @Nullable
        final java.util.Collection<java.lang.String> collectionStringProperty) {
        rememberChangedField("CollectionStringProperty", this.collectionStringProperty);
        this.collectionStringProperty = collectionStringProperty;
    }

    /**
     * Constraints: Nullable<p>Original property name from the Odata EDM: <b>CollectionComplexTypeProperty</b></p>
     * 
     * @param collectionComplexTypeProperty
     *     The collectionComplexTypeProperty to set.
     */
    public void setCollectionComplexTypeProperty(
        @Nullable
        final java.util.Collection<A_TestNestedComplexType> collectionComplexTypeProperty) {
        rememberChangedField("CollectionComplexTypeProperty", this.collectionComplexTypeProperty);
        this.collectionComplexTypeProperty = collectionComplexTypeProperty;
    }

}
